package org.nuxeo.ecm.core.api.provider;

/**
 * Exception thrown by ResultsProvider components
 * 
 * @author ogrisel
 * 
 */
public class ResultsProviderException extends Exception {

    private static final long serialVersionUID = 1L;

    public ResultsProviderException(String message) {
        super(message);
    }
    
    public ResultsProviderException(String message, Throwable t) {
        super(message, t);
    }
    
    public ResultsProviderException(Throwable t) {
        super(t);
    }

}
