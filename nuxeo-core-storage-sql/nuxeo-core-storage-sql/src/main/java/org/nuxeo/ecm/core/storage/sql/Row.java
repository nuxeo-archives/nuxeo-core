/*
 * (C) Copyright 2007-2010 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Florent Guillaume
 */
package org.nuxeo.ecm.core.storage.sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nuxeo.ecm.core.storage.StorageException;

/**
 * The data of a single row in a table (keys/values form a map), or of multiple
 * rows with the same id (values is an array of Serializable).
 * <p>
 * The id of the row is distinguished internally from other columns. For
 * fragments corresponding to created data, the initial id is a temporary one,
 * and it will be changed after database insert.
 */
public final class Row extends RowId implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int DEFAULT = 5;

    /**
     * The row keys, for single row.
     */
    protected String[] keys;

    /**
     * The row values.
     */
    public Serializable[] values;

    /**
     * The size of the allocated part of {@link #values}, for single rows.
     */
    protected int size;

    /**
     * Constructs an empty {@link Row} for the given table with the given id
     * (may be {@code null}).
     */
    public Row(String tableName, Serializable id) {
        super(tableName, id);
        keys = new String[DEFAULT];
        values = new Serializable[DEFAULT];
        // size = 0;
    }

    /**
     * Constructs a new {@link Row} from a map.
     *
     * @param map the initial data to use
     */
    public Row(String tableName, Map<String, Serializable> map) {
        super(tableName, null); // id set through map
        keys = new String[map.size()];
        values = new Serializable[map.size()];
        // size = 0;
        for (Entry<String, Serializable> entry : map.entrySet()) {
            putNew(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Constructs a new {@link Row} from an array of values.
     *
     * @param array the initial data to use
     */
    public Row(String tableName, Serializable id, Serializable[] array) {
        super(tableName, id);
        values = array.clone();
        keys = null;
        size = -1;
    }

    public boolean isCollection() {
        return size == -1;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > values.length) {
            Serializable[] k = keys;
            Serializable[] d = values;
            int newCapacity = (values.length * 3) / 2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            keys = new String[newCapacity];
            values = new Serializable[newCapacity];
            System.arraycopy(d, 0, values, 0, size);
            System.arraycopy(k, 0, keys, 0, size);
        }
    }

    /**
     * Puts a key/value.
     *
     * @param key the key
     * @param value the value
     * @return {@code true} if an old key was overwritten
     * @throws StorageException
     */
    public void put(String key, Serializable value) {
        if (key.equals(Model.MAIN_KEY)) {
            id = value;
            return;
        }
        // linear search but the array is small
        for (int i = 0; i < size; i++) {
            if (key.equals(keys[i])) {
                values[i] = value;
                return;
            }
        }
        ensureCapacity(size + 1);
        keys[size] = key;
        values[size++] = value;
    }

    /**
     * Puts a key/value, assuming the key is not already there.
     *
     * @param key the key
     * @param value the value
     * @throws StorageException
     */
    public void putNew(String key, Serializable value) {
        if (key.equals(Model.MAIN_KEY)) {
            id = value;
            return;
        }
        ensureCapacity(size + 1);
        keys[size] = key;
        values[size++] = value;
    }

    /**
     * Gets a value from a key.
     *
     * @param key the key
     * @return the value
     * @throws StorageException
     */
    public Serializable get(String key) {
        if (key.equals(Model.MAIN_KEY)) {
            return id;
        }
        // linear search but the array is small
        for (int i = 0; i < size; i++) {
            if (key.equals(keys[i])) {
                return values[i];
            }
        }
        return null;
    }

    /**
     * Gets the list of keys. The id is not included.
     */
    public List<String> getKeys() {
        List<String> list = new ArrayList<String>(size);
        for (int i = 0; i < size; i++) {
            list.add(keys[i]);
        }
        return list;
    }

    /**
     * Gets the list of values. The id is not included.
     */
    public List<Serializable> getValues() {
        List<Serializable> list = new ArrayList<Serializable>(size);
        for (int i = 0; i < size; i++) {
            list.add(values[i]);
        }
        return list;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getSimpleName());
        buf.append('(');
        buf.append(tableName);
        buf.append(", ");
        buf.append(id);
        if (size != -1) {
            // single row
            buf.append(", {");
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    buf.append(", ");
                }
                buf.append(keys[i]);
                buf.append('=');
                Serializable value = values[i];
                boolean truncated = false;
                if (value instanceof String && ((String) value).length() > 100) {
                    value = ((String) value).substring(0, 100);
                    truncated = true;
                }
                buf.append(value);
                if (truncated) {
                    buf.append("...");
                }
                buf.append('}');
            }
        } else {
            // multiple rows
            buf.append(", [");
            for (int i = 0; i < values.length; i++) {
                if (i > 0) {
                    buf.append(", ");
                }
                Serializable value = values[i];
                boolean truncated = false;
                if (value instanceof String && ((String) value).length() > 100) {
                    value = ((String) value).substring(0, 100);
                    truncated = true;
                }
                buf.append(value);
                if (truncated) {
                    buf.append("...");
                }
            }
            buf.append(']');
        }
        buf.append(')');
        return buf.toString();
    }

}
