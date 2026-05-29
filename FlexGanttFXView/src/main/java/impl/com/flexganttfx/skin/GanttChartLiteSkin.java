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
package impl.com.flexganttfx.skin;

import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChartLite;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class GanttChartLiteSkin<R extends Row<?, ?, ?>> extends GanttChartBaseSkin<R, GanttChartLite<R>> {

	public GanttChartLiteSkin(GanttChartLite<R> control) {
		super(control);

		bindRows(control.getRows());
		control.rowsProperty().addListener((observable, oldList, newList) -> {
			/*
			 * We have to use a change listener and we have to perform the equality
			 * test on the lists as a ListProperty will fire change events even when
			 * only the content of the list has changed. But we only care about the
			 * replacement of the entire list. Only then do we have to rebind.
			 */
			if (oldList != newList) {
				bindRows(newList);
			}
		});
	}

	private void bindRows(ObservableList<R> newList) {
		FilteredList<R> filteredList = new FilteredList<>(newList);
		filteredList.predicateProperty().bind(getSkinnable().rowFilterProperty());
		getSkinnable().getGraphics().setRows(filteredList);
	}
}
