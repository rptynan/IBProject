/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
 * @author James
 */
// Todo: convert this class to a singleton
public class Configuration {
    
    private final Document doc;
    private final String[] misc;
    private final String[] twitterArgs;
    private final String[] UAPI_Args;
    private final Database DB;
    private final String[] SentimentAnalyserArgs;
    private final String[] KnowledgeGraphArgs;
    private static final Map<String,String> ConfigMap = new HashMap<>();

    /**
     * Builds an object containing the various configuration variables
     *
     * @param path path to a config.xml file
     * @throws FileNotFoundException If the file is not present at the given
     * path
     */
    public  Configuration(String path) throws FileNotFoundException {
        doc = getConfig(path);
        twitterArgs = getTwitterArgs(doc);
        DB = getDatabase(doc);
        misc = getMisc(doc);
        UAPI_Args = getUAPIArgs(doc);
        SentimentAnalyserArgs = getSentimentAnalyserArgs(doc);
        KnowledgeGraphArgs = getKnowledgeGraphArgs(doc);
        getStopWordsPath(doc);
    }

    /**
     * Builds an object containing the various configuration variables This
     * entry point lets you specify the variables manually
     *
     * @param _twitterArgs
     * @param _DBArgs
     * @param _UAPI_Args
     * @param _location
     * @param _SentimentAnalyserArgs
     * @param _KnowledgeGraphArgs
     */
    public Configuration(String[] _twitterArgs, String[] _DBArgs, String[] _UAPI_Args, String _location, String[] _SentimentAnalyserArgs, String[] _KnowledgeGraphArgs) {
        doc = null;
        twitterArgs = _twitterArgs;
        DB = getDatabase(_DBArgs);
        UAPI_Args = _UAPI_Args;
        SentimentAnalyserArgs = _SentimentAnalyserArgs;
        KnowledgeGraphArgs = _KnowledgeGraphArgs;
        misc = new String[2];
        misc[0] = _location;
        misc[1] = "10";
        try{
        ConfigMap.put("Location", _location);
        ConfigMap.put("ThreadPoolSize", misc[1]);
        ConfigMap.put("UAPI_Port", _UAPI_Args[0]);
        ConfigMap.put("SentimentAnalyserKey", _SentimentAnalyserArgs[0]);
        ConfigMap.put("KnowledgeGraphArgs", _KnowledgeGraphArgs[0]);
        }
        catch (Exception ex)
        {
            System.err.println("Error adding items to ConfigMap: "+ex);
        }
    }

    public static boolean valid() {
        return ConfigMap.size()>0;
    }
   
    /**
     * Returns the initialised Database instance
     * @return 
     */
    public Database getDatabase()
    {
        return DB;
    }
    
    public static String[] getTwitterArgs()
    {   String[] ret = new String[5];
        ret[0]=getValue("TwitterOAuthKey");
        ret[1]=getValue("TwitterOAuthSecret");
        ret[2]=getValue("TwitterAccessToken");
        ret[3]=getValue("TwitterAccessSecret");
        ret[4]=getValue("TwitterAccountName");
        return ret;
    }
     /**
     * Returns the default location
     * @return String location
     */
    public static String getLocation()
    {
        return getValue("Location");
    }
    public static int getUAPI_Port()
    {   String s = getValue("UserAPIPort");
        return Integer.parseInt(s);
    }
    public static String getSentimentAnalyserKey()
    {
        return getValue("SentimentAnalyserKey");
    }
    public static String getKnowledgeGraphKey()
    {
        return getValue("KnowledgeGraphKey");
    }
    public static int getThreadPoolSize()
    {
        String s = ConfigMap.get("ThreadPoolSize");
        return Integer.parseInt(s);
    }
    public static String getValue(String key)
    {
        return ConfigMap.get(key);
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
        String[] ret = new String[2];
        NodeList parents = doc.getElementsByTagName("Misc");
        Element parent = (Element) parents.item(0);
        NodeList Item = parent.getElementsByTagName("Location");
        s = Item.item(0).getTextContent();
        ConfigMap.put("Location", s);
        ret[0]=s;
        try{//just incase someone doesn't have the latest config.xml
        Item = parent.getElementsByTagName("ThreadPoolSize");
        s = Item.item(0).getTextContent();
        
        }
        catch (NullPointerException ex)
        {
            s="10";
        }
        ConfigMap.put("ThreadPoolSize", s);
        ret[1]=s;
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
        return twittercreds;
    }

    /**
     * Gets the Database instance
     *
     * @param doc The config.xml document
     * @return the database instance
     */
    private static Database getDatabase(Document doc) {
        if(doc==null)
        {
            return Database.getInstance();
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
        if(args ==null)
        {
            return Database.getInstance();
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
    /**
     * Gets the Arguments for the sentiment analyser
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
        ConfigMap.put("UserAPIPort",s);
        ret[0] = s;
        return ret;
    }
    /**
     * Gets the arguments for the Knowledge Graph
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
     * @param doc The config.xml document
     * @return the path
     */
    private static String getStopWordsPath(Document doc)
    {   String s;
         NodeList parents = doc.getElementsByTagName("StopWords");
        Element parent = (Element) parents.item(0);
        NodeList Item = parent.getElementsByTagName("Path");
        s = Item.item(0).getTextContent();
        ConfigMap.put("StopWordsPath", s);
        return s;
    }
}
