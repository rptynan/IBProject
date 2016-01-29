
package uk.ac.cam.qubec.core;
//test
import uk.ac.cam.quebec.userapi.APIServerAbstract;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.cam.quebec.core.test.TestDatabase;
import uk.ac.cam.quebec.core.test.Worker;
import uk.ac.cam.quebec.core.test.WorkerType;
import uk.ac.cam.quebec.dbwrapper.Database;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.trends.TrendsQueue;
import uk.ac.cam.quebec.userapi.UserAPIServer;
import uk.ac.cam.quebec.wikiwrapper.WikiArticle;

/**
 * A brief implementation of a first pass at some core logic 
 * @author James
 */
public class GroupProjectCore extends Thread implements TrendsQueue{
    private final List<Thread> Threadpool;
    private final BlockingQueue<Worker> ThreadQueue;
    private final BlockingQueue<Object> TweetQueue;
    private final BlockingQueue<WikiArticle> PageQueue;
    private final BlockingQueue<Trend> TrendQueue;
    private final UserAPIServer UAPI;//User API, here for testing only
    private final APIServerAbstract UAPII;//User API Interface
    private final Object TwitterWrapper;
    private final Object WikiWrapper;
    private final Database DB;
    private final static int UAPIPort = 90;
    private final static int ThreadPoolSize = 10;//The thread pool that we want to allocate for each job
    private Thread thisThread;
    private boolean running;
    public GroupProjectCore()
    {
        TweetQueue = new PriorityBlockingQueue<>();
        PageQueue = new PriorityBlockingQueue<>();
        TrendQueue = new PriorityBlockingQueue<>();
        Threadpool = new ArrayList<>();
        DB = new TestDatabase(); 
        ThreadQueue = new PriorityBlockingQueue<>();
        UAPI = new UserAPIServer(UAPIPort,DB,this);
        UAPII = UAPI;
        TwitterWrapper=null;
        WikiWrapper=null;
    }
    @Override
    public void run()
    {   running = true;
        thisThread = Thread.currentThread();
        thisThread.setName("CoreThread");
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
    private void mainLoop()
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
    private void close()
    {
        
    }
    public static void main(String[] args)
    {
        GroupProjectCore core = new GroupProjectCore();
        core.setDaemon(true);
        core.run();//Don't want to invoke a new thread from this entry point.
    }

    @Override
    public void putTrend(Trend trend) {
        boolean b = TrendQueue.add(trend);
    }

    private void populateThreadPool() {
        Worker t = null;
        for(int i=0; i<ThreadPoolSize;i++)
        {
            t = new Worker(WorkerType.Trend);
            Threadpool.add(t);
            ThreadQueue.add(t);
            t = new Worker(WorkerType.Tweet);
            Threadpool.add(t);
            ThreadQueue.add(t);
            t = new Worker(WorkerType.Page);
            Threadpool.add(t);
            ThreadQueue.add(t);            
        }
    }
}
