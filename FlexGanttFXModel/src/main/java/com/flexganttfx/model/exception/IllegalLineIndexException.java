/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.exception;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Row;

import java.text.MessageFormat;

/**
 * An exception type used to indicate that the line index used for an activity
 * is not within the possible range for a given row. Example: a row might have
 * 10 lines but the line index is 15.
 *
 * @since 1.0
 */
public class IllegalLineIndexException extends RuntimeException {

    private static final long serialVersionUID = -6806986448373406748L;

    private final int lineIndex;
    private final int lineCount;
    private final Row<?, ?, ?> row;

    /**
     * Constructs a new exception.
     *
     * @param row       the row where the exception occured
     * @param lineIndex the line index that violated the line count
     * @param lineCount the total number of lines inside the row
     * @since 1.0
     */
    public IllegalLineIndexException(Row<?, ?, ?> row, int lineIndex, int lineCount) {
        super(MessageFormat.format("The given line index {0} violates the current line count {1}", lineIndex, lineCount));
        this.row = row;
        this.lineIndex = lineIndex;
        this.lineCount = lineCount;
    }

    /**
     * Returns the row where the exception occured.
     *
     * @return the affected row
     * @since 1.0
     */
    public Row<?, ?, ?> getRow() {
        return row;
    }

    /**
     * Returns the total number of lines used for the row.
     *
     * @return the line count
     * @see Row#getLineCount()
     * @since 1.0
     */
    public int getLineCount() {
        return lineCount;
    }

    /**
     * Returns the line index that did not fit into the line count.
     *
     * @return the line index
     * @see Row#getLineIndex(Activity)
     * @since 1.0
     */
    public int getLineIndex() {
        return lineIndex;
    }
}
