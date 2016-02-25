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
    CloseCommand("close",ExitCommand),
    StopCommand("stop",ExitCommand),
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
    ClearTasksCommand("clear tasks",ClearWorkCommand),
    ForceRefreshCommand("force refresh",""),
    ListRunningWorkCommand("list running work",""),
    ShowRunningWorkCommand("show running work",ListRunningWorkCommand),
    ShowRunningTasksCommand("show running tasks",ListRunningWorkCommand),
    ListRunningTasksCommand("list running tasks",ListRunningWorkCommand),
    CleanRunningWorkCommand("clean running work",""),
    CleanRunningTasksCommand("clean running tasks",CleanRunningWorkCommand),
    CleanQueuedWorkCommand("clean queued work",""),
    CleanQueuedTasksCommand("clean queued tasks",CleanQueuedWorkCommand),
    CleanTaskQueuesCommand("clean task queues",CleanQueuedWorkCommand),
    CleanWorkQueuesCommand("clean work queues",CleanQueuedWorkCommand),
    InvalidCommand("","(.*)");
    private final Pattern requestPattern;
    private final Pattern fullPattern;
    private final String requestOption;
    private final CoreConsoleCommand isAlias;
    private Matcher match = null;
    private CoreConsoleCommand(String option, CoreConsoleCommand alias)
    {
        requestOption = option;
        requestPattern = alias.getPattern();
        fullPattern = Pattern.compile("("+option + ")(: " + requestPattern.pattern()+")?", Pattern.CASE_INSENSITIVE);
        isAlias = alias;
    }
    private CoreConsoleCommand(String option, String pattern) {
        requestOption = option;
        requestPattern = Pattern.compile(pattern);
        fullPattern = Pattern.compile("("+option + ")(: " + pattern+")?", Pattern.CASE_INSENSITIVE);
        isAlias = null;
    }
    private void setMatch(Matcher m)
    {
        match = m;
    }
    public Matcher getMatch()
    {
        return match;
    }
    private CoreConsoleCommand getAlias()
    {
        return isAlias;
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
    private static final Pattern blankPattern = Pattern.compile("");
    /**
     * Builds the contests of the lookup map at compile time
     */
    static {//This must be positioned after the lookupMap declaration
        for (CoreConsoleCommand t : CoreConsoleCommand.values()) {
            CoreConsoleCommand alias = t.getAlias();
             boolean b = ((alias!=null)&&(t.getPattern().pattern().equals("")));
             if(b)
             {
                 lookupMap.put(t.getOption(), alias);
             }
             else
             {
            lookupMap.put(t.getOption(), t);
             }
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
