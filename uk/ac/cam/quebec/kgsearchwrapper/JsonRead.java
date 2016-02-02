package uk.ac.cam.quebec.kgsearchwrapper;

import com.jayway.jsonpath.IndefinitePathException;
import com.jayway.jsonpath.InvalidCriteriaException;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.InvalidModelPathException;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;

/**
 * A wrapper for the JsonPath class for elegant exception handling.
 */
public class JsonRead {

    public static Object read(Object object, String path) {
        try {
            return JsonPath.read(object, path);
        } catch (IndefinitePathException|InvalidCriteriaException|InvalidJsonException|
                InvalidPathException|InvalidModelPathException ex) {
            //ex.printStackTrace(System.err);
            return "";
        }
    }
}
