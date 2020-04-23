/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.util;

import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.GanttChartBase;
import javafx.beans.binding.Bindings;

/**
 * Created by dirk on 11/07/16.
 */
public class Binder {

    public static void bind(GanttChartBase<?> primary, GanttChartBase<?> gantt, boolean autoBinding) {
        // synch timeline model
        Bindings.bindBidirectional(gantt.getTimeline().modelProperty(), primary
                .getTimeline().modelProperty());

        if (gantt instanceof GanttChart) {
            // synch display mode
            Bindings.bindBidirectional(((GanttChart) gantt).displayModeProperty(),
                    ((GanttChart) primary).displayModeProperty());

            // synch tree tables
            Bindings.bindBidirectional(((GanttChart) gantt).showTreeTableProperty(),
                    ((GanttChart) primary).showTreeTableProperty());
            Bindings.bindBidirectional(((GanttChart) gantt).getTreeTableMasterDetailPane()
                    .dividerPositionProperty(), ((GanttChart) primary).getTreeTableMasterDetailPane()
                    .dividerPositionProperty());
        }

        // synch details node
        Bindings.bindBidirectional(gantt.showDetailProperty(),
                primary.showDetailProperty());
        Bindings.bindBidirectional(gantt.getGraphicsMasterDetailPane()
                .dividerPositionProperty(), primary
                .getGraphicsMasterDetailPane().dividerPositionProperty());

        // synch cursor line
        Bindings.bindBidirectional(gantt.getGraphics()
                .showVerticalCursorProperty(), primary.getGraphics()
                .showVerticalCursorProperty());

        // we are also passing read-only properties via the properties maps
        Bindings.bindContent(gantt.getTimeline().getEventline()
                .getProperties(), primary.getTimeline().getEventline()
                .getProperties());

        if (autoBinding) {
            // synch layers
            Bindings.bindBidirectional(gantt.getGraphics()
                    .maxGridLevelProperty(), primary.getGraphics()
                    .maxGridLevelProperty());
            Bindings.bindBidirectional(gantt.getGraphics()
                    .showGridLineLayerProperty(), primary.getGraphics()
                    .showGridLineLayerProperty());
            Bindings.bindBidirectional(gantt.getGraphics()
                    .showInnerLinesLayerProperty(), primary.getGraphics()
                    .showInnerLinesLayerProperty());
            Bindings.bindBidirectional(gantt.getGraphics()
                    .showRowLayerProperty(), primary.getGraphics()
                    .showRowLayerProperty());
            Bindings.bindBidirectional(gantt.getGraphics()
                    .showCalendarLayerProperty(), primary.getGraphics()
                    .showCalendarLayerProperty());
            Bindings.bindBidirectional(gantt.getGraphics()
                    .showNowLineLayerProperty(), primary.getGraphics()
                    .showNowLineLayerProperty());

            // synch dateline / timeline time intervals (hover, selection, zoom)
            Bindings.bindBidirectional(gantt.getGraphics()
                    .showZoomTimeIntervalLayerProperty(), primary.getGraphics()
                    .showZoomTimeIntervalLayerProperty());
            Bindings.bindBidirectional(gantt.getGraphics()
                    .showSelectedTimeIntervalsLayerProperty(), primary
                    .getGraphics().showSelectedTimeIntervalsLayerProperty());
            Bindings.bindBidirectional(gantt.getGraphics()
                    .showHoverTimeIntervalLayerProperty(), primary.getGraphics()
                    .showHoverTimeIntervalLayerProperty());

        }

        // read only property support

        gantt.getGraphics().editedActivityProperty()
                .addListener((observable, oldValue, newValue) -> {
                    primary.getGraphics().getProperties()
                            .put("currentlyeditedactivity", newValue);
                });
    }
}
