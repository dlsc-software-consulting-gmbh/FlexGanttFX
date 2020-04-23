/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.util;

import com.flexganttfx.model.Activity;

import java.time.Instant;
import java.time.LocalTime;

/**
 * A helper class for working with activities.
 */
public class ActivityHelper {

    /**
     * Checks whether the time bounds of the two given activities intersect with
     * each other.
     *
     * @param activity1
     *            the first activity
     * @param activity2
     *            the second activity
     *
     * @return true if the time bounds intersect
     */
    public static boolean intersect(Activity activity1, Activity activity2) {
        return intersect(activity1.getStartTime(), activity1.getEndTime(), activity2.getStartTime(), activity2.getEndTime());
    }

    /**
     * Checks whether the two given time bounds intersect with each other.
     *
     * @param startTime1
     *            start time of the first time interval
     * @param endTime1
     *            end time of the first time interval
     * @param startTime2
     *            start time of the second time interval
     * @param endTime2
     *            end time of the second time interval
     * @return true if the time intervals intersect with each other
     */
    public static boolean intersect(Instant startTime1, Instant endTime1,
            Instant startTime2, Instant endTime2) {
        return intersect(startTime1.toEpochMilli(), endTime1.toEpochMilli(), startTime2.toEpochMilli(), endTime2.toEpochMilli());
    }

    /**
     * Checks whether the two given time bounds intersect with each other.
     *
     * @param startTime1
     *            start time of the first time interval
     * @param endTime1
     *            end time of the first time interval
     * @param startTime2
     *            start time of the second time interval
     * @param endTime2
     *            end time of the second time interval
     * @return true if the time intervals intersect with each other
     */
    public static boolean intersect(long startTime1, long endTime1, long startTime2, long endTime2) {

        if (startTime1 == startTime2 || endTime1 == endTime2) {
            return true;
        }

        return startTime1 < endTime2 && startTime2 < endTime1;
    }

    /**
     * Checks whether the two given time bounds intersect with each other.
     *
     * @param startTime1
     *            start time of the first time interval
     * @param endTime1
     *            end time of the first time interval
     * @param startTime2
     *            start time of the second time interval
     * @param endTime2
     *            end time of the second time interval
     * @return true if the time intervals intersect with each other
     */
    public static boolean intersect(LocalTime startTime1, LocalTime endTime1, LocalTime startTime2, LocalTime endTime2) {

        // Same start time or same end time?
        if (startTime1.equals(startTime2) || endTime1.equals(endTime2)) {
            return true;
        }

        return startTime1.isBefore(endTime2) && endTime1.isAfter(startTime2);

    }
}
