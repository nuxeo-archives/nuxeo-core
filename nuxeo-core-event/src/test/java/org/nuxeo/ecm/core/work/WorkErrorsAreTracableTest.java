package org.nuxeo.ecm.core.work;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.work.api.Work;
import org.nuxeo.ecm.core.work.api.WorkManager;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LogCaptureFeature;
import org.nuxeo.runtime.test.runner.LogCaptureFeature.NoFilterError;
import org.nuxeo.runtime.test.runner.RuntimeFeature;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features({ RuntimeFeature.class, LogCaptureFeature.class })
@Deploy({ "org.nuxeo.ecm.core.event" })
@LogCaptureFeature.With(value = LogCaptureFeature.Filter.Errors.class, loggers = {
        WorkTraceError.class, WorkManagerImpl.class, AbstractWork.class })
public class WorkErrorsAreTracableTest {

    protected static class Fail extends AbstractWork {
        @Override
        public String getTitle() {
            return Nest.class.getSimpleName();
        }

        @Override
        public void work() throws Exception {
            throw new Error();
        }

        public static class Error extends java.lang.Error {

            private static final long serialVersionUID = 1L;

        }
    }

    protected class Nest extends AbstractWork {

        protected Work sub;

        @Override
        public String getTitle() {
            return Nest.class.getSimpleName();
        }

        @Override
        public void work() throws Exception {
            sub = new Fail();
            manager.schedule(sub);
        }

    }

    @Inject
    protected WorkManager manager;

    @Inject
    protected LogCaptureFeature.Result result;

    @Test
    public void captureSimple() throws NoFilterError, InterruptedException {
        Fail work = new Fail();
        manager.schedule(work);
        awaitFailure(work);
    }

    @Test
    public void captureChained() throws NoFilterError, InterruptedException {
        Nest work = new Nest();
        manager.schedule(work);
        awaitFailure(work);
    }

    protected WorkTraceError awaitFailure(Work work)
            throws InterruptedException, NoFilterError {
        manager.awaitCompletion(1, TimeUnit.SECONDS);
        result.assertHasEvent();
        result.assertContains(
                "Exception during work: Fail(RUNNING, Progress(0.0%, ?/0), null)",
                "Fail(FAILED, Progress(0.0%, ?/0), null)");
        WorkTraceError error = (WorkTraceError) result.getCaughtEvents().get(1).getThrowableInformation().getThrowable();
        assertIsRootWork(work, error);
        return error;
    }

    protected void assertIsRootWork(Work work, WorkTraceError error) {
        for (Throwable cause = error.getCause(); cause != null
                && cause != error; error = (WorkTraceError) cause) {
            ;
        }
        assertEquals(work, error.work);
    }

}
