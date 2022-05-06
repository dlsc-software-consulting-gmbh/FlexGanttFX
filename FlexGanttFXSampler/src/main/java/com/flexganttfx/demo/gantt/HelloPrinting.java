/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.gantt;
/*
import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.msproject.MSProjectApp;
import com.flexganttfx.msproject.view.MSProjectGanttChart;
import com.flexganttfx.view.GanttChart;
import javafx.application.Application;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class HelloPrinting extends FlexGanttFXSample {

    private MSProjectGanttChart gc;

    @Override
    protected GanttChart<?> createGanttChart() throws FileNotFoundException {
        gc = new MSProjectGanttChart();
        gc.load("com/flexganttfx/msproject/files/n0741.mpp", MSProjectApp.class.getResourceAsStream("/com/flexganttfx/msproject/files/n0741.mpp"));
        return gc;
    }

    @Override
    public void dispose() {
        super.dispose();
        gc = null;
    }

    @Override
    public String getSampleName() {
        return "Printing";
    }

    @Override
    public String getSampleDescription() {
        return "A simple way of printing the Gantt chart. First we create a "
                + "snapshot of the chart, then use the generated image to create "
                + "an ImageView node, then print the view.";
    }

    @Override
    public Node getControlPanel() {
        VBox box = new VBox();
        box.setFillWidth(true);
        Button print = new Button("Print");
        print.setOnAction(evt -> print());
        box.getChildren().add(print);
        return box;
    }

    private void print() {
        MSProjectGanttChart newChart = new MSProjectGanttChart();
        newChart.getTimeline().getModel().startTimeProperty().addListener(it -> System.out.println("st: " + newChart.getTimeline().getModel().getStartTime()));
        newChart.getTimeline().getModel().setStartTime(ZonedDateTime.of(LocalDate.of(2004, 7, 5), LocalTime.MIDNIGHT, ZoneId.systemDefault()).toInstant());
        newChart.load("com/flexganttfx/msproject/files/n0741.mpp", MSProjectApp.class.getResourceAsStream("/com/flexganttfx/msproject/files/n0741.mpp"));

        Scene scene = new Scene(newChart, 2000, 1000);
        newChart.getGraphics().getRowPanes().forEach(pane -> pane.getCanvas().draw());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.sizeToScene();
        newChart.applyCss();

        SnapshotParameters params = new SnapshotParameters();
        WritableImage image = new WritableImage((int) newChart.getWidth(), (int) newChart.getHeight());

        WritableImage snapshot = newChart.snapshot(params, image);
        ImageView node = new ImageView(snapshot);

        Printer printer = Printer.getDefaultPrinter();
        PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);
        double scaleX = pageLayout.getPrintableWidth() / node.getBoundsInParent().getWidth();
        double scaleY = pageLayout.getPrintableHeight() / node.getBoundsInParent().getHeight();
        double scale = Math.min(scaleX, scaleY);
        node.getTransforms().add(new Scale(scale, scale));

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean okPageSetup = job.showPageSetupDialog(gc.getScene().getWindow());
            if (okPageSetup) {
                boolean okPrintDialog = job.showPrintDialog(gc.getScene().getWindow());
                if (okPrintDialog) {
                    boolean success = job.printPage(node);
                    if (success) {
                        job.endJob();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
*/