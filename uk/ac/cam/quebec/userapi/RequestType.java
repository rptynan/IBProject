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

    public Pattern getPattern() {
        return requestPattern;
    }

    public String getOption() {
        return requestOption;
    }

    private static final Pattern parsePattern = Pattern.compile("([\\?]+)?.*");
    private static final Map<String, RequestType> lookupMap = new HashMap<>();

    static {
        for (RequestType t : RequestType.values()) {
            lookupMap.put(t.getOption(), t);
        }
    }

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
