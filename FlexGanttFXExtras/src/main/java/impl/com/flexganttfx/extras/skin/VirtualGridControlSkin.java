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
package impl.com.flexganttfx.extras.skin;

import com.flexganttfx.extras.VirtualGridControl;
import com.flexganttfx.model.dateline.VirtualGrid;
import javafx.beans.Observable;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import static java.lang.Double.MAX_VALUE;

public class VirtualGridControlSkin extends SkinBase<VirtualGridControl> {

	private final VBox vbox;

	public VirtualGridControlSkin(VirtualGridControl control) {
		super(control);

		vbox = new VBox();
		vbox.setFillWidth(true);

		getChildren().add(vbox);

		control.showNoGridOptionProperty().addListener(it -> updatePanel());
		control.getGrids().addListener((Observable evt) -> updatePanel());

		updatePanel();
	}

	private void updatePanel() {
		vbox.getChildren().clear();
		ToggleGroup toggleGroup = new ToggleGroup();

		if (getSkinnable().isShowNoGridOption()) {
			ToggleButton noGrid = createButton(getSkinnable().getNoGridText());
			noGrid.setOnAction(evt -> getSkinnable().setValue(null));
			vbox.getChildren().add(noGrid);
			toggleGroup.getToggles().add(noGrid);
		}

		for (VirtualGrid<?> grid : getSkinnable().getGrids()) {
			ToggleButton button = createButton(grid.getName());
			button.setFocusTraversable(false);
			if (getSkinnable().getValue() == grid) {
				button.setSelected(true);
			}
			button.setOnAction(evt -> getSkinnable().setValue(grid));
			toggleGroup.getToggles().add(button);
			vbox.getChildren().add(button);
		}
	}

	private ToggleButton createButton(String name) {
		ToggleButton button = new ToggleButton(name);
		button.getStyleClass().add("grid-button");
		button.setMaxWidth(MAX_VALUE);
		return button;
	}
}
