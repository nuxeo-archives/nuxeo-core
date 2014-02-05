/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Florent Guillaume
 */
package org.nuxeo.ecm.core.storage.sql;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.Callable;

import org.nuxeo.ecm.core.api.Lock;
import org.nuxeo.ecm.core.storage.ConnectionResetException;
import org.nuxeo.ecm.core.storage.StorageException;
import org.nuxeo.ecm.core.storage.sql.IsolatedMapperRunner.Clojure;
import org.nuxeo.ecm.core.storage.sql.jdbc.JDBCMapper;

/**
 * Manager of locks that serializes access to them.
 * <p>
 * The public methods called by the session are {@link #setLock},
 * {@link #removeLock} and {@link #getLock}. Method {@link #shutdown} must be
 * called when done with the lock manager.
 * <p>
 * In cluster mode, changes are executed in a begin/commit so that tests/updates
 * can be atomic.
 * <p>
 * Transaction management can be done by hand because we're dealing with a
 * low-level {@link Mapper} and not something wrapped by a JCA pool.
 */
public class LockManager {

    protected final RepositoryImpl repository;

    protected IsolatedMapperRunner runner;


    /**
     * Creates a lock manager using the given mapper.
     * <p>
     * The mapper will from then on be only used and closed by the lock manager.
     * <p>
     * {@link #shutdown} must be called when done with the lock manager.
     */
    public LockManager(final RepositoryImpl repo, JDBCMapper mapper)
            throws StorageException {
        runner = new IsolatedMapperRunner("nuxeo-locker-"
                + repo.getRepositoryDescriptor().name,
                repo.createCachingMapper(repo.getModel(), mapper));
        repository = repo;
     }

    /**
     * Shuts down the lock manager.
     */
    public void shutdown() throws StorageException {
        runner.shutdown();
    }

    public void startup() throws StorageException {
        runner.inConnection(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                repository.initializeDatabase(runner.mapper);
                return null;
            }

        }).submit();
    }

    /**
     * Gets the lock state of a document.
     * <p>
     * If the document does not exist, {@code null} is returned.
     *
     * @param id the document id
     * @return the existing lock, or {@code null} when there is no lock
     */
    public Lock getLock(final Serializable id) throws StorageException {
        return runner.inConnection(new Callable<Lock>() {
            @Override
            public Lock call() throws Exception {
                return LockManager.this.getLock(runner.mapper, id);
            }

        }).submit();
    }

    /**
     * Sets a lock on a document.
     * <p>
     * If the document is already locked, returns its existing lock status
     * (there is no re-locking, {@link #removeLock} must be called first).
     *
     * @param id the document id
     * @param lock the lock object to set
     * @return {@code null} if locking succeeded, or the existing lock if
     *         locking failed, or a
     */
    public Lock setLock(final Serializable id, final Lock lock) throws StorageException {
        RowId rowId = rowId(id);
        Clojure<Lock> clojure =
                runner.inConnection(new Callable<Lock>() {
                    @Override
                    public Lock call() throws Exception {
                        return LockManager.this.setLock(runner.mapper, id, lock);
                    }
                });
        if (!runner.mapper.isCached(rowId)) {
            clojure = runner.inTransaction(clojure);
        }
        return clojure.submit();
    }

    protected RowId rowId(final Serializable id) {
        return new RowId(Model.LOCK_TABLE_NAME, id);
    }

    /**
     * Removes a lock from a document.
     * <p>
     * The previous lock is returned.
     * <p>
     * If {@code owner} is {@code null} then the lock is unconditionally
     * removed.
     * <p>
     * If {@code owner} is not {@code null}, it must match the existing lock
     * owner for the lock to be removed. If it doesn't match, the returned lock
     * will return {@code true} for {@link Lock#getFailed}.
     *
     * @param id the document id
     * @param force {@code true} to just do the remove and not return the
     *            previous lock
     * @param executor TODO
     * @param the owner to check, or {@code null} for no check
     * @return the previous lock
     */
    public Lock removeLock(final Serializable id, final String owner)
            throws StorageException {
        RowId rowId = rowId(id);
        Clojure<Lock> clojure = runner.inConnection(new Callable<Lock>() {
            @Override
            public Lock call() throws Exception {
                return LockManager.this.removeLock(runner.mapper, id, owner);
            }
        });
        if (runner.mapper.isCached(rowId)) {
            clojure = runner.inTransaction(clojure);
        }
        return clojure.submit();
     }

    public void clearCaches() throws StorageException {
        runner.mapper.clearCache();
    }

    @Override
    public String toString() {
        return "LockManager [mapper=" + runner.mapper + "]";
    }

    /**
     * Checks if a given lock can be removed by the given owner.
     *
     * @param lock the lock
     * @param owner the owner (may be {@code null})
     * @return {@code true} if the lock can be removed
     */
    public static boolean canLockBeRemoved(Lock lock, String owner) {
        return lock != null && (owner == null || owner.equals(lock.getOwner()));
    }

    protected Lock getLock(Mapper mapper, Serializable id) throws StorageException {
        RowId rowId = rowId(id);
        Row row;
        try {
            row = mapper.readSimpleRow(rowId);
        } catch (ConnectionResetException e) {
            // retry once
            row = mapper.readSimpleRow(rowId);
        }
        return row == null ? null : new Lock(
                (String) row.get(Model.LOCK_OWNER_KEY),
                (Calendar) row.get(Model.LOCK_CREATED_KEY));
    }

    protected Lock removeLock(Mapper mapper, Serializable id, String owner) throws StorageException {
        Lock lock = getLock(mapper, id);
        if (!canLockBeRemoved(lock, owner)) {
            if (lock != null) {
                lock = new Lock(lock, true);
            }
        } else {
            mapper.deleteSimpleRows(Model.LOCK_TABLE_NAME, Collections.singleton(id));
        }
        return lock;
    }

    protected Lock setLock(Mapper mapper, final Serializable id, final Lock lock)
            throws StorageException {
        Lock oldLock = getLock(mapper, id);
        if (oldLock == null) {
            Row row = new Row(Model.LOCK_TABLE_NAME, id);
            row.put(Model.LOCK_OWNER_KEY, lock.getOwner());
            row.put(Model.LOCK_CREATED_KEY, lock.getCreated());
            mapper.insertSimpleRows(Model.LOCK_TABLE_NAME,
                    Collections.singletonList(row));
        }
        return oldLock;
    }

}
