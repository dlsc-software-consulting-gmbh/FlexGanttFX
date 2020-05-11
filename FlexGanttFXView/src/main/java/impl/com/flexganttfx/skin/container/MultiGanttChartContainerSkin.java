/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.container;

import javafx.beans.binding.Bindings;
import javafx.scene.control.SplitPane;

import com.flexganttfx.view.container.MultiGanttChartContainerBase;

public class MultiGanttChartContainerSkin extends MultiGanttChartContainerSkinBase<MultiGanttChartContainerBase> {

	public MultiGanttChartContainerSkin(MultiGanttChartContainerBase container) {
		super(container);
		SplitPane splitPane = container.getSplitPane();
		getChildren().add(splitPane);
		Bindings.bindContent(splitPane.getItems(), getSkinnable().getGanttCharts());
	}
}
