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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class ApiClient {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final String LAUNCHES_URL = "https://api.spacexdata.com/v4/launches";
    private static final String ROCKETS_URL = "https://api.spacexdata.com/v4/rockets";
    private static final String LAUNCHPADS_URL = "https://api.spacexdata.com/v4/launchpads";

    private ApiClient() {
    }

    public static JsonArray fetchLaunches() throws IOException, InterruptedException {
        return fetchArray(LAUNCHES_URL);
    }

    public static JsonArray fetchRockets() throws IOException, InterruptedException {
        return fetchArray(ROCKETS_URL);
    }

    public static JsonArray fetchLaunchpads() throws IOException, InterruptedException {
        return fetchArray(LAUNCHPADS_URL);
    }

    private static JsonArray fetchArray(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IOException("SpaceX request failed with HTTP " + response.statusCode());
        }

        JsonElement json = JsonParser.parseString(response.body());
        if (!json.isJsonArray()) {
            throw new IOException("Unexpected SpaceX response format.");
        }

        return json.getAsJsonArray();
    }
}
