/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.gantt;

import com.flexganttfx.demo.FlexGanttFXSampleBase;
import com.flexganttfx.demo.HelloActivity;
import com.flexganttfx.demo.HelloRow;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.VBoxGraphics;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.timeline.Timeline;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class HelloRowCanvas extends FlexGanttFXSampleBase {


    @Override
    public Node getPanel(Stage stage) {
        Timeline timeline = new Timeline();

        VBoxGraphics vboxGraphics = new VBoxGraphics<>();
        vboxGraphics.setStyle("-fx-border-color: red;");
        vboxGraphics.setDebugMode(false);
        vboxGraphics.setTimeline(timeline);
        vboxGraphics.setActivityRenderer(HelloActivity.class, GanttLayout.class, new ActivityBarRenderer<>(vboxGraphics, "HelloActivityRenderer"));
        vboxGraphics.getLayers().add(HelloRow.layer);

        List<HelloRow> rows = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HelloRow row = new HelloRow("Row " + i, 10);
            rows.add(row);
        }

        vboxGraphics.getRows().setAll(rows);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(timeline);
        borderPane.setCenter(vboxGraphics);
        borderPane.setStyle("-fx-background-color: gray;");

        StackPane stackPane = new StackPane(borderPane);
        stackPane.setStyle("-fx-padding: 250px; -fx-background-color: white;");

        return stackPane;
    }

    @Override
    public String getSampleDescription() {
        return "This sample illustrates how the canvas buffer works.";
    }

    @Override
    public String getSampleName() {
        return "Canvas Buffer";
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
