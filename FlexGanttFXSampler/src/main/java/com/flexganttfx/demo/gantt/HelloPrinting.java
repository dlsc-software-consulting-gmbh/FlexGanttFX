/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.gantt;

import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.view.GanttChart;
import javafx.application.Application;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;

import java.io.FileNotFoundException;

public class HelloPrinting extends FlexGanttFXSample {

	private GanttChart<?> gc;

	@Override
	protected GanttChart<?> createGanttChart() throws FileNotFoundException {
		gc = new GanttChart<>();
		return gc;
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
		SnapshotParameters params = new SnapshotParameters();
		WritableImage image = new WritableImage((int) gc.getWidth(), (int) gc.getHeight());

		WritableImage snapshot = gc.snapshot(params, image);
		ImageView node = new ImageView(snapshot);

		Printer printer = Printer.getDefaultPrinter();
		PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE,
				Printer.MarginType.DEFAULT);
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
