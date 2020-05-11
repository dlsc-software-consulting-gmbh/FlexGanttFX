/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.container;

import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.container.ContainerBase;
import com.flexganttfx.view.timeline.Timeline;
import com.flexganttfx.view.util.Position;
import impl.com.flexganttfx.skin.util.Binder;
import javafx.beans.Observable;

import java.util.List;

public abstract class MultiGanttChartContainerSkinBase<T extends ContainerBase> extends ContainerSkinBase<T> {

    public MultiGanttChartContainerSkinBase(T container) {
        super(container);

        updatePositions();
        getSkinnable().getGanttCharts().addListener((Observable observable) -> updatePositions());
    }

    private void updatePositions() {
        List<GanttChartBase<?>> ganttCharts = getSkinnable().getGanttCharts();

        int size = ganttCharts.size();

        if (size == 0) {
            return;
        }

        if (size == 1) {
            ganttCharts.get(0).setPosition(Position.ONLY);
            return;
        }

		/*
         * We are setting the same list of scale resolutions on all Gantt charts
		 * so that all grid lines layer will draw the same grid.
		 */

        GanttChartBase<?> primaryGantt = ganttCharts.get(0);
        Timeline masterTimeline = primaryGantt.getTimeline();

        for (int i = 0; i < size; i++) {

            GanttChartBase<?> ganttChart = ganttCharts.get(i);
            ganttChart.setMasterTimeline(masterTimeline);

            if (ganttChart instanceof GanttChart) {
                ((GanttChart) ganttChart).getTreeTable().getStyleClass().remove(GANTT_TREE_TABLE_VIEW_FIRST);
                ((GanttChart) ganttChart).getTreeTable().getStyleClass().remove(GANTT_TREE_TABLE_VIEW_MIDDLE);
                ((GanttChart) ganttChart).getTreeTable().getStyleClass().remove(GANTT_TREE_TABLE_VIEW_LAST);
            }

            ganttChart.getTimeline().getStyleClass().remove(TIMELINE_FIRST);
            ganttChart.getTimeline().getStyleClass().remove(TIMELINE_MIDDLE);
            ganttChart.getTimeline().getStyleClass().remove(TIMELINE_LAST);

            if (i == 0) {
                ganttChart.setPosition(Position.FIRST);
                if (ganttChart instanceof GanttChart) {
                    ((GanttChart) ganttChart).getTreeTable().getStyleClass().add(GANTT_TREE_TABLE_VIEW_FIRST);
                }
                ganttChart.getTimeline().getStyleClass().add(TIMELINE_FIRST);
            } else if (i == size - 1) {
                ganttChart.setPosition(Position.LAST);
                Binder.bind(primaryGantt, ganttChart, getSkinnable().isAutoBinding());
                if (ganttChart instanceof GanttChart) {
                    ((GanttChart) ganttChart).getTreeTable().getStyleClass().add(GANTT_TREE_TABLE_VIEW_LAST);
                }
                ganttChart.getTimeline().getStyleClass().add(TIMELINE_LAST);
            } else {
                ganttChart.setPosition(Position.MIDDLE);
                Binder.bind(primaryGantt, ganttChart, getSkinnable().isAutoBinding());
                if (ganttChart instanceof GanttChart) {
                    ((GanttChart) ganttChart).getTreeTable().getStyleClass().add(GANTT_TREE_TABLE_VIEW_MIDDLE);
                }
                ganttChart.getTimeline().getStyleClass().add(TIMELINE_MIDDLE);
            }
        }
    }
}
