package uk.ac.cam.quebec.dbwrapper;

/**
 * Exception to wrap all the possible exceptions thrown by the Database.
 *
 * @author Richard
 */
public class DatabaseException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Contructor.
     *
     * @param exception the exception which caused this to be thrown
     */
    public DatabaseException(String message) {
        super(message);
    }

    /**
     * Contructor.
     *
     * @param message details of the exception
     * @param exception the exception which caused this to be thrown
     */
    public DatabaseException(String message, Exception exception) {
        super(message, exception);
    }

}
