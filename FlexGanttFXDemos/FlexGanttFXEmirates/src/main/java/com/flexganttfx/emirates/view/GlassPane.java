/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.emirates.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.util.Objects;

public class GlassPane extends StackPane {

    private final DoubleProperty progress = new SimpleDoubleProperty();

    public GlassPane() {
        getStyleClass().add("glass-pane");

        getStylesheets().add(Objects.requireNonNull(GlassPane.class.getResource("glasspane.css")).toExternalForm());

        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        setMouseTransparent(false);
        visibleProperty().bind(progressProperty().greaterThan(0).and(progressProperty().lessThan(1)));

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(100, 100);
        progressIndicator.setMinSize(100, 100);
        progressIndicator.progressProperty().bind(progressProperty());
        getChildren().add(progressIndicator);

        StackPane.setAlignment(progressIndicator, Pos.CENTER);
    }

    public double getProgress() {
        return progress.get();
    }

    public void setProgress(double progress) {
        this.progress.set(progress);
    }

    public DoubleProperty progressProperty() {
        return progress;
    }
}
