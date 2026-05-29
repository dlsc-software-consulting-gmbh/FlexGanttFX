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
package impl.com.flexganttfx.skin.util;

import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.graphics.ListViewGraphics;
import com.flexganttfx.view.timeline.Timeline;
import javafx.beans.binding.Bindings;

public class Binder {

    public static void bind(GanttChartBase<?> gantt1, GanttChartBase<?> gantt2, boolean autoBinding) {
        // sync timeline model
        final Timeline timeline2 = gantt2.getTimeline();
        final Timeline timeline1 = gantt1.getTimeline();

        Bindings.bindBidirectional(timeline2.modelProperty(), timeline1.modelProperty());

        if (gantt2 instanceof GanttChart) {
            // sync display mode
            Bindings.bindBidirectional(((GanttChart) gantt2).displayModeProperty(), ((GanttChart) gantt1).displayModeProperty());

            // sync tree tables
            Bindings.bindBidirectional(((GanttChart) gantt2).showTreeTableProperty(), ((GanttChart) gantt1).showTreeTableProperty());
            Bindings.bindBidirectional(((GanttChart) gantt2).getTreeTableMasterDetailPane().dividerPositionProperty(), ((GanttChart) gantt1).getTreeTableMasterDetailPane().dividerPositionProperty());
        }

        // sync details node
        Bindings.bindBidirectional(gantt2.showDetailProperty(), gantt1.showDetailProperty());
        Bindings.bindBidirectional(gantt2.getGraphicsMasterDetailPane().dividerPositionProperty(), gantt1.getGraphicsMasterDetailPane().dividerPositionProperty());

        // sync cursor line
        final ListViewGraphics<?> graphics2 = gantt2.getGraphics();
        final ListViewGraphics<?> graphics1 = gantt1.getGraphics();

        Bindings.bindBidirectional(graphics2.showVerticalCursorProperty(), graphics1.showVerticalCursorProperty());

        // we are also passing read-only properties via the properties maps
        Bindings.bindContent(timeline2.getEventline().getProperties(), timeline1.getEventline().getProperties());

        Bindings.bindBidirectional(graphics2.showRowHeadersProperty(), graphics1.showRowHeadersProperty());
        Bindings.bindBidirectional(graphics2.rowHeadersWidthProperty(), graphics1.rowHeadersWidthProperty());
        Bindings.bindBidirectional(graphics2.canvasBufferProperty(), graphics1.canvasBufferProperty());

        if (autoBinding) {
            // sync layers
            Bindings.bindBidirectional(graphics2.maxGridLevelProperty(), graphics1.maxGridLevelProperty());
            Bindings.bindBidirectional(graphics2.showGridLineLayerProperty(), graphics1.showGridLineLayerProperty());
            Bindings.bindBidirectional(graphics2.showInnerLinesLayerProperty(), graphics1.showInnerLinesLayerProperty());
            Bindings.bindBidirectional(graphics2.showRowLayerProperty(), graphics1.showRowLayerProperty());
            Bindings.bindBidirectional(graphics2.showCalendarLayerProperty(), graphics1.showCalendarLayerProperty());
            Bindings.bindBidirectional(graphics2.showNowLineLayerProperty(), graphics1.showNowLineLayerProperty());

            // sync dateline / timeline time intervals (hover, selection, zoom)
            Bindings.bindBidirectional(graphics2.showZoomTimeIntervalLayerProperty(), graphics1.showZoomTimeIntervalLayerProperty());
            Bindings.bindBidirectional(graphics2.showSelectedTimeIntervalsLayerProperty(), graphics1.showSelectedTimeIntervalsLayerProperty());
            Bindings.bindBidirectional(graphics2.showHoverTimeIntervalLayerProperty(), graphics1.showHoverTimeIntervalLayerProperty());
        }

        // read only property support
        graphics2.editedActivityProperty().addListener((observable, oldValue, newValue) -> graphics1.getProperties().put("currentlyeditedactivity", newValue));
    }
}
