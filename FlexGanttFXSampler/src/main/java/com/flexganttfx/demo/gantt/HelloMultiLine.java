/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.gantt;

import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.demo.HelloActivity;
import com.flexganttfx.demo.HelloRow;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Layout;
import com.flexganttfx.model.LinesManager;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.layout.EqualLinesManager;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.ListViewGraphics;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.util.AutoLinesManager;
import com.flexganttfx.view.util.Position;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class HelloMultiLine extends FlexGanttFXSample {

    private HelloRow row;

    private EqualLinesManager<HelloRow, HelloActivity> equalLinesManager;
    private AutoLinesManager<HelloRow, HelloActivity> autoLinesManager;
    private RandomLinesManager randomLinesManager;

    private Layer layer;

    private Slider slider;

    private RadioButton equalButton;

    private RadioButton autoButton;

    private RadioButton randomButton;

    @Override
    public String getSampleName() {
        return "Multi Line";
    }

    @Override
    public String getSampleDescription() {
        return "This sample demonstrates how activities can be placed on multiple lines "
                + "within the same row. Different line managers can be used to place the "
                + "activities with different strategies.";
    }

    @Override
    protected GanttChart<?> createGanttChart() throws Exception {
        GanttChart<HelloRow> gc = new GanttChart<>();

        // the layers
        List<Layer> layers = new ArrayList<>();
        layers.add(layer = new Layer("Layer 1"));
        gc.getLayers().setAll(layers);

        // the row
        row = new HelloRow("Row");
        row.setHeight(400);
        row.setMaxHeight(2000);
        row.setMinHeight(30);

        // the line managers
        autoLinesManager = new AutoLinesManager<>(row, gc.getGraphics());
        equalLinesManager = new MyEqualLinesManager(row);
        randomLinesManager = new RandomLinesManager(row);
        row.setLinesManager(autoLinesManager);

        gc.setRoot(row);

        ListViewGraphics<HelloRow> graphics = gc.getGraphics();
        graphics.setAutoGridEnabled(true);
        graphics.setActivityRenderer(HelloActivity.class, GanttLayout.class, new HelloActivityRenderer(graphics, "Hello Activity Renderer"));
        graphics.setOnActivityChangeFinished(evt -> maybePerformLayout());
        graphics.setOnActivityDeleted(evt -> maybePerformLayout());

        applyLineCount(25);

        return gc;
    }

    private void maybePerformLayout() {
        if (autoButton == null || autoButton.isSelected()) {
            autoLinesManager.layout();
        }
    }

    @Override
    public Node getControlPanel() {
        VBox box = new VBox();
        box.setSpacing(10);
        box.setFillWidth(true);

        equalButton = new RadioButton("Equal Lines");
        autoButton = new RadioButton("Equal Lines (Auto Layout)");
        randomButton = new RadioButton("Random Lines");

        equalButton.setTooltip(new Tooltip("Distribute available row height equally to all lines"));
        autoButton.setTooltip(new Tooltip("Equal line height, activities non overlapping"));
        randomButton.setTooltip(new Tooltip("Randomly place lines and allocate line height"));

        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(equalButton, autoButton, randomButton);
        toggleGroup.selectedToggleProperty().addListener(it -> applyLineCount(row.getLineCount()));
        toggleGroup.selectedToggleProperty().addListener(it -> getGanttChart().getGraphics().showEarliestActivities());

        equalButton.setOnAction(evt -> applyEqualLinesManager());
        autoButton.setOnAction(evt -> applyAutoLinesManager());
        randomButton.setOnAction(evt -> applyRandomLinesManager());

        Label managerLabel = new Label("Manager");
        managerLabel.setMaxWidth(Double.MAX_VALUE);
        managerLabel.setAlignment(Pos.CENTER);
        managerLabel.setStyle("-fx-font-weight: bold");

        Label sliderLabel = new Label("Number of Lines");
        sliderLabel.setMaxWidth(Double.MAX_VALUE);
        sliderLabel.setAlignment(Pos.CENTER);
        sliderLabel.setStyle("-fx-font-weight: bold");

        slider = new Slider(1, 100, 25);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setOrientation(Orientation.VERTICAL);
        slider.setPrefHeight(400);
        slider.setMaxWidth(Double.MAX_VALUE);
        slider.valueProperty().addListener(it -> applyLineCount((int) slider.getValue()));

        Button apply = new Button("Apply");
        apply.setMaxWidth(Double.MAX_VALUE);
        apply.setOnAction(evt -> applyLineCount((int) slider.getValue()));

        box.getChildren().addAll(managerLabel, equalButton, autoButton,
                randomButton, new Separator(Orientation.HORIZONTAL),
                sliderLabel, slider, apply);

        Platform.runLater(() -> equalButton.fire());

        return box;
    }

    private void applyLineCount(int count) {
        row.setLineCount(count);

        LocalDate date = LocalDate.now();

        row.clearActivities();

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < Math.random() * row.getLineCount() / 2; j++) {
                int duration = Math.max(1, (int) (Math.random() * 10));

                LocalTime time = LocalTime.MIN;

                Instant st = ZonedDateTime.of(date, time, ZoneId.systemDefault()).toInstant();
                Instant et = ZonedDateTime.of(date.plusDays(duration), time, ZoneId.systemDefault()).toInstant();

                HelloActivity activity = new HelloActivity();
                activity.setColor(randomColor());
                activity.setStartTime(st);
                activity.setEndTime(et);
                activity.setLineIndex((int) (Math.random() * row.getLineCount()));

                date = date.plusDays(Math.max(1, (int) (Math.random() * 3)));

                row.addActivity(layer, activity);
            }
        }

        if (autoButton == null || autoButton.isSelected()) {
            maybePerformLayout();
        }
    }

    private Color randomColor() {
        switch ((int) (Math.random() * 6)) {
            case 0:
                return Color.LIGHTBLUE;
            case 1:
                return Color.LIGHTCYAN;
            case 2:
                return Color.LIGHTCORAL;
            case 3:
                return Color.LIGHTGOLDENRODYELLOW;
            case 4:
                return Color.LIGHTSALMON;
            case 5:
                return Color.LIGHTSEAGREEN;
            case 6:
                return Color.LIGHTSKYBLUE;
            case 7:
                return Color.LIGHTSTEELBLUE;
            case 8:
                return Color.LIGHTYELLOW;
            default:
                return Color.LIGHTGRAY;
        }
    }

    private void applyRandomLinesManager() {
        row.setLinesManager(randomLinesManager);
    }

    private void applyEqualLinesManager() {
        row.setLinesManager(equalLinesManager);
    }

    private void applyAutoLinesManager() {
        row.setLinesManager(autoLinesManager);
    }

    class MyEqualLinesManager extends
            EqualLinesManager<HelloRow, HelloActivity> {

        public MyEqualLinesManager(HelloRow row) {
            super(row);
        }

        @Override
        public int getLineIndex(HelloActivity activity) {
            return activity.getLineIndex();
        }
    }

    class RandomLinesManager implements LinesManager<HelloActivity> {

        private HelloRow row;

        private double[] locations;
        private double[] heights;

        private GanttLayout layout;

        public RandomLinesManager(HelloRow row) {
            this.row = row;
            this.layout = new GanttLayout();

            row.lineCountProperty().addListener(it -> update());
        }

        private void update() {
            int count = row.getLineCount();

            heights = new double[count];
            locations = new double[count];

            for (int i = 0; i < count; i++) {
                heights[i] = Math.random() * row.getHeight() / 2;
                locations[i] = Math.min(row.getHeight() - heights[i],
                        Math.random() * row.getHeight());
            }
        }

        @Override
        public int getLineIndex(HelloActivity activity) {
            return activity.getLineIndex();
        }

        @Override
        public double getLineLocation(int lineIndex, double rowHeight) {
            return locations[lineIndex];
        }

        @Override
        public double getLineHeight(int lineIndex, double rowHeight) {
            return heights[lineIndex];
        }

        @Override
        public Layout getLineLayout(int lineIndex) {
            return layout;
        }
    }

    class HelloActivityRenderer extends ActivityBarRenderer<HelloActivity> {

        public HelloActivityRenderer(GraphicsBase<?> graphics, String name) {
            super(graphics, name);
            setCornersRounded(false);
            setBarHeight(Row.DEFAULT_ROW_HEIGHT - 4);
        }

        @Override
        protected ActivityBounds drawActivity(
                ActivityRef<HelloActivity> activityRef, Position position,
                GraphicsContext gc, double x, double y, double w, double h,
                boolean selected, boolean hover, boolean highlighted,
                boolean pressed) {

            HelloActivity activity = activityRef.getActivity();

            /*
             * We are customing the renderer based on the color returned by the
             * activity. This is just one way of coloring activities
             * differently.
             */
            setFill(activity.getColor().darker());
            setStroke(activity.getColor().darker().darker());

            /*
             * We want to use a different bar height depending on the height of
             * the line where the activity is shown. This way we will end up
             * with very large bars on large lines and small ones on small
             * lines. This will only be visible in the randome lines manager use
             * case.
             */
            Row<?, ?, HelloActivity> row = activityRef.getRow();
            LinesManager<HelloActivity> manager = row.getLinesManager();
            int lineIndex = manager.getLineIndex(activity);
            if (lineIndex != -1) {
                double lineHeight = manager.getLineHeight(lineIndex,
                        row.getHeight());
                setBarHeight(lineHeight * .8);
            } else {
                setBarHeight(16);
            }

            return super.drawActivity(activityRef, position, gc, x, y, w, h,
                    selected, hover, highlighted, pressed);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
