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
package com.flexganttfx.hospital.model;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Layer;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HospitalDataModelTest {

    @Test
    void shouldCreateConflictFreeInitialSchedule() {
        HospitalDataModel model = new HospitalDataModel();

        assertTrue(model.findConflicts().isEmpty());
    }

    @Test
    void shouldScheduleSurgeriesWithinWorkingHours() {
        HospitalDataModel model = new HospitalDataModel();

        for (HospitalCase hospitalCase : model.getCases()) {
            LocalTime startTime = hospitalCase.getSurgeryStart().atZone(model.getZoneId()).toLocalTime();
            LocalTime endTime = hospitalCase.getSurgeryEnd().atZone(model.getZoneId()).toLocalTime();

            assertTrue(!startTime.isBefore(LocalTime.of(7, 0)));
            assertTrue(!endTime.isAfter(LocalTime.of(18, 0)));
        }
    }

    @Test
    void shouldGenerateSampleScheduleForWholeMonth() {
        HospitalDataModel model = new HospitalDataModel();

        LocalDate earliestDate = model.getCases().stream()
                .map(hospitalCase -> hospitalCase.getSurgeryStart().atZone(model.getZoneId()).toLocalDate())
                .min(LocalDate::compareTo)
                .orElseThrow();
        LocalDate latestDate = model.getCases().stream()
                .map(hospitalCase -> hospitalCase.getSurgeryStart().atZone(model.getZoneId()).toLocalDate())
                .max(LocalDate::compareTo)
                .orElseThrow();

        assertEquals(model.getScheduleDate(), earliestDate);
        assertTrue(!latestDate.isBefore(model.getScheduleDate().plusDays(29)));
    }

    @Test
    void shouldRepresentRoomScheduleWithSingleActivityContainingPhaseDurations() {
        HospitalDataModel model = new HospitalDataModel();
        HospitalCase hospitalCase = model.getCases().get(0);
        HospitalRow roomRow = findRoomRow(model, hospitalCase.getRoomName());

        List<HospitalActivity> activities = activitiesIn(roomRow, model.getLayer());
        long matchingActivities = activities.stream()
                .filter(activity -> activity.getUserObject() == hospitalCase)
                .filter(activity -> activity.getRole() == HospitalActivityRole.SURGERY)
                .count();

        HospitalActivity surgery = hospitalCase.getActivity(HospitalActivityRole.SURGERY);
        assertEquals(1, matchingActivities);
        assertEquals(hospitalCase.getOccupiedStart(), surgery.getStartTime());
        assertEquals(hospitalCase.getOccupiedEnd(), surgery.getEndTime());
        assertEquals(Duration.ofMinutes(30), surgery.getPreparationDuration());
        assertEquals(Duration.ofMinutes(30), surgery.getCleanupDuration());
    }

    @Test
    void shouldRescheduleExistingCaseWithoutBreakingRepositoryMembership() {
        HospitalDataModel model = new HospitalDataModel();
        HospitalCase hospitalCase = model.getCases().get(0);
        HospitalActivity surgery = hospitalCase.getActivity(HospitalActivityRole.SURGERY);
        HospitalRow originalRow = findRoomRow(model, hospitalCase.getRoomName());

        hospitalCase.setRoomName("OR-2".equals(hospitalCase.getRoomName()) ? "OR-3" : "OR-2");
        hospitalCase.setSurgeryInterval(hospitalCase.getSurgeryStart().plus(45, ChronoUnit.MINUTES),
                hospitalCase.getSurgeryEnd().plus(45, ChronoUnit.MINUTES));

        assertDoesNotThrow(() -> model.syncCase(hospitalCase));

        HospitalRow updatedRow = findRoomRow(model, hospitalCase.getRoomName());
        assertTrue(containsActivity(updatedRow, model.getLayer(), surgery));
        assertFalse(containsActivity(originalRow, model.getLayer(), surgery));
    }

    @Test
    void shouldRemoveRescheduledCaseWithoutThrowing() {
        HospitalDataModel model = new HospitalDataModel();
        HospitalCase hospitalCase = model.getCases().get(0);

        hospitalCase.setRoomName("OR-2".equals(hospitalCase.getRoomName()) ? "OR-3" : "OR-2");
        hospitalCase.setSurgeryInterval(hospitalCase.getSurgeryStart().plus(30, ChronoUnit.MINUTES),
                hospitalCase.getSurgeryEnd().plus(30, ChronoUnit.MINUTES));
        model.syncCase(hospitalCase);

        assertDoesNotThrow(() -> model.removeCase(hospitalCase));
        assertFalse(model.getCases().contains(hospitalCase));
    }

    private HospitalRow findRoomRow(HospitalDataModel model, String rowName) {
        return model.getRoomRows().stream()
                .filter(row -> rowName.equals(row.getName()))
                .findFirst()
                .orElseThrow();
    }

    private boolean containsActivity(HospitalRow row, Layer layer, HospitalActivity activity) {
        return activitiesIn(row, layer).stream().anyMatch(item -> item == activity);
    }

    private List<HospitalActivity> activitiesIn(HospitalRow row, Layer layer) {
        Instant earliest = row.getRepository().getEarliestTimeUsed();
        Instant latest = row.getRepository().getLatestTimeUsed();
        if (earliest == null || latest == null) {
            return List.of();
        }

        List<HospitalActivity> activities = new ArrayList<>();
        Iterator<Activity> iterator = row.getRepository().getActivities(layer, earliest, latest, ChronoUnit.MINUTES, modelZone(row));
        while (iterator.hasNext()) {
            Activity item = iterator.next();
            if (item instanceof HospitalActivity) {
                activities.add((HospitalActivity) item);
            }
        }

        return activities;
    }

    private java.time.ZoneId modelZone(HospitalRow row) {
        return row.getZoneId();
    }
}
