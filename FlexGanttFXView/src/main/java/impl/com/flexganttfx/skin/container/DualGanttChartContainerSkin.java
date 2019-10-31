/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.container;

import com.flexganttfx.view.container.DualGanttChartContainerBase;
import org.controlsfx.control.MasterDetailPane;

import javafx.beans.binding.Bindings;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;

public class DualGanttChartContainerSkin extends
		MultiGanttChartContainerSkinBase<DualGanttChartContainerBase> {

	public DualGanttChartContainerSkin(DualGanttChartContainerBase container) {
		super(container);

		MasterDetailPane masterDetailPane = container.getMasterDetailPane();
		masterDetailPane.setMasterNode(new Wrapper(true));
		masterDetailPane.setDetailNode(new Wrapper(false));
		masterDetailPane.setDividerPosition(.6);

		masterDetailPane.setShowDetailNode(container.isShowSecondary());
		Bindings.bindBidirectional(container.showSecondaryProperty(),
				masterDetailPane.showDetailNodeProperty());

		getChildren().add(masterDetailPane);
	}

	private class Wrapper extends BorderPane {

		public Wrapper(boolean primary) {
			if (primary) {
				topProperty().bind(getSkinnable().primaryHeaderProperty());
				centerProperty().bind(
						getSkinnable().primaryGanttChartProperty());
				bottomProperty().bind(getSkinnable().primaryFooterProperty());
			} else {
				topProperty().bind(getSkinnable().secondaryHeaderProperty());
				centerProperty().bind(
						getSkinnable().secondaryGanttChartProperty());
				bottomProperty().bind(getSkinnable().secondaryFooterProperty());

				getSkinnable().secondaryGanttChartProperty()
						.addListener(
								observable -> SplitPane
										.setResizableWithParent(getSkinnable()
												.getSecondaryGanttChart(),
												false));
			}
		}
	}
}
