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
package com.flexganttfx.naturalevents.renderer;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.naturalevents.model.NaturalEventActivity;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.util.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class NaturalEventRenderer extends ActivityBarRenderer<NaturalEventActivity> {

    public NaturalEventRenderer(GraphicsBase<?> graphics) {
        super(graphics, "NaturalEvent");
        setCornersRounded(true);
    }

    @Override
    public ActivityBounds drawActivity(ActivityRef<NaturalEventActivity> ref, Position pos, GraphicsContext gc,
                                       double x, double y, double w, double h, boolean selected, boolean hover,
                                       boolean highlighted, boolean pressed) {
        Color color = colorForCategory(ref.getActivity().getCategoryId());

        setFill(color);
        setFillHover(color.brighter());
        setFillPressed(color.darker());
        setFillSelected(color.darker());
        setStroke(color.darker());
        setTextFill(color.getBrightness() > 0.65 ? Color.BLACK : Color.WHITE);

        ActivityBounds bounds = super.drawActivity(ref, pos, gc, x, y, w, h, selected, hover, highlighted, pressed);
        drawText(ref, truncate(ref.getActivity().getName(), 20), TextPosition.CENTER, gc, x, y, w, h,
                selected, hover, highlighted, pressed);
        return bounds;
    }

    private Color colorForCategory(String categoryId) {
        if ("wildfires".equals(categoryId)) {
            return Color.web("#FF6D00");
        }
        if ("severeStorms".equals(categoryId)) {
            return Color.web("#7B1FA2");
        }
        if ("floods".equals(categoryId)) {
            return Color.web("#1565C0");
        }
        if ("volcanoes".equals(categoryId)) {
            return Color.web("#B71C1C");
        }
        if ("seaLakeIce".equals(categoryId)) {
            return Color.web("#00B8D4");
        }
        if ("drought".equals(categoryId)) {
            return Color.web("#6D4C41");
        }
        if ("landslides".equals(categoryId)) {
            return Color.web("#827717");
        }
        if ("manmade".equals(categoryId)) {
            return Color.web("#546E7A");
        }
        if ("snow".equals(categoryId)) {
            return Color.web("#80DEEA");
        }
        return Color.GRAY;
    }

    private String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text == null ? "" : text;
        }

        return text.substring(0, Math.max(0, maxLength - 3)) + "...";
    }
}
