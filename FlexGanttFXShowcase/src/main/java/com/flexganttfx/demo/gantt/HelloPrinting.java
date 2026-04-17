/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.gantt;

import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.msproject.SampleProjectFactory;
import com.flexganttfx.msproject.view.MSProjectGanttChart;
import com.flexganttfx.view.GanttChart;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class HelloPrinting extends FlexGanttFXSample {

    private MSProjectGanttChart gc;

    @Override
    protected GanttChart<?> createGanttChart() {
        gc = new MSProjectGanttChart();
        loadSoftwareReleasePlan(gc);
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
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_LEFT);
        box.setSpacing(10);

        Button print = new Button("Print");
        print.setOnAction(evt -> print());
        box.getChildren().add(print);

        return box;
    }

    private void print() {
        MSProjectGanttChart newChart = new MSProjectGanttChart();
        loadSoftwareReleasePlan(newChart);

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

    private void loadSoftwareReleasePlan(MSProjectGanttChart chart) {
        chart.load(SampleProjectFactory.ALL.get(0).getFactory().get());
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
