/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.extras.skin;

import static java.lang.Double.MAX_VALUE;
import javafx.beans.Observable;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import com.flexganttfx.extras.VirtualGridControl;
import com.flexganttfx.model.dateline.VirtualGrid;

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
