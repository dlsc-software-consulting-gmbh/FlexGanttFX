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
package com.flexganttfx.view.util;

import com.flexganttfx.core.LoggingDomain;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.timeline.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.controlsfx.control.PlusMinusSlider;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.logging.Level;

/**
 * A specialized {@link PlusMinusSlider} for controlling the {@link Timeline} inside the
 * {@link GanttChart} view. Updates the start time property of the underlying
 * {@link TimelineModel}.
 *
 * @see TimelineModel#setStartTime(Instant)
 */
public class TimelineScrollBar extends PlusMinusSlider {

    private final ObjectProperty<Timeline> timeline = new SimpleObjectProperty<>(this, "timeline");

    /**
     * Constructs a new scrollbar.
     */
    public TimelineScrollBar() {
        getStyleClass().add("time-slider");

        addEventHandler(PlusMinusEvent.VALUE_CHANGED,
                evt -> {
                    long st = getTimeline().getVisibleStartTime().toEpochMilli();
                    long et = getTimeline().getVisibleEndTime().toEpochMilli();

                    long delta = et - st;

                    double value = evt.getValue();

                    long millis = (long) (value * delta) / 10;

                    TimelineModel<?> model = getTimeline().getModel();
                    Instant time = model.getStartTime().plus(Duration.ofMillis(millis));

                    if (LoggingDomain.NAVIGATION.isLoggable(Level.FINER)) {
                        LoggingDomain.NAVIGATION.finer("visible start time: " + st);
                        LoggingDomain.NAVIGATION.finer("visible end time: " + et);
                        LoggingDomain.NAVIGATION.finer("plus minus slider value: " + value);
                        LoggingDomain.NAVIGATION.finer("setting new time on timeline model to " + time);
                    }

                    double moveX = model.calculateLocationForTime(model.getStartTime()) - model.calculateLocationForTime(time);
                    if (Math.abs(moveX) >= 1) {
                        // performance tuning: no need to redraw subpixel changes
                        model.setStartTime(time);
                    }
                });
    }

    /**
     * Returns the user agent stylesheet used by this scroll bar.
     *
     * @return the stylesheet URL
     */
    @Override
    public String getUserAgentStylesheet() {
        if (ThemingUtil.isAtlantaFXActive(getScene())) {
            return Objects.requireNonNull(GanttChartBase.class.getResource("gantt-atlantafx.css")).toExternalForm();
        }
        return Objects.requireNonNull(GanttChartBase.class.getResource("gantt.css")).toExternalForm();
    }

    /**
     * Stores a reference to the timeline that will be controlled by this scrollbar.
     *
     * @return the timeline property
     * @since 1.6.1
     */
    public final ObjectProperty<Timeline> timelineProperty() {
        return timeline;
    }

    /**
     * Returns the value of {@link #timelineProperty()}.
     *
     * @return the controlled timeline
     * @since 1.6.1
     */
    public final Timeline getTimeline() {
        return timeline.get();
    }

    /**
     * Sets the value of {@link #timelineProperty()}.
     *
     * @param timeline the timeline that will be controlled by this scrollbar
     * @since 1.6.1
     */
    public final void setTimeline(Timeline timeline) {
        this.timeline.set(timeline);
    }
}
