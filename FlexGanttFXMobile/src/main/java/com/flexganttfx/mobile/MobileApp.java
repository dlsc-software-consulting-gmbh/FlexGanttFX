package com.flexganttfx.mobile;

import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChart;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MobileApp extends Application {

    @Override
    public void start(Stage stage) {
        FlexGanttFX.setLicenseKey("LIC=DLSC;VEN=DLSC;VER=11;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302D0215009651BAED65BD0B554000F4B355AF1F17D6D4D7A20214051C06EF255EB67663E3446294E8408B29F94E88");

        GanttChart<Row<?, ?, ?>> ganttChart = new GanttChart<>();
        Scene scene = new Scene(ganttChart, 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}