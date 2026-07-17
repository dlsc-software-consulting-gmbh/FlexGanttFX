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
package com.flexganttfx.model.timeline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChronoUnitTimelineModelTest {

    private ChronoUnitTimelineModel timelineModel;

    @BeforeEach
    public void setup() {
        timelineModel = new ChronoUnitTimelineModel();
    }

    @Test
    public void shouldNotThrowAnExceptionWhenUsingInstantMinOrMax() {

        // when
        timelineModel.calculateLocationForTime(Instant.MAX);
        timelineModel.calculateLocationForTime(Instant.MIN);
    }

    @Test
    public void shouldNotThrowAnExceptionWhenUsingDoubleMinOrMax() {

        // when
        timelineModel.calculateTimeForLocation(Double.MAX_VALUE);
        timelineModel.calculateTimeForLocation(Double.MIN_VALUE);
    }

    @Test
    public void shouldReturnZeroForStartTimeLocation() {

        // when
        double location = timelineModel.calculateLocationForTime(timelineModel.getStartTime());

        // then
        assertThat(location, is(equalTo(0.0)));
    }

    @Test
    public void shouldReturnMultipleOfUnitWidthForNextDays() {
        // given
        for (int i = 1; i < 30; i++) {
            Instant nextDay = timelineModel.getStartTime();
            nextDay.plus(Duration.ofDays(i));

            // when
            double location = timelineModel.calculateLocationForTime(nextDay);

            // then
            assertThat(location % timelineModel.getMillisPerPixel(), is(equalTo(0.0)));
        }
    }

    @Test
    public void shouldUpdateMillisPerPixel() {
        // given
        timelineModel.setMinimumMillisPerPixel(100);
        timelineModel.setMaximumMillisPerPixel(500);

        // when
        timelineModel.setMillisPerPixel(222.0);

        // then
        assertThat(timelineModel.getMillisPerPixel(), is(equalTo(222.0)));
    }

    @Test
    public void shouldUpdateStartTime() {
        // given
        Instant time = Instant.now().plus(Duration.ofDays(1));

        // when
        timelineModel.setStartTime(time);

        // then
        assertThat(timelineModel.getStartTime(), is(equalTo(time)));
    }

    @Test
    public void shouldNotAllowNullStartTime() {
        assertThrows(NullPointerException.class, () -> timelineModel.setStartTime(null));
    }
}
