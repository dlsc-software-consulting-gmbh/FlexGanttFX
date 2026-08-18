/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.emirates.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
