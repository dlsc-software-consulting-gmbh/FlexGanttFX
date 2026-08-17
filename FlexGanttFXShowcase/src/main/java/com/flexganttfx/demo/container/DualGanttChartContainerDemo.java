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

import com.flexganttfx.demo.DemoBase;
import com.flexganttfx.demo.DemoRow;
import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.container.DualGanttChartContainer;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DualGanttChartContainerDemo extends DemoBase {

    private DualGanttChartContainer dual;

    @Override
    public String getName() {
        return "Dual";
    }

    @Override
    public void dispose() {
        super.dispose();
        dual = null;
    }

    @Override
    public Node getPanel(Stage panel) {
        GanttChart<DemoRow> gc1 = new GanttChart<>();
        gc1.setRoot(new DemoRow("Root"));
        gc1.setAutoHideScrollBar(false);

        GanttChart<DemoRow> gc2 = new GanttChart<>();
        gc2.setRoot(new DemoRow("Root"));

        dual = new DualGanttChartContainer(gc1, gc2);
        dual.getPrimaryGanttChart().getGraphics().setShowRowHeaders(true);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(dual);
        borderPane.setTop(new GanttChartToolBar<>(gc1));

        return borderPane;
    }

    @Override
    public Node getControlPanel() {
        CheckBox showSecondary = new CheckBox("Show Secondary");
        showSecondary.selectedProperty().bindBidirectional(dual.showSecondaryProperty());
        return showSecondary;
    }

    @Override
    public String getDescription() {
        return "A special multi Gantt chart container that is capable of displaying "
                + "exactly two Gantt charts and keeping their layouts (same "
                + "table width, same timeline) and their scrolling and zooming behavior in "
                + "sync. The container distinguishes between a primary and a secondary Gantt "
                + "chart, where the secondary Gantt chart is located in the detail node section "
                + "of a MasterDetailPane. It can be hidden or shown on demand. Each one "
                + "of the two Gantt charts can have its own header and footer.";
    }

    static void main(String[] args) {
        Application.launch(args);
    }
}
