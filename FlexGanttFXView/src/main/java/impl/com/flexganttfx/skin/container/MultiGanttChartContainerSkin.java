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
package impl.com.flexganttfx.skin.container;

import javafx.beans.binding.Bindings;
import javafx.scene.control.SplitPane;

import com.flexganttfx.view.container.MultiGanttChartContainerBase;

/**
 * Skin for {@link com.flexganttfx.view.container.MultiGanttChartContainerBase}. It installs
 * the split pane used to display the contained charts and keeps its items in sync with the
 * container's chart list.
 */
public class MultiGanttChartContainerSkin extends MultiGanttChartContainerSkinBase<MultiGanttChartContainerBase> {

	/**
	 * Constructs a new skin for the given multi Gantt chart container.
	 *
	 * @param container
	 *            the container
	 */
	public MultiGanttChartContainerSkin(MultiGanttChartContainerBase container) {
		super(container);
		SplitPane splitPane = container.getSplitPane();
		getChildren().add(splitPane);
		Bindings.bindContent(splitPane.getItems(), getSkinnable().getGanttCharts());
	}
}
