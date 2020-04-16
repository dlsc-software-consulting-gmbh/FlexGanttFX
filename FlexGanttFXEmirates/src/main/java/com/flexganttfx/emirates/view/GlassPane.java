package com.flexganttfx.emirates.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;

public class GlassPane extends StackPane {

    private final DoubleProperty progress = new SimpleDoubleProperty();

    public GlassPane() {
        setStyle("-fx-background-color: white; -fx-opacity: .8;");
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
