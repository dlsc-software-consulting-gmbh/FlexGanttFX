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
package com.flexganttfx.experimental;

import javafx.beans.Observable;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class ColumnBrowserSkin<S> extends SkinBase<ColumnBrowser<S>> {
	private final HBox box;

	public ColumnBrowserSkin(ColumnBrowser<S> browser) {
		super(browser);

		browser.setPrefHeight(250);

		box = new HBox();
		getChildren().add(box);

		buildLists();

		browser.getColumnValuesLists().addListener((Observable evt) -> buildLists());
	}

	private void buildLists() {
		box.getChildren().clear();

		for (ColumnValuesList<S, ?> list : getSkinnable().getColumnValuesLists()) {
			HBox.setHgrow(list, Priority.ALWAYS);
			box.getChildren().add(list);
		}
	}
}
