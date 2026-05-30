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
package com.flexganttfx.earthquake.model;

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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ApiClient {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final String API_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=%s&endtime=%s&minmagnitude=%.1f&orderby=time&limit=500";

    private ApiClient() {
    }

    public static List<EarthquakeActivity> fetchEarthquakes(LocalDate startDate, LocalDate endDate, double minMagnitude)
            throws IOException, InterruptedException {
        String url = String.format(Locale.US, API_URL,
                DATE_FORMATTER.format(startDate),
                DATE_FORMATTER.format(endDate),
                minMagnitude);

        var request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        var response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IOException("USGS request failed with HTTP " + response.statusCode());
        }

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonArray features = json.getAsJsonArray("features");
        List<EarthquakeActivity> earthquakes = new ArrayList<>();

        if (features == null) {
            return earthquakes;
        }

        for (JsonElement featureElement : features) {
            if (!featureElement.isJsonObject()) {
                continue;
            }

            JsonObject feature = featureElement.getAsJsonObject();
            JsonObject properties = getObject(feature, "properties");
            if (properties == null) {
                continue;
            }

            JsonElement timeElement = properties.get("time");
            JsonElement magnitudeElement = properties.get("mag");
            if (timeElement == null || timeElement.isJsonNull() || magnitudeElement == null || magnitudeElement.isJsonNull()) {
                continue;
            }

            long time = timeElement.getAsLong();
            double magnitude = magnitudeElement.getAsDouble();
            String place = getString(properties, "place");
            String title = getString(properties, "title");
            String detailUrl = getString(properties, "url");
            double depth = extractDepth(getObject(feature, "geometry"));
            String name = !title.isBlank() ? title : (!place.isBlank() ? place : String.format(Locale.US, "M %.1f", magnitude));

            earthquakes.add(new EarthquakeActivity(name, Instant.ofEpochMilli(time), magnitude, depth, place, detailUrl));
        }

        return earthquakes;
    }

    private static JsonObject getObject(JsonObject json, String memberName) {
        JsonElement element = json.get(memberName);
        return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
    }

    private static String getString(JsonObject json, String memberName) {
        JsonElement element = json.get(memberName);
        return element == null || element.isJsonNull() ? "" : element.getAsString();
    }

    private static double extractDepth(JsonObject geometry) {
        if (geometry == null) {
            return 0;
        }

        JsonElement coordinatesElement = geometry.get("coordinates");
        if (coordinatesElement == null || !coordinatesElement.isJsonArray()) {
            return 0;
        }

        JsonArray coordinates = coordinatesElement.getAsJsonArray();
        if (coordinates.size() < 3 || coordinates.get(2).isJsonNull()) {
            return 0;
        }

        return coordinates.get(2).getAsDouble();
    }
}
