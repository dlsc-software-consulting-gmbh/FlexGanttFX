/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.earthquake.model;

import com.flexganttfx.model.Layer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class DataModel {

    private final MagnitudeBandRow root;
    private final Layer layer;
    private final Instant earliestTime;
    private final Instant latestTime;

    public DataModel(LocalDate start, LocalDate end, double minMag) throws IOException, InterruptedException {
        root = new MagnitudeBandRow("Earthquakes", 0);

        MagnitudeBandRow band5 = MagnitudeBandRow.band5();
        MagnitudeBandRow band6 = MagnitudeBandRow.band6();
        MagnitudeBandRow band7 = MagnitudeBandRow.band7();
        MagnitudeBandRow band8 = MagnitudeBandRow.band8plus();
        root.getChildren().addAll(List.of(band5, band6, band7, band8));

        layer = new Layer("Earthquakes");

        List<EarthquakeActivity> earthquakes = ApiClient.fetchEarthquakes(start, end, minMag);
        Instant earliest = null;
        Instant latest = null;

        for (EarthquakeActivity earthquake : earthquakes) {
            selectRow(earthquake.getMagnitude(), band5, band6, band7, band8).addActivity(layer, earthquake);

            if (earliest == null || earthquake.getStartTime().isBefore(earliest)) {
                earliest = earthquake.getStartTime();
            }
            if (latest == null || earthquake.getEndTime().isAfter(latest)) {
                latest = earthquake.getEndTime();
            }
        }

        earliestTime = earliest != null ? earliest : start.atStartOfDay(ZoneId.systemDefault()).toInstant();
        latestTime = latest != null ? latest : end.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

    public MagnitudeBandRow getRoot() {
        return root;
    }

    public Layer getLayer() {
        return layer;
    }

    public Instant getEarliestTime() {
        return earliestTime;
    }

    public Instant getLatestTime() {
        return latestTime;
    }

    private MagnitudeBandRow selectRow(double magnitude, MagnitudeBandRow band5, MagnitudeBandRow band6,
                                       MagnitudeBandRow band7, MagnitudeBandRow band8) {
        if (magnitude >= 8.0) {
            return band8;
        }
        if (magnitude >= 7.0) {
            return band7;
        }
        if (magnitude >= 6.0) {
            return band6;
        }
        return band5;
    }
}
