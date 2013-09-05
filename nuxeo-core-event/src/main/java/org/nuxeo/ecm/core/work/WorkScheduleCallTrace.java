package org.nuxeo.ecm.core.work;

import org.nuxeo.ecm.core.work.api.Work;

public class WorkScheduleCallTrace extends Error {

    protected static final transient ThreadLocal<Work> enteredLocal = new ThreadLocal<Work>();

    private static final long serialVersionUID = 1L;

    public final transient Work work;

    protected WorkScheduleCallTrace(Work instance) {
        super(instance.toString());
        work = instance;
    }

    protected WorkScheduleCallTrace(Work instance, WorkScheduleCallTrace cause) {
        super(instance.toString(), cause);
        work = instance;
    }

    protected static WorkScheduleCallTrace newInstance(Work work) {
        Work entered = enteredLocal.get();
        if (entered != null) {
            return new WorkScheduleCallTrace(work, entered.getScheduleTrace());
        }
        return new WorkScheduleCallTrace(work);
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

}