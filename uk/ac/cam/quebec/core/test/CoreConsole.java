/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.cam.quebec.core.Configuration;
import uk.ac.cam.quebec.core.ControlInterface;
import uk.ac.cam.quebec.core.GroupProjectCore;
import uk.ac.cam.quebec.dbwrapper.DatabaseTest;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.trends.TrendsQueue;
import uk.ac.cam.quebec.util.WordCounterTest;
import java.util.regex.Matcher;
import uk.ac.cam.quebec.havenapi.HavenException;
import uk.ac.cam.quebec.havenapi.SentimentAnalyserTest;
import uk.ac.cam.quebec.kgsearchwrapper.KGConceptGeneratorTest;
import uk.ac.cam.quebec.twitterwrapper.TwitException;
import uk.ac.cam.quebec.wikiwrapper.WikiException;

/**
 *
 * @author James
 */
public class CoreConsole extends Thread {

    private final BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
    private final GroupProjectCore core;
    private final ControlInterface coreInter;
    private final TrendsQueue coreTrends;
    private final Configuration config;
    private boolean running = true;

    public CoreConsole(GroupProjectCore _core, Configuration _config) {
        core = _core;
        coreInter = _core;
        coreTrends = _core;
        config = _config;
    }

    private void processCommand(String command) throws WikiException, HavenException {
        CoreConsoleCommand c = CoreConsoleCommand.getCommandType(command);
        switch (c) {
            case StartCommand:
                startCore();
                break;
            case ExitCommand:
                exit();
                break;
            case StatusCommand:
                System.out.println(coreInter.getServerInfo());
                break;
            case AddTrendCommand:
                addTrend(c, command);
                break;
            case SentimentAnalysisTestCommand:
                System.out.println("Sentiment analysis test start");
                SentimentAnalyserTest.main(new String[0]);
                System.out.println("Sentiment analysis test end");
                break;
            case KnowledgeGraphTestCommand:
                System.out.println("Knowledge graph test start");
                KGConceptGeneratorTest.main(new String[0]);
                System.out.println("Knowledge graph test end");
                break;
            default:
                oldProcessCommand(command);
                break;
        }

    }

    private void addTrend(CoreConsoleCommand c, String command) {
        Matcher m = c.getFullPattern().matcher(command);
        boolean b = m.matches();
        if (b) {
            String trendName = m.group("trendName");
            String location = config.getLocation();
            if(m.group("trendLocation")!=null)
            {
                location = m.group("trendLocation");
            }
            System.out.println("Adding trend " + trendName+ ", for location "+location);
            Trend T = new Trend(trendName, location, 0);
            if (coreTrends.putTrend(T)) {
                System.out.println("Trend " + trendName + " added successfully");
            } else {
                System.out.println("Failed to add trend " + trendName);
            }
        } else {
            System.out.println("Invalid trend name");
        }
    }

    private void startCore() {
        if (!coreInter.isRunning()) {
            System.out.println("Starting Core");
            core.start();
        } else {
            System.out.println("Unable to start core, core already running");
        }
    }

    private void exit() {
        if (coreInter.isRunning()) {
            System.out.println("Closing Core");
            coreInter.beginClose();
        }
        running = false;
    }

    private void oldProcessCommand(String command) throws WikiException {
        if (command.equalsIgnoreCase("test twitter")) {
            System.out.println("Twitter test start");
            uk.ac.cam.quebec.twitterwrapper.test.Test.main(config.getTwitterArgs());
            System.out.println("Twitter test end");
        } else if (command.equalsIgnoreCase("test wikiproc")) {
            System.out.println("Wiki processing test start");
            uk.ac.cam.quebec.wikiproc.WikiProcessorTest.main(new String[0]);
            System.out.println("Wiki processing test end");
        } else if (command.equalsIgnoreCase("test wikiwrap")) {
            System.out.println("Wiki wrapper test start");
            uk.ac.cam.quebec.wikiwrapper.test.Test.main(new String[0]);
            System.out.println("Wiki wrapper test end");
        } else if (command.startsWith("add trend ")) {
            String s = command.substring(10);
            System.out.println("Adding trend " + s);
            Trend T = new Trend(s, "World", 0);
            if (coreTrends.putTrend(T)) {
                System.out.println("Trend " + s + " added successfully");
            } else {
                System.out.println("Failed to add trend " + s);
            }
        } else if (command.equalsIgnoreCase("repopulate trends")) {
            System.out.println("Repopulating trends");
            coreInter.repopulateTrends();
            System.out.println("Trends repopulated");
        } else if (command.equalsIgnoreCase("test database")) {
            System.out.println("Starting database test");
            DatabaseTest.test();
            System.out.println("Database test finish");
        } else if (command.equalsIgnoreCase("test word counter")) {
            System.out.println("Starting word count test");
            WordCounterTest.test1();
            System.out.println("Word count test finish");
        } else {
            System.out.println("Invalid command");
        }
    }

    @Override
    public void run() {
        try {
            String s;
            System.out.println("Type start to start the core");
            System.out.println("Console initialised:");
            while ((running) && ((s = r.readLine()).length() > 0)) {
                try {
                    processCommand(s);
                } catch (Exception ex) {
                    System.err.println(ex);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(CoreConsole.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Main entry point to the project for debugging
     *
     * @param args args[0] should be the path to a properly formatted
     * configuration file args will be the twitter credentials otherwise
     */
    @SuppressWarnings({"CallToThreadRun", "UseSpecificCatch"})
    public static void main(String[] args) {
        try {
            Configuration config;
            try {
                config = new Configuration(args[0]);
            } catch (FileNotFoundException ex) {
                System.err.println("Config file not found, falling back on defaults");
                String[] UAPI = {"90"};
                String[] SentimentAnalyserArgs = {""};
                String[] KnowledgeGraphArgs = {""};
                String location = "world";
                config = new Configuration(args, null, UAPI, location, SentimentAnalyserArgs, KnowledgeGraphArgs);
            }
            GroupProjectCore core = new GroupProjectCore(config);
            core.setDaemon(true);
            core.setName("CoreThread");
            CoreConsole c = new CoreConsole(core, config);
            c.run();
        } catch (IOException | TwitException ex) {
            System.err.println(ex);
        }
        catch (Exception ex)
        {
            System.err.println(ex);
        }
    }
}
