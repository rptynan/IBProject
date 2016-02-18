/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.ac.cam.quebec.core.test.CoreConsole;
import uk.ac.cam.quebec.dbwrapper.Database;

/**
 * This class represents the values of the config.xml file;
 *
 * @author James
 */
// Todo: convert this class to a singleton
public class Configuration {

    private final Document doc;
    private final String[] misc;
    private static Database DB;
    private static final Set<String> stopWords = new HashSet<>();
    private static final Map<String, Integer> locationLookup = new HashMap<>();
    private static final Map<String, String> ConfigMap = new HashMap<>();

    /**
     * Builds an object containing the various configuration variables
     *
     * @param path path to a config.xml file
     * @throws FileNotFoundException If the file is not present at the given
     * path
     */
    public Configuration(String path) throws FileNotFoundException {
        doc = getConfig(path);
        getTwitterArgs(doc);
        DB = getDatabase(doc);
        misc = getMisc(doc);
        getUAPIArgs(doc);
        getSentimentAnalyserArgs(doc);
        getKnowledgeGraphArgs(doc);
        parseStopWords(doc);
        getTrendsArgs(doc);
        buildTwitterLocationMap();
    }

    /**
     * Builds an object containing the default configuration values
     *
     * @param _twitterArgs
     * @param _SentimentAnalyserArgs
     * @param _KnowledgeGraphArgs
     */
    @Deprecated
    public Configuration(String[] _twitterArgs, String[] _SentimentAnalyserArgs, String[] _KnowledgeGraphArgs) {
        doc = null;
        String[] _UAPI_Args = new String[1];
        _UAPI_Args[0] = "90";
        DB = getDefaultDatabase();
        misc = new String[2];
        misc[0] = "world";
        misc[1] = "10";
        try {
            ConfigMap.put("Location", misc[0]);
            ConfigMap.put("ThreadPoolSize", misc[1]);
            ConfigMap.put("UAPI_Port", _UAPI_Args[0]);
            ConfigMap.put("SentimentAnalyserKey", _SentimentAnalyserArgs[0]);
            ConfigMap.put("KnowledgeGraphArgs", _KnowledgeGraphArgs[0]);
            getTrendsArgs(null);
            ConfigMap.put("TwitterOAuthKey", _twitterArgs[0]);
            ConfigMap.put("TwitterOAuthSecret", _twitterArgs[1]);
            ConfigMap.put("TwitterAccessToken", _twitterArgs[2]);
            ConfigMap.put("TwitterAccessSecret", _twitterArgs[3]);
            ConfigMap.put("TwitterAccountName", _twitterArgs[4]);
        } catch (Exception ex) {
            System.err.println("Error adding items to ConfigMap: " + ex);
        }
    }

    public static boolean valid() {
        return ConfigMap.size() > 0;
    }

    /**
     * Returns the initialised Database instance
     *
     * @return
     */
    public static Database getDatabase() {
        return DB;
    }

    public static String[] getTwitterArgs() {
        String[] ret = new String[5];
        ret[0] = getValue("TwitterOAuthKey");
        ret[1] = getValue("TwitterOAuthSecret");
        ret[2] = getValue("TwitterAccessToken");
        ret[3] = getValue("TwitterAccessSecret");
        ret[4] = getValue("TwitterAccountName");
        return ret;
    }

    public static String[] getLocations() {
        int locationNumbers = Integer.parseInt(getValue("locationNumbers"));
        String[] ret = new String[locationNumbers];
        ret[0] = getValue("Location");
        for (int i = 0; i < (locationNumbers - 1); i++) {
            ret[i + 1] = getValue("Location" + i);
        }
        return ret;
    }

    @Deprecated
    public static String getLocation() {
        return getDefaultLocation();
    }

    /**
     * Returns the default location
     *
     * @return String location
     */
    public static String getDefaultLocation() {
        return getValue("Location");
    }

    public static int getUAPI_Port() {
        String s = getValue("UserAPIPort");
        return Integer.parseInt(s);
    }

    public static String getSentimentAnalyserKey() {
        return getValue("SentimentAnalyserKey");
    }

    public static String getKnowledgeGraphKey() {
        return getValue("KnowledgeGraphKey");
    }

    public static int getThreadPoolSize() {
        String s = ConfigMap.get("ThreadPoolSize");
        return Integer.parseInt(s);
    }

    public static String getValue(String key) {
        return ConfigMap.get(key);
    }

    public static int getTrendsPerLocation() {
        String s = ConfigMap.get("TrendsPerLocation");
        return Integer.parseInt(s);

    }

    public static int getTrendRefreshTime() {
        String s = ConfigMap.get("TrendRefreshTime");
        return Integer.parseInt(s);
    }

    /**
     * Function to get the config.xml file
     *
     * @param path The path to the cofig.xml file
     * @return The configuration file as an xml document
     * @throws java.io.FileNotFoundException If the file is not found;
     */
    private static Document getConfig(String path) throws FileNotFoundException {
        try {
            File inputFile = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            return doc;
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
    private static String[] getMisc(Document doc) {
        String s;
        String[] ret = new String[3];
        NodeList parents = doc.getElementsByTagName("Misc");
        Element parent = (Element) parents.item(0);
        NodeList Item = parent.getElementsByTagName("Location");
        int locationNumbers = Item.getLength();
        s = Item.item(0).getTextContent();
        ConfigMap.put("Location", s);
        ret[0] = s;
        for (int i = 0; i < (locationNumbers - 1); i++) {
            s = Item.item(i + 1).getTextContent();
            ConfigMap.put("Location" + i, s);
        }
        ConfigMap.put("locationNumbers", String.valueOf(locationNumbers));

        try {//just incase someone doesn't have the latest config.xml
            Item = parent.getElementsByTagName("ThreadPoolSize");
            s = Item.item(0).getTextContent();

        } catch (NullPointerException ex) {
            s = "10";
        }
        ConfigMap.put("ThreadPoolSize", s);
        ret[1] = s;

        try {//just incase someone doesn't have the latest config.xml
            Item = parent.getElementsByTagName("ProjectRoot");
            s = Item.item(0).getTextContent();

        } catch (NullPointerException ex) {
            s = ".\\";
        }
        ConfigMap.put("ProjectRoot", s);
        ret[2] = s;

        return ret;
    }

    /**
     * Gets the twitter arguments formatted as a string array
     *
     * @param doc The config.xml document
     * @return A length 5 String array containing the twitter credentials
     */
    private static String[] getTwitterArgs(Document doc) {
        String[] twittercreds = new String[5];
        NodeList parents = doc.getElementsByTagName("Twitter");
        //parents will have more than 1 element if we have multiple twitter
        //accounts 
        int i = parents.getLength();
        String s;
        Element parent = (Element) parents.item(0);
        NodeList Item = parent.getElementsByTagName("OAuth_Key");
        s = Item.item(0).getTextContent();
        ConfigMap.put("TwitterOAuthKey", s);
        twittercreds[0] = s;
        Item = parent.getElementsByTagName("OAuth_Secret");
        s = Item.item(0).getTextContent();
        ConfigMap.put("TwitterOAuthSecret", s);
        twittercreds[1] = s;
        Item = parent.getElementsByTagName("Access_Token");
        s = Item.item(0).getTextContent();
        ConfigMap.put("TwitterAccessToken", s);
        twittercreds[2] = s;
        Item = parent.getElementsByTagName("Access_Secret");
        s = Item.item(0).getTextContent();
        ConfigMap.put("TwitterAccessSecret", s);
        twittercreds[3] = s;
        Item = parent.getElementsByTagName("Account_Name");
        s = Item.item(0).getTextContent();
        ConfigMap.put("TwitterAccountName", s);
        twittercreds[4] = s;

        for (int j = 0; j < (i - 1); j++) {
            parent = (Element) parents.item(j + 1);
            Item = parent.getElementsByTagName("OAuth_Key");
            s = Item.item(0).getTextContent();
            ConfigMap.put("TwitterOAuthKey" + j, s);
            Item = parent.getElementsByTagName("OAuth_Secret");
            s = Item.item(0).getTextContent();
            ConfigMap.put("TwitterOAuthSecret" + j, s);
            Item = parent.getElementsByTagName("Access_Token");
            s = Item.item(0).getTextContent();
            ConfigMap.put("TwitterAccessToken" + j, s);
            Item = parent.getElementsByTagName("Access_Secret");
            s = Item.item(0).getTextContent();
            ConfigMap.put("TwitterAccessSecret" + j, s);
            Item = parent.getElementsByTagName("Account_Name");
            s = Item.item(0).getTextContent();
            ConfigMap.put("TwitterAccountName" + j, s);

        }
        ConfigMap.put("TwitterAccountCount", String.valueOf(i));
        return twittercreds;
    }

    /**
     * Gets the Database instance
     *
     * @param doc The config.xml document
     * @return the database instance
     */
    private static Database getDatabase(Document doc) {
        if (doc == null) {
            return getDefaultDatabase();
        }
        String[] ret = new String[4];
        NodeList parents = doc.getElementsByTagName("Database");
        Element parent = (Element) parents.item(0);
        NodeList Item = parent.getElementsByTagName("UserName");
        ret[0] = Item.item(0).getTextContent();
        Item = parent.getElementsByTagName("Password");
        ret[1] = Item.item(0).getTextContent();
        Item = parent.getElementsByTagName("Path");
        ret[2] = Item.item(0).getTextContent();
        Item = parent.getElementsByTagName("ClearOnStart");
        ret[3] = Item.item(0).getTextContent();
        return getDatabase(ret);
    }

    /**
     * Gets the Database instance
     *
     * @param args A string array containing the database arguments
     * @return the database instance
     */
    private static Database getDatabase(String[] args) {
        if (args == null) {
            return getDefaultDatabase();
        }
        ConfigMap.put("DatabaseUserName", args[0]);
        ConfigMap.put("DatabasePassword", args[1]);
        ConfigMap.put("DatabasePath", args[2]);
        ConfigMap.put("DatabaseClearOnStart", args[3]);
        boolean wipe = args[3].equalsIgnoreCase("true");
        Database.setCredentials(args[0], args[1], "jdbc:mysql://" + args[2], wipe);
        Database DB = Database.getInstance();
        return DB;
    }

    private static Database getDefaultDatabase() {
        return Database.getInstance();
    }

    /**
     * Gets the Arguments for the sentiment analyser
     *
     * @param doc The config.xml document
     * @return A string[1] array containing the arguments
     */
    private static String[] getSentimentAnalyserArgs(Document doc) {
        String[] ret = new String[1];
        String s;
        NodeList parents = doc.getElementsByTagName("SentimentAnalyser");
        Element parent = (Element) parents.item(0);
        NodeList Item = parent.getElementsByTagName("API_KEY");
        s = Item.item(0).getTextContent();
        ConfigMap.put("SentimentAnalyserKey", s);
        ret[0] = s;
        return ret;
    }

    /**
     * Gets the arguments for the UAPI
     *
     * @param doc The config.xml document
     * @return A String[1] containing the arguments
     */
    private static String[] getUAPIArgs(Document doc) {
        String[] ret = new String[1];
        String s;
        NodeList parents = doc.getElementsByTagName("UserAPI");
        Element parent = (Element) parents.item(0);
        NodeList Item = parent.getElementsByTagName("UserAPI_Port");
        s = Item.item(0).getTextContent();
        ConfigMap.put("UserAPIPort", s);
        ret[0] = s;
        return ret;
    }

    /**
     * Gets the arguments for the Knowledge Graph
     *
     * @param doc The config.xml document
     * @return A String[1] containing the arguments
     */
    private static String[] getKnowledgeGraphArgs(Document doc) {
        String[] ret = new String[1];
        String s;
        NodeList parents = doc.getElementsByTagName("KnowledgeGraph");
        Element parent = (Element) parents.item(0);
        NodeList Item = parent.getElementsByTagName("API_KEY");
        s = Item.item(0).getTextContent();
        ConfigMap.put("KnowledgeGraphKey", s);
        ret[0] = s;
        return ret;
    }

    /**
     * Gets the path to the list of stop words
     *
     * @param doc The config.xml document
     * @return the path
     */
    private static String getStopWordsPath(Document doc) {
        String s;
        NodeList parents = doc.getElementsByTagName("StopWords");
        Element parent = (Element) parents.item(0);
        NodeList Item = parent.getElementsByTagName("Path");
        s = Item.item(0).getTextContent();
        ConfigMap.put("StopWordsPath", s);
        return s;
    }

    private static void parseStopWords(Document doc) {
        String path = getStopWordsPath(doc);
        File file = new File(path);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = null;
            while ((line = br.readLine()) != null) {
                stopWords.add(line);
            }
        } catch (IOException e) {
            //stopwords parsing failed
        }
    }

    private static void getTrendsArgs(Document doc) {
        String s;
        try {
            NodeList parents = doc.getElementsByTagName("Trends");
            Element parent = (Element) parents.item(0);
            NodeList Item = parent.getElementsByTagName("TrendsPerLocation");
            s = Item.item(0).getTextContent();
            ConfigMap.put("TrendsPerLocation", s);
            Item = parent.getElementsByTagName("TrendRefreshTime");
            s = Item.item(0).getTextContent();
            ConfigMap.put("TrendRefreshTime", s);
        } catch (NullPointerException ex)//Incase someone has not updated their config file
        {
            ConfigMap.put("TrendsPerLocation", "10");
            ConfigMap.put("TrendRefreshTime", "10");
        }
    }

    private static void buildTwitterLocationMap() {
        locationLookup.put("World", 1);
        locationLookup.put("UK", 23424975);
        locationLookup.put("USA", 23424977);
        locationLookup.put("Australia", 23424748);
        locationLookup.put("Ireland", 23424803);
        locationLookup.put("India", 23424848);
        locationLookup.put("Seattle", 2490383);
        locationLookup.put("London", 44418);

    }
}
