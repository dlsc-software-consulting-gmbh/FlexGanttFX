package com.flexganttfx.tutorial;

import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.model.ActivityLink;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.tutorial.TutorialAircraftFlight.Aircraft;
import com.flexganttfx.tutorial.TutorialAircraftFlight.Flight;
import com.flexganttfx.tutorial.TutorialAircraftFlight.FlightData;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.timeline.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TutorialGantt extends Application {

    private GanttChart<Aircraft> ganttBottom = new GanttChart<>(new Aircraft("ROOTBOTTOM"));

    private int scrollValue = 0;

    private int treetableCount = 0;

    private List<Aircraft> aircraftList = new ArrayList<>();
    private List<Flight> flightList = new ArrayList<>();

    public void start(Stage stage) {

        Layer layer = new Layer("FlightsBottom");
        this.ganttBottom.getLayers().add(0,layer);

        for (int i=0; i<1000; i++) {
            aircraftList.add(new Aircraft(String.valueOf(i)));
        }

        aircraftList.forEach(aircraft -> {
            String flightName = /*"flight " +*/ aircraft.getName();
            Flight flight = new Flight(new FlightData(flightName, 1));
            String flightName1 = /*"flight " +*/ aircraft.getName();
            Flight flight1 = new Flight(new FlightData(flightName, 2));
            aircraft.addActivity(layer, flight);
            aircraft.addActivity(layer, flight1);
            flightList.add(flight);
            flightList.add(flight1);
            ActivityRef sourceActRef = new ActivityRef(aircraft, layer, flight);
            ActivityRef targetActRef = new ActivityRef(aircraft, layer, flight1);
            ActivityLink activityLink = new ActivityLink (sourceActRef, targetActRef) ;
            ganttBottom.getLinks().add(activityLink);
        });

        ganttBottom.getRoot().getChildren().addAll(aircraftList);

        Timeline timeline = ganttBottom.getTimeline();
        timeline.showTemporalUnit(ChronoUnit.HOURS, 10);

        GraphicsBase<Aircraft> graphics = ganttBottom.getGraphics();

        ganttBottom.getGraphics().getActivityFilter();
        BorderPane bp = new BorderPane();
        bp.setCenter(ganttBottom);

        HBox hb  = new HBox(5);
        TextField tf = new TextField();
        tf.setPromptText("Find Aircraft");
        tf.textProperty().addListener((obs, old, newVal) -> {
            if (tf.getText()!= null && tf.getText().contains(newVal)) {
                this.getGantt().setRowFilter(row-> row.getName().contains(newVal));
                //Use flatmap to get total count
                this.treetableCount = this.getGantt().getTreeTable().getRoot().getChildren().size();
                //Reset scroll value
                this.scrollValue = 0;
                this.getGantt().getTreeTable().scrollTo(scrollValue);
                this.getGantt().getTreeTable().getSelectionModel().select(scrollValue);
                this.scrollValue ++;
            } else if (tf.getText().isBlank()) {
                this.getGantt().setRowFilter(null);
                //Reset scroll value
                this.scrollValue = 0;
                this.getGantt().getTreeTable().scrollTo(scrollValue);
            }
        });

        tf.setOnKeyPressed(evt-> {
            if (evt.getCode().equals(KeyCode.ENTER)) {
                if (this.scrollValue == this.treetableCount) {
                    this.scrollValue = 0;
                }
                this.getGantt().getTreeTable().scrollTo(scrollValue);
                this.scrollValue ++;
            }
        });

        TextField tf1 = new TextField();
        tf1.setPromptText("Search Activity");
        tf1.textProperty().addListener((obs, old, newVal) -> {
            if (tf1.getText()!= null && tf1.getText().contains(newVal)) {
                this.getGantt().getGraphics().setActivityFilter(act-> act.getName().contains(newVal));

            } else if (tf1.getText().isBlank()) {
                this.getGantt().getGraphics().setActivityFilter(null);

            }
        });

        tf1.setOnKeyPressed(evt-> {
            if (evt.getCode().equals(KeyCode.ENTER)) {

            }
        });

        hb.getChildren().addAll(tf, tf1);
        bp.setTop(hb);
        Scene scene = new Scene(bp);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        FlexGanttFX.setLicenseKey("LIC=DSTASingapore;VEN=DLSC;VER=12;PRO=STANDARD;RUN=yes;CTR=1;SignCode=3F;Signature=302D02150093B5A59029DC2AB77BFE161715AB2C1130E14F4F0214188D8B82FA4AAEA547BF867DE9DAEFCF540140CF");
        launch(args);
    }

    public GanttChart<Aircraft> getGantt() {
        return this.ganttBottom;
    }
}
