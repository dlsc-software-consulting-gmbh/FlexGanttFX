/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.controls;

import com.flexganttfx.experimental.AccordionPopOver;
import com.flexganttfx.experimental.PopOverHeader;
import com.flexganttfx.experimental.PopOverTitledPane;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class HelloAccordionPopOver extends Application {

	private AccordionPopOver popOver;

	// @Override
	// public String getSampleName() {
	// return "Accordion Pop Over";
	// }

	@Override
	public void start(Stage stage) throws Exception {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setAlignment(Pos.CENTER);
		grid.setOnMouseClicked(evt -> {
			if (popOver != null && !popOver.isDetached()) {
				popOver.hide();
			}
		});

		for (int i = 0; i < 10; i++) {
			final Button button = new Button("Button " + i);
			grid.add(button, i % 2, i / 2);

			button.setOnMouseClicked(showPopOver(button));
		}

		stage.setScene(new Scene(grid));
		stage.setWidth(500);
		stage.setHeight(500);
		stage.show();
	}

	private EventHandler<? super MouseEvent> showPopOver(final Button button) {
		return evt -> {
			if (popOver != null && !popOver.isDetached() && popOver.isShowing()) {
				popOver.hide();
			} else {
				if (evt.getClickCount() == 1) {
					popOver = createPopOver(button);
					popOver.show(button,
							button.localToScreen(0, button.getLayoutY()).getX()
									+ button.getWidth() - 4, evt.getScreenY());
				}
			}
		};
	}

	private AccordionPopOver createPopOver(final Button button) {
		AccordionPopOver popOver = new AccordionPopOver();

		PopOverHeader<?> header = (PopOverHeader<?>) popOver.getHeader();
		header.setTitle("A PopOver Control");
		header.setSubtitle("This example is using an accordion for its content");

		popOver.setTitle(button.getText());

		popOver.getPanes().add(createTitledPane("Titled Pane 1"));
		popOver.getPanes().add(createTitledPane("Titled Pane 2"));
		popOver.getPanes().add(createTitledPane("Titled Pane 3"));
		popOver.getPanes().add(createTitledPane("Titled Pane 4"));
		popOver.setExpandedPane(popOver.getPanes().get(0));
		popOver.setFooter(new Footer());

		ColorPicker picker = (ColorPicker) header.getExtra();
		picker.valueProperty().addListener(
				(value, oldColor, newColor) -> button
						.setBackground(new Background(new BackgroundFill(
								newColor, CornerRadii.EMPTY, Insets.EMPTY))));

		return popOver;
	}

	private TitledPane createTitledPane(String title) {
		final TitledPane pane = new PopOverTitledPane(title, new ExamplePane());
		pane.setTextAlignment(TextAlignment.LEFT);

		Pane connectivityArrow = (Pane) pane.lookup(".arrow");
		if (connectivityArrow != null) {
			connectivityArrow.translateXProperty().bind(
					pane.widthProperty().subtract(
							connectivityArrow.widthProperty().multiply(2)));
		}

		return pane;
	}

	class ExamplePane extends GridPane {
		private DatePicker datePicker = new DatePicker();
		private CheckBox milestone = new CheckBox("Milestone");
		private TextField durationField = new TextField();
		private TextField capacityField = new TextField();
		private Slider percentageSlider = new Slider(0, 100, 0);
		private Label percentageValueLabel = new Label();

		public ExamplePane() {
			Label timeLabel = new Label("Start Time:");
			timeLabel.setLabelFor(datePicker);
			GridPane.setHalignment(timeLabel, HPos.RIGHT);
			add(timeLabel, 0, 0);
			add(datePicker, 1, 0);

			add(milestone, 1, 1);

			Label durationLabel = new Label("Duration:");
			durationLabel.setLabelFor(durationField);
			GridPane.setHalignment(durationLabel, HPos.RIGHT);
			add(durationLabel, 0, 2);
			add(durationField, 1, 2);

			Label percentageLabel = new Label("Completeness:");
			percentageLabel.setLabelFor(percentageSlider);
			GridPane.setHalignment(percentageLabel, HPos.RIGHT);
			percentageValueLabel = new Label("0%");
			add(percentageLabel, 0, 3);
			add(percentageSlider, 1, 3);
			add(percentageValueLabel, 2, 3);

			Label capacityLabel = new Label("Capacity Used:");
			capacityLabel.setLabelFor(capacityField);
			GridPane.setHalignment(capacityLabel, HPos.RIGHT);
			add(capacityLabel, 0, 4);
			add(capacityField, 1, 4);

			setHgap(10);
			setVgap(10);

			durationField.disableProperty().bind(milestone.selectedProperty());
		}
	}

	class Footer extends FlowPane {

		public Footer() {
			super(Orientation.HORIZONTAL);

			setAlignment(Pos.CENTER_RIGHT);

			Button delete = new Button("Delete");
			getChildren().add(delete);
			delete.setOnAction(evt -> {
			});
		}
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
