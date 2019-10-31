/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.container;

import com.flexganttfx.demo.FlexGanttFXSampleBase;
import com.flexganttfx.view.container.QuadGanttChartContainer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.PropertySheet;

public class HelloQuadGanttChartContainer extends FlexGanttFXSampleBase {

    private QuadGanttChartContainer quad;

    @Override
    public String getSampleName() {
        return "Quad";
    }

    @Override
    public Node getPanel(Stage panel) {
        quad = new QuadGanttChartContainer();

        return quad;
    }

    @Override
    public Node getControlPanel() {
        VBox vbox = new VBox(10);
        vbox.setFillWidth(true);

        Button single = new Button("Single");
        single.setMaxWidth(Double.MAX_VALUE);
        single.setOnAction(evt -> quad.showSingleChart());
        vbox.getChildren().add(single);

        Button horizontalSplit = new Button("Horiz. Split");
        horizontalSplit.setMaxWidth(Double.MAX_VALUE);
        horizontalSplit.setOnAction(evt -> quad.showHorizontalSplitScreen(true));
        vbox.getChildren().add(horizontalSplit);

        Button verticalSplit = new Button("Vert. Split");
        verticalSplit.setMaxWidth(Double.MAX_VALUE);
        verticalSplit.setOnAction(evt -> quad.showVerticalSplitScreen(true));
        vbox.getChildren().add(verticalSplit);

        Button allFour = new Button("All Four");
        allFour.setMaxWidth(Double.MAX_VALUE);
        allFour.setOnAction(evt -> quad.showAllFour(true));
        vbox.getChildren().add(allFour);

        PropertySheet propertySheet = new PropertySheet(FXCollections.observableArrayList(quad
                .getPropertySheetItems()));
        VBox.setVgrow(propertySheet, Priority.ALWAYS);

        vbox.getChildren().add(propertySheet);

        return vbox;
    }

    @Override
    public String getSampleDescription() {
        return "A special multi Gantt chart container that is capable of displaying "
                + "exactly four Gantt charts and keeping their layouts (same "
                + "table width, same timeline) and their scrolling and zooming behavior in "
                + "synch.";
    }

    @Override
    public String getJavaDocURL() {
        return getJavaDocBase() + "com/flexganttfx/view/container/QuadGanttChartContainer.html";
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}