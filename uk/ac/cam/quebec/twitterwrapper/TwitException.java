package uk.ac.cam.quebec.twitterwrapper;

/**
 * Generic exception from twitter.
 * 
 * @author Stuart
 * 
 */
public class TwitException extends Exception {
    /**
     * As exception serializable.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor to force the supplying of a message.
     * 
     * @param message
     *            Details of the exception.
     */
    public TwitException(String message) {
        super(message);
    }

}
