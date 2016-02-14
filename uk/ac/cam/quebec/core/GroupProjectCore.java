
package uk.ac.cam.quebec.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import uk.ac.cam.quebec.userapi.APIServerAbstract;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.cam.quebec.dbwrapper.Database;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.trends.TrendsQueue;
import uk.ac.cam.quebec.twitterproc.TwitterProcessor;
import uk.ac.cam.quebec.twitterwrapper.TwitException;
import uk.ac.cam.quebec.twitterwrapper.TwitterLink;
import uk.ac.cam.quebec.userapi.NewAPIServer;
import uk.ac.cam.quebec.wikiproc.WikiProcessor;
import uk.ac.cam.quebec.wikiwrapper.WikiArticle;
//import uk.ac.cam.quebec.kgsearchwrapper.APIConstants;
import uk.ac.cam.quebec.havenapi.APIConstants;

/**
 * A brief implementation of a first pass at some core logic 
 * @author James
 */
public class GroupProjectCore extends Thread implements TrendsQueue, ControlInterface{
    private final List<Thread> ThreadPool = new ArrayList<>();
    private final PriorityBlockingQueue<Worker> ThreadQueue = new PriorityBlockingQueue<>();
    private final PriorityBlockingQueue<Object> TweetQueue = new PriorityBlockingQueue<>();
    private final PriorityBlockingQueue<WikiArticle> PageQueue = new PriorityBlockingQueue<>();
    private final PriorityBlockingQueue<Trend> TrendQueue = new PriorityBlockingQueue<>();
    private final WorkAllocator workAllocator= new WorkAllocator(TweetQueue,PageQueue,TrendQueue,ThreadQueue,ThreadPool);;
    private final NewAPIServer UAPI;//User API, here for testing only
    private final APIServerAbstract UAPII;//User API Interface
    private final TwitterLink twitterWrapper;
    private final TwitterProcessor twitterProcessor;
    private final WikiProcessor wikiProcessor;
    private final Database DB;
    private final static int UAPIPort = 90;
    private final static int ThreadPoolSize = 10;//The thread pool that we want to allocate for each job
    private final Configuration config;
    private boolean running;
    private String location;
    public GroupProjectCore(Configuration _config) throws IOException, TwitException
    {   config = _config;
        DB = config.getDatabase();
        UAPI = new NewAPIServer(DB,config.getUAPI_Port(),this);
        UAPII = UAPI;
        String[] TwitterLoginArgs = config.getTwitterArgs();
        TwitterLink.login(TwitterLoginArgs[0],TwitterLoginArgs[1],TwitterLoginArgs[2],TwitterLoginArgs[3],TwitterLoginArgs[4]);
        twitterWrapper = new TwitterLink();
        twitterProcessor = new TwitterProcessor();
        wikiProcessor=new WikiProcessor();
        location = config.getLocation();
        APIConstants.setCredentials(config.getKnowledgeGraphKey());
        uk.ac.cam.quebec.havenapi.APIConstants.setCredentials(config.getSentimentAnalyserKey());
    }
    
    public GroupProjectCore(String[] TwitterLoginArgs, Database _DB,String _location) throws IOException, TwitException
    {   config = null;
        DB = _DB;
        UAPI = new NewAPIServer(DB,UAPIPort,this);
        UAPII = UAPI;
        TwitterLink.login(TwitterLoginArgs[0],TwitterLoginArgs[1],TwitterLoginArgs[2],TwitterLoginArgs[3],TwitterLoginArgs[4]);
        twitterWrapper = new TwitterLink();
        twitterProcessor = new TwitterProcessor();
        wikiProcessor=new WikiProcessor();
        location = _location;
    }
    @Override
    public void run()
    {   running = true;
        this.setName("CoreThread");
        startUAPI();
        populateThreadPool();
        repopulateTrends();
        mainLoop();
        close();
    }
    private void startUAPI()
    {
    if(!UAPII.isAlive())
    {
        UAPII.setDaemon(true);
        UAPII.setName("UserAPIServer");
        UAPII.start();
    }
    else{
        System.err.println("Attempted to start User API server failed, server already running");
    }
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
    /**
     * This is the main loop of the program, it should loop around in here until
     * 
     */
    private void mainLoop()
    {   
        try {
        Worker w;
        while(running)
        {
            w = ThreadQueue.take();
        //Task task = workAllocator.getTask(w.getWorkerType());
        //w.process(task);
        Trend T = TrendQueue.take();
        w.process(T);
        if(!w.isAlive())
        {
            w.start();
        }
        }
        } catch (InterruptedException ex) {
         System.out.println("Core loop interupted, core should be closing");
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
                w.processObject(o);
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
    private synchronized void close()
    {
        //Todo: put cleanup here
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
            ThreadPool.add(t);
            ThreadQueue.add(t);
            t = new Worker(TaskType.Tweet,this);
            t.setDaemon(true);
            t.setName(t.getWorkerType().toString()+" worker thread id: "+i);
            ThreadPool.add(t);
            ThreadQueue.add(t);
            t = new Worker(TaskType.Page,this);
            t.setDaemon(true);
            t.setName(t.getWorkerType().toString()+" worker thread id: "+i);
            ThreadPool.add(t);
            ThreadQueue.add(t);            
        }
        t = new Worker(TaskType.Core,this);
        t.setDaemon(true);
        t.setName(t.getWorkerType().toString()+" worker thread");
        ThreadPool.add(t);
        ThreadQueue.add(t);
    }
    public void reallocateWorker(Worker w)
    {
        ThreadQueue.add(w);
    }
    @Override
    public String getServerInfo() {
        String s = "";
        if(running)
        {
        s += "Core is running"+System.lineSeparator();
        }
        else
        {
           s += "Core is not running"+System.lineSeparator();
        }
        s += workAllocator.getStatus()+System.lineSeparator();
        s += UAPII.getStatus();
        return s;
        
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

    @Override
    public void repopulateTrends() {
        
        try {
            getTrends();
        } catch (TwitException ex) {
            System.err.println("Error repopulating trends");
            System.err.println(ex);
        }
    }

    @Override
    public void initialiseUAPI() {
        startUAPI();
    }
    @SuppressWarnings("CallToThreadRun")
    public static void main(String[] args) throws IOException, TwitException
    {   
        Configuration config;
            try
            {
            config = new Configuration(args[0]);
            }
            catch (FileNotFoundException ex)
            {   
            System.err.println("Failed to load config file from "+args[0]+" using fallback values");
            String[] UAPI = {"90"};
            String[] SentimentAnalyserArgs = {""};
            String[] KnowledgeGraphArgs = {""};
            String location = "world";
            config = new Configuration(args,null,UAPI,location,SentimentAnalyserArgs,KnowledgeGraphArgs);
            }
        GroupProjectCore core = new GroupProjectCore(config);
        core.setDaemon(true);
        core.run();//Don't want to invoke a new thread from this entry point.
    }
}
