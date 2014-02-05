package org.nuxeo.ecm.core.storage.sql;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

public interface Invalidations {

    /** Pseudo-table for children invalidation. */
    public static final String PARENT = "__PARENT__";

    /** Pseudo-table for series proxies invalidation. */
    public static final String SERIES_PROXIES = "__SERIES_PROXIES__";

    /** Pseudo-table for target proxies invalidation. */
    public static final String TARGET_PROXIES = "__TARGET_PROXIES__";

    public static final int MODIFIED = 1;

    public static final int DELETED = 2;

    boolean isEmpty();

    void clear();

    /** only call this if it's to add at least one element in the set */
    Set<RowId> getKindSet(int kind);

    Set<RowId> getModified();

    Set<RowId> getDeleted();

    Invalidations add(Invalidations other);

    void addModified(RowId rowId);

    void addDeleted(RowId rowId);

    void add(Serializable id, String[] tableNames, int kind);

    public static Invalidations EMPTY = new Invalidations() {

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public void clear() {
            ;
        }

        @Override
        public Set<RowId> getKindSet(int kind) {
            return Collections.emptySet();
        }

        @Override
        public Set<RowId> getModified() {
            return Collections.emptySet();
        }

        @Override
        public Set<RowId> getDeleted() {
            return Collections.emptySet();
        }

        @Override
        public Invalidations add(Invalidations other) {
            return other;
        }

        @Override
        public void addModified(RowId rowId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addDeleted(RowId rowId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Serializable id, String[] tableNames, int kind) {
            throw  new UnsupportedOperationException();
        }

    };
    public static Invalidations ALL = new Invalidations() {

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<RowId> getModified() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<RowId> getDeleted() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<RowId> getKindSet(int kind) {
            throw new UnsupportedOperationException();
        }


        @Override
        public Invalidations add(Invalidations other) {
            return this;
        }

        @Override
        public void addModified(RowId rowId) {
            ;
        }

        @Override
        public void addDeleted(RowId rowId) {
           ;
        }

        @Override
        public void add(Serializable id, String[] tableNames, int kind) {
           ;
        }

    };

}