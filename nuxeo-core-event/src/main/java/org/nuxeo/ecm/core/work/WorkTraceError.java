package org.nuxeo.ecm.core.work;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WorkTraceError extends Error {


    protected static final transient ThreadLocal<AbstractWork> enteredLocal = new ThreadLocal<AbstractWork>();

    protected final Log log ;

    private static final long serialVersionUID = 1L;

    public final transient AbstractWork work;

    protected WorkTraceError(AbstractWork instance) {
        super(instance.toString());
        work = instance;
        log = LogFactory.getLog(WorkTraceError.class);
    }

    protected WorkTraceError(AbstractWork instance, WorkTraceError cause) {
        super(instance.toString(), cause);
        work = instance;
        log = LogFactory.getLog(WorkTraceError.class);
    }

    protected static WorkTraceError newInstance(AbstractWork work) {
        AbstractWork entered = enteredLocal.get();
        if (entered != null) {
            return new WorkTraceError(work, entered.trace);
        }
        return new WorkTraceError(work);
    }

    protected void handleEnter() {
        if (enteredLocal.get() != null) {
            throw new AssertionError("thread local leak, chain should not be re-rentrant");
        }
        enteredLocal.set(work);
     }

    protected static void handleReturn() {
        enteredLocal.remove();
    }

    public void logSelf(String message) {
        log.error(work.toString(), this);
    }

}