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
package com.flexganttfx.hospital.renderer;

import com.flexganttfx.hospital.model.HospitalActivity;
import com.flexganttfx.hospital.model.HospitalActivityRole;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.util.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.time.Duration;

public class HospitalActivityRenderer extends ActivityBarRenderer<HospitalActivity> {

    public HospitalActivityRenderer(GraphicsBase<?> graphics) {
        super(graphics, "Hospital Activity Renderer");
        setCornersRounded(true);
        setCornerRadius(5);
        setBarHeight(18);
        setTextFill(Color.WHITE);
    }

    @Override
    public ActivityBounds drawActivity(ActivityRef<HospitalActivity> activityRef, Position position, GraphicsContext gc,
                                       double x, double y, double w, double h, boolean selected, boolean hover,
                                       boolean highlighted, boolean pressed) {
        HospitalActivity activity = activityRef.getActivity();
        configureColors(activity, hover, highlighted, pressed);

        ActivityBounds bounds = super.drawActivity(activityRef, position, gc, x, y, w, h, selected, hover, highlighted, pressed);
        if (activity.hasPhases()) {
            drawPhases(activityRef, position, gc, bounds, selected, hover, highlighted, pressed);
        }
        drawText(activityRef, labelFor(activity), TextPosition.CENTER, gc, x, y, w, h, selected, hover, highlighted, pressed);
        return bounds;
    }

    private void configureColors(HospitalActivity activity, boolean hover, boolean highlighted, boolean pressed) {
        HospitalActivityRole role = activity.getRole();
        boolean emergency = activity.getUserObject().isEmergency();
        Color fill;
        Color stroke;

        switch (role) {
            case SURGERY:
                fill = getSurgeryFill(activity, hover, highlighted, pressed);
                stroke = emergency ? Color.valueOf("#8F2430") : Color.valueOf("#1F4C7A");
                break;
            case SURGEON:
                fill = Color.valueOf("#C34A36");
                stroke = Color.valueOf("#8A3022");
                break;
            case ANESTHESIA:
                fill = Color.valueOf("#4D8076");
                stroke = Color.valueOf("#32564E");
                break;
            case EQUIPMENT:
                fill = Color.valueOf("#F9A826");
                stroke = Color.valueOf("#BC7610");
                break;
            default:
                fill = Color.DARKSLATEBLUE;
                stroke = Color.BLACK;
                break;
        }

        setFill(fill);
        setFillHover(fill.brighter());
        setFillSelected(fill.deriveColor(0, 1, 0.85, 1));
        setFillHighlight(fill.deriveColor(0, 1, 0.95, 1));
        setFillPressed(fill.darker());
        setStroke(stroke);
        setStrokeHover(stroke);
        setStrokeSelected(stroke);
        setStrokeHighlight(stroke);
        setStrokePressed(stroke);
    }

    private void drawPhases(ActivityRef<HospitalActivity> activityRef, Position position, GraphicsContext gc, ActivityBounds bounds,
                            boolean selected, boolean hover, boolean highlighted, boolean pressed) {
        HospitalActivity activity = activityRef.getActivity();
        double totalMillis = Duration.between(activity.getStartTime(), activity.getEndTime()).toMillis();
        if (totalMillis <= 0 || bounds.getWidth() <= 0) {
            return;
        }

        double prepWidth = bounds.getWidth() * activity.getPreparationDuration().toMillis() / totalMillis;
        double cleanupWidth = bounds.getWidth() * activity.getCleanupDuration().toMillis() / totalMillis;
        double surgeryWidth = Math.max(0, bounds.getWidth() - prepWidth - cleanupWidth);

        gc.setFill(getPrepOrCleanupFill(activity));
        fillPhase(gc, bounds.getMinX(), bounds.getMinY(), prepWidth, bounds.getHeight());

        gc.setFill(getPrepOrCleanupFill(activity));
        fillPhase(gc, bounds.getMinX() + prepWidth + surgeryWidth, bounds.getMinY(), cleanupWidth, bounds.getHeight());

        gc.save();
        gc.setStroke(getStroke(false, hover, highlighted, pressed));
        gc.setLineWidth(1);
        if (prepWidth > 0 && prepWidth < bounds.getWidth()) {
            gc.strokeLine(bounds.getMinX() + prepWidth, bounds.getMinY() + 1, bounds.getMinX() + prepWidth,
                    bounds.getMinY() + bounds.getHeight() - 1);
        }
        if (cleanupWidth > 0 && cleanupWidth < bounds.getWidth()) {
            double cleanupStart = bounds.getMinX() + prepWidth + surgeryWidth;
            gc.strokeLine(cleanupStart, bounds.getMinY() + 1, cleanupStart, bounds.getMinY() + bounds.getHeight() - 1);
        }
        gc.restore();

        drawBorder(activityRef, position, gc, bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight(),
                selected, hover, highlighted, pressed);
    }

    private void fillPhase(GraphicsContext gc, double x, double y, double w, double h) {
        if (w <= 0 || h <= 0) {
            return;
        }

        if (isCornersRounded()) {
            gc.fillRoundRect(x, y, w, h, getCornerRadius(), getCornerRadius());
        } else {
            gc.fillRect(x, y, w, h);
        }
    }

    private void drawPhaseLabel(GraphicsContext gc, String label, double x, double y, double w, double h) {
        if (w < 34) {
            return;
        }

        gc.save();
        gc.setFill(Color.rgb(20, 20, 20, 0.72));
        gc.setFont(getFont());
        gc.fillText(label, x + 6, y + h - 6, Math.max(0, w - 10));
        gc.restore();
    }

    private Color getSurgeryFill(HospitalActivity activity, boolean hover, boolean highlighted, boolean pressed) {
        Color base = activity.getUserObject().isEmergency() ? Color.valueOf("#D94F5C") : Color.valueOf("#2F6FAE");
        if (pressed) {
            return base.darker();
        }
        if (hover || highlighted) {
            return base.brighter();
        }
        return base;
    }

    private Color getPrepOrCleanupFill(HospitalActivity activity) {
        return activity.getUserObject().isEmergency() ? Color.valueOf("#F1DEB2") : Color.valueOf("#F2C572");
    }

    private String labelFor(HospitalActivity activity) {
        if (activity.hasPhases()) {
            return activity.getUserObject().getPatientName() + " - " + activity.getUserObject().getProcedureName();
        }
        return activity.getName();
    }
}
