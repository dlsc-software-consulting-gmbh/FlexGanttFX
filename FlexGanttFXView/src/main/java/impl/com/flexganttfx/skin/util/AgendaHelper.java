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
package impl.com.flexganttfx.skin.util;

import com.flexganttfx.model.layout.AgendaLayout;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * A couple of utility methods used for the {@link AgendaLayout}.
 */
public final class AgendaHelper {

    private AgendaHelper() {
    }

    /**
     * Returns the y coordinate for the given local time within the given time
     * interval and on the given height.
     *
     * @param time      the local time
     * @param startTime the start time of the displayed time range
     * @param endTime   the end time of the displayed time range
     * @param height    the available height in pixels
     * @return the y coordinate for the given time
     */
    private static double getTimeLocation(LocalTime time, LocalTime startTime,
                                          LocalTime endTime, double height) {

        long startNano = startTime.toNanoOfDay();
        long endNano = endTime.toNanoOfDay();

        double npp = (endNano - startNano) / height;

        return (time.toNanoOfDay() - startNano) / npp;
    }

    /**
     * Returns the time at the given y-coordinate within the given height and
     * time range.
     *
     * @param y               the location for which to calculate the time
     * @param availableHeight the available height of the row / line in pixels
     * @param startTime       the start time of the displayed time range
     * @param endTime         the end time of the displayed time range
     * @return the time at the given location
     */
    public static LocalTime getTimeAt(double y, double availableHeight,
                                      LocalTime startTime, LocalTime endTime) {

        long startNano = startTime.toNanoOfDay();
        long endNano = endTime.toNanoOfDay();
        double npp = (endNano - startNano) / availableHeight;

        return LocalTime.ofNanoOfDay(Math.max(LocalTime.MIN.toNanoOfDay(),
                Math.min(LocalTime.MAX.toNanoOfDay(),
                        (long) (y * npp) + startNano)));
    }

    /**
     * Returns a list of agenda line locations for the given
     * {@link AgendaLayout} instance, y offset, and row / line height.
     *
     * @param layout  the agenda layout
     * @param yOffset the y-offset in pixels
     * @param height  the available row / line height
     * @return a list of agenda line locations
     */
    public static List<AgendaLineLocation> getLineLocations(AgendaLayout layout, double yOffset, double height) {

        List<AgendaLineLocation> result = new ArrayList<>();

        final LocalTime startTime = layout.getStartTime().withMinute(0);
        final LocalTime endTime = layout.getEndTime();

        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException(
                    "the end time of the agenda layout is BEFORE the start time, start = "
                            + layout.getStartTime() + ", end = "
                            + layout.getEndTime());
        }

        LocalTime time = startTime;
        LocalTime lastTime = null;

        long stepRate = computeStepRate(layout, height);

        boolean nextDay = false;

        do {

            createLinesForTime(layout, yOffset, height, result, time, lastTime);

            lastTime = time;

            LocalTime nextTime = time.plusHours(stepRate);

            if (nextTime.isBefore(time)) {
                nextDay = true;
            } else {
                time = nextTime;
            }
        } while (!time.isAfter(endTime) && !nextDay);

		/*
         * If end time is "round", e.g. 23:00 then we are done after the loop
		 * but if the end time is something like 23:59 then we need to
		 * "manually" draw it
		 */

        if (!time.minusHours(stepRate).equals(layout.getEndTime())) {
            time = layout.getEndTime();
            createLinesForTime(layout, yOffset, height, result, time, lastTime);
        }

        return result;
    }

    private static void createLinesForTime(AgendaLayout layout, double yOffset,
                                           double height, List<AgendaLineLocation> result, LocalTime time,
                                           LocalTime lastTime) {

        final LocalTime startTime = layout.getStartTime().withMinute(0);
        final LocalTime endTime = layout.getEndTime();

        // major line
        double y = yOffset + getTimeLocation(time, startTime, endTime, height);

        result.add(new AgendaLineLocation(time, y, false));

        if (lastTime != null) {
            // minor line

            long minutesUntil = lastTime.until(time, ChronoUnit.MINUTES) / 2;
            LocalTime minorTime = time.minusMinutes(minutesUntil);

            double minorY = yOffset
                    + getTimeLocation(minorTime, startTime, endTime, height);
            result.add(new AgendaLineLocation(minorTime, minorY, true));
        }
    }

    /*
     * Possible step rates: 1, 2, 3, 4, 6, 12, -1;
     */
    static long computeStepRate(AgendaLayout layout, double height) {
        long st = layout.getStartTime().getHour();
        long et = layout.getEndTime().getHour();

		/*
         * MAX = 23:59:59:99999999, so "almost" 24.
		 */
        if (layout.getEndTime().equals(LocalTime.MAX)) {
            et = 24;
        }

        long hours = et - st;

        /*
         * First we try to find a rate that distributes hours equally
         * from start to end.
         */
        for (long rate = 1; rate <= 12; rate++) {
            if (hours % rate == 0) {
                if (height / (hours / rate) >= layout.getMinLineSpacing()) {
                    return rate;
                }
            }
        }

        /*
         * Now we are happy with just any step rate.
         */
        for (long rate = 1; rate <= 12; rate++) {
            if (height / (hours / rate) >= layout.getMinLineSpacing()) {
                return rate;
            }
        }

        return -1;
    }

    /**
     * Stores information about an agenda line location.
     */
    public static final class AgendaLineLocation {

        private final LocalTime time;
        private final double location;
        private final boolean minor;

        /**
         * Constructs a new agenda line location.
         *
         * @param time     the time represented by the line
         * @param location the y-coordinate of the line
         * @param minor    a flag signalling whether this is a minor or a major line
         */
        public AgendaLineLocation(LocalTime time, double location,
                                  boolean minor) {
            this.time = time;
            this.location = ((int) location) + .5;
            this.minor = minor;
        }

        /**
         * Returns the time represented by the line location.
         *
         * @return the time shown by the line
         */
        public LocalTime getTime() {
            return time;
        }

        /**
         * Returns the y-coordinate of the line.
         *
         * @return the location of the line
         */
        public double getLocation() {
            return location;
        }

        /**
         * Determines if the location represents a major or a minor line.
         *
         * @return true if the line is a minor line
         */
        public boolean isMinor() {
            return minor;
        }
    }
}
