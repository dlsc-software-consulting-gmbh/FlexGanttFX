/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.container;

import com.flexganttfx.demo.FlexGanttFXSampleBase;
import com.flexganttfx.demo.HelloRow;
import com.flexganttfx.extras.properties.QuadGanttChartContainerBaseItemProvider;
import com.flexganttfx.view.GanttChartLite;
import com.flexganttfx.view.container.QuadGanttChartLiteContainer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.PropertySheet;

public class HelloQuadGanttChartLiteContainer extends FlexGanttFXSampleBase {

    private QuadGanttChartLiteContainer quad;

    @Override
    public String getSampleName() {
        return "Quad Lite";
    }

    @Override
    public Node getPanel(Stage panel) {
        GanttChartLite<HelloRow> chart1 = new GanttChartLite<>();
        GanttChartLite<HelloRow> chart2 = new GanttChartLite<>();
        GanttChartLite<HelloRow> chart3 = new GanttChartLite<>();
        GanttChartLite<HelloRow> chart4 = new GanttChartLite<>();

        chart1.getRows().add(new HelloRow("Row"));
        chart2.getRows().add(new HelloRow("Row"));
        chart3.getRows().add(new HelloRow("Row"));
        chart4.getRows().add(new HelloRow("Row"));

        quad = new QuadGanttChartLiteContainer(chart1, chart2, chart3, chart4);

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
        verticalSplit.setOnAction(evt ->quad.showVerticalSplitScreen(true));
        vbox.getChildren().add(verticalSplit);

        Button allFour = new Button("All Four");
        allFour.setMaxWidth(Double.MAX_VALUE);
        allFour.setOnAction(evt -> quad.showAllFour(true));
        vbox.getChildren().add(allFour);

        Button replaceUpperLeft = new Button("Replace UL");
        replaceUpperLeft.setMaxWidth(Double.MAX_VALUE);
        replaceUpperLeft.setOnAction(evt -> replace(Corner.UPPER_LEFT));
        vbox.getChildren().add(replaceUpperLeft);

        Button replaceUpperRight = new Button("Replace UR");
        replaceUpperRight.setMaxWidth(Double.MAX_VALUE);
        replaceUpperRight.setOnAction(evt -> replace(Corner.UPPER_RIGHT));
        vbox.getChildren().add(replaceUpperRight);

        Button replaceLowerLeft = new Button("Replace LL");
        replaceLowerLeft.setMaxWidth(Double.MAX_VALUE);
        replaceLowerLeft.setOnAction(evt -> replace(Corner.LOWER_LEFT));
        vbox.getChildren().add(replaceLowerLeft);

        Button replaceLowerRight = new Button("Replace LR");
        replaceLowerRight.setMaxWidth(Double.MAX_VALUE);
        replaceLowerRight.setOnAction(evt -> replace(Corner.LOWER_RIGHT));
        vbox.getChildren().add(replaceLowerRight);

        QuadGanttChartContainerBaseItemProvider provider = new QuadGanttChartContainerBaseItemProvider();
        PropertySheet propertySheet = new PropertySheet(FXCollections.observableArrayList(provider.getPropertySheetItems(quad)));

        VBox.setVgrow(propertySheet, Priority.ALWAYS);

        vbox.getChildren().add(propertySheet);

        return vbox;
    }

    private void replace(Corner corner) {
        GanttChartLite<HelloRow> replacement = new GanttChartLite<>();
        replacement.getRows().add(new HelloRow("Row"));

        switch (corner) {
            case UPPER_LEFT:
                quad.setUpperLeftGanttChart(replacement);
                break;
            case UPPER_RIGHT:
                quad.setUpperRightGanttChart(replacement);
                break;
            case LOWER_LEFT:
                quad.setLowerLeftGanttChart(replacement);
                break;
            case LOWER_RIGHT:
                quad.setLowerRightGanttChart(replacement);
                break;
        }
    }

    private enum Corner {
        UPPER_LEFT,
        UPPER_RIGHT,
        LOWER_LEFT,
        LOWER_RIGHT
    }

    @Override
    public String getSampleDescription() {
        return "A special multi Gantt chart container that is capable of displaying "
                + "exactly four Gantt charts and keeping their layouts (same "
                + "table width, same timeline) and their scrolling and zooming behavior in "
                + "synch.";
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}