/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.emirates.view;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javax.swing.SwingUtilities;

import org.openstreetmap.gui.jmapviewer.JMapViewer;

public class TestMapViewer extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		SwingNode swingNode = new SwingNode();
		createAndSetSwingContent(swingNode);

		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(swingNode);

		Scene scene = new Scene(borderPane);

		primaryStage.setScene(scene);
		primaryStage.setWidth(800);
		primaryStage.setHeight(800);
		primaryStage.centerOnScreen();
		primaryStage.show();
	}

	private void createAndSetSwingContent(final SwingNode swingNode) {
		SwingUtilities
				.invokeLater(() -> swingNode.setContent(new JMapViewer()));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
