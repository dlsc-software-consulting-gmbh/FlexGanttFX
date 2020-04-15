/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.timeline;

import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.Eventline;
import com.flexganttfx.view.timeline.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.SkinBase;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class TimelineSkin extends SkinBase<Timeline> {

	private final Dateline dateline;
	private final Eventline eventline;

	private final VBox vbox;

	public TimelineSkin(final Timeline timeline) {
		super(timeline);

		this.dateline = timeline.getDateline();
		this.eventline = timeline.getEventline();

		EventHandler<KeyEvent> arrowKeysHandler = event -> {
			switch (event.getCode()) {
			case RIGHT:
				if (event.isShiftDown()) {
					getSkinnable().scrollRightFast();
				} else {
					getSkinnable().scrollRight();
				}
				event.consume();
				break;
			case LEFT:
				if (event.isShiftDown()) {
					getSkinnable().scrollLeftFast();
				} else {
					getSkinnable().scrollLeft();
				}
				event.consume();
				break;
			default:
				break;
			}
		};

		EventHandler<KeyEvent> plusMinusKeyHandler = event -> {
			switch (event.getCharacter()) {
			case "-":
				timeline.zoomOut();
				break;
			case "+":
				timeline.zoomIn();
				break;
			default:
				break;
			}
		};

		timeline.addEventHandler(KeyEvent.KEY_PRESSED, arrowKeysHandler);
		timeline.addEventHandler(KeyEvent.KEY_TYPED, plusMinusKeyHandler);

		VBox.setVgrow(dateline, Priority.ALWAYS);
		VBox.setVgrow(eventline, Priority.NEVER);

		vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		vbox.getStyleClass().add("dateline-eventline-wrapper");
		vbox.setFillWidth(true);

		/*
		 * Very important to set the pref and the min width here as otherwise the eventline
		 * will be as wide as the dateline, including the dateline's buffer. But then the graphics
		 * inside the eventline will use the dateline buffer AND the canvas buffer causing
		 * activities to be drawn incorrectly. Run "HelloGlobalActivities" for an example.
		 */
		vbox.setPrefWidth(0);
		vbox.setMinWidth(0);

		getChildren().add(vbox);

		dateline.minWidthProperty().bind(vbox.widthProperty().add(dateline.datelineBufferProperty().multiply(2)));

		final Rectangle clip = new Rectangle();
		clip.widthProperty().bind(vbox.widthProperty());
		clip.heightProperty().bind(vbox.heightProperty());

		if (!Boolean.getBoolean("timeline.no.clip")) {
			vbox.setClip(clip);
		}

		dateline.visibleProperty().addListener(it -> updateVisibilities());
		eventline.visibleProperty().addListener(it -> updateVisibilities());

		updateVisibilities();
	}

	private void updateVisibilities() {
		vbox.getChildren().clear();

		if (dateline.isVisible()) {
			vbox.getChildren().add(dateline);
		}

		if (eventline.isVisible()) {
			vbox.getChildren().add(eventline);
		}
	}

	public Timeline getTimeline() {
		return getSkinnable();
	}
}
