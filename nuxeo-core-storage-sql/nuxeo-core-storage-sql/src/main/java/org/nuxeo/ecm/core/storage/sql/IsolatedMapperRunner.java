package org.nuxeo.ecm.core.storage.sql;

import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.storage.ConcurrentUpdateStorageException;
import org.nuxeo.ecm.core.storage.StorageException;
import org.nuxeo.runtime.transaction.TransactionHelper;

public class IsolatedMapperRunner {

    protected final Log log = LogFactory.getLog(IsolatedMapperRunner.class);

    protected final Mapper mapper;

    protected final ExecutorService executor;

    public IsolatedMapperRunner(final String name, final Mapper mapper) {
        this.mapper = mapper;
        executor = Executors.newSingleThreadExecutor(new ThreadFactory() {

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("%s-%x", name,hashCode()));
            }

        });
    }

    public <T> T submit(Callable<T> call) throws StorageException {
        try {
            return executor.submit(call).get();
        } catch (InterruptedException cause) {
            Thread.currentThread().interrupt();
            throw new StorageException(
                    "Interrupted isolated thread call", cause);
        } catch (ExecutionException cause) {
            throw new StorageException(
                    "Wrong isolated thread call, error", cause);
        }
    }

    public <T> InConnection<T> inConnection(Callable<T> callable) {
        return new InConnection<T>(callable);
    }

    public <T> InTransaction<T> inTransaction(Callable<T> callable) {
        return new InTransaction<T>(callable);
    }

    public <T> WithRetry<T> withRetry(Callable<T> callable) {
        return new WithRetry<T>(callable);
    }

    protected abstract class Clojure<T> implements Callable<T> {

        protected final Callable<T> callable;

        public Clojure(Callable<T> callable) {
            this.callable = callable;
        }

        public T submit() throws StorageException {
            return IsolatedMapperRunner.this.submit(this);
        }
    }


    protected class Open<T> extends Clojure<T> {

        public Open(Callable<T> callable) {
            super(callable);
        }

        @Override
        public T call() throws Exception {
            mapper.openConnections();
            return callable.call();
        }

    }

    public <T> Open<T> open(Callable<T> callable) {
        return new Open<T>(callable);
    }

    protected class Close<T> extends Clojure<T> {

        public Close(Callable<T> callable) {
            super(callable);
        }

        @Override
        public T call() throws Exception {
            try {
                return callable.call();
            } finally {
                mapper.closeConnections();
            }
        }
    }

    public <T> Close<T> close(Callable<T> callable) {
        return new Close<T>(callable);
    }

    public class InConnection<T> extends Clojure<T> {

        InConnection(Callable<T> callable) {
            super(callable);
        }

        @Override
        public T call() throws Exception {
            mapper.openConnections();
            try {
                return callable.call();
            } finally {
                mapper.closeConnections();
            }
        }

    }

    protected  class InTransaction<T> extends Clojure<T> {

        public InTransaction(Callable<T> callable) {
            super(callable);
        }

        @Override
        public T call() throws Exception {
            boolean ok = false;
            boolean txStarted = TransactionHelper.startTransaction();
            try {
                T result;
                try {
                    result = callable.call();
                } catch (Exception cause) {
                    throw new StorageException(
                            "Cannot operate lock on document", cause);
                }
                ok = true;
                return result;
            } finally {
                if (txStarted) {
                    if (!ok) {
                        TransactionHelper.setTransactionRollbackOnly();
                    }
                    TransactionHelper.commitOrRollbackTransaction();
                }
            }

        }

    }

    protected class WithRetry<T> extends Clojure<T> {

        protected int RETRIES = 10;

        protected WithRetry(Callable<T> callable) {
            super(callable);
        }

        /**
         * Does the exception mean that we should retry the transaction?
         */
        protected boolean shouldRetry(StorageException e) {
            if (e instanceof ConcurrentUpdateStorageException) {
                return true;
            }
            Throwable t = e.getCause();
            if (t instanceof BatchUpdateException && t.getCause() != null) {
                t = t.getCause();
            }
            return t instanceof SQLException && shouldRetry((SQLException) t);
        }


        protected boolean shouldRetry(SQLException e) {
            String sqlState = e.getSQLState();
            if ("23000".equals(sqlState)) {
                // MySQL: Duplicate entry ... for key ...
                // Oracle: unique constraint ... violated
                // SQL Server: Violation of PRIMARY KEY constraint
                return true;
            }
            if ("23001".equals(sqlState)) {
                // H2: Unique index or primary key violation
                return true;
            }
            if ("23505".equals(sqlState)) {
                // PostgreSQL: duplicate key value violates unique constraint
                return true;
            }
            if ("S0003".equals(sqlState) || "S0005".equals(sqlState)) {
                // SQL Server: Snapshot isolation transaction aborted due to update
                // conflict
                return true;
            }
            return false;
        }

        @Override
        public T call() throws Exception {
            long sleepDelay = 1; // 1 ms
            long INCREMENT = 50; // additional 50 ms each time
            for (int i = 0; i < RETRIES; i++) {
                try {
                    return callable.call();
                } catch (StorageException e) {
                    if (shouldRetry(e)) {
                        try {
                            Thread.sleep(sleepDelay);
                        } catch (InterruptedException ie) {
                            throw new RuntimeException(ie);
                        }
                        sleepDelay += INCREMENT;
                        continue;
                    }
                    throw e;
                }
            }
            throw new StorageException("Retry count expired");
        }

    }

    public void shutdown() {
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Cannot wait for lock manager being terminated", e);
        }
    }

}
