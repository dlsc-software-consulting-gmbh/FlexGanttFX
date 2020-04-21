/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 * <p>
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.emirates;

import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.emirates.model.DataModel;
import com.flexganttfx.emirates.model.DataModel.DataSet;
import com.flexganttfx.emirates.view.EmiratesAircraftGanttChart;
import com.flexganttfx.emirates.view.EmiratesToolBar;
import com.flexganttfx.emirates.view.GlassPane;
import com.flexganttfx.emirates.view.IntroPane;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;

public class EmiratesApp extends Application {

    private EmiratesAircraftGanttChart gantt;
    private Stage stage;
    private Node ganttNode;
    private Node introNode;
    private IntroPane introPane;
    private GlassPane glassPane;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        if (!FlexGanttFX.isLicenseKeySet()) {
            FlexGanttFX.setLicenseKey("LIC=CO_FLEXFX_998;VEN=ComponentSource;VER=STANDARD;PRO=11_11;RUN=no;CTR=0;SignCode=3F;Signature=302C0214449DB4F9A907EB223632AD6C99569CEAA48E0E4E021419258FED88B71303A9321E250A1A53ABAD140DFE");
        }

        this.stage = stage;
        this.stage.setTitle("Emirates Aircraft Scheduling");

        StackPane stack = new StackPane();
        stack.getChildren().add(ganttNode = createGanttChart());
        stack.getChildren().add(introNode = createIntro());
        stack.getChildren().add(glassPane = new GlassPane());

        Scene scene = new Scene(stack);
        scene.getStylesheets().add(EmiratesApp.class.getResource("emirates.css").toExternalForm());

        stage.setScene(scene);
        stage.setWidth(1400);
        stage.setHeight(900);
        stage.centerOnScreen();
        stage.show();
    }

    public void load(final DataSet dataSet) {
        gantt.getRows().clear();
        gantt.getLayers().clear();

        glassPane.toFront();
        glassPane.setProgress(0);

        gantt.getTimeline().showTemporalUnit(ChronoUnit.DAYS, 25);

        final Task<DataModel> task = new Task<>() {
            @Override
            protected DataModel call() throws Exception {
                return new DataModel(dataSet, glassPane.progressProperty());
            }
        };

        task.setOnSucceeded(succeededEvent -> Platform.runLater(() -> {
            try {
                DataModel model = task.get();
                gantt.getRows().setAll(model.getRows());
                gantt.getLayers().setAll(model.getLayers());
                Platform.runLater(() -> {
                    introNode.toBack();
                    gantt.getGraphics().showEarliestActivities();
                    glassPane.setProgress(1);
                });
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }));

        Thread thread = new Thread(task);
        thread.start();
    }

    private Region createIntro() {
        HBox placeholder = new HBox();
        placeholder.getStyleClass().add("cover");
        introPane = new IntroPane(this);
        HBox.setMargin(introPane, new Insets(0, 0, 40, 40));
        placeholder.getChildren().add(introPane);
        return placeholder;
    }

    private Region createGanttChart() {
        gantt = new EmiratesAircraftGanttChart();
        gantt.setPrefSize(1300, 1100);
        VBox.setVgrow(gantt, Priority.ALWAYS);

        VBox vbox = new VBox(0);

        MenuBar menuBar = createMenuBar();
        vbox.getChildren().add(menuBar);

        EmiratesToolBar<?> toolBar = new EmiratesToolBar<>(gantt);
        vbox.getChildren().add(toolBar);
        vbox.getChildren().add(gantt);
        return vbox;
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");

        Menu bufferMenu = new Menu("Canvas Buffer");

        MenuItem bufferOff = new MenuItem("Off");
        bufferOff.setOnAction(evt -> gantt.getGraphics().setCanvasBuffer(0));
        MenuItem buffer100 = new MenuItem("100 Pixel");
        buffer100.setOnAction(evt -> gantt.getGraphics().setCanvasBuffer(100));
        MenuItem buffer200 = new MenuItem("200 Pixel");
        buffer200.setOnAction(evt -> gantt.getGraphics().setCanvasBuffer(200));
        MenuItem buffer500 = new MenuItem("500 Pixel");
        buffer500.setOnAction(evt -> gantt.getGraphics().setCanvasBuffer(500));
        bufferMenu.getItems().setAll(bufferOff, buffer100, buffer200, buffer500);
        fileMenu.getItems().add(bufferMenu);

        Menu loadMenu = new Menu("Load");
        for (final DataSet data : DataSet.values()) {
            MenuItem openDataSetItem = new MenuItem(data.getDisplayName());
            openDataSetItem.setOnAction(event -> Platform.runLater(() -> load(data)));
            loadMenu.getItems().add(openDataSetItem);
        }

        fileMenu.getItems().add(loadMenu);

        MenuItem redraw = new MenuItem("Redraw");
        redraw.setOnAction(evt -> {
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
                if (i % 1000 == 0) {
                    System.out.println("repaints done: " + i);
                }
                gantt.getGraphics().redraw();
            }
            System.out.println("time in ms: " + (System.currentTimeMillis() - startTime));
        });

        fileMenu.getItems().add(redraw);

        CheckMenuItem safeItem = new CheckMenuItem("Safe Rendering");
        safeItem.selectedProperty().bindBidirectional(gantt.getGraphics().safeRenderingProperty());
        fileMenu.getItems().add(safeItem);

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(evt -> introNode.toFront());
        fileMenu.getItems().add(exit);

        menuBar.getMenus().add(fileMenu);

        return menuBar;
    }
}
