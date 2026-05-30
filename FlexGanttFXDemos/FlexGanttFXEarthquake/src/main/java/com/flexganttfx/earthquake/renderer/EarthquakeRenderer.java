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
package com.flexganttfx.earthquake.renderer;

import com.flexganttfx.earthquake.model.EarthquakeActivity;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.util.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Locale;

public class EarthquakeRenderer extends ActivityBarRenderer<EarthquakeActivity> {

    public EarthquakeRenderer(GraphicsBase<?> graphics) {
        super(graphics, "Earthquake");
        setCornersRounded(true);
    }

    @Override
    public ActivityBounds drawActivity(ActivityRef<EarthquakeActivity> ref, Position pos, GraphicsContext gc,
                                       double x, double y, double w, double h, boolean selected, boolean hover,
                                       boolean highlighted, boolean pressed) {
        double magnitude = ref.getActivity().getMagnitude();
        Color color = magnitudeColor(magnitude);

        setFill(color);
        setFillHover(color.brighter());
        setFillPressed(color.darker());
        setFillSelected(color.darker());
        setStroke(color.darker());
        setTextFill(magnitude < 6.0 ? Color.BLACK : Color.WHITE);

        ActivityBounds bounds = super.drawActivity(ref, pos, gc, x, y, w, h, selected, hover, highlighted, pressed);
        drawText(ref, String.format(Locale.US, "M%.1f", magnitude), TextPosition.CENTER, gc, x, y, w, h,
                selected, hover, highlighted, pressed);
        return bounds;
    }

    private Color magnitudeColor(double magnitude) {
        if (magnitude < 6.0) {
            return Color.web("#4CAF50");
        }
        if (magnitude < 7.0) {
            return Color.web("#FFC107");
        }
        if (magnitude < 8.0) {
            return Color.web("#FF5722");
        }
        return Color.web("#B71C1C");
    }
}
