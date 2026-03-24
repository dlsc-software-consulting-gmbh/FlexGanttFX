/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.factory.view;

import com.flexganttfx.factory.model.DataModel;
import com.flexganttfx.factory.model.JobStatus;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Root UI for the factory demo. Displays the {@link FactoryGanttChart} and a
 * simple legend bar at the bottom showing the job status colours.
 */
public class FactoryView extends BorderPane {

    public FactoryView() {
        DataModel dataModel = new DataModel();
        setCenter(new FactoryGanttChart(dataModel));
        setBottom(buildLegend());
    }

    private HBox buildLegend() {
        HBox legend = new HBox(16);
        legend.setPadding(new Insets(6, 12, 6, 12));
        legend.setAlignment(Pos.CENTER_LEFT);

        addLegendEntry(legend, "Scheduled",   Color.STEELBLUE);
        addLegendEntry(legend, "In Progress", Color.DARKORANGE);
        addLegendEntry(legend, "Done",        Color.MEDIUMSEAGREEN);
        addLegendEntry(legend, "Delayed",     Color.CRIMSON);

        return legend;
    }

    private void addLegendEntry(HBox container, String text, Color color) {
        Rectangle swatch = new Rectangle(14, 14, color);
        swatch.setArcWidth(3);
        swatch.setArcHeight(3);
        Label label = new Label(text);
        HBox entry = new HBox(5, swatch, label);
        entry.setAlignment(Pos.CENTER_LEFT);
        container.getChildren().add(entry);
    }
}
