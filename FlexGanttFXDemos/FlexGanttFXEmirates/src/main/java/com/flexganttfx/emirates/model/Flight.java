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
package com.flexganttfx.emirates.model;

import com.flexganttfx.model.activity.MutableActivityBase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

	public Flight() {
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

		dateCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
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

	public void setDepartureAirport(String departureAirport) {
		this.departureAirport = departureAirport;
	}

	public void setArrivalAirport(String arrivalAirport) {
		this.arrivalAirport = arrivalAirport;
	}

	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}

	public void setAircraft(String aircraft) {
		this.aircraft = aircraft;
	}

	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}
}
