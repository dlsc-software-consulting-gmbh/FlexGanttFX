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
package com.flexganttfx.f1.model;

import com.flexganttfx.model.Layer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataModel {

    private static final Duration DEFAULT_LAP_DURATION = Duration.ofSeconds(90);

    private final RaceSession session;
    private final F1Root root;
    private final Layer layer;
    private Instant sessionStart;
    private Instant sessionEnd;

    public DataModel(RaceSession session) throws IOException, InterruptedException {
        this.session = session;
        this.root = new F1Root(session.toString());
        this.layer = new Layer("Tire Stints");
        this.sessionStart = session.getDateStart();
        this.sessionEnd = session.getDateEnd();

        build();
    }

    public F1Root getRoot() {
        return root;
    }

    public Layer getLayer() {
        return layer;
    }

    public Instant getSessionStart() {
        return sessionStart;
    }

    public Instant getSessionEnd() {
        return sessionEnd;
    }

    private void build() throws IOException, InterruptedException {
        Map<Integer, DriverRow> driverRowsByNumber = buildRows();
        Map<Integer, List<JsonObject>> stintsByDriver = groupStintsByDriver(ApiClient.fetchStints(session.getSessionKey()));

        for (Map.Entry<Integer, List<JsonObject>> entry : stintsByDriver.entrySet()) {
            DriverRow driverRow = driverRowsByNumber.get(entry.getKey());
            if (driverRow == null) {
                continue;
            }

            LapTimeline lapTimeline = new LapTimeline(ApiClient.fetchLaps(session.getSessionKey(), entry.getKey()), sessionStart);
            for (JsonObject json : entry.getValue()) {
                int stintNumber = Math.max(1, ApiClient.intValue(json, "stint_number", 1));
                int lapStart = Math.max(1, ApiClient.intValue(json, "lap_start", 1));
                int lapEnd = Math.max(lapStart, ApiClient.intValue(json, "lap_end", lapStart));
                TireCompound compound = TireCompound.fromString(ApiClient.stringValue(json, "compound", null));

                Instant start = lapTimeline.resolveLapStart(lapStart);
                Instant end = lapTimeline.resolveLapEnd(lapEnd);
                if (!end.isAfter(start)) {
                    end = start.plus(lapTimeline.estimatedLapDuration());
                }

                TireStint stint = new TireStint(compound, stintNumber, lapStart, lapEnd, start, end);
                driverRow.addActivity(layer, stint);
                updateBounds(start, end);
            }
        }

        if (sessionStart == null) {
            sessionStart = Instant.now();
        }
        if (sessionEnd == null || !sessionEnd.isAfter(sessionStart)) {
            sessionEnd = sessionStart.plus(Duration.ofHours(2));
        }
    }

    private Map<Integer, DriverRow> buildRows() throws IOException, InterruptedException {
        JsonArray drivers = ApiClient.fetchDrivers(session.getSessionKey());
        List<JsonObject> entries = new ArrayList<>();
        for (JsonElement element : drivers) {
            entries.add(element.getAsJsonObject());
        }

        entries.sort(Comparator
                .comparing((JsonObject json) -> ApiClient.stringValue(json, "team_name", "Unknown Team"), String.CASE_INSENSITIVE_ORDER)
                .thenComparingInt(json -> ApiClient.intValue(json, "driver_number", Integer.MAX_VALUE)));

        Map<String, TeamRow> teamRows = new LinkedHashMap<>();
        Map<Integer, DriverRow> driverRowsByNumber = new LinkedHashMap<>();

        for (JsonObject json : entries) {
            int driverNumber = ApiClient.intValue(json, "driver_number", -1);
            if (driverNumber < 0) {
                continue;
            }

            String teamName = ApiClient.stringValue(json, "team_name", "Unknown Team");
            Color teamColor = parseColor(ApiClient.stringValue(json, "team_colour", null));
            TeamRow teamRow = teamRows.computeIfAbsent(teamName, name -> {
                TeamRow row = new TeamRow(name, teamColor);
                root.getChildren().add(row);
                return row;
            });

            String driverName = resolveDriverName(json, driverNumber);
            DriverRow driverRow = new DriverRow(driverName, driverNumber);
            teamRow.getChildren().add(driverRow);
            driverRowsByNumber.put(driverNumber, driverRow);
        }

        return driverRowsByNumber;
    }

    private Map<Integer, List<JsonObject>> groupStintsByDriver(JsonArray stints) {
        Map<Integer, List<JsonObject>> stintsByDriver = new HashMap<>();
        for (JsonElement element : stints) {
            JsonObject json = element.getAsJsonObject();
            int driverNumber = ApiClient.intValue(json, "driver_number", -1);
            if (driverNumber < 0) {
                continue;
            }

            stintsByDriver.computeIfAbsent(driverNumber, key -> new ArrayList<>()).add(json);
        }

        for (List<JsonObject> values : stintsByDriver.values()) {
            values.sort(Comparator
                    .comparingInt((JsonObject json) -> ApiClient.intValue(json, "stint_number", Integer.MAX_VALUE))
                    .thenComparingInt(json -> ApiClient.intValue(json, "lap_start", Integer.MAX_VALUE)));
        }

        return stintsByDriver;
    }

    private void updateBounds(Instant start, Instant end) {
        if (start != null && (sessionStart == null || start.isBefore(sessionStart))) {
            sessionStart = start;
        }
        if (end != null && (sessionEnd == null || end.isAfter(sessionEnd))) {
            sessionEnd = end;
        }
    }

    private String resolveDriverName(JsonObject json, int driverNumber) {
        String fullName = ApiClient.stringValue(json, "full_name", null);
        if (fullName != null && !fullName.isBlank()) {
            return fullName;
        }

        String acronym = ApiClient.stringValue(json, "name_acronym", null);
        if (acronym != null && !acronym.isBlank()) {
            return acronym;
        }

        return "Driver " + driverNumber;
    }

    private Color parseColor(String value) {
        if (value == null || value.isBlank()) {
            return Color.web("#666666");
        }

        String normalized = value.startsWith("#") ? value : "#" + value;
        try {
            return Color.web(normalized);
        } catch (IllegalArgumentException ex) {
            return Color.web("#666666");
        }
    }

    private static final class LapTimeline {

        private final Map<Integer, Instant> lapStarts = new HashMap<>();
        private final Duration estimatedLapDuration;
        private final Instant fallbackStart;
        private final int lastKnownLap;

        private LapTimeline(JsonArray laps, Instant fallbackStart) {
            this.fallbackStart = fallbackStart != null ? fallbackStart : Instant.now();

            Instant previousStart = null;
            Duration runningEstimate = DEFAULT_LAP_DURATION;
            int lapCounter = 0;
            int lastLap = 0;

            for (JsonElement element : laps) {
                JsonObject json = element.getAsJsonObject();
                int lapNumber = ApiClient.intValue(json, "lap_number", -1);
                Instant lapStart = ApiClient.parseInstant(ApiClient.stringValue(json, "date_start", null));
                if (lapNumber <= 0 || lapStart == null) {
                    continue;
                }

                lapStarts.put(lapNumber, lapStart);
                if (previousStart != null) {
                    Duration delta = Duration.between(previousStart, lapStart);
                    if (!delta.isNegative() && !delta.isZero()) {
                        runningEstimate = average(runningEstimate, delta, lapCounter);
                        lapCounter++;
                    }
                }

                previousStart = lapStart;
                lastLap = Math.max(lastLap, lapNumber);
            }

            this.estimatedLapDuration = runningEstimate;
            this.lastKnownLap = lastLap;
        }

        private Instant resolveLapStart(int lapNumber) {
            Instant lapStart = lapStarts.get(lapNumber);
            if (lapStart != null) {
                return lapStart;
            }

            if (!lapStarts.isEmpty() && lastKnownLap > 0 && lapStarts.containsKey(lastKnownLap)) {
                if (lapNumber > lastKnownLap) {
                    return lapStarts.get(lastKnownLap).plus(estimatedLapDuration.multipliedBy(lapNumber - lastKnownLap));
                }

                Instant firstKnown = lapStarts.get(1);
                if (firstKnown != null) {
                    return firstKnown.plus(estimatedLapDuration.multipliedBy(Math.max(0, lapNumber - 1L)));
                }
            }

            return fallbackStart.plus(estimatedLapDuration.multipliedBy(Math.max(0, lapNumber - 1L)));
        }

        private Instant resolveLapEnd(int lapNumber) {
            Instant nextLapStart = lapStarts.get(lapNumber + 1);
            if (nextLapStart != null) {
                return nextLapStart;
            }
            return resolveLapStart(lapNumber).plus(estimatedLapDuration);
        }

        private Duration estimatedLapDuration() {
            return estimatedLapDuration;
        }

        private static Duration average(Duration currentAverage, Duration nextValue, int count) {
            long totalMillis = currentAverage.toMillis() * Math.max(1, count) + nextValue.toMillis();
            return Duration.ofMillis(Math.max(1, totalMillis / Math.max(1, count + 1)));
        }
    }
}
