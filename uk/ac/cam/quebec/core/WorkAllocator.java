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
    private final PriorityBlockingQueue<TaskInterface> TrendQueue;
    private final PriorityBlockingQueue<TaskInterface> CoreQueue;
    private final Queue<Worker> ThreadQueue;
    private final List<Thread> ThreadPool;
    private int startedTasks = 0;
    private final Semaphore taskCount = new Semaphore(0);
    private final WorkerInterface parent;

    WorkAllocator(PriorityBlockingQueue<Worker> _ThreadQueue, List<Thread> _ThreadPool, WorkerInterface _parent) {
        WikiQueue = new MyPriorityBlockingQueue<>(taskCount);
        ThreadQueue = _ThreadQueue;
        ThreadPool = _ThreadPool;
        TweetQueue = new MyPriorityBlockingQueue<>(taskCount);
        CoreQueue = new MyPriorityBlockingQueue<>(taskCount);
        TrendQueue = new MyPriorityBlockingQueue<>(taskCount);
        parent = _parent;
    }

    public String getStatus() {
        String s = "There are: " + TrendQueue.size() + " trends, " + TweetQueue.size() + " tweets and " + WikiQueue.size() + " pages in the queue (" + waitingTasks() + " waiting tasks and " + taskCount.availablePermits() + " permits avaliable). There are currently " + ThreadQueue.size() + "/" + ThreadPool.size() + " idle threads. We have started " + startedTasks + " tasks.";
        return s;
    }

    public boolean putTrend(Trend t) {
        if (t == null) {
            System.err.println("Attempted to add null trend to queue");
            return false;
        }
        TrendTask task = new TrendTask(t);
        return TrendQueue.add(task);
    }

    public boolean putTask(Task t) {
        if (t == null) {
            System.err.println("Attempted to add null task to queue");
            return false;
        }
        TaskType type = t.getType();
        switch (type) {
            case Wiki:
                return WikiQueue.add(t.getTaskInterface());
            case Trend:
                return TrendQueue.add(t.getTaskInterface());
            case Tweet:
                return TweetQueue.add(t.getTaskInterface());
            case Core:
                return CoreQueue.add(t.getTaskInterface());
            default:
                return false;
        }
    }

    public boolean putTask(TaskInterface t) {
        if (t == null) {
            System.err.println("Attempted to add null task to queue");
            return false;
        }
        TaskType type = t.getType();
        switch (type) {
            case Wiki:
                return WikiQueue.add(t);
            case Trend:
                return TrendQueue.add(t);
            case Tweet:
                return TweetQueue.add(t);
            case Core:
                return CoreQueue.add(t);
            default:
                return false;
        }
    }

    public Task getTask(TaskType preferredType) throws InterruptedException {
        int perm = taskCount.availablePermits();
        int tasks = waitingTasks();
        if (perm != tasks) {
            System.err.println("Mismatch between waiting tasks (" + tasks + ") and permits (" + perm + "). " + startedTasks + " tasks previously started");
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
                t = TrendQueue.poll();
                break;
            case Core:
                t = CoreQueue.poll();
                break;
        }
        if (t != null) {
            ret = new Task(t);
            return ret;
        }
        if (!CoreQueue.isEmpty()) {
            t = CoreQueue.poll();
            ret = new Task(t);
            return ret;
        }
        if (!TrendQueue.isEmpty()) {
            t = TrendQueue.poll();
            ret = new Task(t);
            return ret;
        }
        if (!TweetQueue.isEmpty()) {
            t = TweetQueue.poll();
            ret = new Task(t);
            return ret;
        }
        if (!WikiQueue.isEmpty()) {
            t = WikiQueue.poll();
            ret = new Task(t);
            return ret;
        }
        taskCount.release();//If we get to here without a task assigned then 
        startedTasks--;//we should release our permit so another thread can use it
        return getTask(preferredType);//and recurse if we don't want to return null
        //return ret;
    }

    private int waitingTasks() {
        int i = CoreQueue.size();
        i += TweetQueue.size();
        i += WikiQueue.size();
        i += TrendQueue.size();
        return i;
    }

    /**
     * Note not thread safe, for testing only
     */
    public synchronized void clearAllTasks() {
        int i = taskCount.availablePermits();
        if (i != 0) {
            int j = taskCount.drainPermits();
            int k = CoreQueue.size();
            TrendQueue.clear();
            TweetQueue.clear();
            WikiQueue.clear();
            taskCount.release(k);//Don't want to clear the Core Queue tasks
        }
    }

    private Collection<Thread> getRunningTasks() {
        Collection<Thread> disjunction = CollectionUtils.disjunction(ThreadPool, ThreadQueue);
        return disjunction;
    }

    public void cleanRunningTasks() {
        Collection<Thread> disjunction = getRunningTasks();
        Worker w;
        for (Thread t : disjunction) {
            w = (Worker) t;
            if (!w.hasTask()) {
                System.err.println("Force reallocating worker: " + w);
                parent.reallocateWorker(w);
            }
        }
    }

    /**
     * Note not thread safe either
     *
     * @return true if null entries were removed from a collection
     */
    public synchronized boolean cleanQueues() {

        boolean b = false;
        b = b || CoreQueue.remove(null);
        b = b || TweetQueue.remove(null);
        b = b || TrendQueue.remove(null);
        b = b || WikiQueue.remove(null);
        b = b || ThreadQueue.remove(null);
        b = b || ThreadPool.remove(null);
        return b;

    }

    /**
     * Not thread safe
     * @return true if the number of permits needed changing
     */
    public synchronized boolean reconcilePermits() {
        int tasks =waitingTasks(); 
        if (tasks == taskCount.availablePermits()) {
            return false;
        } else {
            taskCount.drainPermits();
            taskCount.release(waitingTasks());
            return true;
        }
    }

    public String getRunningTasksStatus() {
        String s = "";
        Collection<Thread> disjunction = getRunningTasks();
        s = disjunction.stream().map((t) -> t.toString() + System.lineSeparator()).reduce(s, String::concat);
        return s + "There are currently " + disjunction.size() + " tasks running";
    }
}
