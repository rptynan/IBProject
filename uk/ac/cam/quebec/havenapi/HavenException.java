package uk.ac.cam.quebec.havenapi;

/**
 * Exception thrown by the Haven Wrapper in case of any errors
 */
public class HavenException extends Exception {

    public HavenException() {
        super("Haven API Exception");
    }

    public HavenException(String msg) {
        super(msg);
    }

    public HavenException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
