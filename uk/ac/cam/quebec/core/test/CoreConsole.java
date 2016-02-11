/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.ac.cam.quebec.core.ControlInterface;
import uk.ac.cam.quebec.core.GroupProjectCore;
import uk.ac.cam.quebec.dbwrapper.Database;
import uk.ac.cam.quebec.dbwrapper.DatabaseTest;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.trends.TrendsQueue;
import uk.ac.cam.quebec.util.parsing.StopWords;
import uk.ac.cam.quebec.wikiwrapper.WikiException;
import uk.ac.cam.quebec.util.WordCounter;
import uk.ac.cam.quebec.util.WordCounterTest;


/**
 *
 * @author James
 */
public class CoreConsole extends Thread {

    private final BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
    private final GroupProjectCore core;
    private final ControlInterface coreInter;
    private final TrendsQueue coreTrends;
    private final String[] twitterCreds;
    private boolean running = true;

    public void processCommand(String command) throws WikiException {
        CoreConsoleCommand com = CoreConsoleCommand.getCommandtType(command);
        if (command.equalsIgnoreCase("start")) {
            if (!coreInter.isRunning()) {
                System.out.println("Starting Core");
                core.start();
            }
        } else if (command.equalsIgnoreCase("exit")) {
            if (coreInter.isRunning()) {
                System.out.println("Closing Core");
                coreInter.beginClose();
            }
            running = false;
        } else if (command.equalsIgnoreCase("status")) {
            System.out.println(coreInter.getServerInfo());
        } else if (command.equalsIgnoreCase("test twitter")) {
            System.out.println("Twitter test start");
            uk.ac.cam.quebec.twitterwrapper.test.Test.main(twitterCreds);
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
        } else if (command.startsWith("check stop word ")) {
            String s = command.substring(17);
            boolean b = StopWords.isStopWord(s);
            if(b)
            {
                System.out.println(s+" is a stop word");
            }
            else
            {
                System.out.println(s+" is not a stop word");
            }
        } else if (command.equalsIgnoreCase("test word counter"))
        {   System.out.println("Starting word count test");
        WordCounterTest.test1();
        System.out.println("Word count test finish");
        }
        else {
            System.out.println("Invalid command");
        }
    }

    public CoreConsole(GroupProjectCore _core, String[] _args) {
        core = _core;
        coreInter = _core;
        coreTrends = _core;
        twitterCreds = _args;
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
     * Function to get the config.xml file
     *
     * @param path The path to the cofig.xml file
     * @return The configuration file as an xml document
     */
    public static Document getConfig(String path) {
        try {
            File inputFile = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (FileNotFoundException ex) {
            System.out.println("Failed to find the config file");
            return null;
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(CoreConsole.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Function to get the location for trends from the config file
     *
     * @param doc the XML config file
     * @return the location to use
     */
    public static String getLocation(Document doc) {
        String s = "";
        NodeList parents = doc.getElementsByTagName("Misc");
        Element parent = (Element) parents.item(0);
        NodeList Item = parent.getElementsByTagName("Location");
        return Item.item(0).getTextContent();
    }

    /**
     * Gets the twitter arguments formatted as a string array
     *
     * @param doc The config.xml document
     * @return A length 5 String array containing the twitter credentials
     */
    public static String[] getTwitterArgs(Document doc) {
        String[] twittercreds = new String[5];
        if (doc != null) {
            NodeList parents = doc.getElementsByTagName("Twitter");
            //parents will have more than 1 element if we have multiple twitter
            //accounts 
            Element parent = (Element) parents.item(0);
            NodeList Item = parent.getElementsByTagName("OAuth_Key");
            twittercreds[0] = Item.item(0).getTextContent();
            Item = parent.getElementsByTagName("OAuth_Secret");
            twittercreds[1] = Item.item(0).getTextContent();
            Item = parent.getElementsByTagName("Access_Token");
            twittercreds[2] = Item.item(0).getTextContent();
            Item = parent.getElementsByTagName("Access_Secret");
            twittercreds[3] = Item.item(0).getTextContent();
            Item = parent.getElementsByTagName("Account_Name");
            twittercreds[4] = Item.item(0).getTextContent();
        }
        return twittercreds;
    }

    /**
     * Gets the Database instance
     *
     * @param doc The config.xml document
     * @return the database instance
     */
    public static Database getDatabase(Document doc) {
        NodeList parents = doc.getElementsByTagName("Database");
        Element parent = (Element) parents.item(0);
        NodeList Item = parent.getElementsByTagName("UserName");
        String User = Item.item(0).getTextContent();
        Item = parent.getElementsByTagName("Password");
        String Password = Item.item(0).getTextContent();
        Item = parent.getElementsByTagName("Path");
        String Path = Item.item(0).getTextContent();
        Item = parent.getElementsByTagName("ClearOnStart");
        String clear = Item.item(0).getTextContent();
        boolean wipe = clear.equalsIgnoreCase("true");
        Database.setCredentials(User, Password, "jdbc:mysql://" + Path, wipe);
        Database DB = Database.getInstance();
        return DB;
    }

    /**
     * Main entry point to the project for debugging
     *
     * @param args args[0] should be the path to a properly formatted
     * configuration file args will be the twitter credentials otherwise
     */
    public static void main(String[] args) {
        try {
            Document doc = getConfig(args[0]);
            String[] TwitterCreds;
            Database DB;
            String location;
            if (doc == null) {
                TwitterCreds = args; //If we fail to get the configuration file
                DB = Database.getInstance();//fall back to these prompts
                location = "world";
            } else {
                TwitterCreds = getTwitterArgs(doc);
                DB = getDatabase(doc);
                location = getLocation(doc);
            }
            GroupProjectCore core = new GroupProjectCore(TwitterCreds, DB, location);
            core.setDaemon(true);
            core.setName("CoreThread");
            CoreConsole c = new CoreConsole(core, args);
            c.run();
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}
