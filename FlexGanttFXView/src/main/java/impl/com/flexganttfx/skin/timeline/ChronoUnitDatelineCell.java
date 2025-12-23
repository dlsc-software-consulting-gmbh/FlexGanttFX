/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.timeline;

import com.flexganttfx.model.dateline.Resolution;
import com.flexganttfx.model.dateline.Resolution.Position;
import com.flexganttfx.model.util.ChronoUnitUtils;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.DatelineCell;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoField.AMPM_OF_DAY;

public class ChronoUnitDatelineCell extends DatelineCell<ChronoUnit> {

	@Override
	public void update(Instant startTime, Instant endTime, Resolution<ChronoUnit> resolution, Dateline dateline, Position scalePosition) {
		super.update(startTime, endTime, resolution, dateline, scalePosition);

		ChronoUnit temporalUnit = resolution.getTemporalUnit();
		ZoneId zoneId = dateline.getZoneId();
		setText(resolution.format(startTime, zoneId));
		ZonedDateTime zonedStartTime = ChronoUnitUtils.truncate(ZonedDateTime.ofInstant(startTime, zoneId), temporalUnit, 1, dateline.getFirstDayOfWeek());

		switch (resolution.getTemporalUnit()) {
		case DAYS:
			getStyleClass().add(zonedStartTime.getDayOfWeek().toString().toLowerCase());
			break;
		case HALF_DAYS:
			getStyleClass().add(zonedStartTime.get(AMPM_OF_DAY) == 0 ? "am" : "pm");
			break;
		case MONTHS:
			getStyleClass().add(zonedStartTime.getMonth().toString().toLowerCase());
			break;
		default:
			break;
		}

		// TODO: add support for calendars that are attached to the dateline
	}
}
