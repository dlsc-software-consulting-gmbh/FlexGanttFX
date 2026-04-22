/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.hospital.model;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityLink;
import com.flexganttfx.model.Layer;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class HospitalDataModel {

    private static final int SAMPLE_SCHEDULE_DAYS = 30;
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();
    private static final LocalTime SURGERY_DAY_START = LocalTime.of(7, 0);
    private static final LocalTime SURGERY_DAY_END = LocalTime.of(18, 0);

    private final AtomicInteger idCounter = new AtomicInteger(100);
    private final Layer layer = new Layer("Operating Room Schedule");
    private final LocalDate scheduleDate = LocalDate.now().plusDays(1);

    private final HospitalRow roomRoot = new HospitalRow("Operating Rooms");
    private final HospitalRow resourceRoot = new HospitalRow("Resources");

    private final Map<String, HospitalRow> roomRows = new LinkedHashMap<>();
    private final Map<String, HospitalRow> surgeonRows = new LinkedHashMap<>();
    private final Map<String, HospitalRow> anesthesiaRows = new LinkedHashMap<>();
    private final Map<String, HospitalRow> equipmentRows = new LinkedHashMap<>();

    private final Map<String, HospitalCase> cases = new LinkedHashMap<>();
    private final List<ActivityLink<Activity>> links = new ArrayList<>();

    public HospitalDataModel() {
        createRows();
        createSampleCases();
    }

    public Layer getLayer() {
        return layer;
    }

    public HospitalRow getRoomRoot() {
        return roomRoot;
    }

    public HospitalRow getResourceRoot() {
        return resourceRoot;
    }

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public ZoneId getZoneId() {
        return ZONE_ID;
    }

    public List<ActivityLink<Activity>> getLinks() {
        return List.copyOf(links);
    }

    public List<HospitalCase> getCases() {
        return new ArrayList<>(cases.values());
    }

    public List<String> getRoomNames() {
        return new ArrayList<>(roomRows.keySet());
    }

    public List<HospitalRow> getRoomRows() {
        return new ArrayList<>(roomRows.values());
    }

    public List<HospitalRow> getResourceRows() {
        List<HospitalRow> rows = new ArrayList<>();
        rows.addAll(surgeonRows.values());
        rows.addAll(anesthesiaRows.values());
        rows.addAll(equipmentRows.values());
        return rows;
    }

    public List<String> getSurgeonNames() {
        return new ArrayList<>(surgeonRows.keySet());
    }

    public List<String> getAnesthesiologistNames() {
        return new ArrayList<>(anesthesiaRows.keySet());
    }

    public List<String> getEquipmentNames() {
        return new ArrayList<>(equipmentRows.keySet());
    }

    public HospitalCase createCase(String patientName, String procedureName, String roomName, String surgeonName,
                                   String anesthesiologistName, String equipmentName, LocalDate date, int hour,
                                    int minute, int durationMinutes, boolean emergency) {
        return createCase(patientName, procedureName, roomName, surgeonName, anesthesiologistName, equipmentName,
                date, hour, minute, durationMinutes, HospitalCase.DEFAULT_PREPARATION_DURATION,
                HospitalCase.DEFAULT_CLEANUP_DURATION, emergency);
    }

    public HospitalCase createCase(String patientName, String procedureName, String roomName, String surgeonName,
                                   String anesthesiologistName, String equipmentName, LocalDate date, int hour,
                                   int minute, int durationMinutes, Duration preparationDuration,
                                   Duration cleanupDuration, boolean emergency) {
        Instant start = LocalDateTime.of(date, LocalTime.of(hour, minute)).atZone(ZONE_ID).toInstant();
        Instant end = start.plus(durationMinutes, ChronoUnit.MINUTES);
        return new HospitalCase("CASE-" + idCounter.incrementAndGet(), patientName, procedureName, roomName,
                surgeonName, anesthesiologistName, equipmentName, start, end, preparationDuration, cleanupDuration,
                emergency);
    }

    public void addCase(HospitalCase hospitalCase) {
        cases.put(hospitalCase.getId(), hospitalCase);
        ensureActivities(hospitalCase);
        syncCase(hospitalCase);
    }

    public void removeCase(HospitalCase hospitalCase) {
        if (hospitalCase == null) {
            return;
        }

        for (HospitalActivityRole role : HospitalActivityRole.values()) {
            HospitalActivity activity = hospitalCase.getActivity(role);
            if (activity != null) {
                detachActivity(activity);
            }
        }

        cases.remove(hospitalCase.getId());
        rebuildLinks();
    }

    public void syncCase(HospitalCase hospitalCase) {
        Objects.requireNonNull(hospitalCase);
        ensureActivities(hospitalCase);

        HospitalActivity surgery = hospitalCase.getActivity(HospitalActivityRole.SURGERY);
        HospitalActivity surgeon = hospitalCase.getActivity(HospitalActivityRole.SURGEON);
        HospitalActivity anesthesia = hospitalCase.getActivity(HospitalActivityRole.ANESTHESIA);
        HospitalActivity equipment = hospitalCase.getActivity(HospitalActivityRole.EQUIPMENT);

        detachActivity(surgery);
        detachActivity(surgeon);
        detachActivity(anesthesia);
        detachActivity(equipment);

        surgery.setName(hospitalCase.getDisplayName() + " - " + hospitalCase.getProcedureName());
        surgery.setPreparationDuration(hospitalCase.getPreparationDuration());
        surgery.setCleanupDuration(hospitalCase.getCleanupDuration());
        surgery.setStartTime(hospitalCase.getOccupiedStart());
        surgery.setEndTime(hospitalCase.getOccupiedEnd());

        surgeon.setName(hospitalCase.getPatientName() + " - " + hospitalCase.getSurgeonName());
        surgeon.setStartTime(hospitalCase.getSurgeryStart());
        surgeon.setEndTime(hospitalCase.getSurgeryEnd());
        surgeon.setPreparationDuration(Duration.ZERO);
        surgeon.setCleanupDuration(Duration.ZERO);

        anesthesia.setName(hospitalCase.getPatientName() + " - " + hospitalCase.getAnesthesiologistName());
        anesthesia.setStartTime(hospitalCase.getSurgeryStart());
        anesthesia.setEndTime(hospitalCase.getSurgeryEnd());
        anesthesia.setPreparationDuration(Duration.ZERO);
        anesthesia.setCleanupDuration(Duration.ZERO);

        equipment.setName(hospitalCase.getPatientName() + " - " + hospitalCase.getEquipmentName());
        equipment.setStartTime(hospitalCase.getSurgeryStart());
        equipment.setEndTime(hospitalCase.getSurgeryEnd());
        equipment.setPreparationDuration(Duration.ZERO);
        equipment.setCleanupDuration(Duration.ZERO);

        attachActivity(surgery, roomRows.get(hospitalCase.getRoomName()));
        attachActivity(surgeon, surgeonRows.get(hospitalCase.getSurgeonName()));
        attachActivity(anesthesia, anesthesiaRows.get(hospitalCase.getAnesthesiologistName()));
        attachActivity(equipment, equipmentRows.get(hospitalCase.getEquipmentName()));

        rebuildLinks();
    }

    public List<ScheduleConflict> findConflicts() {
        List<ScheduleConflict> conflicts = new ArrayList<>();
        findConflictsInRows(conflicts, new ArrayList<>(roomRows.values()), true);
        findConflictsInRows(conflicts, new ArrayList<>(surgeonRows.values()), false);
        findConflictsInRows(conflicts, new ArrayList<>(anesthesiaRows.values()), false);
        findConflictsInRows(conflicts, new ArrayList<>(equipmentRows.values()), false);
        return conflicts;
    }

    public void applySuggestion(ScheduleConflict conflict) {
        HospitalActivity first = conflict.getFirst();
        HospitalActivity second = conflict.getSecond();

        HospitalCase earlierCase = first.getStartTime().isAfter(second.getStartTime()) ? second.getUserObject() : first.getUserObject();
        HospitalCase laterCase = earlierCase == first.getUserObject() ? second.getUserObject() : first.getUserObject();
        HospitalActivity earlierActivity = earlierCase == first.getUserObject() ? first : second;

        if (conflict.isRoomConflict()) {
            Instant occupiedStart = earlierActivity.getEndTime();
            laterCase.setOccupiedInterval(occupiedStart, occupiedStart.plus(laterCase.getOccupiedDuration()));
        } else {
            Duration duration = laterCase.getSurgeryDuration();
            Instant suggestedStart = earlierActivity.getEndTime();
            laterCase.setSurgeryInterval(suggestedStart, suggestedStart.plus(duration));
        }
        syncCase(laterCase);
    }

    private void createRows() {
        HospitalRow theatres = new HospitalRow("Theatres");
        roomRoot.getChildren().add(theatres);
        for (int i = 1; i <= 9; i++) {
            addChildRow(theatres, roomRows, "OR-" + i);
        }

        HospitalRow surgeons = new HospitalRow("Surgeons");
        resourceRoot.getChildren().add(surgeons);
        addChildRow(surgeons, surgeonRows, "Dr. Chen");
        addChildRow(surgeons, surgeonRows, "Dr. Fischer");
        addChildRow(surgeons, surgeonRows, "Dr. Rossi");
        addChildRow(surgeons, surgeonRows, "Dr. Kumar");
        addChildRow(surgeons, surgeonRows, "Dr. Novak");
        addChildRow(surgeons, surgeonRows, "Dr. Silva");

        HospitalRow anesthesia = new HospitalRow("Anesthesia");
        resourceRoot.getChildren().add(anesthesia);
        addChildRow(anesthesia, anesthesiaRows, "Dr. Alvarez");
        addChildRow(anesthesia, anesthesiaRows, "Dr. Meier");
        addChildRow(anesthesia, anesthesiaRows, "Dr. Sato");
        addChildRow(anesthesia, anesthesiaRows, "Dr. Laurent");

        HospitalRow equipment = new HospitalRow("Equipment");
        resourceRoot.getChildren().add(equipment);
        addChildRow(equipment, equipmentRows, "Da Vinci Robot");
        addChildRow(equipment, equipmentRows, "Hybrid C-Arm");
        addChildRow(equipment, equipmentRows, "Ortho Tower");
    }

    private void addChildRow(HospitalRow parent, Map<String, HospitalRow> rows, String name) {
        HospitalRow row = new HospitalRow(name);
        parent.getChildren().add(row);
        rows.put(name, row);
    }

    private void createSampleCases() {
        String[] patients = {
                "Nora Patel", "Leo Meyer", "Ella Rossi", "Mia Weber", "Jonas Keller",
                "Clara Baum", "Felix Hartmann", "Sofia Marino", "David Schmid", "Lina Vogt",
                "Paul Gerber", "Eva Keller", "Owen Costa", "Lea Berger", "Tom Novak",
                "Iris Walter", "Mila Santos", "Noah Steiner", "Ava Laurent", "Ben Fischer",
                "Mara Silva", "Jan Meier", "Tina Koch", "Luis Romero", "Emma Sato"
        };
        String[] procedures = {
                "Knee Replacement", "Spinal Fusion", "Valve Repair", "Shoulder Revision", "Tumor Resection",
                "Hip Arthroscopy", "Aneurysm Repair", "Mitral Clip", "Lumbar Decompression", "ACL Revision",
                "Thoracic Drainage", "Rotator Cuff Repair", "Robotic Prostatectomy", "Cervical Fusion", "Bypass Revision",
                "Fracture Fixation", "Lobectomy", "Nephrectomy", "Carotid Endarterectomy", "Meniscus Repair",
                "Colectomy", "Pacemaker Lead Revision", "Pelvic Reconstruction", "Hernia Repair", "Valve Replacement"
        };
        String[] roomNames = getRoomNames().toArray(new String[0]);
        String[] surgeonNames = getSurgeonNames().toArray(new String[0]);
        String[] anesthesiaNames = getAnesthesiologistNames().toArray(new String[0]);
        String[] equipmentNames = getEquipmentNames().toArray(new String[0]);
        int[] durations = {90, 105, 120, 135, 150, 165, 180};
        Instant startOfDay = LocalDateTime.of(scheduleDate, LocalTime.of(7, 0)).atZone(ZONE_ID).toInstant();
        LocalDate lastScheduleDate = scheduleDate.plusDays(SAMPLE_SCHEDULE_DAYS - 1);

        Map<String, Instant> roomAvailability = initializeAvailability(roomNames, startOfDay);
        Map<String, Instant> surgeonAvailability = initializeAvailability(surgeonNames, startOfDay);
        Map<String, Instant> anesthesiaAvailability = initializeAvailability(anesthesiaNames, startOfDay);
        Map<String, Instant> equipmentAvailability = initializeAvailability(equipmentNames, startOfDay);

        for (int i = 0; ; i++) {
            int roomIndex = i % roomNames.length;
            int duration = durations[(i * 2 + roomIndex) % durations.length];
            boolean emergency = i == 8 || i == 17 || i == 23;
            String roomName = roomNames[roomIndex];
            String surgeonName = surgeonNames[i % surgeonNames.length];
            String anesthesiaName = anesthesiaNames[(i + 1) % anesthesiaNames.length];
            String equipmentName = equipmentNames[(i + 2) % equipmentNames.length];
            Instant surgeryStart = normalizeSurgeryStart(latest(
                    roomAvailability.get(roomName),
                    surgeonAvailability.get(surgeonName),
                    anesthesiaAvailability.get(anesthesiaName),
                    equipmentAvailability.get(equipmentName)
            ), duration);
            Instant surgeryEnd = surgeryStart.plus(duration, ChronoUnit.MINUTES);

            HospitalCase hospitalCase = new HospitalCase(
                    "CASE-" + idCounter.incrementAndGet(),
                    sampleLabel(patients, i),
                    sampleLabel(procedures, i),
                    roomName,
                    surgeonName,
                    anesthesiaName,
                    equipmentName,
                    surgeryStart,
                    surgeryEnd,
                    HospitalCase.DEFAULT_PREPARATION_DURATION,
                    HospitalCase.DEFAULT_CLEANUP_DURATION,
                    emergency
            );
            addCase(hospitalCase);

            roomAvailability.put(roomName, surgeryEnd
                    .plus(HospitalCase.DEFAULT_PREPARATION_DURATION)
                    .plus(HospitalCase.DEFAULT_CLEANUP_DURATION));
            surgeonAvailability.put(surgeonName, surgeryEnd);
            anesthesiaAvailability.put(anesthesiaName, surgeryEnd);
            equipmentAvailability.put(equipmentName, surgeryEnd);

            if (!LocalDateTime.ofInstant(surgeryStart, ZONE_ID).toLocalDate().isBefore(lastScheduleDate)) {
                break;
            }
        }
    }

    private String sampleLabel(String[] values, int index) {
        String value = values[index % values.length];
        int cycle = index / values.length;
        return cycle == 0 ? value : value + " " + (cycle + 1);
    }

    private Map<String, Instant> initializeAvailability(String[] resourceNames, Instant startTime) {
        Map<String, Instant> availability = new LinkedHashMap<>();
        for (String resourceName : resourceNames) {
            availability.put(resourceName, startTime);
        }
        return availability;
    }

    private Instant latest(Instant... instants) {
        Instant latest = instants[0];
        for (int i = 1; i < instants.length; i++) {
            if (instants[i].isAfter(latest)) {
                latest = instants[i];
            }
        }
        return latest;
    }

    private Instant normalizeSurgeryStart(Instant candidateStart, int durationMinutes) {
        Instant normalizedStart = candidateStart;

        while (true) {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(normalizedStart, ZONE_ID);
            LocalDate date = localDateTime.toLocalDate();
            LocalTime time = localDateTime.toLocalTime();

            if (time.isBefore(SURGERY_DAY_START)) {
                normalizedStart = LocalDateTime.of(date, SURGERY_DAY_START).atZone(ZONE_ID).toInstant();
                continue;
            }

            if (!time.isBefore(SURGERY_DAY_END)) {
                normalizedStart = LocalDateTime.of(date.plusDays(1), SURGERY_DAY_START).atZone(ZONE_ID).toInstant();
                continue;
            }

            Instant end = normalizedStart.plus(durationMinutes, ChronoUnit.MINUTES);
            LocalDateTime endDateTime = LocalDateTime.ofInstant(end, ZONE_ID);
            if (!endDateTime.toLocalDate().equals(date) || endDateTime.toLocalTime().isAfter(SURGERY_DAY_END)) {
                normalizedStart = LocalDateTime.of(date.plusDays(1), SURGERY_DAY_START).atZone(ZONE_ID).toInstant();
                continue;
            }

            return normalizedStart;
        }
    }

    private void ensureActivities(HospitalCase hospitalCase) {
        for (HospitalActivityRole role : HospitalActivityRole.values()) {
            if (hospitalCase.getActivity(role) == null) {
                if (role.isRoomRole()) {
                    hospitalCase.setActivity(role, new HospitalActivity(hospitalCase, role,
                            hospitalCase.getOccupiedStart(), hospitalCase.getOccupiedEnd(),
                            hospitalCase.getPreparationDuration(), hospitalCase.getCleanupDuration()));
                } else {
                    hospitalCase.setActivity(role, new HospitalActivity(hospitalCase, role,
                            hospitalCase.getSurgeryStart(), hospitalCase.getSurgeryEnd(),
                            Duration.ZERO, Duration.ZERO));
                }
            }
        }
    }

    private void attachActivity(HospitalActivity activity, HospitalRow targetRow) {
        if (targetRow == null) {
            return;
        }

        targetRow.addActivity(layer, activity);
    }

    private void detachActivity(HospitalActivity activity) {
        HospitalRow row = findOwningRow(activity);
        if (row != null) {
            row.removeActivity(layer, activity);
        }
    }

    private HospitalRow findOwningRow(HospitalActivity activity) {
        for (HospitalRow row : getAllLeafRows()) {
            Instant earliest = row.getRepository().getEarliestTimeUsed();
            Instant latest = row.getRepository().getLatestTimeUsed();
            if (earliest == null || latest == null) {
                continue;
            }

            Iterator<Activity> iterator = row.getRepository().getActivities(layer, earliest, latest, ChronoUnit.MINUTES, ZONE_ID);
            while (iterator.hasNext()) {
                if (iterator.next() == activity) {
                    return row;
                }
            }
        }

        return null;
    }

    private List<HospitalRow> getAllLeafRows() {
        List<HospitalRow> rows = new ArrayList<>();
        rows.addAll(roomRows.values());
        rows.addAll(surgeonRows.values());
        rows.addAll(anesthesiaRows.values());
        rows.addAll(equipmentRows.values());
        return rows;
    }

    private void rebuildLinks() {
        links.clear();
    }

    private void findConflictsInRows(List<ScheduleConflict> conflicts, List<HospitalRow> rows, boolean roomConflict) {
        for (HospitalRow row : rows) {
            List<HospitalActivity> activities = getActivities(row, roomConflict);
            activities.sort(Comparator.comparing(HospitalActivity::getStartTime));

            for (int i = 0; i < activities.size(); i++) {
                HospitalActivity current = activities.get(i);
                for (int j = i + 1; j < activities.size(); j++) {
                    HospitalActivity next = activities.get(j);
                    if (!next.getStartTime().isBefore(current.getEndTime())) {
                        break;
                    }
                    if (!current.getUserObject().getId().equals(next.getUserObject().getId())) {
                        String message = roomConflict
                                ? row.getName() + " is overbooked between " + current.getName() + " and " + next.getName()
                                : row.getName() + " is double-booked for " + current.getUserObject().getPatientName()
                                        + " and " + next.getUserObject().getPatientName();
                        conflicts.add(new ScheduleConflict(message, row.getName(), current, next, roomConflict));
                    }
                }
            }
        }
    }

    private List<HospitalActivity> getActivities(HospitalRow row, boolean roomConflict) {
        List<HospitalActivity> activities = new ArrayList<>();
        Instant start = row.getRepository().getEarliestTimeUsed();
        Instant end = row.getRepository().getLatestTimeUsed();
        if (start == null || end == null) {
            return activities;
        }
        Iterator<Activity> iterator = row.getRepository().getActivities(layer, start, end, ChronoUnit.MINUTES, ZONE_ID);
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity instanceof HospitalActivity) {
                HospitalActivity hospitalActivity = (HospitalActivity) activity;
                if (roomConflict && hospitalActivity.getRole().isRoomRole()) {
                    activities.add(hospitalActivity);
                } else if (!roomConflict && hospitalActivity.getRole().isResourceRole()) {
                    activities.add(hospitalActivity);
                }
            }
        }
        return activities;
    }

    public static final class ScheduleConflict {

        private final String message;
        private final String rowName;
        private final HospitalActivity first;
        private final HospitalActivity second;
        private final boolean roomConflict;

        public ScheduleConflict(String message, String rowName, HospitalActivity first, HospitalActivity second,
                                boolean roomConflict) {
            this.message = message;
            this.rowName = rowName;
            this.first = first;
            this.second = second;
            this.roomConflict = roomConflict;
        }

        public String getMessage() {
            return message;
        }

        public String getRowName() {
            return rowName;
        }

        public HospitalActivity getFirst() {
            return first;
        }

        public HospitalActivity getSecond() {
            return second;
        }

        public boolean isRoomConflict() {
            return roomConflict;
        }

        @Override
        public String toString() {
            return message;
        }
    }
}
