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
package com.flexganttfx.model.dateline;

import com.flexganttfx.core.StringUtils;
import com.flexganttfx.model.util.SimpleUnit;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * The dateline model provides the dateline control with various pieces of
 * information so that it can layout itself correctly.
 * <ul>
 * <li><b>Scale Resolutions</b> - a scale resolution defines which temporal unit
 * to show (e.g. HOURS) and how to format it. It also contains the information
 * whether it can be shown in a top, bottom, or middle scale. Each model usually
 * defines a long list of such resolutions. The more resolutions are defined the
 * more flexible the dateline control becomes.</li>
 * <li><b>Time Zones</b> - The dateline control allows the user to switch
 * between different time zones. The model defines which zones are available.</li>
 * <li><b>Scale Count</b> - The dateline control is composed of a set of
 * dateline scales (top, bottom, several middle scales). The model can be used
 * to define the currently visible, the minimum and the maximum number of scales
 * that the user can choose to see.</li>
 * <li><b>Temporal Units</b> - The dateline control calls back onto the model to
 * lookup the "next" temporal unit after it has either failed or succeeded to
 * create a scale for the current unit.</li>
 * </ul>
 *
 * <h2>Code Example</h2>
 *
 * <pre>
 * DatelineModel&lt;ChronoUnit&gt; model = new ChronoUnitDatelineModel();
 * model.setMinScaleCount(1);
 * model.setMaxScaleCount(3);
 * model.setScaleCount(2);
 * model.addZoneId("Europe/Zurich");
 *
 * dateline.setModel(model);
 * </pre>
 *
 * @see ChronoUnitDatelineModel
 * @see SimpleUnitDatelineModel
 * @see Resolution
 * @see com.flexganttfx.model.timeline.TimelineModel
 * 
 * @param <T>
 *            the type of the temporal unit for which the model is defined
 *            (normally {@link ChronoUnit} or {@link SimpleUnit}.
 * 
 * @since 1.0
 */
public abstract class DatelineModel<T extends TemporalUnit> {

	/**
	 * Constructs a new model and populates the list of available zone IDs.
	 * 
	 * @since 1.0
	 */
	protected DatelineModel() {
		addZoneId("Europe/Berlin");
		addZoneId("America/New_York");
		addZoneId("Australia/Darwin");
		addZoneId("Australia/Sydney");
		addZoneId("America/Argentina/Buenos_Aires");
		addZoneId("Africa/Cairo");
		addZoneId("America/Anchorage");
		addZoneId("America/Sao_Paulo");
		addZoneId("Asia/Dhaka");
		addZoneId("Africa/Harare");
		addZoneId("America/St_Johns");
		addZoneId("America/Chicago");
		addZoneId("Asia/Shanghai");
		addZoneId("Africa/Addis_Ababa");
		addZoneId("Europe/Paris");
		addZoneId("America/Indiana/Indianapolis");
		addZoneId("Asia/Kolkata");
		addZoneId("Asia/Tokyo");
		addZoneId("Pacific/Apia");
		addZoneId("Asia/Yerevan");
		addZoneId("Pacific/Auckland");
		addZoneId("Asia/Karachi");
		addZoneId("America/Phoenix");
		addZoneId("America/Puerto_Rico");
		addZoneId("America/Los_Angeles");
		addZoneId("Pacific/Guadalcanal");
		addZoneId("Asia/Ho_Chi_Minh");
	}

	private final ObservableSet<String> availableZoneIds = FXCollections.observableSet();

	/**
	 * Adds a {@link ZoneId} to the model.
	 * 
	 * @param zoneId
	 *            the zone ID to add
	 * @throws IllegalArgumentException if the given zone ID is {@code null} or blank
	 * @since 1.0
	 */
	public final void addZoneId(String zoneId) {
		if (StringUtils.isBlank(zoneId)) {
			throw new IllegalArgumentException("zoneId can not be blank");
		}

		getAvailableZoneIds().add(zoneId);
	}

	private final ObservableMap<T, ObservableList<Resolution<? extends T>>> resolutionMap = FXCollections.observableHashMap();

	private final ObservableList<Resolution<? extends T>> resolutions = FXCollections.observableArrayList();

	private final ObservableList<T> temporalUnits = FXCollections.observableArrayList();

	/**
	 * Adds a resolution to the model.
	 * 
	 * @param resolution
	 *            the resolution to add
	 * @since 1.0
	 */
	public final void addResolution(Resolution<T> resolution) {
		requireNonNull(resolution);

		resolutions.add(resolution);

		T temporalUnit = resolution.getTemporalUnit();

		ObservableList<Resolution<? extends T>> list = resolutionMap.computeIfAbsent(temporalUnit, k -> FXCollections.observableArrayList());
		list.add(resolution);

		if (!temporalUnits.contains(temporalUnit)) {
			temporalUnits.add(temporalUnit);
		}
	}

	/**
	 * Removes a resolution from the model.
	 * 
	 * @param resolution
	 *            the resolution that will be removed
	 * @since 1.0
	 */
	public final void removeResolution(Resolution<? extends T> resolution) {
		requireNonNull(resolution);

		resolutions.remove(resolution);

		T temporalUnit = resolution.getTemporalUnit();
		ObservableList<Resolution<? extends T>> list = resolutionMap
				.get(temporalUnit);
		if (list != null) {
			list.remove(resolution);
			if (list.isEmpty()) {
				resolutionMap.remove(temporalUnit);
			}
		}
	}

	/**
	 * Removes all resolutions from the model.
	 * 
	 * @since 1.0
	 */
	public final void clearResolutions() {
		resolutions.clear();
		temporalUnits.clear();
		resolutionMap.clear();
	}

	/**
	 * Removes all resolutions for the given temporal unit.
	 * 
	 * @param temporalUnit
	 *            the temporal unit for which to remove all resolutions
	 * @since 1.0
	 */
	public final void clearResolutions(T temporalUnit) {
		temporalUnits.remove(temporalUnit);

		List<Resolution<? extends T>> list = new ArrayList<>(
				resolutionMap.get(temporalUnit));
		list.forEach(this::removeResolution);
	}

	/**
	 * Returns all temporal units that are being used by the model.
	 * 
	 * @return the list of temporal units used by the model
	 * @since 1.0
	 */
	public final ObservableList<T> getTemporalUnits() {
		return new ReadOnlyListWrapper<>(temporalUnits);
	}

	// Scale count support

	private final IntegerProperty scaleCount = new SimpleIntegerProperty(this, "scaleCount", 2) {

		@Override
		public void set(int value) {
			if (value < getMinScaleCount() || value > getMaxScaleCount()) {
				throw new IllegalArgumentException(
						"scale count must be between " + getMinScaleCount()
								+ " and " + getMaxScaleCount() + " but was "
								+ value);
			}

			super.set(value);
		}
	};

	/**
	 * The property used to store the number of currently visible scales.
	 * 
	 * <p>
	 * Setting a value smaller than {@link #getMinScaleCount()} or larger than {@link #getMaxScaleCount()} will cause an {@link IllegalArgumentException}.
	 *
	 * @return the scale count
	 * @since 1.0
	 */
	public final IntegerProperty scaleCountProperty() {
		return scaleCount;
	}

	/**
	 * Returns the value of {@link #scaleCountProperty()}.
	 * 
	 * @return the scale count
	 * @since 1.0
	 */
	public final int getScaleCount() {
		return scaleCountProperty().get();
	}

	/**
	 * Sets the value of {@link #scaleCountProperty()}.
	 * 
	 * @param count
	 *            the new scale count
	 *             {@link #getMinScaleCount()} or larger than {@link #getMaxScaleCount()}
	 * @since 1.0
	 */
	public final void setScaleCount(int count) {
		scaleCountProperty().set(count);
	}

	// Maximum scale count support

	private final IntegerProperty maxScaleCount = new SimpleIntegerProperty(this, "maxScaleCount", 5) {

		@Override
		public void set(int value) {
			if (value < 1 || value > 5) {
				throw new IllegalArgumentException(
						"max scale count must be between 1 and 5 but was "
								+ value);
			}
			if (value < getMinScaleCount()) {
				throw new IllegalArgumentException(
						"max scale count must be larger than or equal to min scale count");
			}

			super.set(value);

			setScaleCount(Math.min(getScaleCount(), value));
		}
	};

	/**
	 * Returns the property used to store the maximum scale count.
	 * 
	 * <p>
	 * Setting a value outside the range 1 to 5 or a value smaller than {@link #getMinScaleCount()} will cause an {@link IllegalArgumentException}.
	 *
	 * @return the maximum scale count property
	 * @since 1.0
	 */
	public final IntegerProperty maxScaleCountProperty() {
		return maxScaleCount;
	}

	/**
	 * Returns the value of the maximum scale count property.
	 * 
	 * @return the maximum scale count
	 * @since 1.0
	 */
	public final int getMaxScaleCount() {
		return maxScaleCountProperty().get();
	}

	/**
	 * Sets the value of {@link #maxScaleCountProperty()}.
	 * 
	 * @param count
	 *            the new maximum scale count
	 *             1 to 5 or if it is smaller than {@link #getMinScaleCount()}
	 * @since 1.0
	 */
	public final void setMaxScaleCount(int count) {
		maxScaleCountProperty().set(count);
	}

	// Minimum scale count support

	private final IntegerProperty minScaleCount = new SimpleIntegerProperty(this, "minScaleCount", 1) {

		@Override
		public void set(int value) {
			if (value < 1 || value > 5) {
				throw new IllegalArgumentException(
						"min scale count must be between 1 and 5 but was "
								+ value);
			}
			if (value > getMaxScaleCount()) {
				throw new IllegalArgumentException(
						"min scale count must be smaller than or equal to max scale count");
			}

			super.set(value);

			setScaleCount(Math.max(getScaleCount(), value));
		}
	};

	/**
	 * Returns the property used to store the minimum scale count.
	 * 
	 * <p>
	 * Setting a value outside the range 1 to 5 or a value larger than {@link #getMaxScaleCount()} will cause an {@link IllegalArgumentException}.
	 *
	 * @return the minimum scale count property
	 * 
	 * @since 1.0
	 */
	public final IntegerProperty minScaleCountProperty() {
		return minScaleCount;
	}

	/**
	 * Returns the value of {@link #minScaleCountProperty()}.
	 * 
	 * @return the minimum scale count
	 * @since 1.0
	 */
	public final int getMinScaleCount() {
		return minScaleCountProperty().get();
	}

	/**
	 * Sets the value of {@link #minScaleCountProperty()}.
	 * 
	 * @param count
	 *            the new minimum scale count
	 *             1 to 5 or if it is larger than {@link #getMaxScaleCount()}
	 * @since 1.0
	 */
	public final void setMinScaleCount(int count) {
		minScaleCountProperty().set(count);
	}

	/**
	 * Returns all {@link ZoneId} instances that are available for the user to
	 * switch to.
	 * 
	 * @return the available zone IDs
	 * @since 1.0
	 */
	public final ObservableSet<String> getAvailableZoneIds() {
		return availableZoneIds;
	}

	/**
	 * Returns all resolutions that are defined for / supported by this model.
	 * 
	 * @return the dateline resolutions
	 * @since 1.0
	 */
	public final ObservableList<Resolution<? extends T>> getResolutions() {
		return new ReadOnlyListWrapper<>(resolutions);
	}

	/**
	 * Returns all resolutions that are available for the given temporal unit.
	 * 
	 * @param temporalUnit
	 *            the temporal unit for which resolutions are looked up
	 * @return the available resolutions for the given temporal unit
	 * @since 1.0
	 */
	public final Iterator<? extends Resolution<? extends T>> getResolutions(T temporalUnit) {
		List<Resolution<? extends T>> resolutions = resolutionMap.get(temporalUnit);
		if (resolutions == null) {
			return Collections.emptyIterator();
		}

		return resolutions.iterator();
	}

	/**
	 * Returns the next larger temporal unit for the given temporal unit, e.g.
	 * when passing {@link ChronoUnit#HOURS} this method might return
	 * {@link ChronoUnit#DAYS}.
	 * 
	 * @param unit
	 *            the unit for which to return the next higher unit
	 * @return the next higher temporal unit
	 * @since 1.0
	 */
	public abstract T nextTemporalUnit(T unit);
}
