/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.editor;

import com.flexganttfx.core.FlexGanttFX;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A showcase application to show off the conflict resolution feature.
 */
public class AgendaGanttChartApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		AgendaGanttChart ganttChart = new AgendaGanttChart();
		Scene scene = new Scene(ganttChart);
		primaryStage.setScene(scene);
		primaryStage.setWidth(1200);
		primaryStage.setHeight(1000);
		primaryStage.centerOnScreen();
		primaryStage.show();
	}

	public static void main(String[] args) {
		if (!FlexGanttFX.isLicenseKeySet()) {
			FlexGanttFX.setLicenseKey("LIC=DLSC;VEN=DLSC;VER=12;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302C02142BD7F914E6633D7DBA0B8564D8FC20EC249BCFD702142558B5C6FF46325A0A698A1E8036828E54D6FEC8");
		}
		launch(args);
	}
}
