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
package com.flexganttfx.space.renderer;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.space.model.TelemetryActivity;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ChartActivityRenderer;
import com.flexganttfx.view.util.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Renders telemetry data as chart bars whose height reflects the signal strength
 * (chartValue). The fill colour shifts from green (strong signal) to red (weak
 * signal) to give an immediate visual indication of link quality.
 */
public class TelemetryRenderer extends ChartActivityRenderer<TelemetryActivity> {

    public TelemetryRenderer(GraphicsBase<?> graphics) {
        super(graphics, "Telemetry");
        setCornersRounded(false);
        setAlpha(0.8);
    }

    @Override
    protected ActivityBounds drawActivity(ActivityRef<TelemetryActivity> activityRef,
                                          Position position, GraphicsContext gc,
                                          double x, double y, double w, double h,
                                          boolean selected, boolean hover,
                                          boolean highlighted, boolean pressed) {
        double signal = activityRef.getActivity().getChartValue();
        Color barColor = Color.GREEN.interpolate(Color.RED, 1.0 - signal);
        gc.setFill(barColor.deriveColor(0, 1, 1, selected ? 1.0 : 0.75));
        gc.fillRect(x, y, w, h);
        return new ActivityBounds(activityRef, x, y, w, h);
    }
}
