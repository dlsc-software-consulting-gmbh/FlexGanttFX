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
package com.flexganttfx.factory.view;

import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.factory.model.DataModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Root UI for the factory demo. Displays the {@link FactoryGanttChart} and a
 * simple legend bar at the bottom showing the job status colours.
 */
public class FactoryView extends BorderPane {

    public FactoryView() {
        DataModel dataModel = new DataModel();
        FactoryGanttChart gantt = new FactoryGanttChart(dataModel);
        setTop(new GanttChartToolBar<>(gantt));
        setCenter(gantt);
        setBottom(buildLegend());
    }

    private HBox buildLegend() {
        HBox legend = new HBox(16);
        legend.setPadding(new Insets(6, 12, 6, 12));
        legend.setAlignment(Pos.CENTER_LEFT);
        legend.setStyle("-fx-background-color: -color-bg-subtle;");

        addLegendEntry(legend, "Scheduled",   Color.STEELBLUE);
        addLegendEntry(legend, "In Progress", Color.DARKORANGE);
        addLegendEntry(legend, "Done",        Color.MEDIUMSEAGREEN);
        addLegendEntry(legend, "Delayed",     Color.CRIMSON);

        return legend;
    }

    private void addLegendEntry(HBox container, String text, Color color) {
        Rectangle swatch = new Rectangle(14, 14, color);
        swatch.setArcWidth(3);
        swatch.setArcHeight(3);
        Label label = new Label(text);
        HBox entry = new HBox(5, swatch, label);
        entry.setAlignment(Pos.CENTER_LEFT);
        container.getChildren().add(entry);
    }
}
