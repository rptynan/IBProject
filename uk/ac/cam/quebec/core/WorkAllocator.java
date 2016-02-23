/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;
import org.apache.commons.collections.CollectionUtils;
import uk.ac.cam.quebec.trends.Trend;

/**
 * This class is used to allocate tasks to the worker threads
 *
 * @author James
 */
public class WorkAllocator {

    private final PriorityBlockingQueue<TaskInterface> TweetQueue;
    private final PriorityBlockingQueue<TaskInterface> WikiQueue;
    private final PriorityBlockingQueue<TaskInterface> TrendTaskQueue;
    private final PriorityBlockingQueue<TaskInterface> CoreQueue;
    private final Queue<Worker> ThreadQueue;
    private final List<Thread> ThreadPool;
    private int startedTasks = 0;
    private final Semaphore taskCount= new Semaphore(0);
    

    WorkAllocator(PriorityBlockingQueue<Worker> _ThreadQueue, List<Thread> _ThreadPool) {
        WikiQueue = new MyPriorityBlockingQueue<>(taskCount);
        ThreadQueue = _ThreadQueue;
        ThreadPool = _ThreadPool;
        TweetQueue = new MyPriorityBlockingQueue<>(taskCount);
        CoreQueue = new MyPriorityBlockingQueue<>(taskCount);
        TrendTaskQueue = new MyPriorityBlockingQueue<>(taskCount);
    }

    public String getStatus() {
        String s = "There are: " + TrendTaskQueue.size() + " trends, " + TweetQueue.size() + " tweets and " + WikiQueue.size() + " pages in the queue ("+waitingTasks()+" waiting tasks and "+taskCount.availablePermits()+" permits avaliable). There are currently " + ThreadQueue.size() + "/" + ThreadPool.size() + " idle threads. We have executed "+startedTasks+" tasks.";
        return s;
    }
    public boolean putTrend(Trend t)
    {   TrendTask task = new TrendTask(t);
        return TrendTaskQueue.add(task);
    }
    public boolean putTask(Task t) {
        TaskType type = t.getTaskType();
        switch (type) {
            case Wiki:
                return WikiQueue.add(t.getTaskInterface());
            case Trend:
                return TrendTaskQueue.add(t.getTaskInterface());
            case Tweet:
                return TweetQueue.add(t.getTaskInterface());
            case Core:
                return CoreQueue.add(t.getTaskInterface());
            default:
                return false;
        }
    }

    public Task getTask(TaskType preferredType) throws InterruptedException {
        if(taskCount.availablePermits()!=waitingTasks())
        {
            System.err.println("Mismatch between waiting tasks and permits");
        }
        taskCount.acquire();
        startedTasks++;
        Task ret = null;
        TaskInterface t = null;
        switch (preferredType) {
            case Tweet:
                t = TweetQueue.poll();
                break;
            case Wiki:
                t = WikiQueue.poll();
                break;
            case Trend:
                t = TrendTaskQueue.poll();
                break;
            case Core:
                t = CoreQueue.poll();
                break;
        }
        if(t!=null)
        {
            ret = new Task(t, preferredType);
            return ret;
        }
        if (!CoreQueue.isEmpty()) {
            t = CoreQueue.poll();
                ret = new Task(t,TaskType.Core);
                return ret;
        }
        if (!TrendTaskQueue.isEmpty()) {
            t = TrendTaskQueue.poll();
            ret = new Task(t, TaskType.Trend);
            return ret;
        }
        if (!TweetQueue.isEmpty()) {
            t =TweetQueue.poll();
            ret = new Task(t, TaskType.Tweet);
            return ret;
        }
        if (!WikiQueue.isEmpty()) {
             t = WikiQueue.poll();
            ret = new Task(t, TaskType.Trend);
            return ret;
        }
        //taskCount.release();//If we get to here without a task assigned then 
        //startedTasks--;//we should release our permit so another thread can use it
        return ret;
    }
    private int waitingTasks()
    {
        int i = CoreQueue.size();
        i+= TweetQueue.size();
        i+= WikiQueue.size();
        i+=TrendTaskQueue.size();
        return i;
    }
    /**
     * Note not thread safe, for testing only
     */
    public void clearAllTasks() {
        int i=taskCount.availablePermits();
     if(i!=0)
     {int j = taskCount.drainPermits();
         int k = CoreQueue.size();
         TrendTaskQueue.clear();
         TweetQueue.clear();
         WikiQueue.clear();
         taskCount.release(k);//Don't want to clear the Core Queue tasks
     }
    }
    public String getRunningTasks()
    {String s = "";
    Collection<Thread> disjunction = CollectionUtils.disjunction(ThreadPool, ThreadQueue);
    s = disjunction.stream().map((t) -> t.toString()+System.lineSeparator()).reduce(s, String::concat);                  
    return s + "There are currently "+disjunction+" tasks running";
    }
}
