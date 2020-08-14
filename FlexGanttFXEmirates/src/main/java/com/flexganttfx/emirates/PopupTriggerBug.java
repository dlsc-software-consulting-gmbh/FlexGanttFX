package com.flexganttfx.emirates;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PopupTriggerBug extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Label label = new Label("Bring up context menu via right mouse button ....");
        label.setAlignment(Pos.CENTER);
        label.setOnMousePressed(evt -> {
            label.setText("is popup trigger? -> " + evt.isPopupTrigger());
        });
        Scene scene = new Scene(label);
        primaryStage.setScene(scene);
        primaryStage.setWidth(400);
        primaryStage.setHeight(200);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}