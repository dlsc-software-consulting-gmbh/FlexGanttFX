/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.emirates.model;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Iterator;

import com.flexganttfx.model.Layer;
import com.flexganttfx.model.activity.ChartActivity;
import com.flexganttfx.model.activity.MutableChartActivity;
import com.flexganttfx.model.activity.MutableChartActivityBase;
import com.flexganttfx.model.layout.ChartLayout;
import com.flexganttfx.model.repository.IntervalTreeActivityRepository;
import com.flexganttfx.model.util.TimeInterval;

public class Group extends ModelObject<Group, Aircraft, ChartActivity> {

	private ChartLayout layout = new ChartLayout();

	public Group(String name) {
		setName(name);
		setLayout(layout);
		setHeight(10);
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!(obj instanceof Group)) {
			return false;
		}

		Group g = (Group) obj;
		return getName().equals(g.getName());
	}

	public int getMaximumNumberOfDesiredFlights() {
		return getChildren().size() * 4;
	}

	public int getMinimumNumberOfDesiredFlights() {
		return getChildren().size() * 2;
	}

	public void updateUsageProfile(TimeInterval horizon, Layer capacityLayer,
			Collection<Layer> layers) {

		IntervalTreeActivityRepository<?> repository = (IntervalTreeActivityRepository<?>) getRepository();
		repository.clearActivities();

		ZonedDateTime dayStartTime = ZonedDateTime.ofInstant(
				horizon.getStartTime(), getZoneId()).truncatedTo(
						ChronoUnit.DAYS);
		ZonedDateTime dayEndTime = ZonedDateTime
				.ofInstant(horizon.getStartTime(), getZoneId())
				.truncatedTo(ChronoUnit.DAYS).plusDays(1);

		int maximumNumberOfFlights = 0;

		ZonedDateTime stopTime = ZonedDateTime.ofInstant(horizon.getEndTime(),
				getZoneId());

		do {
			int numberOfFlights = calculateNumberOfFlights(dayStartTime,
					dayEndTime, layers);

			maximumNumberOfFlights = Math.max(maximumNumberOfFlights,
					numberOfFlights);

			if (numberOfFlights > 0) {
				MutableChartActivity capacity = new MutableChartActivityBase<Object>();
				capacity.setChartValue(numberOfFlights);
				capacity.setStartTime(Instant.from(dayStartTime));
				capacity.setEndTime(Instant.from(dayEndTime));

				addActivity(capacityLayer, capacity);				
			}

			dayStartTime = dayStartTime.plusDays(1);
			dayEndTime = dayStartTime.plusDays(1);

		} while (dayEndTime.isBefore(stopTime));

		layout.setMinValue(0);
		layout.setMaxValue(getMaximumNumberOfDesiredFlights() * 1.5);

		layout.getMajorTicks().clear();
		layout.getMinorTicks().clear();

		layout.getMajorTicks().add(
				new Double(getMinimumNumberOfDesiredFlights()));
		layout.getMajorTicks().add(
				new Double(getMaximumNumberOfDesiredFlights()));
	}

	private int calculateNumberOfFlights(ZonedDateTime start,
			ZonedDateTime end, Collection<Layer> layers) {
		int numberOfFlights = 0;

		for (Aircraft child : getChildren()) {

			Aircraft aircraft = child;

			for (Layer layer : layers) {
				Iterator<Flight> iter = aircraft.getRepository().getActivities(
						layer, Instant.from(start), Instant.from(end),
						ChronoUnit.DAYS, getZoneId());

				while (iter.hasNext()) {
					numberOfFlights++;
					iter.next();
				}
			}
		}

		return numberOfFlights;
	}
}
