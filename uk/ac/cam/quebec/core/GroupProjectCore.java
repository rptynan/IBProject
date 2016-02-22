package uk.ac.cam.quebec.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import uk.ac.cam.quebec.dbwrapper.Database;
import uk.ac.cam.quebec.kgsearchwrapper.APIConstants;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.trends.TrendsQueue;
import uk.ac.cam.quebec.twitterproc.TwitterProcessor;
import uk.ac.cam.quebec.twitterwrapper.TwitException;
import uk.ac.cam.quebec.twitterwrapper.TwitterLink;
import uk.ac.cam.quebec.userapi.APIServerAbstract;
import uk.ac.cam.quebec.userapi.NewAPIServer;
import uk.ac.cam.quebec.wikiproc.WikiProcessor;

/**
 * A brief implementation of a first pass at some core logic
 *
 * @author James
 */
public class GroupProjectCore extends Thread implements TrendsQueue, ControlInterface, WorkerInterface {

    private final List<Thread> ThreadPool = new ArrayList<>();
    private final PriorityBlockingQueue<Worker> ThreadQueue = new PriorityBlockingQueue<>();
    private final WorkAllocator workAllocator = new WorkAllocator(ThreadQueue, ThreadPool);
    private final NewAPIServer UAPI;//User API, here for testing only
    private final APIServerAbstract UAPII;//User API Interface
    private final TwitterLink twitterWrapper;//Always goood
    private final TwitterProcessor twitterProcessor;//to try and ensure
    private final WikiProcessor wikiProcessor;//that all our singleton classes
    private final Database DB;//are initialised to start with
    private final int ThreadPoolSize;//The thread pool that we want to allocate for each job
    private final Configuration config;
    private boolean running;
    private final String defaultLocation;
    private final TrendRefreshTask refreshTask;

    @SuppressWarnings("static-access")
    public GroupProjectCore(Configuration _config) throws IOException, TwitException {
        config = _config;
        DB = config.getDatabase();
        UAPI = new NewAPIServer(DB, config.getUAPI_Port(), this);
        UAPII = UAPI;
        String[] TwitterLoginArgs = config.getTwitterArgs();
        TwitterLink.login(TwitterLoginArgs[0], TwitterLoginArgs[1], TwitterLoginArgs[2], TwitterLoginArgs[3], TwitterLoginArgs[4]);
        twitterWrapper = new TwitterLink();
        twitterProcessor = new TwitterProcessor();
        wikiProcessor = new WikiProcessor();
        defaultLocation = config.getDefaultLocation();
        APIConstants.setCredentials(config.getKnowledgeGraphKey());
        uk.ac.cam.quebec.havenapi.APIConstants.setCredentials(config.getSentimentAnalyserKey());
        ThreadPoolSize = config.getThreadPoolSize();
        refreshTask = makeTrendRefreshTask();
    }

    @Deprecated
    public GroupProjectCore(String[] TwitterLoginArgs, Database _DB, String _location) throws IOException, TwitException {
        int UAPIPort = 90;
        config = new Configuration(TwitterLoginArgs, new String[1], new String[1]);
        DB = _DB;
        UAPI = new NewAPIServer(DB, UAPIPort, this);
        UAPII = UAPI;
        ThreadPoolSize = 10;
        TwitterLink.login(TwitterLoginArgs[0], TwitterLoginArgs[1], TwitterLoginArgs[2], TwitterLoginArgs[3], TwitterLoginArgs[4]);
        twitterWrapper = new TwitterLink();
        twitterProcessor = new TwitterProcessor();
        wikiProcessor = new WikiProcessor();
        defaultLocation = _location;
        refreshTask = makeTrendRefreshTask();
    }

    @Override
    public void run() {
        running = true;
        this.setName("CoreThread");
        startUAPI();
        populateThreadPool();
        mainLoop();
        close();
    }

    private void startUAPI() {
        if (!UAPII.isAlive()) {
            UAPII.setDaemon(true);
            UAPII.setName("UserAPIServer");
            UAPII.start();
        } else {
            System.err.println("Attempted to start User API server failed, server already running");
        }
    }

    @SuppressWarnings("static-access")
    private void getTrends() throws TwitException {
        String[] locations = config.getLocations();
        for (String location : locations) {
            List<String> tr = twitterWrapper.getTrends(location);
            Trend trend = null;
            int trendLimit = config.getTrendsPerLocation();
            for (String s : tr) {
                if (trendLimit == 0) {
                    break;
                }
                trend = new Trend(s, location, 4);
                workAllocator.putTrend(trend);
                trendLimit--;
            }
        }
    }

    /**
     * This is the main loop of the program, it should loop around in here until
     *
     */
    private void mainLoop() {
        try {
            Worker w;
            Task task = new Task(refreshTask, TaskType.Core);
            workAllocator.putTask(task);
            while (running) {
                w = ThreadQueue.take();
                task = workAllocator.getTask(w.getWorkerType());
                boolean process = w.process(task);
                if (!w.isAlive()) {
                    w.start();
                }
                if (!process) {
                    System.err.println("There was an error processing the task");
                }
            }
        } catch (InterruptedException ex) {
            System.out.println("Core loop interupted, core should be closing");
        }
    }
    @SuppressWarnings("static-access")
    private TrendRefreshTask makeTrendRefreshTask() {        
        int delay = config.getTrendRefreshTime();
        TrendRefreshTask tmp = new TrendRefreshTask(delay, this);
        return tmp;
    }

    /**
     * Kill everything neatly
     */
    private synchronized void close() {
        //Todo: put cleanup here
    }

    @Override
    public boolean putTrend(Trend trend) {
        return workAllocator.putTrend(trend);
    }

    private void populateThreadPool() {
        Worker t = null;
        for (int i = 0; i < ThreadPoolSize; i++) {
            t = new Worker(TaskType.Trend, this);
            t.setDaemon(true);
            t.setName(t.getWorkerType().toString() + " worker thread id: " + i);
            ThreadPool.add(t);
            ThreadQueue.add(t);
            t = new Worker(TaskType.Tweet, this);
            t.setDaemon(true);
            t.setName(t.getWorkerType().toString() + " worker thread id: " + i);
            ThreadPool.add(t);
            ThreadQueue.add(t);
            t = new Worker(TaskType.Wiki, this);
            t.setDaemon(true);
            t.setName(t.getWorkerType().toString() + " worker thread id: " + i);
            ThreadPool.add(t);
            ThreadQueue.add(t);
        }
        t = new Worker(TaskType.Core, this);
        t.setDaemon(true);
        t.setName(t.getWorkerType().toString() + " worker thread");
        ThreadPool.add(t);
        ThreadQueue.add(t);
    }

    @Override
    public void reallocateWorker(Worker w) {
        ThreadQueue.add(w);
    }

    @Override
    public String getServerInfo() {
        String s = "";
        if (running) {
            s += "Core is running" + System.lineSeparator();
        } else {
            s += "Core is not running" + System.lineSeparator();
        }
        s += workAllocator.getStatus() + System.lineSeparator();
        s += UAPII.getStatus() + System.lineSeparator();
        s += refreshTask.getStatus();
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
    public void repopulateTrends() throws TwitException {
        // try {
        getTrends();
        /* } catch (TwitException ex) {
         System.err.println("Error repopulating trends");
         System.err.println(ex);
         }*/
    }

    @Override
    public boolean addTask(Task t) {
        return workAllocator.putTask(t);
    }

    @Override
    public boolean addTasks(Collection<Task> t) {
        boolean b0 = true;
        if (t == null) {
            return b0;
        }

        for (Task task : t) {
            b0 = b0 && workAllocator.putTask(task);
        }
        return b0;
    }

    @Override
    public void initialiseUAPI() {
        startUAPI();
    }

    @SuppressWarnings("CallToThreadRun")
    public static void main(String[] args) throws IOException, TwitException {
        Configuration config;
        try {
            config = new Configuration(args[0]);
        } catch (FileNotFoundException ex) {
            System.err.println("Failed to load config file from " + args[0] + " using fallback values");
            String[] SentimentAnalyserArgs = {""};
            String[] KnowledgeGraphArgs = {""};
            config = new Configuration(args, SentimentAnalyserArgs, KnowledgeGraphArgs);
        }
        GroupProjectCore core = new GroupProjectCore(config);
        core.setDaemon(true);
        core.run();//Don't want to invoke a new thread from this entry point.
    }

    @Override
    public long timeUntilRepopulate() {
        return refreshTask.remainingTime();
    }

    @Override
    public void forceRepopulate() {
        refreshTask.forceRefresh();
    }

    @Override
    public void clearAllTasks() {
      workAllocator.clearAllTasks();
    }
}
