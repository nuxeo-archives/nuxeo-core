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
import java.util.HashSet;
import java.util.Set;

/**
 * A set of invalidations.
 * <p>
 * Records both modified and deleted fragments, as well as "parents modified"
 * fragments.
 */
public class InvalidationsRows implements Serializable, Invalidations {

    private static final long serialVersionUID = 1L;


    /** null when empty */
    protected final Set<RowId> modified = new HashSet<>();

    /** null when empty */
    protected final Set<RowId> deleted = new HashSet<>();

    public InvalidationsRows() {
    }

    @Override
    public boolean isEmpty() {
        return modified.size() == 0 && deleted.size() == 0;
    }

    @Override
    public void clear() {
        modified.clear();
        deleted.clear();
    }


    @Override
    public Set<RowId> getModified() {
        return modified;
    }

    @Override
    public Set<RowId> getDeleted() {
        return deleted;
    }

    @Override
    public Set<RowId> getKindSet(int kind) {
        switch (kind) {
        case MODIFIED:
            return modified;
        case DELETED:
            return deleted;
        }
        throw new AssertionError();
    }


    @Override
    public Invalidations add(Invalidations other) {
        if (other.equals(Invalidations.ALL)) {
            return Invalidations.ALL;
        }
        addModified(other.getKindSet(MODIFIED));
        addDeleted(other.getKindSet(DELETED));
        return this;
    }

    @Override
    public void addModified(RowId rowId) {
        modified.add(rowId);
    }

    protected void addModified(Set<RowId> rowIds) {
        modified.addAll(rowIds);
    }


    @Override
    public void addDeleted(RowId rowId) {
        deleted.add(rowId);
    }

    protected void addDeleted(Set<RowId> rowIds) {
        deleted.addAll(rowIds);
    }


    @Override
    public void add(Serializable id, String[] tableNames, int kind) {
        if (tableNames.length == 0) {
            return;
        }
        Set<RowId> set = getKindSet(kind);
        for (String tableName : tableNames) {
            set.add(new RowId(tableName, id));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(
                this.getClass().getSimpleName() + '(');
        if (!modified.isEmpty()) {
            sb.append("modified=");
            sb.append(modified);
            if (deleted != null) {
                sb.append(',');
            }
        }
        if (!deleted.isEmpty()) {
            sb.append("deleted=");
            sb.append(deleted);
        }
        sb.append(')');
        return sb.toString();
    }

    public static final class InvalidationsPair implements Serializable {

        private static final long serialVersionUID = 1L;

        public final Invalidations cacheInvalidations;

        public final Invalidations eventInvalidations;

        public InvalidationsPair(Invalidations cacheInvalidations,
                Invalidations eventInvalidations) {
            this.cacheInvalidations = cacheInvalidations;
            this.eventInvalidations = eventInvalidations;
        }
    }

}
