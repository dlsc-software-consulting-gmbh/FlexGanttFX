/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.airport.model;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityLink;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates a realistic sample day of airport ground operations for Frankfurt
 * International Airport. Eight aircraft each make 2–3 rotations with a full
 * sequence of ground ops: BAGGAGE_UNLOAD → CLEANING → CATERING → BAGGAGE_LOAD
 * → BOARDING → PUSHBACK (with REFUELING overlapping CLEANING).
 *
 * <p>ActivityLinks expressing FINISH_TO_START dependencies between sequential
 * ops are stored in {@link #getLinks()}.
 */
public class SampleDataGenerator {

    private static final String[] AIRCRAFT_IDS = {
        "D-AIMA", "D-AIMB", "D-AIMC", "D-AIMD",
        "D-AIME", "D-AIMF", "D-AIMG", "D-AIMH"
    };

    // Flight slot offsets from midnight (hours)
    private static final int[] SLOT_HOURS = {6, 10, 14, 18};

    private static final String[][] GATE_NAMES = {
        {"A10", "A11", "A12", "A13"},
        {"B21", "B22", "B23", "B24"}
    };

    private final AircraftRow fleetRoot;
    private final Terminal terminalsRoot;
    private final List<AircraftRow> aircraftRows = new ArrayList<>();
    private final List<Terminal> terminals = new ArrayList<>();
    private final List<ActivityLink<Activity>> links = new ArrayList<>();
    private final Layer flightsLayer;
    private final Layer groundOpsLayer;

    public SampleDataGenerator(Layer flightsLayer, Layer groundOpsLayer) {
        this.flightsLayer = flightsLayer;
        this.groundOpsLayer = groundOpsLayer;

        fleetRoot = new AircraftRow("FRA Fleet");
        fleetRoot.setExpanded(true);
        terminalsRoot = new Terminal("FRA Terminals");
        terminalsRoot.setExpanded(true);

        buildGateHierarchy();
        buildAircraftData();
    }

    private void buildGateHierarchy() {
        for (int t = 0; t < 2; t++) {
            Terminal terminal = new Terminal("Terminal " + (t + 1));
            terminal.setExpanded(true);
            for (String gateName : GATE_NAMES[t]) {
                terminal.getChildren().add(new Gate(gateName));
            }
            terminalsRoot.getChildren().add(terminal);
            terminals.add(terminal);
        }
    }

    private void buildAircraftData() {
        Instant midnight = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        for (int i = 0; i < AIRCRAFT_IDS.length; i++) {
            AircraftRow row = new AircraftRow(AIRCRAFT_IDS[i]);
            fleetRoot.getChildren().add(row);
            aircraftRows.add(row);

            // Each aircraft gets 2–3 flight slots (alternate to stagger)
            int slots = (i % 2 == 0) ? 3 : 2;
            int[] slotOffsets = (slots == 3)
                    ? new int[]{SLOT_HOURS[0], SLOT_HOURS[1], SLOT_HOURS[2]}
                    : new int[]{SLOT_HOURS[1], SLOT_HOURS[3]};

            // Pick a gate for this aircraft (round-robin across all gates)
            Gate gate = findGate(i % 8);

            for (int s = 0; s < slotOffsets.length; s++) {
                Instant slotStart = midnight.plus(Duration.ofHours(slotOffsets[s]));
                addFlightAndOps(row, gate, AIRCRAFT_IDS[i], s, slotStart);
            }
        }
    }

    private void addFlightAndOps(AircraftRow aircraftRow, Gate gate,
                                  String aircraftId, int slotIndex, Instant slotStart) {
        String flightNo = "LH" + (100 + aircraftRows.size() * 10 + slotIndex);

        // Flight block spans the full turnaround (≈ 4 hours)
        Instant flightEnd = slotStart.plus(Duration.ofHours(4));
        Flight flight = new Flight(flightNo, slotStart, flightEnd);
        aircraftRow.addActivity(flightsLayer, flight);
        gate.addActivity(flightsLayer, flight);

        // Sequential ground ops
        Instant t = slotStart.plus(Duration.ofMinutes(5));

        GroundOp baggageUnload = new GroundOp("Baggage Unload", OpType.BAGGAGE_UNLOAD,
                t, t = t.plus(Duration.ofMinutes(45)));
        GroundOp cleaning = new GroundOp("Cleaning", OpType.CLEANING,
                t, t.plus(Duration.ofMinutes(30)));
        // Refueling starts same time as cleaning (overlaps)
        GroundOp refueling = new GroundOp("Refueling", OpType.REFUELING,
                t, t.plus(Duration.ofMinutes(35)));
        t = t.plus(Duration.ofMinutes(30)); // advance past cleaning

        GroundOp catering = new GroundOp("Catering", OpType.CATERING,
                t, t = t.plus(Duration.ofMinutes(40)));
        GroundOp baggageLoad = new GroundOp("Baggage Load", OpType.BAGGAGE_LOAD,
                t, t = t.plus(Duration.ofMinutes(50)));
        GroundOp boarding = new GroundOp("Boarding", OpType.BOARDING,
                t, t = t.plus(Duration.ofMinutes(60)));
        GroundOp pushback = new GroundOp("Pushback", OpType.PUSHBACK,
                t, t.plus(Duration.ofMinutes(20)));

        for (GroundOp op : new GroundOp[]{baggageUnload, cleaning, refueling, catering, baggageLoad, boarding, pushback}) {
            aircraftRow.addActivity(groundOpsLayer, op);
            gate.addActivity(groundOpsLayer, op);
        }

        // ActivityLinks: FINISH_TO_START sequential chain (on aircraft row)
        addLink(aircraftRow, baggageUnload, cleaning);
        addLink(aircraftRow, cleaning, catering);
        addLink(aircraftRow, catering, baggageLoad);
        addLink(aircraftRow, baggageLoad, boarding);
        addLink(aircraftRow, boarding, pushback);
    }

    private void addLink(AircraftRow row, GroundOp source, GroundOp target) {
        ActivityRef<Activity> sourceRef = new ActivityRef<>(row, groundOpsLayer, source);
        ActivityRef<Activity> targetRef = new ActivityRef<>(row, groundOpsLayer, target);
        links.add(new ActivityLink<>(sourceRef, targetRef, ActivityLink.LinkType.END_TO_START));
    }

    private Gate findGate(int index) {
        // Spread aircraft across the 8 gates
        int terminalIndex = index / 4;
        int gateIndex = index % 4;
        return (Gate) terminals.get(terminalIndex).getChildren().get(gateIndex);
    }

    public AircraftRow getFleetRoot() {
        return fleetRoot;
    }

    public Terminal getTerminalsRoot() {
        return terminalsRoot;
    }

    public List<ActivityLink<Activity>> getLinks() {
        return links;
    }
}
