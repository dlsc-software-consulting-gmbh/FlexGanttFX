/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.timeline;

import com.flexganttfx.model.Row;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.model.util.TimeInterval;
import com.flexganttfx.view.graphics.SingleRowGraphics;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.Eventline;
import com.flexganttfx.view.timeline.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Tooltip;
import javafx.scene.shape.Rectangle;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.time.zone.ZoneOffsetTransition;

public class EventlineSkin extends SkinBase<Eventline> {

    private final Label timeCursor;

    private final Label markedStartTime;

    private final Label markedEndTime;

    private final Label dst;

    private final SingleRowGraphics<Row<?,?,?>> graphics;

    private DateTimeFormatter formatter;

    public EventlineSkin(final Eventline eventline) {
        super(eventline);

        timeCursor = new Label();
        timeCursor.setMouseTransparent(true);
        timeCursor.setManaged(false);
        timeCursor.getStyleClass().add("time-cursor");
        timeCursor.visibleProperty().bind(
                Bindings.and(eventline.showTimeCursorProperty(),
                        Bindings.isNotNull(eventline.cursorTimeProperty())));

        markedStartTime = new Label();
        markedStartTime.setMouseTransparent(true);
        markedStartTime.setManaged(false);
        markedStartTime.getStyleClass().addAll("marked-time", "marked-time-start");
        markedStartTime.visibleProperty().bind(
                Bindings.and(eventline.showMarkedTimeIntervalProperty(),
                        Bindings.isNotNull(eventline
                                .markedTimeIntervalProperty())));

        markedEndTime = new Label();
        markedEndTime.setMouseTransparent(true);
        markedEndTime.setManaged(false);
        markedEndTime.getStyleClass().addAll("marked-time", "marked-time-end");
        markedEndTime.visibleProperty().bind(
                Bindings.and(eventline.showMarkedTimeIntervalProperty(),
                        Bindings.isNotNull(eventline
                                .markedTimeIntervalProperty())));

        dst = new Label("DST");
        dst.setManaged(false);
        dst.getStyleClass().addAll("dst-marker");
        dst.setTooltip(new Tooltip("Daylight Savings Time Change"));

        getSkinnable().showDSTMarkerProperty().addListener(it -> eventline.requestLayout());

        graphics = eventline.getGraphics();
        graphics.getStyleClass().add("frozen-row");
        graphics.setContextMenu(null);
        graphics.setContextMenuCallback(null);
        graphics.setTimeline(eventline.getTimeline());
        graphics.setShowGridLineLayer(false);
        graphics.getBackgroundSystemLayers().clear();
        graphics.getForegroundSystemLayers().clear();

        Rectangle eventlineClip = new Rectangle();
        eventlineClip.widthProperty().bind(getSkinnable().widthProperty());
        eventlineClip.heightProperty().bind(getSkinnable().heightProperty());
        graphics.setClip(eventlineClip);

        eventline.frozenRowProperty().addListener(it -> updateRowList());
        updateRowList();

        getChildren().setAll(graphics, dst, markedStartTime, markedEndTime, timeCursor);

        registerListeners();

        formatter = getSkinnable().getDateTimeFormatter();
        getSkinnable().dateTimeFormatterProperty().addListener(observable -> {
            formatter = getSkinnable().getDateTimeFormatter();
            getSkinnable().requestLayout();
        });
    }

    private void updateRowList() {
        getSkinnable().getGraphics().getRows().setAll(getSkinnable().getFrozenRow());
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        graphics.resizeRelocate(contentX, contentY, contentWidth, contentHeight);

        // time cursor
        Timeline timeline = getSkinnable().getTimeline();
        Instant time = getSkinnable().getCursorTime();

        if (time != null) {
            double cursorLocation = getSkinnable().getCursorLocation();

            Dateline dateline = timeline.getDateline();
            ZoneId zoneId = dateline.getZoneId();

            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(time, zoneId);

            String text = formatter.format(zonedDateTime);
            timeCursor.setText(text);
            timeCursor.resizeRelocate(cursorLocation, contentY, timeCursor.prefWidth(-1), contentHeight);
        }

        TimeInterval markedTime = getSkinnable().getMarkedTimeInterval();

        if (markedTime != null) {
            Instant markedStart = markedTime.getStartTime();
            Instant markedEnd = markedTime.getEndTime();

            Dateline dateline = timeline.getDateline();
            ZoneId zoneId = dateline.getZoneId();

            ZonedDateTime zonedStartTime = ZonedDateTime.ofInstant(markedStart, zoneId);
            ZonedDateTime zonedEndTime = ZonedDateTime.ofInstant(markedEnd, zoneId);

            markedStartTime.setText(formatter.format(zonedStartTime));
            markedEndTime.setText(formatter.format(zonedEndTime));

            TimelineModel<?> timelineModel = timeline.getModel();
            double xStart = timelineModel.calculateLocationForTime(markedStart);
            double xEnd = timelineModel.calculateLocationForTime(markedEnd);

            markedStartTime.resizeRelocate(
                    xStart - markedStartTime.prefWidth(-1), contentY,
                    markedStartTime.prefWidth(-1), contentHeight);

            markedEndTime.resizeRelocate(xEnd, contentY,
                    markedEndTime.prefWidth(-1), contentHeight);
        }

        // daylight savings time
        Dateline dateline = timeline.getDateline();
        TemporalUnit unit = dateline.getPrimaryTemporalUnit();

        boolean showDST = false;

        if (unit != null && (unit.equals(ChronoUnit.HOURS) || unit.equals(ChronoUnit.MINUTES))) {
            final Instant startTime = timeline.getVisibleStartTime();
            ZoneId zoneId = dateline.getZoneId();
            final ZoneOffsetTransition transition = zoneId.getRules().nextTransition(startTime);
            if (transition != null) {
                final double dstWidth = dst.prefWidth(-1);
                double location = snapPosition(timeline.getModel().calculateLocationForTime(transition.getInstant())) - dstWidth / 2;
                if (location < dateline.getWidth()) {
                    dst.resizeRelocate(location, contentY, dstWidth, contentHeight);
                    showDST = true;
                }
            }
        }

        dst.setVisible(showDST && getSkinnable().isShowDSTMarker());
    }

    private void registerListeners() {

		/*
         * Start time changes = scrolling.
		 */
        final ChangeListener<Instant> instantChangedListener = (value,
                                                                oldStartTime, newStartTime) -> getSkinnable().requestLayout();

        Timeline timeline = getSkinnable().getTimeline();
        timeline.getModel().startTimeProperty()
                .addListener(instantChangedListener);

		/*
		 * MPP changes = zoom in / out.
		 */
        final ChangeListener<Number> numberChangedListener = (value, oldWidth,
                                                              newWidth) -> getSkinnable().requestLayout();

        getSkinnable().cursorLocationProperty().addListener(numberChangedListener);

        getSkinnable().markedTimeIntervalProperty().addListener(observable -> getSkinnable().requestLayout());

        timeline.getModel().millisPerPixelProperty().addListener(numberChangedListener);

        timeline.modelProperty().addListener(
                new WeakChangeListener<>((value, oldModel, newModel) -> {
                    if (oldModel != null) {
                        oldModel.startTimeProperty().removeListener(instantChangedListener);
                        oldModel.millisPerPixelProperty().removeListener(numberChangedListener);
                    }

                    if (newModel != null) {
                        newModel.startTimeProperty().addListener(instantChangedListener);
                        newModel.millisPerPixelProperty().addListener(numberChangedListener);
                    }

                    getSkinnable().requestLayout();
                }));
    }
}
