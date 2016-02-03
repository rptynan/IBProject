package uk.ac.cam.quebec.wikiwrapper;

/**
 * Generic error with getting data from Wikipedia.
 * 
 * @author Stuart
 *
 */
public class WikiException extends Exception {
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
    public WikiException(String message) {
        super(message);
    }

}
