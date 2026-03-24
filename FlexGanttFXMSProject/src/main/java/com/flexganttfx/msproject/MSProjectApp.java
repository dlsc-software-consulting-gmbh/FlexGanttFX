/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.msproject;

import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.extras.GanttChartStatusBar;
import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.extras.properties.view.GanttChartConfigurationView;
import com.flexganttfx.model.ActivityLink;
import com.flexganttfx.msproject.model.MSProjectTaskRow;
import com.flexganttfx.msproject.view.MSProjectGanttChart;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.graphics.renderer.CurvedLinkRenderer;
import com.jpro.webapi.WebAPI;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;

public class MSProjectApp extends Application {

	private static final String STAGE_TITLE = "MSProject Reader";
	private MSProjectGanttChart gantt;
	private FileChooser fileChooser;
	private Stage stage;

	@Override
	public void start(Stage stage) {
		if (!FlexGanttFX.isLicenseKeySet()) {
			FlexGanttFX.setLicenseKey("LIC=DLSC;VEN=DLSC;VER=12;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302C02142BD7F914E6633D7DBA0B8564D8FC20EC249BCFD702142558B5C6FF46325A0A698A1E8036828E54D6FEC8");
		}

		this.stage = stage;
		this.stage.setTitle(STAGE_TITLE);

		gantt = new MSProjectGanttChart();
		gantt.getGraphics().setLinkRenderer(ActivityLink.class, new CurvedLinkRenderer<>(gantt.getGraphics(), "Custom Link Renderer") {
			@Override
			public void draw(ActivityLink<?> link, GraphicsContext gc, Rectangle2D sourceBounds, Rectangle2D targetBounds) {
				if (link.getTargetActivityRef().getActivity().getStartTime().isBefore(link.getSourceActivityRef().getActivity().getEndTime())) {
					setStrokeColor(Color.CRIMSON);
					setArrowHeadColor(Color.CRIMSON);
				} else {
					setStrokeColor(Color.SLATEGRAY);
					setArrowHeadColor(Color.SLATEGRAY);
				}

				super.draw(link, gc, sourceBounds, targetBounds);
			}
		});

		// Load the first sample project as the default
		SampleProject defaultProject = SampleProjectFactory.ALL.get(0);
		gantt.load(defaultProject.getFactory().get());
		gantt.setDetail(new GanttChartConfigurationView(gantt));

		VBox.setVgrow(gantt, Priority.ALWAYS);

		VBox vbox = new VBox(0);

		MenuBar menuBar = createMenuBar();
		vbox.getChildren().add(menuBar);

		// Project selector bar
		HBox selectorBar = buildSelectorBar();
		vbox.getChildren().add(selectorBar);

		GanttChartStatusBar<MSProjectTaskRow> statusBar = new GanttChartStatusBar<>(gantt);

		Scene scene = new Scene(vbox);

		if (WebAPI.isBrowser()) {
			vbox.getChildren().addAll(gantt, statusBar);
		} else {
			GanttChartToolBar<MSProjectTaskRow> toolBar = new GanttChartToolBar<>(gantt);
			vbox.getChildren().addAll(toolBar, gantt, statusBar);

			ComboBox<GanttChartBase.ScrollBarType> box = new ComboBox<>();
			box.getItems().setAll(GanttChartBase.ScrollBarType.values());
			box.valueProperty().bindBidirectional(gantt.scrollBarTypeProperty());
			toolBar.getItems().add(1, box);
		}

		stage.setScene(scene);
		stage.sizeToScene();
		stage.centerOnScreen();
		stage.show();
	}

	private HBox buildSelectorBar() {
		Label label = new Label("Project:");
		label.setStyle("-fx-font-weight: bold;");

		ComboBox<SampleProject> projectBox = new ComboBox<>();
		projectBox.getItems().setAll(SampleProjectFactory.ALL);
		projectBox.getSelectionModel().selectFirst();
		projectBox.setPrefWidth(260);

		projectBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null) {
				gantt.load(newVal.getFactory().get());
				stage.setTitle(STAGE_TITLE + " – " + newVal.getName());
			}
		});

		HBox bar = new HBox(10, label, projectBox);
		bar.setAlignment(Pos.CENTER_LEFT);
		bar.setPadding(new Insets(6, 12, 6, 12));
		bar.setStyle("-fx-background-color: #F5F5F5; -fx-border-color: #DDDDDD; -fx-border-width: 0 0 1 0;");
		return bar;
	}

	private MenuBar createMenuBar() {
		MenuBar menuBar = new MenuBar();

		Menu fileMenu = new Menu("File");
		MenuItem openItem = new MenuItem("Open...");
		openItem.setOnAction(event -> openFile());
		fileMenu.getItems().add(openItem);
		menuBar.getMenus().add(fileMenu);
		return menuBar;
	}

	protected void openFile() {
		if (fileChooser == null) {
			fileChooser = new FileChooser();
		}

		File file = fileChooser.showOpenDialog(gantt.getScene().getWindow());
		if (file != null) {
			try {
				gantt.load(file);
				stage.setTitle(STAGE_TITLE + ": " + file.getName());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
