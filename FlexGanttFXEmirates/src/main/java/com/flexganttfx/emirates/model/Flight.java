/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.emirates.model;

import com.flexganttfx.model.activity.MutableActivityBase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Flight extends MutableActivityBase<String> {

	public enum ServiceType {
		E, N, Y, J, Z
    }

	private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private static final DateFormat timeFormat = new SimpleDateFormat("HHmm");

	private static final Calendar dateCalendar = Calendar.getInstance();
	private static final Calendar timeCalendar = Calendar.getInstance();

	public static final Set<String> serviceTypes = new HashSet<>();

	private String departureAirport;
	private String arrivalAirport;
	private String flightNo;
	private String aircraft;
	private ServiceType serviceType = ServiceType.E;

	private boolean invalid;

	private int lineIndex = -1;

	public Flight(ROW row) {
		super(row.getFLIGHTNO());

		try {
			Date departureDate = dateFormat.parse(row.getORIGINDATE());
			Date departureTime = timeFormat.parse(Short.toString(row
					.getSTANDARDDEPTIME()));

			Date arrivalDate = dateFormat.parse(row.getARRIVALDATE());
			Date arrivalTime = timeFormat.parse(Short.toString(row
					.getSTANDARDARRIVALTIME()));

			departureDate = createCompleteDate(departureDate, departureTime);
			arrivalDate = createCompleteDate(arrivalDate, arrivalTime);

			setStartTime(departureDate.toInstant());
			setDuration(Duration.between(departureDate.toInstant(),
					arrivalDate.toInstant()));

			String st = row.getSERVICETYPE();
			serviceTypes.add(st);

			serviceType = ServiceType.valueOf(st);

			departureAirport = row.getDEPARTURESTATION();
			arrivalAirport = row.getARRIVALSTATION();
			flightNo = row.getFLIGHTNO();
			aircraft = row.getResource();

			// setTextDecorator(TextDecoratorPosition.LEFT, departureAirport);
		} catch (ParseException e) { // $codepro.audit.disable logExceptions
			invalid = true;
		}
	}

	public boolean isInvalid() {
		return invalid;
	}

	public String getFlightNo() {
		return flightNo;
	}

	public String getDepartureAirport() {
		return departureAirport;
	}

	public String getArrivalAirport() {
		return arrivalAirport;
	}

	public ServiceType getServiceType() {
		return serviceType;
	}

	private Date createCompleteDate(Date date, Date time) {
		dateCalendar.setTime(date);
		timeCalendar.setTime(time);

		dateCalendar.set(Calendar.HOUR_OF_DAY,
				timeCalendar.get(Calendar.HOUR_OF_DAY));
		dateCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));

		return dateCalendar.getTime();
	}

	public String getAircraft() {
		return aircraft;
	}

	public void setLineIndex(int lineIndex) {
		this.lineIndex = lineIndex;
	}

	public int getLineIndex() {
		return lineIndex;
	}
}
