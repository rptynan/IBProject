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
    private static final long serialVersionUID = 1L+1;

    /**
     * Constructor to force the supplying of a message.
     * 
     * @param message
     *            Details of the exception.
     */
    public WikiException(String message) {
        super(message);
    }
    
    /**
     * Constructor to force the supplying of a message and a cause
     * @param message Details of the exception
     * @param ex the inner exception
     */
    public WikiException(String message, Exception ex)
    {super(message,ex);
        
    }

}
