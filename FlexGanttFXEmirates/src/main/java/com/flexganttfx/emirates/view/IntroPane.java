/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.emirates.view;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.emirates.EmiratesApp;
import com.flexganttfx.emirates.model.DataModel.DataSet;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

public class IntroPane extends VBox {

	public IntroPane(final EmiratesApp app) {
		getStyleClass().add("intro-pane");

		Label emiratesLogo = new Label();
		emiratesLogo.setGraphic(new ImageView(EmiratesApp.class.getResource("logo-emirates.gif").toExternalForm()));
		emiratesLogo.getStyleClass().add("emirates-logo");
		emiratesLogo.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		emiratesLogo.setAlignment(Pos.CENTER_RIGHT);
		HBox.setHgrow(emiratesLogo, Priority.ALWAYS);

		Label dlscLogo = new Label();
		dlscLogo.getStyleClass().add("dlsc-logo");
		dlscLogo.setMaxWidth(Double.MAX_VALUE);
		final ImageView imageView = new ImageView(EmiratesApp.class.getResource("logo-dlsc.png").toExternalForm());
		imageView.setFitHeight(70);
		imageView.setPreserveRatio(true);

		dlscLogo.setGraphic(imageView);
		dlscLogo.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		dlscLogo.setAlignment(Pos.CENTER_RIGHT);

		HBox headerBox = new HBox();
		headerBox.getStyleClass().add("logo-header");
		headerBox.getChildren().add(emiratesLogo);
		headerBox.getChildren().add(dlscLogo);
		getChildren().add(headerBox);

		VBox innerBox = new VBox();
		innerBox.getStyleClass().add("inner-box");
		VBox.setVgrow(innerBox, Priority.ALWAYS);

		getChildren().add(innerBox);

		Label title = new Label("FlexGanttFX " + FlexGanttFX.getVersion());
		title.getStyleClass().add("demo-title");
		title.setContentDisplay(ContentDisplay.RIGHT);
		innerBox.getChildren().add(title);

		Label subtitle = new Label("Aircraft Scheduling");
		subtitle.getStyleClass().add("demo-subtitle");
		subtitle.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		subtitle.setAlignment(Pos.TOP_LEFT);
		innerBox.getChildren().add(subtitle);
		VBox.setVgrow(subtitle, Priority.ALWAYS);

		HBox buttonBox = new HBox();
		Label label = new Label(); // spacer
		label.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(label, Priority.ALWAYS);
		buttonBox.getChildren().add(label);

		Label promptLabel = new Label("Select a Dataset");
		promptLabel.setAlignment(Pos.CENTER_RIGHT);
		promptLabel.getStyleClass().add("prompt-label");
		promptLabel.setGraphic(new FontIcon(MaterialDesign.MDI_ARROW_RIGHT));
		promptLabel.setContentDisplay(ContentDisplay.RIGHT);
		buttonBox.getChildren().add(promptLabel);

		FadeTransition fadeTransition = new FadeTransition();
		fadeTransition.setNode(promptLabel);
		fadeTransition.setFromValue(1);
		fadeTransition.setToValue(.2);
		fadeTransition.setCycleCount(FadeTransition.INDEFINITE);
		fadeTransition.setDuration(Duration.millis(1000));
		fadeTransition.setAutoReverse(true);
		fadeTransition.play();

		buttonBox.setAlignment(Pos.CENTER_LEFT);
		buttonBox.getStyleClass().add("button-box");
		for (final DataSet dataSet : DataSet.values()) {
			final Button button = new Button(dataSet.getDisplayName());
			button.setOnAction(event -> {
				fadeTransition.stop();
				button.setDisable(true);
				button.setCursor(Cursor.WAIT);
				IntroPane.this.setCursor(Cursor.WAIT);
				Platform.runLater(() -> app.load(dataSet));
			});
			button.setMaxHeight(Double.MAX_VALUE);
			button.setAlignment(Pos.BOTTOM_RIGHT);
			button.setContentDisplay(ContentDisplay.TOP);
			buttonBox.setFillHeight(true);
			buttonBox.getChildren().add(button);
			switch (dataSet) {
			case LARGE:
				button.getStyleClass().add("data-set-large");
				break;
			case MEDIUM:
				button.getStyleClass().add("data-set-medium");
				break;
			case SMALL:
				button.getStyleClass().add("data-set-small");
				break;
			default:
				break;

			}
		}

		innerBox.getChildren().add(buttonBox);
		setEffect(new DropShadow(20, Color.GRAY));
	}
}
