/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.userapi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that handles the parsing of the Request
 *
 * @author James
 */
public enum RequestType {

    TestRequest("Test", "(.)*"),
    TestRequest2("stuff","(.)*"),
    DefaultRequest("", "(.)*"),
    InvalidRequest("inv", "(.)*"),
    TrendsRequest("Trends","(.)*");
    private final Pattern requestPattern;
    private final Pattern fullPattern;
    private final String requestOption;

    private RequestType(String option, String pattern) {
        requestOption = option;
        requestPattern = Pattern.compile(pattern);
        fullPattern = Pattern.compile("\\/("+option + ")\\?" + pattern);
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
    public static final Pattern parsePattern = Pattern.compile("\\/([^\\?]+)\\??(.*)");
    private static final Map<String, RequestType> lookupMap = new HashMap<>();

    /**
     * Builds the contests of the lookup map at compile time
     */
    static {//This must be positioned after the lookupMap declaration
        for (RequestType t : RequestType.values()) {
            lookupMap.put(t.getOption(), t);
        }
    }
    /**
     * This gets the Type of the request
     *
     * @param message The message to get the type of
     * @return the type of the request
     */
    public static final RequestType getRequestType(String message) {
        if(message==null)
        {
            return InvalidRequest;
        }
        Matcher m = parsePattern.matcher(message);
        if (!m.matches()) {
            return InvalidRequest;
        } else {
            String s = m.group(1);
            if (lookupMap.containsKey(s)) {
                return lookupMap.get(s);
            }
        }
        return DefaultRequest;
    }
    /**
     * This returns an unmodifiable copy of the lookup map, should only be used
     * for debugging
     *
     * @return An unmodifiable view of the lookupMap
     */
    public static final Map<String, RequestType> getLookupMap() {
        return Collections.unmodifiableMap(lookupMap);
    }
}