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
import com.flexganttfx.view.container.DualGanttChartLiteContainer;
import com.flexganttfx.view.GanttChartLite;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

public class HelloDualGanttChartLiteContainer extends FlexGanttFXSampleBase {

	private DualGanttChartLiteContainer dual;

	@Override
	public String getSampleName() {
		return "Dual Lite";
	}

	@Override
	public Node getPanel(Stage panel) {

		GanttChartLite<HelloRow> ganttChartLite1 = new GanttChartLite<>();
		GanttChartLite<HelloRow> ganttChartLite2 = new GanttChartLite<>();

		HelloRow row1 = new HelloRow("Row 1");
		HelloRow row2 = new HelloRow("Row 2");

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
	public String getSampleDescription() {
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
