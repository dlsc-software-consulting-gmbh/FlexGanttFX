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
package com.flexganttfx.space.model;

import com.flexganttfx.model.activity.MutableChartActivityBase;

import java.time.Instant;

/**
 * A telemetry data point rendered as a chart bar. {@code chartValue} holds
 * signal strength (0.0–1.0); {@code batteryLevel} holds the battery percentage
 * (0.0–1.0) for optional use by the renderer.
 */
public class TelemetryActivity extends MutableChartActivityBase<String> {

    private double batteryLevel;

    public TelemetryActivity(String name, Instant start, Instant end,
                             double signalStrength, double batteryLevel) {
        setName(name);
        setStartTime(start);
        setEndTime(end);
        setChartValue(signalStrength);
        this.batteryLevel = batteryLevel;
    }

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(double batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
}
