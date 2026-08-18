/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.demo.timeline;

import com.flexganttfx.model.timeline.TimelineModel;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.time.Duration;
import java.time.Instant;

public class TimelineModelControlPanel extends VBox {

	public TimelineModelControlPanel(final TimelineModel<?> timelineModel, Instant startTime, Instant endTime) {

		setPadding(new Insets(10, 10, 10, 10));
		setFillWidth(true);
		setSpacing(10);

		Format format = new Format() {

			private static final long serialVersionUID = 1L;

			@Override
			public Object parseObject(String source, ParsePosition pos) {
				Double value = Double.parseDouble(source);
				pos.setIndex(source.length() - 1);
				return value;
			}

			@Override
			public StringBuffer format(Object obj, StringBuffer toAppendTo,
					FieldPosition pos) {
				return toAppendTo.append(obj);
			}
		};

		// millis per pixel

		getChildren().add(new Label("Millis Per Pixel"));

		TextField temporalUnitWidthField = new TextField();
		Bindings.bindBidirectional(temporalUnitWidthField.textProperty(), timelineModel.millisPerPixelProperty(), format);
		getChildren().add(temporalUnitWidthField);

		Slider unitWidthSlider = new Slider(1000, 96 * 60 * 60 * 1000, 24 * 60 * 60 * 1000);
		Bindings.bindBidirectional(unitWidthSlider.valueProperty(), timelineModel.millisPerPixelProperty());
		Bindings.bindBidirectional(temporalUnitWidthField.textProperty(), unitWidthSlider.valueProperty(), format);
		getChildren().add(unitWidthSlider);

		// start time

		getChildren().add(new Label("Start Time"));

		if (startTime == null) {
			startTime = Instant.now();
			endTime = startTime.plus(Duration.ofDays(100));
			timelineModel.setStartTime(startTime);
		}

		final Slider slider = new Slider(startTime.toEpochMilli(),
				endTime.toEpochMilli(), 0);
		slider.valueProperty().addListener((value, oldNumber, newNumber) -> {
			Instant time = Instant.ofEpochMilli(newNumber.longValue());
			timelineModel.setStartTime(time);
		});
		getChildren().add(slider);

		timelineModel.startTimeProperty().addListener(
				(value, oldTime, newTime) -> slider.setValue(newTime
						.toEpochMilli()));
	}
}
