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
package com.flexganttfx.demo.layout;

import com.flexganttfx.demo.GanttChartDemoBase;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Layout;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.ChartActivity;
import com.flexganttfx.model.activity.MutableActivityBase;
import com.flexganttfx.model.activity.MutableChartActivityBase;
import com.flexganttfx.model.exception.IllegalLineIndexException;
import com.flexganttfx.model.layout.AgendaLayout;
import com.flexganttfx.model.layout.ChartLayout;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.model.layout.LinesManagerBase;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.graphics.renderer.ActivityRenderer;
import com.flexganttfx.view.timeline.Timeline.ZoomMode;
import com.flexganttfx.view.util.Position;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.time.temporal.ChronoUnit.DAYS;

public class MixedLayoutsDemo extends GanttChartDemoBase {

    private static final long MAX_DAYS = 700;

    private Instant startTime;
    private Instant endTime;

    private GanttChart<CapacityRow> ganttChart;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void dispose() {
        super.dispose();
        ganttChart = null;
    }

    @Override
    public String getName() {
        return "Mixed";
    }

    @Override
    public String getDescription() {
        return "Shows how to use multiple layout types in the same row of a GanttChart.";
    }

    @Override
    public Node getControlPanel() {
        ComboBox<FilterMode> box = new ComboBox<>();
        box.getItems().addAll(FilterMode.values());
        box.setValue(FilterMode.NONE);
        box.valueProperty().addListener(it -> {
            switch (box.getValue()) {
                case NONE:
                    ganttChart.getGraphics().setActivityFilter(activity -> true);
                    break;
                case GANTT:
                    ganttChart.getGraphics().setActivityFilter(activity -> activity instanceof CapacityIndicator);
                    break;
                case CAPACITY:
                    ganttChart.getGraphics().setActivityFilter(activity -> activity instanceof ChartActivity);
                    break;
                case AGENDA:
                    ganttChart.getGraphics().setActivityFilter(activity -> activity instanceof AgendaEntry);
                    break;
            }
        });

        HBox hbox = new HBox(5);

        Label label = new Label("Filter");
        hbox.getChildren().addAll(label, box);
        hbox.setAlignment(Pos.CENTER_LEFT);

        return hbox;
    }

    @Override
    protected GanttChart<?> createGanttChart() {
        ganttChart = new GanttChart<>(new CapacityRow(true));
        ganttChart.getGraphics().setActivityRenderer(CapacityIndicator.class, GanttLayout.class, new CapacityIndicatorRenderer(ganttChart.getGraphics()));
        ganttChart.getTimeline().setZoomMode(ZoomMode.KEEP_START_TIME);
        ganttChart.getGraphics().setActivityRenderer(AgendaEntry.class, AgendaLayout.class, new AgendaEntryRenderer(ganttChart.getGraphics()));
        ganttChart.getTreeTableMasterDetailPane().setDividerPosition(.1);
        ganttChart.setDisplayMode(GanttChart.DisplayMode.GRAPHICS_ONLY);

        Layer capacitiesLayer = new Layer("Capacities");
        Layer agendaLayer = new Layer("Agenda");

        ganttChart.getLayers().add(capacitiesLayer);
        ganttChart.getLayers().add(agendaLayer);

        for (int i = 0; i < 10; i++) {

            CapacityRow childRow = new CapacityRow();
            childRow.setName("Row " + (i + 1));

            for (int j = 0; j < MAX_DAYS; j++) {
                MutableChartActivityBase<String> capacity = new MutableChartActivityBase<>();
                capacity.setName("Capacity " + j);

                ZonedDateTime zonedDateTime = ZonedDateTime.now().truncatedTo(DAYS).plus(Duration.ofDays(j));

                capacity.setStartTime(Instant.from(zonedDateTime));
                capacity.setDuration(Duration.ofDays(1));

                if (Math.random() < .1) {
                    capacity.setChartValue(-Math.random() * 20);
                } else {
                    capacity.setChartValue(Math.random() * 100);
                }

                CapacityIndicator header = new CapacityIndicator(capacity);
                header.setStartTime(Instant.from(zonedDateTime));
                header.setDuration(Duration.ofDays(1));

                childRow.addActivity(capacitiesLayer, header);
                childRow.addActivity(capacitiesLayer, capacity);

                for (int hour = 6; hour < 20; hour++) {
                    if (Math.random() > .8) {
                        int duration = (int) ((Math.random() * 3) + 1);
                        LocalTime localStartTime = LocalTime.of(hour, 0);
                        LocalTime localEndTime = LocalTime.of(hour + duration, 0);

                        AgendaEntry entry = new AgendaEntry();
                        entry.setStartTime(Instant.from(zonedDateTime.with(localStartTime)));
                        entry.setEndTime(Instant.from(zonedDateTime.with(localEndTime)));

                        childRow.addActivity(agendaLayer, entry);

                        hour += duration;
                    }
                }

                if (startTime == null || startTime.isAfter(capacity.getStartTime())) {
                    startTime = capacity.getStartTime();
                }

                if (endTime == null || endTime.isBefore(capacity.getEndTime())) {
                    endTime = capacity.getEndTime();
                }

                if (startTime == null || startTime.isAfter(capacity.getStartTime())) {
                    startTime = capacity.getStartTime();
                }

                if (endTime == null || endTime.isBefore(capacity.getEndTime())) {
                    endTime = capacity.getEndTime();
                }
            }

            ganttChart.getRoot().getChildren().add(childRow);
        }

        if (startTime == null) {
            startTime = Instant.now();
        }

        startTime = startTime.truncatedTo(ChronoUnit.DAYS);

        ganttChart.getTreeTable().setShowRoot(false);
        ganttChart.getRoot().setExpanded(true);
        ganttChart.getTreeTable().getTreeColumn().setPrefWidth(100);

        ganttChart.getTimeline().getModel().setStartTime(startTime);
        ganttChart.getTimeline().getModel().setNow(Instant.now().plus(Duration.ofDays(5)));
        ganttChart.getTimeline().setZoomAnimated(true);

        ganttChart.getGraphics().setActivityEditingCallback(CapacityIndicator.class, param -> false);
        ganttChart.getGraphics().setShowHorizontalCursor(true);

        return ganttChart;
    }

    private enum FilterMode {
        NONE, GANTT, CAPACITY, AGENDA
    }

    class AgendaEntry extends MutableActivityBase<String> {
        private final int colorCode;
        private final boolean alarmSet;
        private final boolean recurring;

        public AgendaEntry() {
            colorCode = (int) (Math.random() * 3);
            alarmSet = Math.random() > .8;
            recurring = Math.random() > .9;
        }

        public int getColorCode() {
            return colorCode;
        }

        public boolean isAlarmSet() {
            return alarmSet;
        }

        public boolean isRecurring() {
            return recurring;
        }
    }

    class AgendaEntryRenderer extends ActivityRenderer<AgendaEntry> {
        private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
        private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

        private final Map<Integer, Color> fillColorMap = new HashMap<>();
        private final Map<Integer, Color> strokeColorMap = new HashMap<>();
        private final Map<Integer, Color> textColorMap = new HashMap<>();

        private final Font font = Font.font("system", FontWeight.BOLD, 10);

        private final Image alarmImage;
        private final Image recurringImage;

        public AgendaEntryRenderer(GraphicsBase<?> graphics) {
            super(graphics, "Agenda Entries");

            fillColorMap.put(0, Color.GREEN);
            fillColorMap.put(1, Color.CRIMSON);
            fillColorMap.put(2, Color.ORANGE);

            strokeColorMap.put(0, Color.GREEN.darker());
            strokeColorMap.put(1, Color.CRIMSON.darker());
            strokeColorMap.put(2, Color.ORANGE.darker());

            textColorMap.put(0, Color.GREEN.darker().darker().darker());
            textColorMap.put(1, Color.CRIMSON.darker().darker().darker());
            textColorMap.put(2, Color.ORANGE.darker().darker().darker());

            setCornerRadius(6);
            setPadding(new Insets(0, 3, 0, 2));

            alarmImage = new Image(Objects.requireNonNull(AgendaEntryRenderer.class.getResource("alarmclock.png")).toExternalForm());
            recurringImage = new Image(Objects.requireNonNull(AgendaEntryRenderer.class.getResource("arrow_loop2.png")).toExternalForm());
        }

        @Override
        protected ActivityBounds drawActivity(ActivityRef<AgendaEntry> activityRef, Position position, GraphicsContext gc, double x, double y, double w, double h, boolean selected, boolean hover, boolean highlighted, boolean pressed) {

            AgendaEntry entry = activityRef.getActivity();

            int colorCode = entry.getColorCode();
            setFill(fillColorMap.get(colorCode));
            setFillHover(fillColorMap.get(colorCode).brighter());
            setFillSelected(getFill());
            setFillPressed(getFill());

            setStroke(strokeColorMap.get(colorCode));
            setStrokeHover(strokeColorMap.get(colorCode));
            setStrokeSelected(getStroke());
            setStrokePressed(getStroke());

            if (pressed || selected) {
                setAlpha(1);
            } else {
                setAlpha(.66);
            }

            setAlpha(getAlpha() * activityRef.getLayer().getFadeInOutOpacity());

            ActivityBounds bounds = super.drawActivity(activityRef, position, gc, x, y, w, h, selected, hover, highlighted, pressed);

            if (w > 70) {
                gc.setFont(font);
                gc.setFill(textColorMap.get(colorCode));

                Row<?, ?, ?> row = activityRef.getRow();
                ZonedDateTime zonedStart = ZonedDateTime.ofInstant(entry.getStartTime(), row.getZoneId());
                ZonedDateTime zonedEnd = ZonedDateTime.ofInstant(entry.getEndTime(), row.getZoneId());

                String dateText = dateFormatter.format(zonedStart);
                String startText = timeFormatter.format(zonedStart);
                String endText = timeFormatter.format(zonedEnd);

                Insets padding = getPadding();
                x += padding.getLeft();
                y += padding.getTop();
                w -= (padding.getLeft() + padding.getRight());
                h -= (padding.getTop() + padding.getBottom());

                if (h >= 20) {
                    gc.setTextAlign(TextAlignment.LEFT);
                    gc.setTextBaseline(VPos.TOP);
                    gc.fillText(dateText, snapPositionX(x + 4), snapPositionY(y + 4));
                    gc.setTextAlign(TextAlignment.RIGHT);

                    if (w > 110) {
                        gc.fillText(startText, snapPositionX(x + w - 4), snapPositionY(y + 4));
                    }
                }

                if (h >= 40) {
                    switch (position) {
                        case FIRST:
                        case MIDDLE:
                        default:
                            break;
                        case LAST:
                        case ONLY:
                            gc.setTextBaseline(VPos.BOTTOM);
                            gc.fillText(endText, snapPositionX(x + w - 4), snapPositionY(y + h - 4));
                            break;
                    }

                    gc.save();
                    gc.setGlobalAlpha(1);

                    if (entry.isAlarmSet()) {
                        gc.drawImage(alarmImage, snapPositionX(x + 2), snapPositionY(y + h - 18));
                    }

                    if (entry.isRecurring() && h > 80) {
                        gc.drawImage(recurringImage, snapPositionX(x + w / 2 - recurringImage.getWidth() / 2), snapPositionY(y + h / 2 - recurringImage.getHeight() / 2));
                    }

                    gc.restore();
                }
            }

            return bounds;
        }
    }

    class CapacityIndicator extends MutableActivityBase<Object> {

        private final ChartActivity capacity;

        public CapacityIndicator(ChartActivity capacity) {
            this.capacity = capacity;
        }

        public ChartActivity getCapacity() {
            return capacity;
        }
    }

    class CapacityIndicatorRenderer extends ActivityBarRenderer<CapacityIndicator> {

        private final Map<Integer, Paint> paintMap = new HashMap<>();
        private final Map<Integer, Paint> textFillMap = new HashMap<>();

        public CapacityIndicatorRenderer(GraphicsBase<?> graphics) {
            super(graphics, "Capacity Indicator");

            paintMap.put(0, Color.DARKGREEN);
            paintMap.put(10, Color.GREEN);
            paintMap.put(20, Color.LIGHTGREEN);
            paintMap.put(50, Color.LIGHTYELLOW);
            paintMap.put(60, Color.YELLOW);
            paintMap.put(30, Color.ORANGE);
            paintMap.put(40, Color.DARKORANGE);
            paintMap.put(70, Color.RED);
            paintMap.put(80, Color.DARKRED);
            paintMap.put(90, Color.VIOLET);
            paintMap.put(100, Color.DARKVIOLET);

            textFillMap.put(0, Color.WHITE);
            textFillMap.put(10, Color.WHITE);
            textFillMap.put(20, Color.BLACK);
            textFillMap.put(50, Color.BLACK);
            textFillMap.put(60, Color.BLACK);
            textFillMap.put(30, Color.ORANGE);
            textFillMap.put(40, Color.WHITE);
            textFillMap.put(70, Color.WHITE);
            textFillMap.put(80, Color.WHITE);
            textFillMap.put(90, Color.WHITE);
            textFillMap.put(100, Color.WHITE);
        }

        @Override
        protected ActivityBounds drawActivity(ActivityRef<CapacityIndicator> activityRef, Position position, GraphicsContext gc, double x, double y, double w, double h, boolean selected, boolean hover, boolean highlighted, boolean pressed) {

            CapacityIndicator indicator = activityRef.getActivity();
            ChartActivity capacity = indicator.getCapacity();
            double capacityUsed = capacity.getChartValue();

            int limit = Math.max(0, Math.min(100, (int) (capacityUsed - capacityUsed % 10)));

            gc.setFill(paintMap.get(limit));

            if (w > 4) {
                gc.fillRect(x + 1, y + 1, w - 2, h - 2);
            } else {
                gc.fillRect(x, y + 1, w, h - 2);
            }

            setTextFill(textFillMap.get(limit));

            drawText(activityRef, Integer.toString((int) capacityUsed), TextPosition.CENTER, gc, x, y, w, h, selected, hover, highlighted, pressed);

            return new ActivityBounds(activityRef, x, y, w, h);
        }
    }

    class CapacityRow extends Row<CapacityRow, CapacityRow, Activity> {

        public CapacityRow(boolean parent) {
            setLinesManager(new CapacityRowLineManager(this));

            if (parent) {
                setHeight(24);
            } else {
                setLineCount(3);
                setHeight(300);
            }

            setMaxHeight(1000);
        }

        public CapacityRow() {
            this(false);
        }
    }

    public class CapacityRowLineManager extends LinesManagerBase<Activity> {

        private final GanttLayout ganttLayout = new GanttLayout();

        private final ChartLayout capacityLayout = new ChartLayout();

        private final AgendaLayout agendaLayout = new AgendaLayout();

        public CapacityRowLineManager(CapacityRow row) {
            super(row);

            capacityLayout.setMaxValue(100);
            capacityLayout.setMinValue(-20);
            capacityLayout.getMajorTicks().addAll(0.0, 50.0, 100.0);
            capacityLayout.getMinorTicks().addAll(-10.0, 10.0, 20.0, 30.0, 40.0, 60.0, 70.0, 80.0, 90.0);

            agendaLayout.setStartTime(LocalTime.of(0, 0));
            agendaLayout.setEndTime(LocalTime.of(23, 0));
        }

        @Override
        public int getLineIndex(Activity activity) {
            if (activity instanceof MutableChartActivityBase) {
                return 1;
            } else if (activity instanceof CapacityIndicator) {
                return 0;
            }

            return 2;
        }

        @Override
        public double getLineHeight(int lineIndex, double rowHeight) throws IllegalLineIndexException {
            if (lineIndex == 0) {
                return 20;
            } else if (lineIndex == 1) {
                return 100;
            }

            // last line fills the row
            return getRow().getHeight() - getLineHeight(0, rowHeight) - getLineHeight(1, rowHeight);
        }

        @Override
        public double getLineLocation(int lineIndex, double rowHeight) throws IllegalLineIndexException {
            switch (lineIndex) {
                case 0:
                    return 0;
                case 1:
                    return getLineHeight(0, rowHeight);
                case 2:
                    return getLineLocation(1, rowHeight) + getLineHeight(1, rowHeight);
            }

            return 0;
        }

        @Override
        public Layout getLineLayout(int lineIndex) throws IllegalLineIndexException {
            switch (lineIndex) {
                case 0:
                    return ganttLayout;
                case 1:
                    return capacityLayout;
                default:
                    return agendaLayout;
            }
        }
    }
}
