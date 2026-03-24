/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.launcher;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.Desktop;
import java.net.URI;

/**
 * A fancy tile card representing a single FlexGanttFX demo.
 * <p>
 * Layout:
 * <ul>
 *   <li>Coloured gradient header strip with a large white icon</li>
 *   <li>Title and description in the card body</li>
 *   <li>Button bar: "Launch" and optional "Online Demo →"</li>
 * </ul>
 */
public class DemoCard extends VBox {

    public DemoCard(DemoDescriptor demo) {
        getStyleClass().add("demo-card");

        getChildren().addAll(
                buildHeader(demo),
                buildBody(demo),
                buildButtonBar(demo)
        );
    }

    private StackPane buildHeader(DemoDescriptor demo) {
        FontIcon icon = new FontIcon(demo.getIcon());
        icon.setIconSize(72);
        icon.getStyleClass().add("card-icon");

        StackPane header = new StackPane(icon);
        header.getStyleClass().add("card-header");
        header.setStyle("-fx-background-color: " + demo.getHeaderGradient() + ";");
        header.setPrefHeight(130);
        header.setMinHeight(130);
        header.setMaxHeight(130);
        return header;
    }

    private VBox buildBody(DemoDescriptor demo) {
        Label titleLabel = new Label(demo.getTitle());
        titleLabel.getStyleClass().add("card-title");

        Label subtitleLabel = new Label(demo.getSubtitle());
        subtitleLabel.getStyleClass().add("card-subtitle");

        Label descLabel = new Label(demo.getDescription());
        descLabel.getStyleClass().add("card-description");
        descLabel.setWrapText(true);

        VBox body = new VBox(6, titleLabel, subtitleLabel, descLabel);
        body.getStyleClass().add("card-body");
        body.setPadding(new Insets(16, 18, 10, 18));
        VBox.setVgrow(body, Priority.ALWAYS);
        return body;
    }

    private HBox buildButtonBar(DemoDescriptor demo) {
        HBox bar = new HBox(8);
        bar.getStyleClass().add("card-button-bar");
        bar.setPadding(new Insets(10, 18, 16, 18));
        bar.setAlignment(Pos.CENTER_LEFT);

        if (demo.hasLauncher()) {
            Button launchBtn = new Button("Launch");
            launchBtn.getStyleClass().addAll("btn-launch");
            launchBtn.setStyle("-fx-background-color: " + demo.getAccentColor() + ";");
            launchBtn.setOnAction(e -> {
                try {
                    demo.getLauncher().run();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            bar.getChildren().add(launchBtn);
        }

        if (demo.hasOnlineUrl()) {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button onlineBtn = new Button("Online Demo →");
            onlineBtn.getStyleClass().add("btn-online");
            onlineBtn.setOnAction(e -> openUrl(demo.getOnlineUrl()));

            if (demo.hasLauncher()) {
                bar.getChildren().addAll(spacer, onlineBtn);
            } else {
                // No local launch — show a wider, centred online button
                onlineBtn.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(onlineBtn, Priority.ALWAYS);
                onlineBtn.setStyle("-fx-background-color: " + demo.getAccentColor() + ";");
                onlineBtn.getStyleClass().remove("btn-online");
                onlineBtn.getStyleClass().add("btn-launch");
                bar.getChildren().add(onlineBtn);
            }
        }

        return bar;
    }

    private static void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
