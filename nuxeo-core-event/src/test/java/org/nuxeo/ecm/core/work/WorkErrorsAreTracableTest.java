package org.nuxeo.ecm.core.work;

import static org.junit.Assert.assertEquals;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.spi.LoggingEvent;
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
@LogCaptureFeature.With(value=WorkErrorsAreTracableTest.ChainFilter.class, includes=WorkTraceError.class, excludes=AbstractWork.class)
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

    public static class ChainFilter implements LogCaptureFeature.Filter {

        @Override
        public boolean accept(LoggingEvent event) {
            String category = event.getLogger().getName();
            return category.startsWith(WorkTraceError.class.getName());
        }

    }

    @Inject
    protected WorkManager manager;

    @Inject
    protected LogCaptureFeature.Result result;

    @Test
    public void captureSimple() throws NoFilterError,
            InterruptedException {
        Fail work = new Fail();
        manager.schedule(work);
        awaitFailure(work);
    }

    @Test
    public void captureChained() throws NoFilterError,
            InterruptedException {
        Nest work = new Nest();
        manager.schedule(work);
        awaitFailure(work);
    }

    protected WorkTraceError awaitFailure(Work work)
            throws InterruptedException, NoFilterError {
        manager.awaitCompletion(100, TimeUnit.MILLISECONDS);
        result.assertHasEvent();
        WorkTraceError error = (WorkTraceError) result.getCaughtEvents().get(0).getThrowableInformation().getThrowable();
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
