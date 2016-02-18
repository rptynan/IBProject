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
    private static final long serialVersionUID = 1L+1;

    /**
     * Constructor to force the supplying of a message.
     * 
     * @param message
     *            Details of the exception.
     */
    public TwitException(String message) {
        super(message);
    }

    /**
     * Constructor to force the supplying of a message and the inner exception
     * @param message Details of the exception
     * @param inner The inner exception
     */
    public TwitException(String message, Exception inner)
    {
        super(message,inner);
    }
}
