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
package com.flexganttfx.extras.properties;

import com.flexganttfx.model.Row;
import com.flexganttfx.model.dateline.VirtualGrid;
import com.flexganttfx.view.graphics.GraphicsBase;
import javafx.beans.value.ObservableValue;
import org.controlsfx.control.PropertySheet.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A property sheet item provider for {@link GraphicsBase}.
 *
 * @param <R> the row type
 */
public class GraphicsBaseItemProvider<R extends Row<?, ?, ?>> implements ItemProvider<GraphicsBase<R>> {

    private static final String GRAPHICS_VIEW_PROPERTIES = "Control: Graphics";

    @Override
    public List<Item> getPropertySheetItems(GraphicsBase<R> graphics) {

        List<Item> items = new ArrayList<>();

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.showRowHeadersProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setShowRowHeaders((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isShowRowHeaders();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Row Headers";
            }

            @Override
            public String getDescription() {
                return "Show / hide row headers";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.rowHeadersWidthProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setRowHeadersWidth((Double) value);
            }

            @Override
            public Object getValue() {
                return graphics.getRowHeadersWidth();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Row Header Width";
            }

            @Override
            public String getDescription() {
                return "The width of the row headers";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.canvasBufferProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setCanvasBuffer((Double) value);
            }

            @Override
            public Object getValue() {
                return graphics.getCanvasBuffer();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Canvas Buffer";
            }

            @Override
            public String getDescription() {
                return "Increases the canvas widths to reduce redraws";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.safeRenderingProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setSafeRendering((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isSafeRendering();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Safe Rendering";
            }

            @Override
            public String getDescription() {
                return "Enables / disables safe rendering mode.";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.showZoneIdProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setShowZoneId((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isShowZoneId();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Zone Id";
            }

            @Override
            public String getDescription() {
                return "Shows / hides the zone IDs used by the rows.";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.rowEditingModeProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setRowEditingMode((GraphicsBase.RowEditingMode) value);
            }

            @Override
            public Object getValue() {
                return graphics.getRowEditingMode();
            }

            @Override
            public Class<?> getType() {
                return GraphicsBase.RowEditingMode.class;
            }

            @Override
            public String getName() {
                return "Row Editing Mode";
            }

            @Override
            public String getDescription() {
                return "Single or multi row editing.";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.animateRowEditorProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setAnimateRowEditor((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isAnimateRowEditor();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Animate Row Editor";
            }

            @Override
            public String getDescription() {
                return "Use flip animation to show / hide the row editor.";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.showVerticalCursorProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setShowVerticalCursor((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isShowVerticalCursor();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Vertical Cursor";
            }

            @Override
            public String getDescription() {
                return "Enables / disables the vertical cursor line.";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.showHorizontalCursorProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setShowHorizontalCursor((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isShowHorizontalCursor();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Horizontal Cursor";
            }

            @Override
            public String getDescription() {
                return "Enables / disables the horizontal cursor line.";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.maxGridLevelProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setMaxGridLevel((Integer) value);
            }

            @Override
            public Object getValue() {
                return graphics.getMaxGridLevel();
            }

            @Override
            public Class<?> getType() {
                return Integer.class;
            }

            @Override
            public String getName() {
                return "Max Grid Level";
            }

            @Override
            public String getDescription() {
                return "Determines the number of grid levels shown by the grid layer.";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.virtualGridProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setVirtualGrid((VirtualGrid<?>) value);
            }

            @Override
            public Object getValue() {
                return graphics.getVirtualGrid();
            }

            @Override
            public Class<?> getType() {
                return VirtualGrid.class;
            }

            @Override
            public String getName() {
                return "Virtual Grid";
            }

            @Override
            public String getDescription() {
                return "Sets a virtual grid.";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.fadeInOutVisibilityChangesProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setFadeInOutVisibilityChanges((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isFadeInOutVisibilityChanges();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Fade in/out";
            }

            @Override
            public String getDescription() {
                return "Controls whether visibility changes will be animated with a quick fade in/out.";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional
                        .of(graphics.fadeInOutVisibilityChangesDurationProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setFadeInOutVisibilityChangesDuration((Double) value);
            }

            @Override
            public Object getValue() {
                return graphics.getFadeInOutVisibilityChangesDuration();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Fade in/out duration";
            }

            @Override
            public String getDescription() {
                return "The duration in millis of the visibility change animation.";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.selectionModeProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setSelectionMode((GraphicsBase.SelectionMode) value);
            }

            @Override
            public Object getValue() {
                return graphics.getSelectionMode();
            }

            @Override
            public Class<?> getType() {
                return GraphicsBase.SelectionMode.class;
            }

            @Override
            public String getName() {
                return "Selection Mode";
            }

            @Override
            public String getDescription() {
                return "Sets a selection mode (single, multiple, none).";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.debugModeProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setDebugMode((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isDebugMode();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Debug Mode";
            }

            @Override
            public String getDescription() {
                return "Adds debug information to the rendered activities (bounds).";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.lassoSelectionBehaviourProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setLassoSelectionBehaviour((GraphicsBase.LassoSelectionBehaviour) value);
            }

            @Override
            public Object getValue() {
                return graphics.getLassoSelectionBehaviour();
            }

            @Override
            public Class<?> getType() {
                return GraphicsBase.LassoSelectionBehaviour.class;
            }

            @Override
            public String getName() {
                return "Selection Behaviour";
            }

            @Override
            public String getDescription() {
                return "Controls whether activities are selected by the lasso.";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.highlightDelayProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setHighlightDelay((long) value);
            }

            @Override
            public Object getValue() {
                return graphics.getHighlightDelay();
            }

            @Override
            public Class<?> getType() {
                return Long.class;
            }

            @Override
            public String getName() {
                return "Highlight Delay";
            }

            @Override
            public String getDescription() {
                return "Controls the blinking speed of highlighted activities.";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.showHoverTimeIntervalLayerProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setShowHoverTimeIntervalLayer((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isShowHoverTimeIntervalLayer();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Hover Interval";
            }

            @Override
            public String getDescription() {
                return "Hide or show the time interval over which the mouse cursor hovers in the dateline.";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.showSelectedTimeIntervalsLayerProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setShowSelectedTimeIntervalsLayer((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isShowSelectedTimeIntervalsLayer();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Selected Intervals";
            }

            @Override
            public String getDescription() {
                return "Hide or show the time interval selections made in the dateline.";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.showGridLineLayerProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setShowGridLineLayer((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isShowGridLineLayer();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Grid Lines";
            }

            @Override
            public String getDescription() {
                return "Enables / disables grid lines";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.showInnerLinesLayerProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setShowInnerLinesLayer((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isShowInnerLinesLayer();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Inner Lines";
            }

            @Override
            public String getDescription() {
                return "Enables / disables display of divider lines for inner lines";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.showAgendaLinesLayerProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setShowAgendaLinesLayer((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isShowAgendaLinesLayer();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Agenda Lines";
            }

            @Override
            public String getDescription() {
                return "Enables / disables display of agenda lines (hours, minutes)";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.showNowLineLayerProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setShowNowLineLayer((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isShowNowLineLayer();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Now Line";
            }

            @Override
            public String getDescription() {
                return "Enables / disables display of now line";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.showDSTLineLayerProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setShowDSTLineLayer((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isShowDSTLineLayer();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show DST Line";
            }

            @Override
            public String getDescription() {
                return "Enables / disables display of DST line";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.showRowLayerProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setShowRowLayer((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isShowRowLayer();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Row Backgrounds";
            }

            @Override
            public String getDescription() {
                return "Enables / disables display of individual row backgrounds";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.showChartLinesLayerProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setShowChartLinesLayer((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isShowChartLinesLayer();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Chart Lines";
            }

            @Override
            public String getDescription() {
                return "Enables / disables display of chart lines.";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.showCalendarLayerProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setShowCalendarLayer((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isShowCalendarLayer();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Calendars";
            }

            @Override
            public String getDescription() {
                return "Enables / disables display of calendars";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.showLayoutLayerProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setShowLayoutLayer((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isShowLayoutLayer();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Layout Decoration";
            }

            @Override
            public String getDescription() {
                return "Enables / disables display of layout specific decorations.";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.showZoomTimeIntervalLayerProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setShowZoomTimeIntervalLayer((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isShowZoomTimeIntervalLayer();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Zoom Interval";
            }

            @Override
            public String getDescription() {
                return "Hide or show the time interval when zooming inside the dateline.";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.showMarkedTimeIntervalProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setShowMarkedTimeInterval((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isShowMarkedTimeInterval();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Marked Interval";
            }

            @Override
            public String getDescription() {
                return "Hide or show the time interval currently marked (e.g. while dragging).";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.dragAndDropFeedbackProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setDragAndDropFeedback((GraphicsBase.DragAndDropFeedback) value);
            }

            @Override
            public Object getValue() {
                return graphics.getDragAndDropFeedback();
            }

            @Override
            public Class<?> getType() {
                return GraphicsBase.DragAndDropFeedback.class;
            }

            @Override
            public String getName() {
                return "Drag & Drop Feedback";
            }

            @Override
            public String getDescription() {
                return "Control visual drag feedback";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.autoGridEnabledProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setAutoGridEnabled((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isAutoGridEnabled();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Autogrid";
            }

            @Override
            public String getDescription() {
                return "Enable or disable the autogrid feature.";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.lassoEnabledProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setLassoEnabled((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isLassoEnabled();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Lasso Enabled";
            }

            @Override
            public String getDescription() {
                return "Enable or disable the lasso.";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.lassoSnapsToGridProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setLassoSnapsToGrid((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isLassoSnapsToGrid();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Lasso Grid";
            }

            @Override
            public String getDescription() {
                return "Enable or disable the lasso snaps to grid feature.";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.horizontalDragEnabledProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setHorizontalDragEnabled((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isHorizontalDragEnabled();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Horizontal Drag";
            }

            @Override
            public String getDescription() {
                return "Enable or disable horizontal dragging";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(graphics.autoMarkedTimeIntervalProperty());
            }

            @Override
            public void setValue(Object value) {
                graphics.setAutoMarkedTimeInterval((Boolean) value);
            }

            @Override
            public Object getValue() {
                return graphics.isAutoMarkedTimeInterval();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Auto Marked Interval";
            }

            @Override
            public String getDescription() {
                return "Enable or disable the automatic update of the marked time interval.";
            }

            @Override
            public String getCategory() {
                return GRAPHICS_VIEW_PROPERTIES;
            }
        });

        return items;
    }
}
