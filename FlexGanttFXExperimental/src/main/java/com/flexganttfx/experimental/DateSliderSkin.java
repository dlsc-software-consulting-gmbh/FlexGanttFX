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

import javafx.beans.InvalidationListener;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Created by dirk on 21.06.16.
 */
public class DateSliderSkin extends SkinBase<DateSlider> {

    private final Slider slider;
    private final Label earliestLabel;
    private final Label latestLabel;
    private final Tooltip tooltip;

    public DateSliderSkin(DateSlider control) {
        super(control);

        slider = new Slider();

        earliestLabel = new Label();
        earliestLabel.getStyleClass().add("earliest-date-label");

        latestLabel = new Label();
        latestLabel.getStyleClass().add("latest-date-label");

        tooltip = new Tooltip("Hello World");
        tooltip.getStyleClass().add("date-tooltip");

        InvalidationListener updateListener = it -> updateSliderMaxValue();

        control.earliestDateProperty().addListener(updateListener);
        control.latestDateProperty().addListener(updateListener);
        control.dateProperty().addListener(it -> updateSliderValue());
        control.dateTimeFormatterProperty().addListener(it -> updateLabels());

        updateSliderMaxValue();
        updateSliderValue();
        updateLabels();

        slider.valueProperty().addListener(it -> updateDate());

        HBox box = new HBox(10);
        box.setFillHeight(true);
        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(earliestLabel, slider, latestLabel);

        HBox.setHgrow(earliestLabel, Priority.NEVER);
        HBox.setHgrow(slider, Priority.ALWAYS);
        HBox.setHgrow(latestLabel, Priority.NEVER);

        slider.setTooltip(tooltip);

        slider.setOnMousePressed(evt -> {
            updateToolTipLocation();
            tooltip.show(slider.getScene().getWindow());
        });

        slider.setOnMouseDragged(evt -> {
            updateToolTipLocation();
        });

        slider.setOnMouseReleased(evt -> {
            tooltip.hide();
        });

        getChildren().add(box);
    }

    private void updateToolTipLocation() {
        Region thumb = getThumb();
        Point2D point2D = thumb.localToScreen(0, 0);
        tooltip.setAnchorX(point2D.getX() + thumb.getWidth() / 2);
        tooltip.setAnchorY(point2D.getY() + thumb.getHeight() + 4);
    }

    private Region getThumb() {
        return (Region) slider.lookup(".thumb");
    }

    private void updateLabels() {
        earliestLabel.setText(getSkinnable().getDateTimeFormatter().format(getSkinnable().getEarliestDate()));
        latestLabel.setText(getSkinnable().getDateTimeFormatter().format(getSkinnable().getLatestDate()));
    }

    boolean updatingDate;

    private void updateDate() {
        updatingDate = true;
        DateSlider control = getSkinnable();
        LocalDate date = control.getEarliestDate().plusDays((long) slider.getValue());
        control.getProperties().put("flexganttfx.dateslider.date", date);
        tooltip.setText(control.getDateTimeFormatter().format(date));
        updatingDate = false;
    }

    private void updateSliderValue() {
        if (!updatingDate) {
            DateSlider control = getSkinnable();
            long days = ChronoUnit.DAYS.between(control.getEarliestDate(), control.getDate());
            slider.setValue(days);
        }
    }

    private void updateSliderMaxValue() {
        DateSlider control = getSkinnable();
        long days = ChronoUnit.DAYS.between(control.getEarliestDate(), control.getLatestDate());
        slider.setMax(days);
    }
}
