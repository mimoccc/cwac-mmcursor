/*
 * Copyright (c) 2008-2009 CommonsWare, LLC
 * Portions Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.commonsware.cwac.cursor;

import android.database.AbstractCursor;
import android.database.CursorIndexOutOfBoundsException;
import java.util.ArrayList;

/**
 * A mutable cursor implementation backed by an list of {@code Object}s. Use
 * {@link #newRow()} to add rows. Automatically expands internal capacity
 * as needed.
 */
public class MutableMatrixCursor extends AbstractCursor {

    private final String[] columnNames;
    private ArrayList<Object[]> data;
    private final int columnCount;

    /**
     * Constructs a new cursor with the given initial capacity.
     *
     * @param columnNames names of the columns, the ordering of which
     *  determines column ordering elsewhere in this cursor
     * @param initialCapacity in rows
     */
    public MutableMatrixCursor(String[] columnNames, int initialCapacity) {
        this.columnNames = columnNames;
        this.columnCount = columnNames.length;

        if (initialCapacity < 1) {
            initialCapacity = 1;
        }

        this.data = new ArrayList<Object[]>(initialCapacity);
    }

    /**
     * Constructs a new cursor.
     *
     * @param columnNames names of the columns, the ordering of which
     *  determines column ordering elsewhere in this cursor
     */
    public MutableMatrixCursor(String[] columnNames) {
        this(columnNames, 16);
    }

    /**
     * Gets value at the given column for the current row.
     */
    private Object get(int column) {
        if (column < 0 || column >= columnCount) {
            throw new CursorIndexOutOfBoundsException("Requested column: "
                    + column + ", # of columns: " +  columnCount);
        }
        if (mPos < 0) {
            throw new CursorIndexOutOfBoundsException("Before first row.");
        }
        if (mPos >= data.size()) {
            throw new CursorIndexOutOfBoundsException("After last row.");
        }
        
        Object[] row=data.get(mPos);
        
        return row[column];
    }

    /**
     * Adds a new row to the end and returns a builder for that row. Not safe
     * for concurrent use.
     *
     * @return builder which can be used to set the column values for the new
     *  row
     */
    public RowBuilder newRow() {
        Object[] localData = new Object[columnCount];
        
        data.add(localData);
        
        return new RowBuilder(localData);
    }

    /**
     * Adds a new row to the end with the given column values. Not safe
     * for concurrent use.
     *
     * @throws IllegalArgumentException if {@code columnValues.length !=
     *  columnNames.length}
     * @param columnValues in the same order as the the column names specified
     *  at cursor construction time
     */
    public void addRow(Object[] columnValues) {
        if (columnValues.length != columnCount) {
            throw new IllegalArgumentException("columnNames.length = "
                    + columnCount + ", columnValues.length = "
                    + columnValues.length);
        }

        data.add(columnValues);
    }

    /**
     * Adds a new row to the end with the given column values. Not safe
     * for concurrent use.
     *
     * @throws IllegalArgumentException if {@code columnValues.size() !=
     *  columnNames.length}
     * @param columnValues in the same order as the the column names specified
     *  at cursor construction time
     */
    public void addRow(Iterable<?> columnValues) {
        if (columnValues instanceof ArrayList<?>) {
            addRow((ArrayList<?>) columnValues);
            return;
        }

        int current = 0;
        Object[] localData = new Object[columnCount];
        for (Object columnValue : columnValues) {
            if (current == columnCount) {
                // TODO: null out row?
                throw new IllegalArgumentException(
                        "columnValues.size() > columnNames.length");
            }
            localData[current++] = columnValue;
        }

        if (current != columnCount) {
            // TODO: null out row?
            throw new IllegalArgumentException(
                    "columnValues.size() < columnNames.length");
        }

        // Increase row count here in case we encounter an exception.
        data.add(localData);
    }

    /** Optimization for {@link ArrayList}. */
    private void addRow(ArrayList<?> columnValues) {
        int size = columnValues.size();
        if (size != columnCount) {
            throw new IllegalArgumentException("columnNames.length = "
                    + columnCount + ", columnValues.size() = " + size);
        }

        Object[] localData = new Object[columnCount];
        for (int i = 0; i < size; i++) {
            localData[i] = columnValues.get(i);
        }
        data.add(localData);
    }

    /**
     * Builds a row, starting from the left-most column and adding one column
     * value at a time. Follows the same ordering as the column names specified
     * at cursor construction time.
     */
    public class RowBuilder {
        private Object[] row;
        private int index=0;

        RowBuilder(Object[] row) {
            this.row=row;
        }

        /**
         * Sets the next column value in this row.
         *
         * @throws CursorIndexOutOfBoundsException if you try to add too many
         *  values
         * @return this builder to support chaining
         */
        public RowBuilder add(Object columnValue) {
            if (index == columnCount) {
                throw new CursorIndexOutOfBoundsException(
                        "No more columns left.");
            }

            row[index++] = columnValue;
            return this;
        }
    }
    
    public void remove(int position) {
        mPos=-1;
        data.remove(position);
    }

    // AbstractCursor implementation.

    public int getCount() {
        return data.size();
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public String getString(int column) {
        return String.valueOf(get(column));
    }

    public short getShort(int column) {
        Object value = get(column);
        return (value instanceof String)
                ? Short.valueOf((String) value)
                : ((Number) value).shortValue();
    }

    public int getInt(int column) {
        Object value = get(column);
        return (value instanceof String)
                ? Integer.valueOf((String) value)
                : ((Number) value).intValue();
    }

    public long getLong(int column) {
        Object value = get(column);
        return (value instanceof String)
                ? Long.valueOf((String) value)
                : ((Number) value).longValue();
    }

    public float getFloat(int column) {
        Object value = get(column);
        return (value instanceof String)
                ? Float.valueOf((String) value)
                : ((Number) value).floatValue();
    }

    public double getDouble(int column) {
        Object value = get(column);
        return (value instanceof String)
                ? Double.valueOf((String) value)
                : ((Number) value).doubleValue();
    }

    public boolean isNull(int column) {
        return get(column) == null;
    }
}