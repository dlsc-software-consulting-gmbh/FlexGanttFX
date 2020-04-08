/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.gantt;

import com.flexganttfx.demo.FlexGanttFXSampleBase;
import com.flexganttfx.demo.HelloActivity;
import com.flexganttfx.demo.HelloRow;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.ListViewGraphics;
import com.flexganttfx.view.graphics.SingleRowGraphics;
import com.flexganttfx.view.graphics.SplitPaneGraphics;
import com.flexganttfx.view.graphics.VBoxGraphics;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.timeline.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.MasterDetailPane;

import java.util.ArrayList;
import java.util.List;

public class HelloGraphicsView extends FlexGanttFXSampleBase {

    enum PriorityStrategy {
        ALL, NONE, RANDOM
    }

    private static final boolean DEBUG_MODE = false;

    private Region singleRowNode;
    private Region listViewNode;
    private Region vBoxNode;
    private Region splitPaneNode;

    private TextArea textArea;

    @Override
    public Node getControlPanel() {
        RadioButton singleRowButton = new RadioButton("Single Row");
        RadioButton listViewButton = new RadioButton("ListView");
        RadioButton vBoxButton = new RadioButton("VBox");
        RadioButton splitPaneButton = new RadioButton("SplitPane");

        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(singleRowButton, listViewButton, vBoxButton, splitPaneButton);
        singleRowButton.setSelected(true);

        ComboBox<PriorityStrategy> prioBox = new ComboBox<>();
        prioBox.getItems().setAll(PriorityStrategy.values());
        prioBox.setValue(PriorityStrategy.ALL);
        prioBox.setOnAction(evt -> updatePriorityStrategy(prioBox.getValue()));
        prioBox.disableProperty().bind(Bindings.not(vBoxButton.selectedProperty()));

        BorderPane priBoxPane = new BorderPane();
        priBoxPane.setCenter(prioBox);
        Label label = new Label("VBox Grow Priority:");
        BorderPane.setMargin(label, new Insets(0, 6, 0, 0));
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        BorderPane.setAlignment(prioBox, Pos.CENTER_RIGHT);
        priBoxPane.setLeft(label);

        Slider heightSlider = new Slider(0, 1000, Row.DEFAULT_ROW_HEIGHT);
        heightSlider.valueProperty().addListener(evt -> updateRowHeight(heightSlider.getValue()));
        heightSlider.disableProperty().bind(Bindings.not(singleRowButton.selectedProperty()));

        BorderPane sliderPane = new BorderPane();
        sliderPane.setCenter(heightSlider);
        sliderPane.setLeft(new Label("Row Height:"));

        singleRowButton.setOnAction(evt -> singleRowNode.toFront());
        listViewButton.setOnAction(evt -> listViewNode.toFront());
        vBoxButton.setOnAction(evt -> vBoxNode.toFront());
        splitPaneButton.setOnAction(evt -> splitPaneNode.toFront());

        VBox.setMargin(sliderPane, new Insets(0, 0, 0, 40));
        VBox.setMargin(priBoxPane, new Insets(0, 0, 0, 40));

        VBox controlPanel = new VBox();
        controlPanel.setFillWidth(false);
        controlPanel.getChildren().addAll(singleRowButton, sliderPane, listViewButton, vBoxButton, priBoxPane, splitPaneButton);
        controlPanel.setSpacing(10);

        return controlPanel;
    }

    private void updateRowHeight(double value) {
        singleRow.setHeight(value);
    }

    private void updatePriorityStrategy(PriorityStrategy value) {
        switch (value) {
            case ALL:
                vboxGraphics
                        .setPriorityCallback(param -> Priority.ALWAYS);
                break;
            case NONE:
                vboxGraphics
                        .setPriorityCallback(param -> Priority.NEVER);
                break;
            case RANDOM:
                vboxGraphics
                        .setPriorityCallback(param -> {
                            if (Math.random() < .5) {
                                return Priority.NEVER;
                            }
                            return Priority.ALWAYS;
                        });
                break;
            default:
                break;
        }
    }

    @Override
    public Node getPanel(Stage stage) {
        MasterDetailPane masterDetailPane = new MasterDetailPane(Side.BOTTOM);

        StackPane stackPane = new StackPane();
        singleRowNode = createGraphicsSingleRowView();
        listViewNode = createGraphicsListView();
        vBoxNode = createGraphicsVBox();
        splitPaneNode = createGraphicsSplitPane();

        stackPane.getChildren().addAll(listViewNode, vBoxNode, splitPaneNode, singleRowNode);
        masterDetailPane.setMasterNode(stackPane);

        textArea = new TextArea();
        textArea.setWrapText(true);

        masterDetailPane.setDetailNode(textArea);

        return masterDetailPane;
    }

    private void info(Object object) {
        textArea.appendText("Incoming: " + object.getClass().getSimpleName());
        textArea.appendText(System.getProperty("line.separator"));
        textArea.appendText(System.getProperty("line.separator"));
        textArea.appendText(object.toString());
        textArea.appendText(System.getProperty("line.separator"));
        textArea.appendText("------------------");
        textArea.appendText(System.getProperty("line.separator"));
    }

    private HelloRow singleRow;

    private Region createGraphicsSingleRowView() {
        Timeline timeline = new Timeline();

        SingleRowGraphics<HelloRow> graphics = new SingleRowGraphics<>();
        graphics.setTimeline(timeline);
        graphics.setOnLassoSelection(evt -> info(evt));
        graphics.setOnActivityChange(evt -> info(evt));
        graphics.setActivityRenderer(HelloActivity.class, GanttLayout.class, new ActivityBarRenderer<>(graphics, "HelloActivityRenderer"));
        graphics.getLayers().add(HelloRow.layer);
        graphics.setDebugMode(DEBUG_MODE);

        singleRow = new HelloRow("Row", 100);
        graphics.getRows().setAll(singleRow);

        VBox box = new VBox();
        box.getChildren().add(timeline);
        box.getChildren().add(graphics);
        box.setStyle("-fx-background-color: gray;");

        return box;
    }

    private Region createGraphicsListView() {
        Timeline timeline = new Timeline();

        ListViewGraphics<HelloRow> graphics = new ListViewGraphics<>();
        graphics.setTimeline(timeline);
        graphics.setDebugMode(DEBUG_MODE);
        graphics.setActivityRenderer(HelloActivity.class, GanttLayout.class, new ActivityBarRenderer<>(graphics, "HelloActivityRenderer"));
        graphics.getLayers().add(HelloRow.layer);
        graphics.setOnLassoSelection(evt -> info(evt));
        graphics.setOnActivityChange(evt -> info(evt));

        List<HelloRow> rows = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            HelloRow row = new HelloRow("Row " + i, 10);
            rows.add(row);
        }

        graphics.getRows().setAll(rows);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(timeline);
        borderPane.setCenter(graphics);
        borderPane.setStyle("-fx-background-color: gray;");

        return borderPane;
    }

    private VBoxGraphics<HelloRow> vboxGraphics;

    private Region createGraphicsVBox() {
        Timeline timeline = new Timeline();

        vboxGraphics = new VBoxGraphics<>();
        vboxGraphics.setDebugMode(DEBUG_MODE);
        vboxGraphics.setOnLassoSelection(evt -> textArea.appendText(evt.toString()));
        vboxGraphics.setOnActivityChange(evt -> info(evt));

        vboxGraphics.setTimeline(timeline);
        vboxGraphics.setActivityRenderer(HelloActivity.class, GanttLayout.class, new ActivityBarRenderer<>(vboxGraphics, "HelloActivityRenderer"));
        vboxGraphics.getLayers().add(HelloRow.layer);

        List<HelloRow> rows = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            HelloRow row = new HelloRow("Row " + i, 10);
            rows.add(row);
        }

        vboxGraphics.getRows().setAll(rows);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(timeline);
        borderPane.setCenter(vboxGraphics);
        borderPane.setStyle("-fx-background-color: gray;");

        return borderPane;
    }

    private Region createGraphicsSplitPane() {
        Timeline timeline = new Timeline();

        SplitPaneGraphics<HelloRow> graphics = new SplitPaneGraphics<>();
        graphics.getSplitPane().setDividerPositions(.2, .4, .6, .8);
        graphics.setOnLassoSelection(evt -> info(evt));
        graphics.setOnActivityChange(evt -> info(evt));
        graphics.setDebugMode(DEBUG_MODE);
        graphics.setTimeline(timeline);
        graphics.setActivityRenderer(HelloActivity.class, GanttLayout.class, new ActivityBarRenderer<>(graphics, "HelloActivityRenderer"));
        graphics.getLayers().add(HelloRow.layer);

        List<HelloRow> rows = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            HelloRow row = new HelloRow("Row " + i, 10);
            rows.add(row);
        }

        graphics.getRows().setAll(rows);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(timeline);
        borderPane.setCenter(graphics);
        borderPane.setStyle("-fx-background-color: gray;");

        return borderPane;
    }

    @Override
    public String getSampleDescription() {
        return "The GraphicsView is the control responsible for the graphical display of activities. This view "
                + "is used as a sub-control of the GanttChart class but can also be used standalone. Four different "
                + "graphic view types are available: single row, list, box, split pane.";
    }

    @Override
    public String getSampleName() {
        return "Graphics View";
    }

    @Override
    public String getControlStylesheetURL() {
        return "/" + GraphicsBase.class.getPackage().getName().replace('.', '/') + "/graphics.css";
    }

    @Override
    public String getJavaDocURL() {
        return getJavaDocBase() + "com/flexganttfx/view/graphics/GraphicsBase.html";
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
