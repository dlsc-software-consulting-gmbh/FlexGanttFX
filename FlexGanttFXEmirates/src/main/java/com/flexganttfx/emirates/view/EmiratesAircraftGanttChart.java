/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.emirates.view;

import com.flexganttfx.emirates.model.Flight;
import com.flexganttfx.emirates.model.Group;
import com.flexganttfx.emirates.model.ModelObject;
import com.flexganttfx.extras.RowControls;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.view.GanttChartLite;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.GraphicsBase.RowHeader;
import com.flexganttfx.view.graphics.ListViewGraphics;
import com.flexganttfx.view.timeline.Timeline;
import com.jpro.webapi.WebAPI;
import impl.com.flexganttfx.skin.graphics.DragCanvas;
import impl.com.flexganttfx.skin.graphics.GraphicsBaseSkin;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

import java.time.temporal.ChronoUnit;

public class EmiratesAircraftGanttChart extends GanttChartLite<ModelObject<?, ?, ?>> {

    public EmiratesAircraftGanttChart() {
        setAutoHideScrollBar(false);

        Timeline timeline = getTimeline();
        timeline.showTemporalUnit(ChronoUnit.HOURS, 50);
        timeline.setMoveAnimated(!WebAPI.isBrowser());
        timeline.setZoomAnimated(!WebAPI.isBrowser());
        timeline.getEventline().setRowHeaderTitle("Aircraft");

        ListViewGraphics<ModelObject<?, ?, ?>> graphics = getGraphics();

        graphics.setShowVerticalCursor(true);
        graphics.setShowRowHeaders(true);
        graphics.setRowHeadersWidth(80);
        graphics.getBackgroundSystemLayers().add(new GroupSystemLayer(graphics));
        graphics.setActivityRenderer(Flight.class, GanttLayout.class, new FlightRenderer(graphics));
        graphics.setRowHeaderFactory(g -> new RowHeader<>(graphics) {
            {
                setAlignment(Pos.CENTER);
                itemProperty().addListener(it -> {
                    final ModelObject<?, ?, ?> item = getItem();
                    if (item != null && !(item instanceof Group)) {
                        setText(item.getName());
                    } else {
                        setText("");
                    }
                });
            }
        });

        graphics.setEditModeCallback(Flight.class, GanttLayout.class, param -> GraphicsBase.EditMode.DRAGGING_VERTICAL);
        graphics.setActivityEditingCallback(Flight.class, param -> param.getEditMode().equals(GraphicsBase.EditMode.DRAGGING_VERTICAL));

        graphics.skinProperty().addListener(it -> {
            GraphicsBaseSkin skin = (GraphicsBaseSkin) graphics.getSkin();
            DragCanvas canvas = skin.getDragCanvas();
            canvas.setIncludeSelectedActivitiesInDrag(true);
        });


        if (!WebAPI.isBrowser()) {
            final Image a332 = new Image(EmiratesAircraftGanttChart.class.getResource("aircraft-332.jpg").toExternalForm());
            final Image a343 = new Image(EmiratesAircraftGanttChart.class.getResource("aircraft-343.jpg").toExternalForm());
            final Image a380 = new Image(EmiratesAircraftGanttChart.class.getResource("aircraft-380.jpg").toExternalForm());
            final Image a772 = new Image(EmiratesAircraftGanttChart.class.getResource("aircraft-772.jpg").toExternalForm());
            final Image a777 = new Image(EmiratesAircraftGanttChart.class.getResource("aircraft-777.jpg").toExternalForm());

            graphics.setAnimateRowEditor(!WebAPI.isBrowser());
            graphics.setRowEditorFactory(param -> {
                GridPane pane = new GridPane();

                RowConstraints row1 = new RowConstraints();
                RowConstraints row2 = new RowConstraints();
                RowConstraints row3 = new RowConstraints();
                RowConstraints row4 = new RowConstraints();

                row1.setVgrow(Priority.ALWAYS);
                row2.setVgrow(Priority.ALWAYS);
                row3.setVgrow(Priority.ALWAYS);
                row4.setVgrow(Priority.ALWAYS);

                pane.getRowConstraints().setAll(row1, row2, row3, row4);

                ColumnConstraints col1 = new ColumnConstraints();
                ColumnConstraints col2 = new ColumnConstraints();
                ColumnConstraints col3 = new ColumnConstraints();
                ColumnConstraints col4 = new ColumnConstraints();
                ColumnConstraints col5 = new ColumnConstraints();

                col1.setHgrow(Priority.NEVER);
                col2.setHgrow(Priority.NEVER);
                col3.setHgrow(Priority.ALWAYS);
                col4.setHgrow(Priority.NEVER);
                col5.setHgrow(Priority.ALWAYS);

                pane.getColumnConstraints().setAll(col1, col2, col3, col4, col5);

                pane.setHgap(10);
                pane.setVgap(10);
                pane.setGridLinesVisible(true);
                pane.setStyle("-fx-background-color: aliceblue;");
                pane.setAlignment(Pos.CENTER_LEFT);

                String name = param.getRow().getName();

                ImageView imageView = new ImageView();
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);

                if (name.startsWith("332")) {
                    imageView.setImage(a332);
                } else if (name.startsWith("34") || name.startsWith("34")) {
                    imageView.setImage(a343);
                } else if (name.startsWith("38")) {
                    imageView.setImage(a380);
                } else if (name.startsWith("772") || name.startsWith("773")) {
                    imageView.setImage(a772);
                } else if (name.startsWith("77")) {
                    imageView.setImage(a777);
                }

                StackPane imageWrapper = new StackPane(imageView);
                GridPane.setMargin(imageWrapper, new Insets(20));

                Label nameKey = new Label("Aircraft name");
                Label numberKey = new Label("Number of flights:");
                Label typeKey = new Label("Aircraft type:");

                Label nameValue = new Label(param.getRow().getName());
                Label numberValue = new Label(Integer.toString(param.getRow().getFlights()));
                Label typeValue = new Label("Unknown Type");

                Label label1 = new Label("Key 1");
                Label label2 = new Label("Key 2");
                Label label3 = new Label("Key 3");
                Label label4 = new Label("Key 4");

                Label value1 = new Label("Value 1");
                Label value2 = new Label("Value 2");
                Label value3 = new Label("Value 3");
                Label value4 = new Label("Value 4");

                GridPane.setRowSpan(imageWrapper, 4);

                // column 1
                pane.add(imageWrapper, 0, 0);

                // column 2 + 3
                pane.add(nameKey, 1, 0);
                pane.add(numberKey, 1, 1);
                pane.add(typeKey, 1, 2);

                pane.add(nameValue, 2, 0);
                pane.add(numberValue, 2, 1);
                pane.add(typeValue, 2, 2);

                // column 4 + 5
                pane.add(label1, 3, 0);
                pane.add(label2, 3, 1);
                pane.add(label3, 3, 2);
                pane.add(label4, 3, 3);

                pane.add(value1, 4, 0);
                pane.add(value2, 4, 1);
                pane.add(value3, 4, 2);
                pane.add(value4, 4, 3);

                pane.setPrefHeight(200);
                return pane;
            });

            graphics.setRowControlsFactory(param -> new RowControls<>(param.getGraphics(), param.getRow()));
        }
    }
}
