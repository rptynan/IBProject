/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author James
 */
public enum CoreConsoleCommand {
    
    StartCommand("start", ""),
    ExitCommand("exit",""),
    StatusCommand("status", ""),
    TwitterTestCommand("test twitter", ""),
    WikiProcTestCommand("test wikiproc",""),
    WikiWrapTestCommand("test wikiwrap",""),
    SentimentAnalysisTestCommand("test sentiment",""),
    KnowledgeGraphTestCommand("test knowledge graph",""),
    AddTrendCommand("add trend","(?<trendName>[^\\,]*)(, (?<trendLocation>\\w+)(, (?<trendPriority>\\d+))?)?"),
    RepopulateTrendCommand("repopulate trends",""),
    TestDatabaseCommand("test database",""),
    CheckStopWordCommand("check stop word","(?<stopWord>.+)"),
    TestWordCounterCommand("test word counter",""),
    HelpCommand("help","(?<Command>\\w+)?"),
    CheckStyleCommand("check style",""),
    ClearWorkCommand("clear work",""),
    ForceRefreshCommand("force refresh",""),
    InvalidCommand("","(.*)");
    private final Pattern requestPattern;
    private final Pattern fullPattern;
    private final String requestOption;
    private Matcher match = null;

    private CoreConsoleCommand(String option, String pattern) {
        requestOption = option;
        requestPattern = Pattern.compile(pattern);
        fullPattern = Pattern.compile("("+option + ")(: " + pattern+")?", Pattern.CASE_INSENSITIVE);
    }
    private void setMatch(Matcher m)
    {
        match = m;
    }
    public Matcher getMatch()
    {
        return match;
    }
    /**
     * The returns the pattern that should be used to parse the options
     *
     * @return the associated pattern
     */
    public Pattern getPattern() {
        return requestPattern;
    }
    /**
     * The returns the pattern that should be used to parse the entire URI
     *
     * @return the associated pattern
     */
    public Pattern getFullPattern() {
        return fullPattern;
    }
    /**
     * This returns the name selector of the option
     *
     * @return The associated selector
     */
    public String getOption() {
        return requestOption;
    }
    /**
     * The static pattern that should be used to parse a generic request
     */
    public static final Pattern parsePattern = Pattern.compile("([^\\:]+)(: (.*))?", Pattern.CASE_INSENSITIVE);
    private static final Map<String, CoreConsoleCommand> lookupMap = new HashMap<>();

    /**
     * Builds the contests of the lookup map at compile time
     */
    static {//This must be positioned after the lookupMap declaration
        for (CoreConsoleCommand t : CoreConsoleCommand.values()) {
            lookupMap.put(t.getOption(), t);
        }
    }
    /**
     * This gets the Type of the request
     *
     * @param message The message to get the type of
     * @return the type of the request
     */
    public static final CoreConsoleCommand getCommandType(String message) {
        if(message==null)
        {
            return InvalidCommand;
        }
        Matcher m = parsePattern.matcher(message);
        if (!m.matches()) {
            return InvalidCommand;
        } else {
            String s = m.group(1);
            if (lookupMap.containsKey(s)) {
                CoreConsoleCommand c = lookupMap.get(s);
                c.setMatch(m);
                return c;
            }
        }
        return InvalidCommand;
    }
    /**
     * This returns an unmodifiable copy of the lookup map, should only be used
     * for debugging
     *
     * @return An unmodifiable view of the lookupMap
     */
    public static final Map<String, CoreConsoleCommand> getLookupMap() {
        return Collections.unmodifiableMap(lookupMap);
    }
}
