/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.emirates.view;

import com.flexganttfx.emirates.model.Aircraft;
import com.flexganttfx.emirates.model.Flight;
import com.flexganttfx.emirates.model.Group;
import com.flexganttfx.emirates.model.ModelObject;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.model.util.TimeInterval;
import com.flexganttfx.view.GanttChartLite;
import com.flexganttfx.view.graphics.ActivityEvent;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.GraphicsBase.RowHeader;
import com.flexganttfx.view.timeline.Timeline;
import com.jpro.webapi.WebAPI;
import impl.com.flexganttfx.skin.graphics.DragCanvas;
import impl.com.flexganttfx.skin.graphics.GraphicsBaseSkin;
import javafx.event.EventHandler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class EmiratesAircraftGanttChart extends GanttChartLite<ModelObject<?, ?, ?>> {

	public EmiratesAircraftGanttChart() {
		Timeline timeline = getTimeline();
		timeline.showTemporalUnit(ChronoUnit.HOURS, 50);

		timeline.setMoveAnimated(!WebAPI.isBrowser());
		timeline.setZoomAnimated(!WebAPI.isBrowser());

		setRowHeaderWidth(80);

		getGraphics().getBackgroundSystemLayers().add(new GroupSystemLayer(getGraphics()));
		getGraphics().setActivityRenderer(Flight.class, GanttLayout.class,new FlightRenderer(getGraphics()));
		getGraphics().setRowHeaderFactory(graphics -> new RowHeader<>() {
			{
				itemProperty().addListener(it -> {
					final ModelObject<?, ?, ?> item = getItem();
					if (item != null && !(item instanceof Group)) {
						setText(item.getName());
					} else {
						setText("");
					}
				});
			}
		});

		EventHandler<ActivityEvent> updateListener = new EventHandler<>() {
			@Override
			public void handle(ActivityEvent event) {
				updateRow(event.getActivityRef().getRow());
				updateRow(event.getOldRow());
			}

			private void updateRow(Row<?, ?, ?> row) {
				if (row != null && row instanceof Aircraft) {
					Aircraft aircraft = (Aircraft) row;
					aircraft.updateInnerLines();

					Group group = (Group) row.getParent();
					List<Layer> layers = getLayers();

					Instant st = aircraft.getEarliestTimeUsed();
					Instant et = aircraft.getLatestTimeUsed();

					group.updateUsageProfile(new TimeInterval(st, et), layers.get(layers.size() - 1), layers);
				}

			}
		};

		getGraphics().addEventHandler(ActivityEvent.START_TIME_CHANGE_FINISHED, updateListener);
		getGraphics().addEventHandler(ActivityEvent.END_TIME_CHANGE_FINISHED, updateListener);
		getGraphics().addEventHandler(ActivityEvent.DRAG_FINISHED, updateListener);
		getGraphics().addEventHandler(ActivityEvent.HORIZONTAL_DRAG_FINISHED, updateListener);

		getGraphics().setEditModeCallback(Flight.class, GanttLayout.class, param -> GraphicsBase.EditMode.DRAGGING_VERTICAL);
		getGraphics().setActivityEditingCallback(Flight.class, param -> param.getEditMode().equals(GraphicsBase.EditMode.DRAGGING_VERTICAL));

        getGraphics().skinProperty().addListener(it -> {
            GraphicsBaseSkin skin = (GraphicsBaseSkin) getGraphics().getSkin();
            DragCanvas canvas = skin.getDragCanvas();
            canvas.setIncludeSelectedActivitiesInDrag(true);
        });
	}
}
