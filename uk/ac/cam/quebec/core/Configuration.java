/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
 * Todo: convert this class to a singleton
 *
 * @author James
 */
public class Configuration {

    private final Document doc;
    private final String location;
    private final String[] twitterArgs;
    private final String[] UAPI_Args;
    private final Database DB;
    private final String[] SentimentAnalyserArgs;
    private final String[] KnowledgeGraphArgs;

    /**
     * Builds an object containing the various configuration variables
     *
     * @param path path to a config.xml file
     * @throws FileNotFoundException If the file is not present at the given
     * path
     */
    public Configuration(String path) throws FileNotFoundException {
        doc = getConfig(path);
        twitterArgs = getTwitterArgs(doc);
        DB = getDatabase(doc);
        location = getLocation(doc);
        UAPI_Args = getUAPIArgs(doc);
        SentimentAnalyserArgs = getSentimentAnalyserArgs(doc);
        KnowledgeGraphArgs = getKnowledgeGraphArgs(doc);
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
        location = _location;
    }

    public boolean valid() {
        return (location != null);
    }
    /**
     * 
     * @return 
     */
    public String getLocation()
    {
        return location;
    }
    /**
     * 
     * @return 
     */
    public Database getDatabase()
    {
        return DB;
    }
    public int getUAPI_Port()
    {
        return Integer.parseInt(UAPI_Args[0]);
    }
    public String[] getTwitterArgs()
    {
        return twitterArgs;
    }
    public String getSentimentAnalyserKey()
    {
        return SentimentAnalyserArgs[0];
    }
    public String getKnowledgeGraphKey()
    {
        return KnowledgeGraphArgs[0];
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
    private static String getLocation(Document doc) {
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
    private static String[] getTwitterArgs(Document doc) {
        String[] twittercreds = new String[5];
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
        NodeList parents = doc.getElementsByTagName("SentimentAnalyser");
        Element parent = (Element) parents.item(0);
        NodeList Item = parent.getElementsByTagName("API_KEY");
        ret[0] = Item.item(0).getTextContent();
        return ret;
    }
    /**
     * Gets the arguments for the UAPI
     * @param doc The config.xml document
     * @return A String[1] containing the arguments
     */
    private static String[] getUAPIArgs(Document doc) {
        String[] ret = new String[1];
        NodeList parents = doc.getElementsByTagName("UserAPI");
        Element parent = (Element) parents.item(0);
        NodeList Item = parent.getElementsByTagName("UserAPI_Port");
        ret[0] = Item.item(0).getTextContent();
        return ret;
    }
    /**
     * Gets the arguments for the Knowledge Graph
     * @param doc The config.xml document
     * @return A String[1] containing the arguments
     */
    private static String[] getKnowledgeGraphArgs(Document doc) {
        String[] ret = new String[1];
        NodeList parents = doc.getElementsByTagName("KnowledgeGraph");
        Element parent = (Element) parents.item(0);
        NodeList Item = parent.getElementsByTagName("API_KEY");
        ret[0] = Item.item(0).getTextContent();
        return ret;
    }
}
