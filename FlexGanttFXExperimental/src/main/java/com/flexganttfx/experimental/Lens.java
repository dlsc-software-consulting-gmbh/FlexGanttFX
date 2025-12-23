/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
/**
 *
 */
package com.flexganttfx.experimental;

import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.GraphicsBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import static java.util.Objects.requireNonNull;

/**
 * @param <R>
 *            the type of the rows that are displayed by the Gantt chart.
 */
public class Lens<R extends Row<?, ?, ?>> extends Control {

    private final GraphicsBase<R> graphics;

    public Lens(GraphicsBase<R> graphics) {
        this.graphics = requireNonNull(graphics);

        getStyleClass().add("graphics-lens");

        prefWidthProperty().bind(graphics.widthProperty());
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new LensSkin<>(this);
    }

    /**
     * @return the graphics
     * @since 1.1
     */
    public GraphicsBase<R> getGraphics() {
        return graphics;
    }

    private final ObservableList<R> rows = FXCollections.observableArrayList();

    public final ObservableList<R> getRows() {
        return rows;
    }

    private final IntegerProperty startIndex = new SimpleIntegerProperty(this,
            "startIndex", 0);

    public final IntegerProperty startIndexProperty() {
        return startIndex;
    }

    public final void setStartIndex(int index) {
        if (index < 0) {
            throw new IllegalArgumentException(
                    "index must be >= 0 but was " + index);
        }
        startIndexProperty().set(index);
    }

    public final int getStartIndex() {
        return startIndexProperty().get();
    }

    private final IntegerProperty rowCount = new SimpleIntegerProperty(this,
            "rowCount", 4);

    public final IntegerProperty rowCountProperty() {
        return rowCount;
    }

    public final void setRowCount(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException(
                    "row count must be larger than 0 but was " + count);
        }
        rowCountProperty().set(count);
    }

    public final int getRowCount() {
        return rowCountProperty().get();
    }
}
