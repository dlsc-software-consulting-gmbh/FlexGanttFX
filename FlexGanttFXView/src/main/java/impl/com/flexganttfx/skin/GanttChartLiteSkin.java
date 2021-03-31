/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
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
