package org.nuxeo.ecm.core.test;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.storage.sql.SessionImpl.OwnerException;
import org.nuxeo.ecm.core.storage.sql.ra.PoolingRepositoryFactory;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.core.test.annotations.TransactionalConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LogCaptureFeature;
import org.nuxeo.runtime.test.runner.LogCaptureFeature.NoLogCaptureFilterException;
import org.nuxeo.runtime.transaction.TransactionHelper;
import org.nuxeo.runtime.transaction.TransactionRuntimeException;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features({ LogCaptureFeature.class, TransactionalFeature.class, CoreFeature.class })
@TransactionalConfig(autoStart = true)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD, repositoryFactoryClass = PoolingRepositoryFactory.class)
public class CanStealSessionTest {

    @Inject
    CoreSession session;

    public class Thief implements Runnable {

        final Object sessionStolen = new Object();

        final Object waitingRollback = new Object();

        final Object txRollbacked = new Object();

        boolean done = false;

        @Override
        public void run() {
            TransactionHelper.startTransaction();
            try {
                session.getDocument(new PathRef("/"));
                Assert.assertTrue(TransactionHelper.isTransactionMarkedRollback());
                synchronized (sessionStolen) {
                    sessionStolen.notify();
                }
                synchronized (txRollbacked) {
                    txRollbacked.wait(1000);
                }
                session.getDocument(new PathRef("/default-domain"));
            } catch (Exception e) {
                throw new Error("Cannot access to root document in stoler", e);
            } finally {
                TransactionHelper.commitOrRollbackTransaction();
            }
            done = true;
        }

    }

    @Before
    public void fetchConnection() throws ClientException {
        session.getDocument(new PathRef("/default-domain"));
    }

    public static class FilterSessionErrors implements LogCaptureFeature.Filter {

        @Override
        public boolean accept(LoggingEvent event) {
            ThrowableInformation info = event.getThrowableInformation();
            if (info == null) {
                return false;
            }
            Throwable throwable = info.getThrowable();
            return throwable instanceof OwnerException;
        }

    }

    @Inject LogCaptureFeature.Result logResults;

    @Test
    @LogCaptureFeature.FilterWith(FilterSessionErrors.class)
    public void steal() throws ClientException, InterruptedException, NoLogCaptureFilterException {

        Thief thief = new Thief();
        Thread thiefThread = new Thread(thief, "stoler");
        thiefThread.start();
        try {
            synchronized (thief.sessionStolen) {
                thief.sessionStolen.wait(1000);
            }
            session.getDocument(new PathRef("/default-domain/workspaces"));

        } catch (OwnerException e) {
            ;
        } finally {
            try {
                TransactionHelper.commitOrRollbackTransaction();
            } catch (TransactionRuntimeException e) {
                ;
            } finally {
                synchronized (thief.txRollbacked) {
                    thief.txRollbacked.notify();
                }
                try {
                    thiefThread.join(1000);
                } finally {
                    Assert.assertTrue(thief.done);
                    logResults.assertHasEvent();
                    Assert.assertEquals(logResults.getCaughtEvents().size(), 2);
                }
            }
        }
    }

    @Test
    public void runUnrestriced() {
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() throws ClientException {
                session.getDocument(new PathRef("/default-domain/workspaces"));
            }
        };
    }
}
