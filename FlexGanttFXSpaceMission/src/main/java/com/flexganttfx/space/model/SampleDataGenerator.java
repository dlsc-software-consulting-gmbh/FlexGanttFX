/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.space.model;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityLink;
import com.flexganttfx.model.ActivityLink.LinkType;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates realistic sample data for the Space Mission demo:
 * 6 spacecraft with contacts, maneuvers, science ops, and maintenance; 4 ground
 * stations; a dedicated telemetry row; and 5 activity links demonstrating all
 * four link types.
 */
public class SampleDataGenerator {

    private static final int DAYS = 30;

    private final SpaceFleet fleet;
    private final MissionControl missionControl;
    private final Layer contactLayer;
    private final Layer maneuverLayer;
    private final Layer scienceLayer;
    private final Layer maintenanceLayer;
    private final Layer telemetryLayer;
    private final List<ActivityLink<Activity>> links = new ArrayList<>();

    public SampleDataGenerator(Layer contactLayer, Layer maneuverLayer,
                               Layer scienceLayer, Layer maintenanceLayer,
                               Layer telemetryLayer) {
        this.contactLayer    = contactLayer;
        this.maneuverLayer   = maneuverLayer;
        this.scienceLayer    = scienceLayer;
        this.maintenanceLayer = maintenanceLayer;
        this.telemetryLayer  = telemetryLayer;

        fleet          = new SpaceFleet("Space Fleet");
        missionControl = new MissionControl("Mission Control");

        generateSpacecraft();
        generateGroundStations();
    }

    private void generateSpacecraft() {
        Instant base = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(15, ChronoUnit.DAYS);
        Random rng = new Random(7);

        String[] names = {"ISS", "Hubble", "Webb", "Artemis I", "Voyager", "Cassini"};

        List<Spacecraft> spacecraft = new ArrayList<>();
        for (String name : names) {
            Spacecraft sc = new Spacecraft(name);
            fleet.getChildren().add(sc);
            spacecraft.add(sc);
        }

        // ISS — contacts and science ops spread over 30 days
        Spacecraft iss = spacecraft.get(0);
        ContactWindow cw1 = addContact(iss, base, 2, 4);
        Maneuver m1 = addManeuver(iss, base, 5, 6);
        addScienceOp(iss, base, 8, 14, 65.0);
        addScienceOp(iss, base, 16, 20, 30.0);
        addContact(iss, base, 22, 24);
        addMaintenance(iss, base, 25, 27);

        // Hubble — maneuver -> science op link
        Spacecraft hubble = spacecraft.get(1);
        Maneuver m2 = addManeuver(hubble, base, 1, 3);
        ScienceOp so2 = addScienceOp(hubble, base, 4, 10, 80.0);
        addContact(hubble, base, 12, 13);
        addScienceOp(hubble, base, 18, 25, 15.0);
        addMaintenance(hubble, base, 27, 29);

        // Webb — contacts, science
        Spacecraft webb = spacecraft.get(2);
        ContactWindow cw3 = addContact(webb, base, 0, 2);
        ScienceOp so3 = addScienceOp(webb, base, 3, 9, 90.0);
        addScienceOp(webb, base, 12, 18, 50.0);
        addContact(webb, base, 20, 22);
        addManeuver(webb, base, 24, 25);

        // Artemis I
        Spacecraft artemis = spacecraft.get(3);
        Maneuver m4 = addManeuver(artemis, base, 3, 5);
        ContactWindow cw4 = addContact(artemis, base, 6, 8);
        addScienceOp(artemis, base, 10, 16, 40.0);
        addMaintenance(artemis, base, 20, 22);
        addContact(artemis, base, 24, 26);

        // Voyager
        Spacecraft voyager = spacecraft.get(4);
        ContactWindow cw5 = addContact(voyager, base, 1, 3);
        MaintenanceOp mo5 = addMaintenance(voyager, base, 4, 6);
        addScienceOp(voyager, base, 9, 15, 100.0);
        addManeuver(voyager, base, 17, 18);
        addContact(voyager, base, 22, 24);

        // Cassini
        Spacecraft cassini = spacecraft.get(5);
        ContactWindow cw6 = addContact(cassini, base, 0, 2);
        addManeuver(cassini, base, 3, 4);
        addScienceOp(cassini, base, 7, 12, 55.0);
        addMaintenance(cassini, base, 15, 17);
        addContact(cassini, base, 20, 22);

        // Add telemetry row for ISS using ChartLayout
        Spacecraft telemetryRow = new Spacecraft("ISS Telemetry");
        fleet.getChildren().add(telemetryRow);
        addTelemetryData(telemetryRow, base, rng);

        // Build 5 activity links covering all 4 link types
        // 1. ISS: ContactWindow -> Maneuver (END_TO_START)
        ActivityRef<Activity> issContact  = new ActivityRef<>(iss, contactLayer, cw1);
        ActivityRef<Activity> issManeuver = new ActivityRef<>(iss, maneuverLayer, m1);
        links.add(new ActivityLink<>(issContact, issManeuver, LinkType.END_TO_START));

        // 2. Hubble: Maneuver -> ScienceOp (END_TO_START)
        ActivityRef<Activity> hubbleManeuver  = new ActivityRef<>(hubble, maneuverLayer, m2);
        ActivityRef<Activity> hubbleScience   = new ActivityRef<>(hubble, scienceLayer,  so2);
        links.add(new ActivityLink<>(hubbleManeuver, hubbleScience, LinkType.END_TO_START));

        // 3. Webb: ContactWindow -> ScienceOp (START_TO_START)
        ActivityRef<Activity> webbContact = new ActivityRef<>(webb, contactLayer, cw3);
        ActivityRef<Activity> webbScience = new ActivityRef<>(webb, scienceLayer,  so3);
        links.add(new ActivityLink<>(webbContact, webbScience, LinkType.START_TO_START));

        // 4. Artemis: Maneuver -> ContactWindow (END_TO_END)
        ActivityRef<Activity> artemisManeuver = new ActivityRef<>(artemis, maneuverLayer, m4);
        ActivityRef<Activity> artemisContact  = new ActivityRef<>(artemis, contactLayer,  cw4);
        links.add(new ActivityLink<>(artemisManeuver, artemisContact, LinkType.END_TO_END));

        // 5. Voyager: ContactWindow -> MaintenanceOp (START_TO_END)
        ActivityRef<Activity> voyagerContact     = new ActivityRef<>(voyager, contactLayer,     cw5);
        ActivityRef<Activity> voyagerMaintenance = new ActivityRef<>(voyager, maintenanceLayer, mo5);
        links.add(new ActivityLink<>(voyagerContact, voyagerMaintenance, LinkType.START_TO_END));
    }

    private void generateGroundStations() {
        Instant base = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(15, ChronoUnit.DAYS);

        String[] stationNames = {
                "Johnson Space Center", "European Space Agency",
                "JAXA Tsukuba", "Deep Space Network"
        };

        for (int i = 0; i < stationNames.length; i++) {
            GroundStation gs = new GroundStation(stationNames[i]);
            missionControl.getChildren().add(gs);

            // Each station has contact windows distributed across the 30-day window
            for (int d = i; d < DAYS; d += 4 + i) {
                Instant start = base.plus(d, ChronoUnit.DAYS).plus(6 + i * 2, ChronoUnit.HOURS);
                Instant end   = start.plus(2, ChronoUnit.HOURS);
                gs.addActivity(contactLayer, new ContactWindow(stationNames[i] + " Pass", start, end));
            }
        }
    }

    private void addTelemetryData(Spacecraft row, Instant base, Random rng) {
        double signal = 0.6;
        double battery = 0.9;
        for (int h = 0; h < DAYS * 24; h += 6) {
            signal  = clamp(signal  + (rng.nextDouble() - 0.5) * 0.2, 0.1, 1.0);
            battery = clamp(battery + (rng.nextDouble() - 0.5) * 0.1, 0.2, 1.0);
            Instant start = base.plus(h, ChronoUnit.HOURS);
            Instant end   = start.plus(6, ChronoUnit.HOURS);
            row.addActivity(telemetryLayer, new TelemetryActivity("Signal", start, end, signal, battery));
        }
    }

    private ContactWindow addContact(Spacecraft sc, Instant base, int startDay, int endDay) {
        Instant s = base.plus(startDay, ChronoUnit.DAYS);
        Instant e = base.plus(endDay,   ChronoUnit.DAYS);
        ContactWindow cw = new ContactWindow(sc.getName() + " Contact", s, e);
        sc.addActivity(contactLayer, cw);
        return cw;
    }

    private Maneuver addManeuver(Spacecraft sc, Instant base, int startDay, int endDay) {
        Instant s = base.plus(startDay, ChronoUnit.DAYS);
        Instant e = base.plus(endDay,   ChronoUnit.DAYS);
        Maneuver m = new Maneuver(sc.getName() + " Burn", s, e);
        sc.addActivity(maneuverLayer, m);
        return m;
    }

    private ScienceOp addScienceOp(Spacecraft sc, Instant base, int startDay, int endDay, double pct) {
        Instant s = base.plus(startDay, ChronoUnit.DAYS);
        Instant e = base.plus(endDay,   ChronoUnit.DAYS);
        ScienceOp so = new ScienceOp(sc.getName() + " Observation", s, e, pct);
        sc.addActivity(scienceLayer, so);
        return so;
    }

    private MaintenanceOp addMaintenance(Spacecraft sc, Instant base, int startDay, int endDay) {
        Instant s = base.plus(startDay, ChronoUnit.DAYS);
        Instant e = base.plus(endDay,   ChronoUnit.DAYS);
        MaintenanceOp mo = new MaintenanceOp(sc.getName() + " Maintenance", s, e);
        sc.addActivity(maintenanceLayer, mo);
        return mo;
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    public SpaceFleet getFleet() {
        return fleet;
    }

    public MissionControl getMissionControl() {
        return missionControl;
    }

    public List<ActivityLink<Activity>> getLinks() {
        return links;
    }
}
