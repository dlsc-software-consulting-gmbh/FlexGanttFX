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

import com.flexganttfx.model.Layer;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataModel {

    private final EventCategoryRow root;
    private final Layer layer;

    public DataModel(int days) throws IOException, InterruptedException {
        root = new EventCategoryRow("Natural Events", "root", Color.TRANSPARENT);

        Map<String, EventCategoryRow> rows = new LinkedHashMap<>();
        rows.put("wildfires", new EventCategoryRow("Wildfires", "wildfires", Color.web("#FF6D00")));
        rows.put("severeStorms", new EventCategoryRow("Severe Storms", "severeStorms", Color.web("#7B1FA2")));
        rows.put("floods", new EventCategoryRow("Floods", "floods", Color.web("#1565C0")));
        rows.put("volcanoes", new EventCategoryRow("Volcanoes", "volcanoes", Color.web("#B71C1C")));
        rows.put("seaLakeIce", new EventCategoryRow("Sea / Lake Ice", "seaLakeIce", Color.web("#00B8D4")));
        rows.put("drought", new EventCategoryRow("Drought", "drought", Color.web("#6D4C41")));
        rows.put("landslides", new EventCategoryRow("Landslides", "landslides", Color.web("#827717")));
        rows.put("manmade", new EventCategoryRow("Manmade", "manmade", Color.web("#546E7A")));
        rows.put("snow", new EventCategoryRow("Snow", "snow", Color.web("#80DEEA")));
        EventCategoryRow otherRow = new EventCategoryRow("Other", "other", Color.GRAY);
        rows.put("other", otherRow);

        layer = new Layer("Natural Events");

        List<NaturalEventActivity> events = ApiClient.fetchEvents(days);
        for (NaturalEventActivity event : events) {
            EventCategoryRow row = rows.getOrDefault(event.getCategoryId(), otherRow);
            row.addActivity(layer, event);
        }

        root.getChildren().addAll(rows.values());
    }

    public EventCategoryRow getRoot() {
        return root;
    }

    public Layer getLayer() {
        return layer;
    }
}
