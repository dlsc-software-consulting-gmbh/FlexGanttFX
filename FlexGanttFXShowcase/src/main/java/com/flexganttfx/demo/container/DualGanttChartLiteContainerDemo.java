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
import com.flexganttfx.view.GanttChartLite;
import com.flexganttfx.view.container.DualGanttChartLiteContainer;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

public class DualGanttChartLiteContainerDemo extends DemoBase {

	private DualGanttChartLiteContainer dual;

	@Override
	public String getName() {
		return "Dual Lite";
	}

	@Override
	public Node getPanel(Stage panel) {

		GanttChartLite<DemoRow> ganttChartLite1 = new GanttChartLite<>();
		GanttChartLite<DemoRow> ganttChartLite2 = new GanttChartLite<>();

		DemoRow row1 = new DemoRow("Row 1");
		DemoRow row2 = new DemoRow("Row 2");

		ganttChartLite1.getRows().add(row1);
		ganttChartLite2.getRows().add(row2);

		dual =  new DualGanttChartLiteContainer(ganttChartLite1, ganttChartLite2);

		return dual;
	}

	@Override
	public void dispose() {
		super.dispose();
		dual = null;
	}

	@Override
	public Node getControlPanel() {
		CheckBox showSecondary = new CheckBox("Show Secondary");
		showSecondary.selectedProperty().bindBidirectional(dual.showSecondaryProperty());
		return showSecondary;
	}

	@Override
	public String getDescription() {
		return "A special multi graphics container that is capable of displaying "
				+ "exactly two charts and keeping their layouts (same "
				+ "table width, same timeline) and their scrolling and zooming behavior in "
				+ "synch. The container distinguishes between a primary and a secondary chart "
				+ ", where the secondary chart is located in the detail node section "
				+ "of a MasterDetailPane. It can be hidden or shown on demand. Each one "
				+ "of the two charts can have its own header and footer.";
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
