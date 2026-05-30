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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ApiClient {

    private static final String BASE = "https://api.openf1.org/v1";
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    private ApiClient() {
    }

    private static String get(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        IOException failure = null;
        for (int attempt = 0; attempt < 3; attempt++) {
            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 400) {
                return response.body();
            }

            if (response.statusCode() != 429 && response.statusCode() < 500) {
                throw new IOException("HTTP " + response.statusCode() + " while calling " + url);
            }

            failure = new IOException("HTTP " + response.statusCode() + " while calling " + url);
            Thread.sleep(500L * (attempt + 1));
        }

        throw failure == null ? new IOException("HTTP request failed for " + url) : failure;
    }

    public static List<RaceSession> fetchSessions(int year) throws IOException, InterruptedException {
        JsonArray array = JsonParser.parseString(get(BASE + "/sessions?year=" + year + "&session_type=Race")).getAsJsonArray();
        List<RaceSession> sessions = new ArrayList<>();

        for (JsonElement element : array) {
            JsonObject json = element.getAsJsonObject();
            String sessionName = stringValue(json, "session_name", "Race");
            if (!"Race".equalsIgnoreCase(sessionName)) {
                continue;
            }

            String location = stringValue(json, "location", stringValue(json, "meeting_name", "Unknown"));
            String countryName = stringValue(json, "country_name", "Unknown");
            Instant dateStart = parseInstant(stringValue(json, "date_start", null));
            Instant dateEnd = parseInstant(stringValue(json, "date_end", null));

            sessions.add(new RaceSession(
                    intValue(json, "session_key", -1),
                    location,
                    location,
                    countryName,
                    intValue(json, "year", year),
                    dateStart,
                    dateEnd));
        }

        sessions.sort(Comparator.comparing(RaceSession::getDateStart, Comparator.nullsLast(Comparator.naturalOrder())));
        return sessions;
    }

    public static JsonArray fetchStints(int sessionKey) throws IOException, InterruptedException {
        return JsonParser.parseString(get(BASE + "/stints?session_key=" + sessionKey)).getAsJsonArray();
    }

    public static JsonArray fetchDrivers(int sessionKey) throws IOException, InterruptedException {
        return JsonParser.parseString(get(BASE + "/drivers?session_key=" + sessionKey)).getAsJsonArray();
    }

    public static JsonArray fetchLaps(int sessionKey, int driverNumber) throws IOException, InterruptedException {
        JsonArray raw = JsonParser.parseString(get(BASE + "/laps?session_key=" + sessionKey + "&driver_number=" + driverNumber)).getAsJsonArray();
        List<JsonObject> laps = new ArrayList<>();
        for (JsonElement element : raw) {
            laps.add(element.getAsJsonObject());
        }
        laps.sort(Comparator.comparingInt(lap -> intValue(lap, "lap_number", Integer.MAX_VALUE)));

        JsonArray sorted = new JsonArray();
        for (JsonObject lap : laps) {
            sorted.add(lap);
        }
        return sorted;
    }

    static Instant parseInstant(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Instant.parse(value);
        } catch (DateTimeParseException ignored) {
        }

        try {
            return OffsetDateTime.parse(value).toInstant();
        } catch (DateTimeParseException ignored) {
        }

        try {
            return LocalDateTime.parse(value).toInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException ignored) {
        }

        return null;
    }

    static String stringValue(JsonObject json, String key, String fallback) {
        JsonElement element = json.get(key);
        if (element == null || element.isJsonNull()) {
            return fallback;
        }
        return element.getAsString();
    }

    static int intValue(JsonObject json, String key, int fallback) {
        JsonElement element = json.get(key);
        if (element == null || element.isJsonNull()) {
            return fallback;
        }
        try {
            return element.getAsInt();
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
