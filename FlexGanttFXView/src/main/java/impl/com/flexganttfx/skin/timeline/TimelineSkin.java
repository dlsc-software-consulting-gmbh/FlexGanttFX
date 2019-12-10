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

	private final VBox box;

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

		box = new VBox();
		box.setAlignment(Pos.CENTER);
		box.getStyleClass().add("dateline-eventline-wrapper");
		box.setFillWidth(true);

		getChildren().add(box);

		dateline.minWidthProperty().bind(timeline.widthProperty().add(100));

		final Rectangle clip = new Rectangle();
		clip.widthProperty().bind(timeline.widthProperty());
		clip.heightProperty().bind(timeline.heightProperty());
		//timeline.setClip(clip);

		dateline.visibleProperty().addListener(it -> updateVisibilities());
		eventline.visibleProperty().addListener(it -> updateVisibilities());

		updateVisibilities();
	}

	private void updateVisibilities() {
		box.getChildren().clear();

		if (dateline.isVisible()) {
			box.getChildren().add(dateline);
		}
		if (eventline.isVisible()) {
			box.getChildren().add(eventline);
		}
	}

	public Timeline getTimeline() {
		return getSkinnable();
	}
}
