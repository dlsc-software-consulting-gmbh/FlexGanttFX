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
package com.flexganttfx.spacex.model;

import com.flexganttfx.model.Layer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataModel {

    private static final String ALL = "ALL";
    private static final String SUCCESS = "SUCCESS";
    private static final String FAILURE = "FAILURE";
    private static final String UNKNOWN_ROCKET_ID = "unknown-rocket";
    private static final String UNKNOWN_LAUNCHPAD_ID = "unknown-launchpad";

    private final SpaceXRoot root;
    private final Layer layer;
    private final Instant earliestTime;

    public DataModel(int fromYear, int toYear, String statusFilter) throws IOException, InterruptedException {
        root = new SpaceXRoot();
        layer = new Layer("Launches");

        Map<String, String> rocketNames = extractRocketNames(ApiClient.fetchRockets());
        Map<String, JsonObject> launchpadInfo = indexById(ApiClient.fetchLaunchpads());
        JsonArray launches = ApiClient.fetchLaunches();

        Map<String, RocketRow> rocketRows = new LinkedHashMap<>();
        Map<String, LaunchPadRow> launchPadRows = new LinkedHashMap<>();

        Instant earliest = null;

        for (JsonElement element : launches) {
            JsonObject launch = asObject(element);
            if (launch == null) {
                continue;
            }

            LaunchActivity activity = createLaunchActivity(launch);
            if (activity == null) {
                continue;
            }

            int year = activity.getStartTime().atZone(ZoneOffset.UTC).getYear();
            if (year < fromYear || year > toYear || !matchesStatus(activity.getSuccess(), statusFilter)) {
                continue;
            }

            String rocketId = normalizeId(activity.getRocketId(), UNKNOWN_ROCKET_ID);
            String launchpadId = normalizeId(activity.getLaunchpadId(), UNKNOWN_LAUNCHPAD_ID);

            RocketRow rocketRow = rocketRows.computeIfAbsent(rocketId, id -> {
                RocketRow row = new RocketRow(resolveRocketName(rocketNames, id), id);
                root.getChildren().add(row);
                return row;
            });

            String launchpadKey = rocketId + "::" + launchpadId;
            LaunchPadRow launchPadRow = launchPadRows.computeIfAbsent(launchpadKey, key -> {
                LaunchPadRow row = createLaunchPadRow(launchpadInfo, launchpadId);
                rocketRow.getChildren().add(row);
                return row;
            });

            launchPadRow.addActivity(layer, activity);

            if (earliest == null || activity.getStartTime().isBefore(earliest)) {
                earliest = activity.getStartTime();
            }
        }

        earliestTime = earliest != null
                ? earliest
                : LocalDate.of(fromYear, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    public SpaceXRoot getRoot() {
        return root;
    }

    public Layer getLayer() {
        return layer;
    }

    public Instant getEarliestTime() {
        return earliestTime;
    }

    private static LaunchActivity createLaunchActivity(JsonObject launch) {
        String dateUtc = getString(launch, "date_utc");
        if (dateUtc.isBlank()) {
            return null;
        }

        Instant startTime;
        try {
            startTime = Instant.parse(dateUtc);
        } catch (DateTimeParseException ex) {
            return null;
        }

        int flightNumber = getInt(launch, "flight_number");
        String missionName = getString(launch, "name");
        if (missionName.isBlank()) {
            missionName = flightNumber > 0 ? "Flight " + flightNumber : "Unnamed Launch";
        }

        return new LaunchActivity(
                missionName,
                startTime,
                flightNumber,
                getBooleanOrNull(launch, "success"),
                getBoolean(launch, "upcoming"),
                getString(launch, "rocket"),
                getString(launch, "launchpad")
        );
    }

    private static Map<String, String> extractRocketNames(JsonArray rockets) {
        Map<String, String> names = new LinkedHashMap<>();
        for (JsonElement element : rockets) {
            JsonObject rocket = asObject(element);
            if (rocket == null) {
                continue;
            }

            String id = getString(rocket, "id");
            if (!id.isBlank()) {
                names.put(id, getString(rocket, "name"));
            }
        }
        return names;
    }

    private static Map<String, JsonObject> indexById(JsonArray array) {
        Map<String, JsonObject> result = new LinkedHashMap<>();
        for (JsonElement element : array) {
            JsonObject object = asObject(element);
            if (object == null) {
                continue;
            }

            String id = getString(object, "id");
            if (!id.isBlank()) {
                result.put(id, object);
            }
        }
        return result;
    }

    private static LaunchPadRow createLaunchPadRow(Map<String, JsonObject> launchpadInfo, String launchpadId) {
        JsonObject launchpad = launchpadInfo.get(launchpadId);
        String name = getString(launchpad, "name");
        if (name.isBlank()) {
            name = "Unknown Launchpad";
        }

        String fullName = getString(launchpad, "full_name");
        if (fullName.isBlank()) {
            fullName = name;
        }

        return new LaunchPadRow(name, launchpadId, fullName);
    }

    private static String resolveRocketName(Map<String, String> rocketNames, String rocketId) {
        String name = rocketNames.get(rocketId);
        return name == null || name.isBlank() ? "Unknown Rocket" : name;
    }

    private static boolean matchesStatus(Boolean success, String statusFilter) {
        if (SUCCESS.equals(statusFilter)) {
            return Boolean.TRUE.equals(success);
        }
        if (FAILURE.equals(statusFilter)) {
            return Boolean.FALSE.equals(success);
        }
        return ALL.equals(statusFilter) || statusFilter == null || statusFilter.isBlank();
    }

    private static JsonObject asObject(JsonElement element) {
        return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
    }

    private static String normalizeId(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static String getString(JsonObject json, String memberName) {
        if (json == null) {
            return "";
        }

        JsonElement element = json.get(memberName);
        return element == null || element.isJsonNull() ? "" : element.getAsString();
    }

    private static int getInt(JsonObject json, String memberName) {
        JsonElement element = json.get(memberName);
        return element == null || element.isJsonNull() ? 0 : element.getAsInt();
    }

    private static Boolean getBooleanOrNull(JsonObject json, String memberName) {
        JsonElement element = json.get(memberName);
        return element == null || element.isJsonNull() ? null : element.getAsBoolean();
    }

    private static boolean getBoolean(JsonObject json, String memberName) {
        JsonElement element = json.get(memberName);
        return element != null && !element.isJsonNull() && element.getAsBoolean();
    }
}
