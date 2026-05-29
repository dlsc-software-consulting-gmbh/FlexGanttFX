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
package com.flexganttfx.ical.model.calendar;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import net.fortuna.ical4j.model.component.VEvent;

import com.flexganttfx.model.calendar.MutableCalendarActivityBase;

public class ICalCalendarActivity extends MutableCalendarActivityBase<VEvent> {

	public ICalCalendarActivity(VEvent event) {
		super(event.getSummary().getValue());

		Instant st = Instant.from(ZonedDateTime.ofInstant(
				event.getStartDate().getDate().toInstant(),
				ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS));
		Instant et = st.plus(Duration.ofDays(1));

		setStartTime(st);
		setEndTime(et);
	}
}
