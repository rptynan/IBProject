/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.userapi;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author James
 */
public enum RequestType {

    TestRequest("Test", ""),
    DefaultRequest("", ""),
    InvalidRequest("Invalid", "");
    private final Pattern requestPattern;
    private final String requestOption;
    private RequestType(String option, String pattern) {
        requestOption = option;
        requestPattern = Pattern.compile(option + "?" + pattern);
    }
/**
 * The returns the pattern that should be used to parse the request
 * @return the associated pattern
 */
    public Pattern getPattern() {
        return requestPattern;
    }
/**
 * This returns the name selector of the option
 * @return The associated selector
 */
    public String getOption() {
        return requestOption;
    }
/**
 * The static pattern that should be used to parse a generic request
 */
    private static final Pattern parsePattern = Pattern.compile("([\\?]+)?(.*)");
    private static final Map<String, RequestType> lookupMap = new HashMap<>();
    /**
     * Builds the contests of the lookup map at compile time
     */
    static {
        for (RequestType t : RequestType.values()) {
            lookupMap.put(t.getOption(), t);
        }
    }
    /**
     * This gets the Type of the request  
     * @param message The message to get the type of
     * @return the type of the request
     */
    public static final RequestType getRequestType(String message) {
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

}
