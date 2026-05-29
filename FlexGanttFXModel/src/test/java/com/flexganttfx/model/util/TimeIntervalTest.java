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
package com.flexganttfx.model.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TimeIntervalTest {

    @Test
    public void shouldCreateInterval() {
        // given
        Instant startTime = Instant.now();
        Instant endTime = Instant.now().plus(Duration.ofDays(1));

        // when
        TimeInterval interval = new TimeInterval(startTime, endTime);

        // then
        assertThat(interval.getStartTime(), is(equalTo(startTime)));
        assertThat(interval.getEndTime(), is(equalTo(endTime)));
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenBothArgumentsMissing() {
        Assertions.assertThrows(NullPointerException.class, () ->
                new TimeInterval(null, null));
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenFirstArgumentMissing() {
        Assertions.assertThrows(NullPointerException.class, () ->
                new TimeInterval(null, Instant.now()));
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenSecondArgumentMissing() {
        Assertions.assertThrows(NullPointerException.class, () ->
                new TimeInterval(Instant.now(), null));
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenStartAfterEnd() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new TimeInterval(Instant.now().plus(Duration.ofDays(1)), Instant.now()));
    }
}
