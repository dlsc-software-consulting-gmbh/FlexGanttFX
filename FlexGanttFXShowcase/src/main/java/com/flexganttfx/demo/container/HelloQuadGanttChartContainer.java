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
package com.flexganttfx.demo.container;

import com.flexganttfx.demo.FlexGanttFXSampleBase;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.container.QuadGanttChartContainer;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelloQuadGanttChartContainer extends FlexGanttFXSampleBase {

    private QuadGanttChartContainer quad;

    @Override
    public String getSampleName() {
        return "Quad";
    }

    @Override
    public void dispose() {
        super.dispose();
        quad = null;
    }

    @Override
    public Node getPanel(Stage panel) {
        quad = new QuadGanttChartContainer();
        quad.getUpperLeftGanttChart().getGraphics().setShowRowHeaders(true);
        quad.getUpperLeftGanttChart().getGraphics().setRowHeadersWidth(200);

        GanttChart gc1 = new GanttChart();
        GanttChart gc2 = new GanttChart();
        GanttChart gc3 = new GanttChart();
        GanttChart gc4 = new GanttChart();

        gc1.setRoot(new Row<>() {});
        gc2.setRoot(new Row<>() {});
        gc3.setRoot(new Row<>() {});
        gc4.setRoot(new Row<>() {});

        quad.setUpperLeftGanttChart(gc1);
        quad.setLowerLeftGanttChart(gc2);
        quad.setUpperRightGanttChart(gc3);
        quad.setLowerRightGanttChart(gc4);

        quad.getUpperLeftGanttChart().getGraphics().setShowRowHeaders(true);

        return quad;
    }

    @Override
    public Node getControlPanel() {
        HBox box = new HBox(10);
        box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Button single = new Button("Single");
        single.setMaxWidth(Double.MAX_VALUE);
        single.setOnAction(evt -> quad.showSingleChart());
        box.getChildren().add(single);

        Button horizontalSplit = new Button("Horiz. Split");
        horizontalSplit.setMaxWidth(Double.MAX_VALUE);
        horizontalSplit.setOnAction(evt -> quad.showHorizontalSplitScreen(true));
        box.getChildren().add(horizontalSplit);

        Button verticalSplit = new Button("Vert. Split");
        verticalSplit.setMaxWidth(Double.MAX_VALUE);
        verticalSplit.setOnAction(evt -> quad.showVerticalSplitScreen(true));
        box.getChildren().add(verticalSplit);

        Button allFour = new Button("All Four");
        allFour.setMaxWidth(Double.MAX_VALUE);
        allFour.setOnAction(evt -> quad.showAllFour(true));
        box.getChildren().add(allFour);

        CheckBox showLower = new CheckBox("Show Lower");
        showLower.selectedProperty().bindBidirectional(quad.showLowerProperty());
        box.getChildren().add(showLower);

        CheckBox animated = new CheckBox("Animated");
        animated.selectedProperty().bindBidirectional(quad.animatedProperty());
        box.getChildren().add(animated);

        return box;
    }

    @Override
    public String getSampleDescription() {
        return "A special multi Gantt chart container that is capable of displaying "
                + "exactly four Gantt charts and keeping their layouts (same "
                + "table width, same timeline) and their scrolling and zooming behavior in "
                + "synch.";
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
