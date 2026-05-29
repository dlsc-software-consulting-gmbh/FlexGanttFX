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
import com.flexganttfx.demo.HelloRow;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.GanttChartLite;
import com.flexganttfx.view.container.QuadGanttChartLiteContainer;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelloQuadGanttChartLiteContainer extends FlexGanttFXSampleBase {

    private QuadGanttChartLiteContainer quad;

    @Override
    public String getSampleName() {
        return "Quad Lite";
    }

    @Override
    public void dispose() {
        super.dispose();
        quad = null;
    }

    @Override
    public Node getPanel(Stage panel) {
        GanttChartLite<HelloRow> chart1 = new GanttChartLite<>();
        GanttChartLite<HelloRow> chart2 = new GanttChartLite<>();
        GanttChartLite<HelloRow> chart3 = new GanttChartLite<>();
        GanttChartLite<HelloRow> chart4 = new GanttChartLite<>();

        chart1.setAutoHideScrollBar(false);
        chart2.setAutoHideScrollBar(false);
        chart3.setAutoHideScrollBar(false);
        chart4.setAutoHideScrollBar(false);

        chart1.setScrollBarType(GanttChartBase.ScrollBarType.FIXED_HORIZON);
        chart2.setScrollBarType(GanttChartBase.ScrollBarType.FIXED_HORIZON);
        chart3.setScrollBarType(GanttChartBase.ScrollBarType.FIXED_HORIZON);
        chart4.setScrollBarType(GanttChartBase.ScrollBarType.FIXED_HORIZON);

        chart1.getRows().add(new HelloRow("Row"));
        chart2.getRows().add(new HelloRow("Row"));
        chart3.getRows().add(new HelloRow("Row"));
        chart4.getRows().add(new HelloRow("Row"));

        quad = new QuadGanttChartLiteContainer(chart1, chart2, chart3, chart4);

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
        verticalSplit.setOnAction(evt ->quad.showVerticalSplitScreen(true));
        box.getChildren().add(verticalSplit);

        Button allFour = new Button("All Four");
        allFour.setMaxWidth(Double.MAX_VALUE);
        allFour.setOnAction(evt -> quad.showAllFour(true));
        box.getChildren().add(allFour);

        Button replaceUpperLeft = new Button("Replace UL");
        replaceUpperLeft.setMaxWidth(Double.MAX_VALUE);
        replaceUpperLeft.setOnAction(evt -> replace(Corner.UPPER_LEFT));
        box.getChildren().add(replaceUpperLeft);

        Button replaceUpperRight = new Button("Replace UR");
        replaceUpperRight.setMaxWidth(Double.MAX_VALUE);
        replaceUpperRight.setOnAction(evt -> replace(Corner.UPPER_RIGHT));
        box.getChildren().add(replaceUpperRight);

        Button replaceLowerLeft = new Button("Replace LL");
        replaceLowerLeft.setMaxWidth(Double.MAX_VALUE);
        replaceLowerLeft.setOnAction(evt -> replace(Corner.LOWER_LEFT));
        box.getChildren().add(replaceLowerLeft);

        Button replaceLowerRight = new Button("Replace LR");
        replaceLowerRight.setMaxWidth(Double.MAX_VALUE);
        replaceLowerRight.setOnAction(evt -> replace(Corner.LOWER_RIGHT));
        box.getChildren().add(replaceLowerRight);

        CheckBox showLower = new CheckBox("Show Lower");
        showLower.selectedProperty().bindBidirectional(quad.showLowerProperty());
        box.getChildren().add(showLower);

        CheckBox animated = new CheckBox("Animated");
        animated.selectedProperty().bindBidirectional(quad.animatedProperty());
        box.getChildren().add(animated);

        return box;
    }

    private void replace(Corner corner) {
        GanttChartLite<HelloRow> replacement = new GanttChartLite<>();
        replacement.setAutoHideScrollBar(false);
        replacement.setScrollBarType(GanttChartBase.ScrollBarType.FIXED_HORIZON);
        replacement.getRows().add(new HelloRow("Row"));

        switch (corner) {
            case UPPER_LEFT:
                quad.setUpperLeftGanttChart(replacement);
                break;
            case UPPER_RIGHT:
                quad.setUpperRightGanttChart(replacement);
                break;
            case LOWER_LEFT:
                quad.setLowerLeftGanttChart(replacement);
                break;
            case LOWER_RIGHT:
                quad.setLowerRightGanttChart(replacement);
                break;
        }
    }

    private enum Corner {
        UPPER_LEFT,
        UPPER_RIGHT,
        LOWER_LEFT,
        LOWER_RIGHT
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
