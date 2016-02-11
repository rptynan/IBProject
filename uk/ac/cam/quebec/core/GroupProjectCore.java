
package uk.ac.cam.quebec.core;

import java.io.IOException;
import uk.ac.cam.quebec.userapi.APIServerAbstract;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.cam.quebec.core.test.Worker;
import uk.ac.cam.quebec.dbwrapper.Database;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.trends.TrendsQueue;
import uk.ac.cam.quebec.twitterproc.TwitterProcessor;
import uk.ac.cam.quebec.twitterwrapper.TwitException;
import uk.ac.cam.quebec.twitterwrapper.TwitterLink;
import uk.ac.cam.quebec.userapi.NewAPIServer;
import uk.ac.cam.quebec.wikiproc.WikiProcessor;
import uk.ac.cam.quebec.wikiwrapper.WikiArticle;

/**
 * A brief implementation of a first pass at some core logic 
 * @author James
 */
public class GroupProjectCore extends Thread implements TrendsQueue, ControlInterface{
    private final List<Thread> Threadpool;
    private final PriorityBlockingQueue<Worker> ThreadQueue;
    private final PriorityBlockingQueue<Object> TweetQueue;
    private final PriorityBlockingQueue<WikiArticle> PageQueue;
    private final PriorityBlockingQueue<Trend> TrendQueue;
    private final WorkAllocator workAllocator;
    private final NewAPIServer UAPI;//User API, here for testing only
    private final APIServerAbstract UAPII;//User API Interface
    private final TwitterLink twitterWrapper;
    private final TwitterProcessor twitterProcessor;
    private final WikiProcessor wikiProcessor;
    private final Database DB;
    private final static int UAPIPort = 90;
    private final static int ThreadPoolSize = 10;//The thread pool that we want to allocate for each job
    private boolean running;
    private String location = "World";
    public GroupProjectCore(String[] TwitterLoginArgs, Database _DB) throws IOException, TwitException
    {
        TweetQueue = new PriorityBlockingQueue<>();
        PageQueue = new PriorityBlockingQueue<>();
        TrendQueue = new PriorityBlockingQueue<>();//Todo: replace with priority multiqueue
        workAllocator = new WorkAllocator(TweetQueue,PageQueue,TrendQueue);
        Threadpool = new ArrayList<>();
        DB = _DB;
        ThreadQueue = new PriorityBlockingQueue<>();
        UAPI = new NewAPIServer(DB,UAPIPort,this);
        UAPII = UAPI;
        TwitterLink.login(TwitterLoginArgs[0],TwitterLoginArgs[1],TwitterLoginArgs[2],TwitterLoginArgs[3],TwitterLoginArgs[4]);
        twitterWrapper = new TwitterLink();
        twitterProcessor = new TwitterProcessor();
        wikiProcessor=new WikiProcessor();
    }
    @Override
    public void run()
    {   running = true;
        this.setName("CoreThread");
        startUAPI();
        populateThreadPool();
        mainLoop();
        close();
    }
    private void startUAPI()
    {
        UAPII.setDaemon(true);
        UAPII.setName("UserAPIServer");
        UAPII.start();
    }
    private void getTrends() throws TwitException
    {   List<String> tr = twitterWrapper.getTrends(location);
        Trend trend = null;
        for(String s : tr)
        {
            trend = new Trend(s,location,4);
            TrendQueue.add(trend);
        }
        
    }
    private void mainLoop()
    {   
        try {
        getTrends();
        Worker w;
        while(running)
        {
            w = ThreadQueue.take();
        Trend T = TrendQueue.take();
        w.process(T);
        if(!w.isAlive())
        {
            w.start();
        }
//w.start();
        //TwitterProcessor.process(T);
        //wikiProcessor.process(T);
        }
        } catch (InterruptedException ex) {
            Logger.getLogger(GroupProjectCore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TwitException ex) {
            Logger.getLogger(GroupProjectCore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * This is for when we want to make it really multithreaded
     */
    private void mainLoopOld()
    {Worker w;
        while(running)
        {   
        try {
            w = ThreadQueue.take();//we want to block until a thread is free
            Object o = null;
            switch(w.getWorkerType())//Then we try to assign it the apropriate job for the thread
            {
                case Trend:
                o = TrendQueue.poll();
                if(o!=null)//but if there is no work avaliable then do someone else's job
                {
                break;
                }
                case Tweet:
                o = TweetQueue.poll();
                if(o!=null)
                {
                break;
                }
                case Page:
                o = PageQueue.poll();
                if(o!=null)
                {
                break;
                }
                default:
                //Todo: add multiqueue take here
                break;
            }
            if(o!=null)
            {
                w.process(o);
            }
                    } catch (InterruptedException ex) {
            Logger.getLogger(GroupProjectCore.class.getName()).log(Level.SEVERE, null, ex);
            running = false;//something interupts us then we should close down.
        }
        }
    }
    /**
     * Kill everything neatly
     */
    private void close()
    {
        
    }
    public static void main(String[] args) throws IOException, TwitException
    {Database DB = Database.getInstance();
        GroupProjectCore core = new GroupProjectCore(args,DB);
        core.setDaemon(true);
        core.run();//Don't want to invoke a new thread from this entry point.
    }

    @Override
    public boolean putTrend(Trend trend) {
        return TrendQueue.add(trend);
    }

    private void populateThreadPool() {
        Worker t = null;
        for(int i=0; i<ThreadPoolSize;i++)
        {
            t = new Worker(TaskType.Trend,this);
            t.setDaemon(true);
            t.setName(t.getWorkerType().toString()+" worker thread id: "+i);
            Threadpool.add(t);
            ThreadQueue.add(t);
            t = new Worker(TaskType.Tweet,this);
            t.setDaemon(true);
            t.setName(t.getWorkerType().toString()+" worker thread id: "+i);
            Threadpool.add(t);
            ThreadQueue.add(t);
            t = new Worker(TaskType.Page,this);
            t.setDaemon(true);
            t.setName(t.getWorkerType().toString()+" worker thread id: "+i);
            Threadpool.add(t);
            ThreadQueue.add(t);            
        }
        t = new Worker(TaskType.Core,this);
        t.setDaemon(true);
        t.setName(t.getWorkerType().toString()+" worker thread");
        Threadpool.add(t);
        ThreadQueue.add(t);
    }
    public void reallocateWorker(Worker w)
    {
        ThreadQueue.add(w);
    }
    @Override
    public String getServerInfo() {
        if(running)
        {
        String s = "";
        s += "There are: " + TrendQueue.size()+ " trends, "+TweetQueue.size()+" tweets and "+PageQueue.size()+" pages in the queue. There are currently "+ThreadQueue.size()+" threads idle.";
        return s;
        }
        else
        {
            return "Core is not running";
        }
    }

    @Override
    public void beginClose() {
      running = false;
      this.interrupt();
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
