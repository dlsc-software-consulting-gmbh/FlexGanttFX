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

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class HospitalCase {

    public static final Duration DEFAULT_PREPARATION_DURATION = Duration.ofMinutes(30);
    public static final Duration DEFAULT_CLEANUP_DURATION = Duration.ofMinutes(30);

    private final String id;
    private final Map<HospitalActivityRole, HospitalActivity> activities = new EnumMap<>(HospitalActivityRole.class);

    private String patientName;
    private String procedureName;
    private String roomName;
    private String surgeonName;
    private String anesthesiologistName;
    private String equipmentName;
    private Instant surgeryStart;
    private Instant surgeryEnd;
    private Duration preparationDuration;
    private Duration cleanupDuration;
    private boolean emergency;

    public HospitalCase(String id, String patientName, String procedureName, String roomName, String surgeonName,
                        String anesthesiologistName, String equipmentName, Instant surgeryStart, Instant surgeryEnd,
                        Duration preparationDuration, Duration cleanupDuration, boolean emergency) {
        this.id = requireNonNull(id);
        setPatientName(patientName);
        setProcedureName(procedureName);
        setRoomName(roomName);
        setSurgeonName(surgeonName);
        setAnesthesiologistName(anesthesiologistName);
        setEquipmentName(equipmentName);
        setPreparationDuration(preparationDuration);
        setCleanupDuration(cleanupDuration);
        setSurgeryInterval(surgeryStart, surgeryEnd);
        setEmergency(emergency);
    }

    public String getId() {
        return id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = requireNonNull(patientName);
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = requireNonNull(procedureName);
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = requireNonNull(roomName);
    }

    public String getSurgeonName() {
        return surgeonName;
    }

    public void setSurgeonName(String surgeonName) {
        this.surgeonName = requireNonNull(surgeonName);
    }

    public String getAnesthesiologistName() {
        return anesthesiologistName;
    }

    public void setAnesthesiologistName(String anesthesiologistName) {
        this.anesthesiologistName = requireNonNull(anesthesiologistName);
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = requireNonNull(equipmentName);
    }

    public Instant getSurgeryStart() {
        return surgeryStart;
    }

    public Instant getSurgeryEnd() {
        return surgeryEnd;
    }

    public void setSurgeryInterval(Instant surgeryStart, Instant surgeryEnd) {
        this.surgeryStart = requireNonNull(surgeryStart);
        this.surgeryEnd = requireNonNull(surgeryEnd);
        if (!surgeryEnd.isAfter(surgeryStart)) {
            throw new IllegalArgumentException("surgery end must be after surgery start");
        }
    }

    public Duration getPreparationDuration() {
        return preparationDuration;
    }

    public void setPreparationDuration(Duration preparationDuration) {
        this.preparationDuration = requireNonNull(preparationDuration);
    }

    public Duration getCleanupDuration() {
        return cleanupDuration;
    }

    public void setCleanupDuration(Duration cleanupDuration) {
        this.cleanupDuration = requireNonNull(cleanupDuration);
    }

    public boolean isEmergency() {
        return emergency;
    }

    public void setEmergency(boolean emergency) {
        this.emergency = emergency;
    }

    public Duration getSurgeryDuration() {
        return Duration.between(surgeryStart, surgeryEnd);
    }

    public Instant getOccupiedStart() {
        return surgeryStart.minus(preparationDuration);
    }

    public Instant getOccupiedEnd() {
        return surgeryEnd.plus(cleanupDuration);
    }

    public Duration getOccupiedDuration() {
        return Duration.between(getOccupiedStart(), getOccupiedEnd());
    }

    public void setOccupiedInterval(Instant occupiedStart, Instant occupiedEnd) {
        Instant normalizedStart = requireNonNull(occupiedStart);
        Instant normalizedEnd = requireNonNull(occupiedEnd);
        if (!normalizedEnd.isAfter(normalizedStart)) {
            throw new IllegalArgumentException("occupied end must be after occupied start");
        }

        Instant newSurgeryStart = normalizedStart.plus(preparationDuration);
        Instant newSurgeryEnd = normalizedEnd.minus(cleanupDuration);
        if (!newSurgeryEnd.isAfter(newSurgeryStart)) {
            throw new IllegalArgumentException("occupied interval must leave room for the surgery phase");
        }

        setSurgeryInterval(newSurgeryStart, newSurgeryEnd);
    }

    public HospitalActivity getActivity(HospitalActivityRole role) {
        return activities.get(role);
    }

    public void setActivity(HospitalActivityRole role, HospitalActivity activity) {
        activities.put(role, activity);
    }

    public String getDisplayName() {
        return (emergency ? "ER - " : "") + patientName;
    }
}
