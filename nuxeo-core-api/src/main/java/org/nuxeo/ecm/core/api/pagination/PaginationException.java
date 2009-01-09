package org.nuxeo.ecm.core.api.pagination;

/**
 * Exception thrown by Pages components
 * 
 * @author ogrisel
 * 
 */
public class PaginationException extends Exception {

    private static final long serialVersionUID = 1L;

    public PaginationException(String message) {
        super(message);
    }
    
    public PaginationException(String message, Throwable t) {
        super(message, t);
    }
    
    public PaginationException(Throwable t) {
        super(t);
    }

}
