package gov.medicaid.processmanager.exceptions;

/**
 * @author Marvin Wolfthal
 * @since 7/4/2018
 */
public class ProcessManagerException extends Exception {

    private static final long serialVersionUID = -3905384743856534328L;

    public ProcessManagerException() {
        super();
    }

    public ProcessManagerException(String message) {
        super(message);
    }

    public ProcessManagerException(Throwable t) {
        super(t);
    }
}
