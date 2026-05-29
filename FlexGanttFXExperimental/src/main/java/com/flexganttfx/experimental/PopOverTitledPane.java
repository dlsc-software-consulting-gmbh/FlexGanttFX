/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing/>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://github.com/dlemmermann/FlexGanttFX/blob/master/LICENSE>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
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
