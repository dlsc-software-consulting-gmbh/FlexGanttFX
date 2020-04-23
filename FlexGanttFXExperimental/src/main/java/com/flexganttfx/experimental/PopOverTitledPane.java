/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.experimental;

import static javafx.scene.control.ContentDisplay.GRAPHIC_ONLY;
import static javafx.scene.control.ContentDisplay.TEXT_ONLY;
import javafx.animation.FadeTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.util.Duration;

public class PopOverTitledPane extends TitledPane {

	public PopOverTitledPane(final String title, final Node detailedContent) {
		this(title, null, detailedContent);
	}

	public PopOverTitledPane(final String title, final Node summaryContent,
			final Node detailedContent) {

		super(title, detailedContent);

		if (title == null) {
			throw new IllegalArgumentException("title can not be null");
		}

		if (detailedContent == null) {
			throw new IllegalArgumentException(
					"detailed content can not be null");
		}

		setContentDisplay(TEXT_ONLY);
		setGraphic(summaryContent);

		expandedProperty().addListener(
				(value, oldExpanded, newExpanded) -> {
					if (newExpanded) {
						setContentDisplay(TEXT_ONLY);
						detailedContent.setOpacity(0);
						FadeTransition fadeInContent = new FadeTransition(
								getFadingDuration());
						fadeInContent.setFromValue(0);
						fadeInContent.setToValue(1);
						fadeInContent.setNode(detailedContent);
						fadeInContent.play();
					} else {
						if (summaryContent != null) {
							setContentDisplay(GRAPHIC_ONLY);
							summaryContent.setOpacity(0);
							FadeTransition fadeInSummary = new FadeTransition(
									getFadingDuration());
							fadeInSummary.setFromValue(0);
							fadeInSummary.setToValue(1);
							fadeInSummary.setNode(summaryContent);
							fadeInSummary.play();
						}
					}
				});
	}

	private final ObjectProperty<Duration> fadingDuration = new SimpleObjectProperty<>(
			this, "fadingDuration", Duration.seconds(.5));

	public final ObjectProperty<Duration> fadingDurationProperty() {
		return fadingDuration;
	}

	public final void setFadingDuration(Duration duration) {
		fadingDurationProperty().set(duration);
	}

	public final Duration getFadingDuration() {
		return fadingDurationProperty().get();
	}
}
