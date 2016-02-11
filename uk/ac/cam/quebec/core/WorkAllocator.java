/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import uk.ac.cam.quebec.core.test.Worker;
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
    private final PriorityBlockingQueue<Trend> TrendQueue;
    private final PriorityBlockingQueue<Object> CoreQueue;
    private final Queue<Worker>ThreadQueue;
    private final List<Thread>ThreadPool;

    WorkAllocator(PriorityBlockingQueue<Object> _TweetQueue, PriorityBlockingQueue<WikiArticle> _PageQueue, PriorityBlockingQueue<Trend> _TrendQueue, PriorityBlockingQueue<Worker> _ThreadQueue, List<Thread> _ThreadPool) {
        TweetQueue = _TweetQueue;
        PageQueue = _PageQueue;
        TrendQueue = _TrendQueue;
        ThreadQueue = _ThreadQueue;
        ThreadPool = _ThreadPool;
        CoreQueue = new PriorityBlockingQueue<>();
    }

    
    public String getStatus()
    {
        String s = "There are: " + TrendQueue.size()+ " trends, "+TweetQueue.size()+" tweets and "+PageQueue.size()+" pages in the queue. There are currently "+ThreadQueue.size()+"/"+ThreadPool.size()+" idle threads.";
        return s;
    }
    public Task getTask(TaskType preferredType) {
        Task ret = null;
        Object o = null;
        switch (preferredType) {
            case Tweet:
                o = TweetQueue.poll();
                break;
            case Page:
                o = PageQueue.poll();
                break;
            case Trend:
                o = TrendQueue.poll();
                break;
            case Core:
                o = CoreQueue.poll();
                break;
        }
        if(o!=null)
        {
            ret = new Task(o,preferredType);
            return ret;
        }
        if (!CoreQueue.isEmpty())
        {
            ret = new Task(CoreQueue.poll(),TaskType.Core);
            return ret;
        }
        if (!TrendQueue.isEmpty())
        {
            ret = new Task(TrendQueue.poll(),TaskType.Trend);
            return ret;
        }
        if (!TweetQueue.isEmpty())
        {
            ret = new Task(TweetQueue.poll(),TaskType.Tweet);
            return ret;
        }
        if (!PageQueue.isEmpty())
        {
            ret = new Task(PageQueue.poll(),TaskType.Page);
            return ret;
        }
        return ret;
    }
}
