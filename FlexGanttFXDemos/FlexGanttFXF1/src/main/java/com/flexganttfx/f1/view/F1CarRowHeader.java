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
package com.flexganttfx.f1.view;

import com.flexganttfx.f1.model.DriverRow;
import com.flexganttfx.f1.model.F1Root;
import com.flexganttfx.f1.model.TeamRow;
import com.flexganttfx.view.graphics.GraphicsBase;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContentDisplay;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Row header for the F1 Race Strategy demo. Displayed in the narrow strip
 * between the tree-table and the chart canvas (GraphicsBase row header area).
 *
 * <ul>
 *   <li>Driver rows: top-down F1 car silhouette colored with the team livery.</li>
 *   <li>Team rows: solid team-color band with the team name.</li>
 * </ul>
 */
public class F1CarRowHeader extends GraphicsBase.RowHeader<F1Root> {

    private final Canvas canvas;

    public F1CarRowHeader(GraphicsBase<F1Root> graphics) {
        super(graphics);

        canvas = new Canvas() {
            @Override public boolean isResizable()           { return true; }
            @Override public double prefWidth(double h)      { return getWidth(); }
            @Override public double prefHeight(double w)     { return getHeight(); }
        };

        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        canvas.widthProperty().addListener(it -> draw());
        canvas.heightProperty().addListener(it -> draw());

        setGraphic(canvas);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setAlignment(Pos.CENTER);

        itemProperty().addListener(it -> draw());
    }

    private void draw() {
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);

        // Access via Object to avoid checkcast against F1Root for heterogeneous row types
        Object rawItem = getItem();

        if (rawItem instanceof DriverRow driverRow) {
            TeamRow team = driverRow.getParent();
            if (team == null) {
                return;
            }
            drawCar(gc, w, h, team.getTeamColor());
        } else if (rawItem instanceof TeamRow teamRow) {
            drawTeamBand(gc, w, h, teamRow.getTeamColor(), teamRow.getName());
        }
    }

    /**
     * Draws a top-down F1 car silhouette (nose facing right) using the given team color.
     */
    private void drawCar(GraphicsContext gc, double w, double h, Color teamColor) {
        Color body  = teamColor;
        Color wing  = teamColor.deriveColor(0, 1.0, 0.55, 1.0);
        Color nose  = teamColor.deriveColor(0, 1.0, 0.75, 1.0);
        Color cockpit = teamColor.deriveColor(0, 0.9, 0.35, 1.0);
        Color tire  = Color.color(0.11, 0.11, 0.11);

        double pad = 3.0;
        double aw  = w - 2 * pad;
        double ah  = h - 2 * pad;
        double ox  = pad;
        double oy  = pad;

        // ── Rear wing (left edge, tall) ───────────────────────────────────
        gc.setFill(wing);
        gc.fillRoundRect(ox, oy + 0.06 * ah, 0.07 * aw, 0.88 * ah, 2, 2);

        // ── Rear tires ────────────────────────────────────────────────────
        gc.setFill(tire);
        double rtX = ox + 0.09 * aw;
        double rtW = 0.14 * aw;
        double rtH = 0.30 * ah;
        gc.fillRoundRect(rtX, oy,                  rtW, rtH, 2, 2);
        gc.fillRoundRect(rtX, oy + (1.0 - 0.30) * ah, rtW, rtH, 2, 2);

        // ── Main body ─────────────────────────────────────────────────────
        gc.setFill(body);
        gc.fillRoundRect(ox + 0.07 * aw, oy + 0.17 * ah, 0.80 * aw, 0.66 * ah, 5, 5);

        // ── Cockpit (dark oval) ───────────────────────────────────────────
        gc.setFill(cockpit);
        gc.fillOval(ox + 0.35 * aw, oy + 0.30 * ah, 0.22 * aw, 0.40 * ah);

        // ── Front tires ───────────────────────────────────────────────────
        gc.setFill(tire);
        double ftX = ox + 0.64 * aw;
        double ftW = 0.12 * aw;
        double ftH = 0.27 * ah;
        gc.fillRoundRect(ftX, oy,                  ftW, ftH, 2, 2);
        gc.fillRoundRect(ftX, oy + (1.0 - 0.27) * ah, ftW, ftH, 2, 2);

        // ── Nose (tapered toward front) ───────────────────────────────────
        gc.setFill(nose);
        gc.beginPath();
        gc.moveTo(ox + 0.87 * aw, oy + 0.22 * ah);
        gc.lineTo(ox + 0.87 * aw, oy + 0.78 * ah);
        gc.lineTo(ox + 0.97 * aw, oy + 0.55 * ah);
        gc.lineTo(ox + 0.97 * aw, oy + 0.45 * ah);
        gc.closePath();
        gc.fill();

        // ── Front wing (right edge, tall) ─────────────────────────────────
        gc.setFill(wing);
        gc.fillRoundRect(ox + 0.90 * aw, oy + 0.06 * ah, 0.08 * aw, 0.88 * ah, 2, 2);
    }

    /**
     * Draws a solid team-color band with the team name for team-level rows.
     */
    private void drawTeamBand(GraphicsContext gc, double w, double h, Color teamColor, String teamName) {
        // Solid band
        gc.setFill(teamColor.deriveColor(0, 1, 0.8, 0.85));
        gc.fillRect(0, 0, w, h);

        // Thin bright border on right edge
        gc.setStroke(Color.color(1, 1, 1, 0.35));
        gc.setLineWidth(1.0);
        gc.strokeLine(w - 0.5, 0, w - 0.5, h);

        // Team name text — choose contrast color based on perceived brightness
        double brightness = 0.299 * teamColor.getRed() + 0.587 * teamColor.getGreen() + 0.114 * teamColor.getBlue();
        gc.setFill(brightness > 0.55 ? Color.color(0, 0, 0, 0.85) : Color.color(1, 1, 1, 0.95));
        gc.setFont(Font.font("System", FontWeight.BOLD, Math.max(8, h * 0.38)));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(teamName, w / 2.0, h / 2.0, w - 8);
    }
}
