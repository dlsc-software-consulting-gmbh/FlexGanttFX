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
package com.flexganttfx.naturalevents.model;

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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public final class ApiClient {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final String API_URL = "https://eonet.gsfc.nasa.gov/api/v3/events?status=all&days=%d&limit=500";

    private ApiClient() {
    }

    public static List<NaturalEventActivity> fetchEvents(int days) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(API_URL, days)))
                .GET()
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IOException("NASA EONET request failed with HTTP " + response.statusCode());
        }

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonArray events = json.getAsJsonArray("events");
        List<NaturalEventActivity> activities = new ArrayList<>();

        if (events == null) {
            return activities;
        }

        Instant now = Instant.now();

        for (JsonElement eventElement : events) {
            if (!eventElement.isJsonObject()) {
                continue;
            }

            JsonObject event = eventElement.getAsJsonObject();
            JsonObject category = getFirstObject(event.getAsJsonArray("categories"));
            String eventId = getString(event, "id");
            String title = getString(event, "title");
            String categoryId = category == null ? "other" : getString(category, "id");
            String categoryTitle = category == null ? "Other" : getString(category, "title");

            Instant start = null;
            Instant end = null;
            int geometryCount = 0;
            JsonArray geometry = event.getAsJsonArray("geometry");

            if (geometry != null) {
                for (JsonElement geometryElement : geometry) {
                    if (!geometryElement.isJsonObject()) {
                        continue;
                    }

                    Instant time = parseInstant(getString(geometryElement.getAsJsonObject(), "date"));
                    if (time == null) {
                        continue;
                    }

                    geometryCount++;
                    if (start == null || time.isBefore(start)) {
                        start = time;
                    }
                    if (end == null || time.isAfter(end)) {
                        end = time;
                    }
                }
            }

            if (start == null) {
                continue;
            }

            Instant effectiveEnd;
            JsonElement closedElement = event.get("closed");
            if (closedElement == null || closedElement.isJsonNull()) {
                effectiveEnd = now;
            } else if (geometryCount > 1 && end != null) {
                effectiveEnd = end;
            } else {
                effectiveEnd = start.plus(1, ChronoUnit.DAYS);
            }

            if (!effectiveEnd.isAfter(start)) {
                effectiveEnd = start.plus(1, ChronoUnit.DAYS);
            }

            activities.add(new NaturalEventActivity(eventId, title, categoryId, categoryTitle, start, effectiveEnd));
        }

        return activities;
    }

    private static JsonObject getFirstObject(JsonArray array) {
        if (array == null || array.isEmpty()) {
            return null;
        }

        JsonElement element = array.get(0);
        return element.isJsonObject() ? element.getAsJsonObject() : null;
    }

    private static String getString(JsonObject json, String memberName) {
        JsonElement element = json.get(memberName);
        return element == null || element.isJsonNull() ? "" : element.getAsString();
    }

    private static Instant parseInstant(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Instant.parse(value);
        } catch (Exception ex) {
            return null;
        }
    }
}
