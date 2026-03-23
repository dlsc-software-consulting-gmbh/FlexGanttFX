/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.launcher;

import com.flexganttfx.covid.CovidApp;
import com.flexganttfx.emirates.EmiratesApp;
import com.flexganttfx.factory.FactoryApp;
import com.flexganttfx.msproject.MSProjectApp;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import java.awt.Desktop;
import java.net.URI;
import java.util.List;

/**
 * Root view of the demo launcher. Displays a dark header and a responsive
 * tile grid of {@link DemoCard}s — one per major FlexGanttFX demo.
 */
public class LauncherView extends BorderPane {

    private static final String WEBSITE_URL = "https://www.flexganttfx.com";

    public LauncherView(Application hostApp) {
        getStyleClass().add("launcher-root");
        setTop(buildHeader());
        setCenter(buildTileGrid());
    }

    // -------------------------------------------------------------------------
    // Header
    // -------------------------------------------------------------------------

    private HBox buildHeader() {
        Label title = new Label("FlexGanttFX");
        title.getStyleClass().add("header-title");

        Label subtitle = new Label("Demo Launcher");
        subtitle.getStyleClass().add("header-subtitle");

        HBox titleBox = new HBox(10, title, subtitle);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button websiteBtn = new Button("Visit flexganttfx.com  ↗");
        websiteBtn.getStyleClass().add("btn-website");
        websiteBtn.setOnAction(e -> openUrl(WEBSITE_URL));

        HBox header = new HBox(20, titleBox, spacer, websiteBtn);
        header.getStyleClass().add("launcher-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(22, 36, 22, 36));
        return header;
    }

    // -------------------------------------------------------------------------
    // Tile grid
    // -------------------------------------------------------------------------

    private TilePane buildTileGrid() {
        TilePane tiles = new TilePane();
        tiles.getStyleClass().add("tile-pane");
        tiles.setPrefColumns(3);
        tiles.setHgap(24);
        tiles.setVgap(24);
        tiles.setPadding(new Insets(36, 40, 40, 40));

        for (DemoDescriptor demo : buildDescriptors()) {
            tiles.getChildren().add(new DemoCard(demo));
        }

        return tiles;
    }

    // -------------------------------------------------------------------------
    // Demo descriptors
    // -------------------------------------------------------------------------

    private List<DemoDescriptor> buildDescriptors() {
        return List.of(
                new DemoDescriptor(
                        "Emirates Airlines",
                        "Aircraft Fleet Scheduling",
                        "Emirates Airlines uses FlexGanttFX at their headquarters in Dubai to fine-tune "
                        + "the utilisation of their aircraft fleet. This demo visualises scheduling "
                        + "procedures and algorithms developed by Emirates.",
                        MaterialDesign.MDI_AIRPLANE,
                        "linear-gradient(to bottom, #0A2654, #1565C0)",
                        "#1565C0",
                        launchApp(EmiratesApp.class),
                        "https://demos.jpro.one/flexganttfx.html"
                ),
                new DemoDescriptor(
                        "MSProject File Reader",
                        "Microsoft Project Import",
                        "Import and visualise Microsoft Project (.mpp) files using the open-source MPXJ "
                        + "library. This demo shows how FlexGanttFX can read data from third-party "
                        + "sources and render rich Gantt charts from real project data.",
                        MaterialDesign.MDI_CHART_GANTT,
                        "linear-gradient(to bottom, #1B5E20, #2E7D32)",
                        "#2E7D32",
                        launchApp(MSProjectApp.class),
                        "https://demos.jpro.one/msproject.html"
                ),
                new DemoDescriptor(
                        "Factory Scheduling",
                        "Manufacturing Floor Planner",
                        "A high-density production scheduling demo featuring 8 production lines with 5 "
                        + "machines each. Jobs are colour-coded by status (Scheduled, In Progress, Done, "
                        + "Delayed) and show a completion progress overlay.",
                        MaterialDesign.MDI_FACTORY,
                        "linear-gradient(to bottom, #BF360C, #E64A19)",
                        "#E64A19",
                        launchApp(FactoryApp.class),
                        null
                ),
                new DemoDescriptor(
                        "COVID-19 Tracker",
                        "Pandemic Data Visualiser",
                        "Visualises global COVID-19 case data on a FlexGanttFX timeline. Countries are "
                        + "shown as rows and case counts over time as activities, making it easy to "
                        + "compare the spread of the pandemic across regions.",
                        MaterialDesign.MDI_BIOHAZARD,
                        "linear-gradient(to bottom, #004D40, #00695C)",
                        "#00695C",
                        launchApp(CovidApp.class),
                        null
                ),
                new DemoDescriptor(
                        "FlexGanttFX Sampler",
                        "Interactive Feature Showcase",
                        "An interactive catalogue of over 30 focused samples — each demonstrating one or "
                        + "two specific FlexGanttFX features such as drag & drop, custom renderers, "
                        + "layouts, links, tooltips, printing, and more.",
                        MaterialDesign.MDI_VIEW_DASHBOARD,
                        "linear-gradient(to bottom, #1A237E, #283593)",
                        "#283593",
                        null, // open module — launch via online demo only
                        "https://www.jfx-ensemble.com/?page=project/flexganttfx"
                )
        );
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Returns a {@link Runnable} that instantiates the given {@link Application}
     * subclass, calls {@code init()}, and shows it in a new {@link Stage}.
     * This avoids calling {@code Application.launch()} a second time.
     */
    private static Runnable launchApp(Class<? extends Application> appClass) {
        return () -> {
            try {
                Application app = appClass.getDeclaredConstructor().newInstance();
                app.init();
                Stage stage = new Stage();
                app.start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };
    }

    private static void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
