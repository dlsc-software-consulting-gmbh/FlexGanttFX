/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.extras;

import com.dlsc.gemsfx.PopOver;
import com.flexganttfx.extras.util.Messages;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.graphics.ListViewGraphics;
import com.flexganttfx.view.timeline.Timeline.ZoomMode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import java.text.MessageFormat;

import static com.dlsc.gemsfx.PopOver.ArrowLocation.TOP_CENTER;
import static java.util.Objects.requireNonNull;

/**
 * A toolbar implementation that can be used in combination with the Gantt chart
 * control. Please note that this toolbar is used for rapid prototyping and does
 * not present a feature-complete implementation that could be used for any kind
 * of application. An entire framework could be written just for that purpose.
 *
 * <h2>Example</h2>
 * <pre>{@code
 * GanttChart<MyRow> gantt = new GanttChart<>(new MyRow("Root"));
 *
 * GanttChartToolBar<MyRow> toolBar = new GanttChartToolBar<>(gantt);
 *
 * BorderPane pane = new BorderPane();
 * pane.setTop(toolBar);
 * pane.setCenter(gantt);
 * }</pre>
 *
 * @see GanttChartStatusBar
 *
 * @param <R>
 *            the type of the rows in the Gantt chart
 *
 * @since 1.0
 */
public class GanttChartToolBar<R extends Row<?, ?, ?>> extends ToolBar {

    /**
     * Constructs a new toolbar control. The Gantt chart has to be set later by
     * calling {@link #setGanttChart(GanttChartBase)}.
     *
     * @since 1.0
     */
    public GanttChartToolBar() {
        setOrientation(Orientation.HORIZONTAL);
        ganttChartProperty().addListener(observable -> buildToolBar());
    }

    /**
     * Constructs a new toolbar control.
     *
     * @param ganttChart the Gantt chart for which the toolbar will be used
     * @since 1.0
     */
    public GanttChartToolBar(GanttChartBase<R> ganttChart) {
        this();
        setGanttChart(ganttChart);
    }

    @Override
    public String getUserAgentStylesheet() {
        return requireNonNull(GanttChartToolBar.class.getResource("toolbar.css")).toExternalForm();
    }

    private final ObjectProperty<GanttChartBase<R>> ganttChart = new SimpleObjectProperty<>(this, "ganttChart");

    /**
     * The ganttChart property. Stores the Gantt chart for which the toolbar
     * provides navigation and display controls.
     *
     * @return the ganttChart property
     * @since 1.0
     */
    public final ObjectProperty<GanttChartBase<R>> ganttChartProperty() {
        return ganttChart;
    }

    public final GanttChartBase<R> getGanttChart() {
        return ganttChartProperty().get();
    }

    public final void setGanttChart(GanttChartBase<R> ganttChart) {
        requireNonNull(ganttChart);
        ganttChartProperty().set(ganttChart);
    }

    private final ObjectProperty<ContentDisplay> contentDisplay = new SimpleObjectProperty<>(this, "contentDisplay", ContentDisplay.GRAPHIC_ONLY);

    public final ContentDisplay getContentDisplay() {
        return contentDisplay.get();
    }

    /**
     * A property used to control how the toolbar buttons display their content.
     * The value is bidirectionally bound to the {@code contentDisplay} property
     * of each button in the toolbar, so changing this property updates all
     * buttons at once. The default value is {@link ContentDisplay#GRAPHIC_ONLY}.
     *
     * @return the content display property
     * @since 1.0
     */
    public final ObjectProperty<ContentDisplay> contentDisplayProperty() {
        return contentDisplay;
    }

    public final void setContentDisplay(ContentDisplay contentDisplay) {
        this.contentDisplay.set(contentDisplay);
    }

    private void buildToolBar() {
        getItems().clear();

        if (layerControlsPopOver != null) {
            layerControlsPopOver.hide();
            layerControlsPopOver = null;
        }

        GanttChartBase<R> ganttChart = getGanttChart();

        if (ganttChart != null) {

            Button timeNow = new Button(Messages.getString("GanttChartToolBar.BUTTON_NOW"));
            timeNow.setTooltip(new Tooltip(Messages.getString("GanttChartToolBar.BUTTON_NOW_TOOLTIP")));
            timeNow.setGraphic(new FontIcon(MaterialDesign.MDI_DEBUG_STEP_INTO));
            timeNow.setOnAction(showTimeNow());
            timeNow.contentDisplayProperty().bindBidirectional(contentDisplayProperty());
            getItems().add(timeNow);

            Button earliest = new Button(Messages.getString("GanttChartToolBar.BUTTON_EARLIEST"));
            earliest.setTooltip(new Tooltip(Messages.getString("GanttChartToolBar.BUTTON_EARLIEST_TOOLTIP")));
            earliest.setGraphic(new FontIcon(MaterialDesign.MDI_PAGE_FIRST));
            earliest.setOnAction(showEarliestActivities());
            earliest.contentDisplayProperty().bindBidirectional(contentDisplayProperty());
            getItems().add(earliest);

            Button latest = new Button(Messages.getString("GanttChartToolBar.BUTTON_LATEST"));
            latest.setTooltip(new Tooltip(Messages.getString("GanttChartToolBar.BUTTON_LATEST_TOOLTIP")));
            latest.setGraphic(new FontIcon(MaterialDesign.MDI_PAGE_LAST));
            latest.setOnAction(showLatestActivities());
            latest.contentDisplayProperty().bindBidirectional(contentDisplayProperty());
            getItems().add(latest);

            Button showAll = new Button(Messages.getString("GanttChartToolBar.BUTTON_ALL"));
            showAll.setTooltip(new Tooltip(Messages.getString("GanttChartToolBar.BUTTON_ALL_TOOLTIP")));
            showAll.setGraphic(new FontIcon(MaterialDesign.MDI_ARROW_COMPRESS_ALL));
            showAll.setOnAction(showAllActivities());
            showAll.contentDisplayProperty().bindBidirectional(contentDisplayProperty());
            getItems().add(showAll);

            getItems().add(new Separator(Orientation.VERTICAL));

            Button zoomIn = new Button(Messages.getString("GanttChartToolBar.BUTTON_ZOOM_IN"));
            zoomIn.setTooltip(new Tooltip(Messages.getString("GanttChartToolBar.BUTTON_ZOOM_IN_TOOLTIP")));
            zoomIn.setGraphic(new FontIcon(MaterialDesign.MDI_MAGNIFY_PLUS));
            zoomIn.setOnAction(zoomIn());
            zoomIn.contentDisplayProperty().bindBidirectional(contentDisplayProperty());
            getItems().add(zoomIn);

            Button zoomOut = new Button(Messages.getString("GanttChartToolBar.BUTTON_ZOOM_OUT"));
            zoomOut.setTooltip(new Tooltip(Messages.getString("GanttChartToolBar.BUTTON_ZOOM_OUT_TOOLTIP")));
            zoomOut.setGraphic(new FontIcon(MaterialDesign.MDI_MAGNIFY_MINUS));
            zoomOut.setOnAction(zoomOut());
            zoomOut.contentDisplayProperty().bindBidirectional(contentDisplayProperty());
            getItems().add(zoomOut);

            ComboBox<ZoomMode> zoomModeBox = new ComboBox<>();
            zoomModeBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(ZoomMode mode) {
                    switch (mode) {
                        case KEEP_START_TIME:
                            return Messages.getString("GanttChartToolBar.ZOOM_MODE_KEEP_START");
                        case KEEP_END_TIME:
                            return Messages.getString("GanttChartToolBar.ZOOM_MODE_KEEP_END");
                        case CENTER:
                        default:
                            return Messages.getString("GanttChartToolBar.ZOOM_MODE_KEEP_CENTER");
                    }
                }

                @Override
                public ZoomMode fromString(String string) {
                    return null;
                }
            });
            zoomModeBox.getItems().setAll(ZoomMode.values());
            zoomModeBox.valueProperty().bindBidirectional(getGanttChart().getTimeline().zoomModeProperty());
            zoomModeBox.setTooltip(new Tooltip(Messages.getString("GanttChartToolBar.TOOLTIP_ZOOM_MODE")));
            getItems().add(zoomModeBox);

            getItems().add(new Separator(Orientation.VERTICAL));

            ToggleButton links = new ToggleButton(Messages.getString("GanttChartToolBar.BUTTON_LINKS"));
            links.setTooltip(new Tooltip(Messages.getString("GanttChartToolBar.BUTTON_LINKS_TOOLTIP")));
            links.setGraphic(new FontIcon(MaterialDesign.MDI_VECTOR_LINE));
            links.selectedProperty().bindBidirectional(ganttChart.getGraphics().showLinksProperty());
            links.contentDisplayProperty().bindBidirectional(contentDisplayProperty());
            getItems().add(links);

            ToggleButton headers = new ToggleButton(Messages.getString("GanttChartToolBar.BUTTON_SCALE"));
            headers.setTooltip(new Tooltip(Messages.getString("GanttChartToolBar.BUTTON_SCALE_TOOLTIP")));
            headers.setGraphic(new FontIcon(MaterialDesign.MDI_RULER));
            headers.selectedProperty().bindBidirectional(ganttChart.getGraphics().showRowHeadersProperty());
            headers.contentDisplayProperty().bindBidirectional(contentDisplayProperty());
            getItems().add(headers);

            Button layers = new Button(Messages.getString("GanttChartToolBar.BUTTON_LAYERS"));
            layers.setTooltip(new Tooltip(Messages.getString("GanttChartToolBar.BUTTON_LAYERS_TOOLTIP")));
            layers.setGraphic(new FontIcon(MaterialDesign.MDI_LAYERS));
            layers.setOnAction(showLayerControls(layers));
            layers.contentDisplayProperty().bindBidirectional(contentDisplayProperty());
            getItems().add(layers);

            Button radar = new Button(Messages.getString("GanttChartToolBar.BUTTON_RADAR"));
            radar.setTooltip(new Tooltip(Messages.getString("GanttChartToolBar.BUTTON_RADAR_TOOLTIP")));
            radar.setGraphic(new FontIcon(MaterialDesign.MDI_RADAR));
            radar.setOnAction(showRadarPopOver(radar));
            radar.contentDisplayProperty().bindBidirectional(contentDisplayProperty());
            getItems().add(radar);

            if (ganttChart instanceof GanttChart) {
                ToggleButton table = new ToggleButton(Messages.getString("GanttChartToolBar.BUTTON_TABLE"));
                table.setTooltip(new Tooltip(Messages.getString("GanttChartToolBar.BUTTON_TABLE_TOOLTIP")));
                table.setGraphic(new FontIcon(MaterialDesign.MDI_TABLE));
                table.selectedProperty().bindBidirectional(((GanttChart)ganttChart).showTreeTableProperty());
                table.contentDisplayProperty().bindBidirectional(contentDisplayProperty());
                getItems().add(table);
            }

            getItems().add(new Separator(Orientation.VERTICAL));

            ListViewGraphics<R> graphics = ganttChart.getGraphics();

            ToggleButton cursor = new ToggleButton(Messages.getString("GanttChartToolBar.BUTTON_CURSOR"));
            cursor.setTooltip(new Tooltip(Messages.getString("GanttChartToolBar.BUTTON_CURSOR_TOOLTIP")));
            cursor.setGraphic(new FontIcon(MaterialDesign.MDI_CURSOR_TEXT));
            cursor.selectedProperty().bindBidirectional(graphics.showVerticalCursorProperty());
            cursor.contentDisplayProperty().bindBidirectional(contentDisplayProperty());
            getItems().add(cursor);

            MenuButton gridLines = new MenuButton(Messages.getString("GanttChartToolBar.BUTTON_GRID"));
            gridLines.setTooltip(new Tooltip(Messages.getString("GanttChartToolBar.BUTTON_GRID_TOOLTIP")));
            gridLines.setGraphic(new FontIcon(MaterialDesign.MDI_GRID));
            gridLines.contentDisplayProperty().bindBidirectional(contentDisplayProperty());

            MenuItem gridOff = new MenuItem(Messages.getString("GanttChartToolBar.MENU_ITEM_GRID_OFF"));
            gridOff.setGraphic(new FontIcon(MaterialDesign.MDI_GRID_OFF));
            gridOff.setOnAction(hideGridLines());
            gridLines.getItems().add(gridOff);

            for (int i = 1; i <= 2; i++) {
                MenuItem gridOn = new MenuItem(MessageFormat.format(Messages.getString("GanttChartToolBar.MENU_ITEM_GRID_LEVELS"), i));
                gridLines.getItems().add(gridOn);
                gridOn.setOnAction(showGridLines(i));
            }

            getItems().add(gridLines);

            ToggleButton calendars = new ToggleButton(Messages.getString("GanttChartToolBar.BUTTON_CALENDARS"));
            calendars.setTooltip(new Tooltip(Messages.getString("GanttChartToolBar.BUTTON_CALENDARS_TOOLTIP")));
            calendars.setGraphic(new FontIcon(MaterialDesign.MDI_CALENDAR));
            calendars.selectedProperty().bindBidirectional(graphics.showCalendarLayerProperty());
            calendars.contentDisplayProperty().bindBidirectional(contentDisplayProperty());
            getItems().add(calendars);

            ToggleButton nowLine = new ToggleButton(Messages.getString("GanttChartToolBar.BUTTON_NOW_LINE"));
            nowLine.setTooltip(new Tooltip(Messages.getString("GanttChartToolBar.BUTTON_NOW_LINE_TOOLTIP")));
            nowLine.setGraphic(new FontIcon(MaterialDesign.MDI_CLOCK));
            nowLine.selectedProperty().bindBidirectional(graphics.showNowLineLayerProperty());
            nowLine.contentDisplayProperty().bindBidirectional(contentDisplayProperty());
            getItems().add(nowLine);

            ToggleButton detail = new ToggleButton(Messages.getString("GanttChartToolBar.BUTTON_DETAIL"));
            detail.setTooltip(new Tooltip(Messages.getString("GanttChartToolBar.BUTTON_DETAIL_TOOLTIP")));
            detail.setGraphic(new FontIcon(MaterialDesign.MDI_BOOK_OPEN));
            detail.selectedProperty().bindBidirectional(ganttChart.showDetailProperty());
            detail.contentDisplayProperty().bindBidirectional(contentDisplayProperty());
            getItems().add(detail);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            getItems().add(spacer);

            TextField filterField = new TextField();
            filterField.setTooltip(new Tooltip(Messages.getString("GanttChartToolBar.TOOLTIP_FILTER")));
            filterField.getStyleClass().add("search-field");
            filterField.setPromptText("Filter");
            filterField.textProperty().addListener(it -> filter(filterField.getText()));
            HBox.setMargin(filterField, new Insets(0, 5, 0, 0));
            getItems().add(filterField);
        }
    }

    private void filter(String txt) {
        if (txt.trim().equals("")) {
            // intentional null to see if missing filter crashes anything
            getGanttChart().setRowFilter(null);
        } else {
            getGanttChart().setRowFilter(row -> row.getName().toLowerCase().contains(txt.toLowerCase()));
        }
    }

    private PopOver layerControlsPopOver;

    private EventHandler<ActionEvent> showLayerControls(Button button) {
        return evt -> {
            if (layerControlsPopOver == null) {
                LayersView<R> layersView = new LayersView<>();
                layersView.setGraphics(getGanttChart().getGraphics());
                layerControlsPopOver = new PopOver(layersView);
                layerControlsPopOver.setArrowLocation(TOP_CENTER);
            }

            layerControlsPopOver.show(button);
        };
    }

    private PopOver radarPopOver;

    private EventHandler<ActionEvent> showRadarPopOver(Button button) {
        return evt -> {
            if (radarPopOver == null) {
                RadarView<R> radarView = new RadarView<>();
                radarView.setGraphics(getGanttChart().getGraphics());
                radarPopOver = new PopOver(radarView);
                radarPopOver.setArrowLocation(TOP_CENTER);
            }

            radarPopOver.show(button);
        };
    }

    private EventHandler<ActionEvent> showGridLines(final int level) {
        return evt -> {
            getGanttChart().getGraphics().setMaxGridLevel(level);
            getGanttChart().getGraphics().setShowGridLineLayer(true);
        };
    }

    private EventHandler<ActionEvent> hideGridLines() {
        return evt -> getGanttChart().getGraphics().setShowGridLineLayer(false);
    }

    private EventHandler<ActionEvent> zoomOut() {
        return evt -> getGanttChart().getMasterTimeline().zoomOut();
    }

    private EventHandler<ActionEvent> zoomIn() {
        return evt -> getGanttChart().getMasterTimeline().zoomIn();
    }

    private EventHandler<ActionEvent> showAllActivities() {
        return evt -> getGanttChart().getGraphics().showAllActivities();
    }

    private EventHandler<ActionEvent> showLatestActivities() {
        return evt -> getGanttChart().getGraphics().showLatestActivities();
    }

    private EventHandler<ActionEvent> showEarliestActivities() {
        return evt -> getGanttChart().getGraphics().showEarliestActivities();
    }

    private EventHandler<ActionEvent> showTimeNow() {
        return evt -> getGanttChart().getMasterTimeline().showNow();
    }
}
