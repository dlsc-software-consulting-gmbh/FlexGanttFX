package com.flexganttfx.view;

import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityLink;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.MutableActivityBase;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class LinksProblem extends Application
{

    class ModelObject< P extends Row< ?, ?, ? >, C extends Row< ?, ?, ? >, A extends Activity > extends
        Row< P, C, A >
    {}

    class Fleet extends ModelObject< Row< ?, ?, ? >, Aircraft, Activity >
    {}

    class Aircraft extends ModelObject< Fleet, Fleet, Flight >
    {}

    class Flight extends MutableActivityBase< Object >
    {}

    @Override
    public void start( Stage stage ) throws Exception
    {

        Fleet fleet = new Fleet();
        fleet.setExpanded( true );

        GanttChart< ModelObject< ?, ?, ? >> gantt = new GanttChart<>( fleet );

        Layer layer = new Layer( "Layer" );
        gantt.getLayers().addAll( layer );

        // Create the aircrafts.
        Aircraft aircraft1 = new Aircraft();
        Aircraft aircraft2 = new Aircraft();

        // Add the aircrafts to the fleet.
        fleet.getChildren().addAll( aircraft1, new Aircraft(), new Aircraft(), new Aircraft(),
            new Aircraft(), new Aircraft(), new Aircraft(), new Aircraft(), new Aircraft(), new Aircraft(),
            new Aircraft(), new Aircraft(), aircraft2, new Aircraft(), new Aircraft() );

        // Create the flights
        Flight flight1 = new Flight();
        Flight flight2 = new Flight();

        aircraft1.addActivity( layer, flight1 );
        aircraft2.addActivity( layer, flight2 );

        // Add link between activities
        gantt.getLinks().add(
            new ActivityLink< Flight >( new ActivityRef< Flight >( aircraft1, layer, flight1 ),
                new ActivityRef< Flight >( aircraft2, layer, flight2 ) ) );

        // Button that increases the size of all rows
        Button btn = new Button();
        btn.setText( "+" );
        btn.setOnAction( event -> gantt.getRoot().getChildren()
            .forEach( row -> row.setHeight( row.getHeight() * 1.1 ) ) );

        // Button that decreases the size of all rows
        Button btn2 = new Button();
        btn2.setText( "-" );
        btn2.setOnAction( event -> gantt.getRoot().getChildren()
            .forEach( row -> row.setHeight( row.getHeight() / 1.1 ) ) );

        gantt.getRoot().setExpanded( true );
        gantt.getTreeTable().setShowRoot( false );

        BorderPane root = new BorderPane();
        root.setCenter( gantt );

        HBox hbox = new HBox( 8 );
        hbox.getChildren().addAll( btn, btn2 );

        root.setBottom( hbox );

        Scene scene = new Scene( root );
        stage.setTitle( "Link problem" );
        stage.setScene( scene );
        stage.centerOnScreen();
        stage.sizeToScene();
        stage.show();
    }

    public static void main( String[] args )
    {
        GanttLicense.readAndSet();
        Application.launch( args );
    }

    public static class GanttLicense
    {
        public static void readAndSet()
        {

                if( FlexGanttFX.getLicense() == null )
                {
                    FlexGanttFX.setLicenseKey("LIC=DLSC;VEN=DLSC;VER=12;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302C02142BD7F914E6633D7DBA0B8564D8FC20EC249BCFD702142558B5C6FF46325A0A698A1E8036828E54D6FEC8");
                }

        }
    }
}