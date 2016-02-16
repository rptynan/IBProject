/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;
import uk.ac.cam.quebec.core.test.TestTask;
import uk.ac.cam.quebec.core.test.TrendTask;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.wikiwrapper.WikiArticle;

/**
 * This class is used to allocate tasks to the worker threads
 *
 * @author James
 */
public class WorkAllocator {

    private final PriorityBlockingQueue<Object> TweetQueue;
    private final PriorityBlockingQueue<WikiArticle> PageQueue;
    private final PriorityBlockingQueue<TaskInterface> TrendTaskQueue;
    private final PriorityBlockingQueue<TaskInterface> CoreQueue;
    private final Queue<Worker> ThreadQueue;
    private final List<Thread> ThreadPool;
    private final Semaphore taskCount= new Semaphore(0);
    

    WorkAllocator(PriorityBlockingQueue<Object> _TweetQueue, PriorityBlockingQueue<WikiArticle> _PageQueue, PriorityBlockingQueue<Worker> _ThreadQueue, List<Thread> _ThreadPool) {
        TweetQueue = _TweetQueue;
        PageQueue = _PageQueue;
        ThreadQueue = _ThreadQueue;
        ThreadPool = _ThreadPool;
        CoreQueue = new MyPriorityBlockingQueue<>(taskCount);
        TrendTaskQueue = new MyPriorityBlockingQueue<>(taskCount);
    }

    public String getStatus() {
        String s = "There are: " + TrendTaskQueue.size() + " trends, " + TweetQueue.size() + " tweets and " + PageQueue.size() + " pages in the queue ("+taskCount.availablePermits()+" permits avaliable). There are currently " + ThreadQueue.size() + "/" + ThreadPool.size() + " idle threads.";
        return s;
    }
    public boolean putTrend(Trend t)
    {   TrendTask task = new TrendTask(t);
        return TrendTaskQueue.add(task);
    }
    public boolean putTask(Task t) {
        TaskType type = t.getTaskType();
        switch (type) {
            case Page:
                return false;
            case Trend:
                return TrendTaskQueue.add(t.getTaskInterface());
            case Tweet:
                return false;
            case Core:
                return CoreQueue.add(t.getTaskInterface());
            default:
                return false;
        }
    }

    public Task getTask(TaskType preferredType) throws InterruptedException {
        taskCount.acquire();
        Task ret = null;
        TaskInterface t = null;
        Object o = null;
        switch (preferredType) {
            case Tweet:
                o = TweetQueue.poll();
                break;
            case Page:
                o = PageQueue.poll();
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
        if (o != null) {
            TestTask tst = new TestTask(o);
            ret = new Task(tst, preferredType);
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
            TestTask tst = new TestTask(TweetQueue.poll());
            ret = new Task(tst, TaskType.Tweet);
            return ret;
        }
        if (!PageQueue.isEmpty()) {
            TestTask tst = new TestTask(PageQueue.poll());
            ret = new Task(tst, TaskType.Page);
            return ret;
        }
        return ret;
    }
}
