/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 * <p>
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.timeline;

import com.flexganttfx.model.dateline.DatelineModel;
import com.flexganttfx.model.dateline.Resolution;
import com.flexganttfx.model.dateline.Resolution.Position;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.model.util.TimeInterval;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.DatelineScrollingEvent;
import com.flexganttfx.view.timeline.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.flexganttfx.model.dateline.Resolution.Position.BOTTOM;
import static com.flexganttfx.model.dateline.Resolution.Position.MIDDLE;
import static com.flexganttfx.model.dateline.Resolution.Position.ONLY;
import static com.flexganttfx.model.dateline.Resolution.Position.TOP;

public class DatelineSkin extends SkinBase<Dateline> {

    protected final Region lasso;
    private final VBox scalesBox;
    private final Map<Integer, DatelineScale> rowScaleMap = new HashMap<>();

    private final ChangeListener<Instant> startTimeListener = (value, oldTime, newTime) -> {
        final Dateline dateline = getSkinnable();

        final double offset = getDateline().getTimeline().getModel().getOffset();
        final double x = dateline.getTimeline().getModel().calculateLocationForTime(oldTime) - offset;
        final double newTranslateX = dateline.getTranslateX() + x;

        if (Math.abs(newTranslateX) < dateline.getDatelineBuffer()) {
            dateline.setTranslateX(newTranslateX);
        } else {
            boolean scrollingRight = (newTranslateX - dateline.getTranslateX()) < 0;

            if (scrollingRight) {
                dateline.setTranslateX(Math.max(0, dateline.getDatelineBuffer()));
            } else {
                dateline.setTranslateX(Math.min(0, -dateline.getDatelineBuffer()));
            }

            updateDatelineCells();
        }

    };
    private final WeakChangeListener weakStartTimeListener = new WeakChangeListener(startTimeListener);
    private boolean initialSetup;

    public DatelineSkin(Dateline dateline) {
        super(dateline);

        this.lasso = new Region();
        this.lasso.setVisible(false);
        this.lasso.getStyleClass().add("zoom-lasso");
        this.lasso.setManaged(false);
        this.lasso.setMouseTransparent(true);

        scalesBox = new VBox();
        scalesBox.setSnapToPixel(false); // important, otherwise we get gaps
        scalesBox.getStyleClass().add("dateline-content");

        getChildren().addAll(scalesBox, lasso);

        registerDatelineListeners(dateline);
        registerDatelineModelListeners(dateline);
        registerTimelineListeners(dateline.getTimeline());

        dateline.selectedTimeIntervalProperty().addListener(it -> updateLasso());

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(dateline.widthProperty());
        clip.heightProperty().bind(dateline.heightProperty());
        dateline.setClip(clip);

        dateline.getTimeline().offsetProperty().addListener(it -> updateDatelineCells());
        dateline.widthProperty().addListener(it -> updateDatelineCells());

        dateline.widthProperty().addListener((value, oldNumber, newNumber) -> fireScrollingEvent());
        dateline.heightProperty().addListener((value, oldNumber, newNumber) -> fireScrollingEvent());

        buildRows();
    }

    private Dateline getDateline() {
        return getSkinnable();
    }

    private void registerTimelineListeners(final Timeline timeline) {

        /*
         * Start time changes = scrolling.
         */

        TimelineModel<?> timelineModel = timeline.getModel();
        ObjectProperty<Instant> startTimeProperty = timelineModel.startTimeProperty();
        startTimeProperty.addListener(weakStartTimeListener);

        /*
         * MPP changes = zoom in / out.
         */
        final ChangeListener<Number> mppListener = (value, oldWidth, newWidth) -> {
            for (DatelineScale scale : rowScaleMap.values()) {
                scale.setResolution(null);
            }

            updateDatelineCells();
        };

        timeline.getModel().millisPerPixelProperty().addListener(mppListener);

        timeline.modelProperty().addListener(
                new WeakChangeListener<>((value, oldModel, newModel) -> {

                    if (oldModel != null) {
                        oldModel.startTimeProperty().removeListener(weakStartTimeListener);
                        oldModel.millisPerPixelProperty().removeListener(mppListener);
                    }

                    if (newModel != null) {
                        newModel.startTimeProperty().addListener(weakStartTimeListener);
                        newModel.millisPerPixelProperty().addListener(mppListener);
                    }

                    getSkinnable().requestLayout();
                }));
    }

    private void updateDatelineCells() {
        buildCells();
        fireScrollingEvent();
    }

    private void fireScrollingEvent() {
        Timeline timeline = getSkinnable().getTimeline();

        Instant startTime = timeline.getModel().getStartTime();
        Instant endTime = timeline.getModel().calculateTimeForLocation(
                getSkinnable().getTimeline().getWidth());

        DatelineScrollingEvent event = new DatelineScrollingEvent(
                getSkinnable(), getSkinnable(),
                DatelineScrollingEvent.VISIBLE_RANGE_CHANGED, startTime,
                endTime, getSkinnable().getZoneId());

        getSkinnable().fireEvent(event);
    }

    private void registerDatelineListeners(Dateline dateline) {
        dateline.datelineBufferProperty().addListener(it -> getSkinnable().requestLayout());

        dateline.zoneIdProperty().addListener((value, oldZoneId, newZoneId) -> {
            dateline.getSelectedIntervals().clear();
            getSkinnable().requestLayout();
        });
    }

    private void registerDatelineModelListeners(Dateline dateline) {
        final ListChangeListener<Resolution<?>> resolutionListener = change -> buildRows();

        dateline.getModel().getResolutions().addListener(resolutionListener);

        final ChangeListener<Number> scaleCountChangeListener = (value, oldWidth, newWidth) -> {
            buildRows();
            getSkinnable().requestLayout();
        };

        dateline.getModel().scaleCountProperty().addListener(scaleCountChangeListener);

        dateline.modelProperty().addListener((value, oldModel, newModel) -> {
            if (oldModel != null) {
                oldModel.getResolutions().removeListener(resolutionListener);
                oldModel.scaleCountProperty().removeListener(scaleCountChangeListener);
            }
            if (newModel != null) {
                newModel.getResolutions().addListener(resolutionListener);
                newModel.scaleCountProperty().addListener(scaleCountChangeListener);
            }
        });
    }

    void buildRows() {
        if (getDateline().isVisible()) {
            rowScaleMap.clear();
            scalesBox.getChildren().clear();
            DatelineModel<?> model = getDateline().getModel();
            int scaleCount = model.getScaleCount();
            for (int i = 0; i < scaleCount; i++) {
                DatelineScale scale = new DatelineScale(getDateline(), getScalePosition(i, scaleCount));
                VBox.setVgrow(scale, Priority.ALWAYS);
                scalesBox.getChildren().add(0, scale);
                rowScaleMap.put(i, scale);
            }

            scalesBox.requestLayout();
        }
    }

    private Position getScalePosition(int row, int scaleCount) {
        if (scaleCount == 1) {
            return ONLY;
        }

        if (row == 0) {
            return BOTTOM;
        } else if (row == scaleCount - 1) {
            return TOP;
        }

        return MIDDLE;
    }

    private void buildCells() {
        List<Resolution<?>> resolutions = new ArrayList<>();

        if (getDateline().getWidth() > 0 && getDateline().getHeight() > 0) {
            Dateline dateline = getDateline();
            DatelineModel<?> model = dateline.getModel();
            Timeline timeline = dateline.getTimeline();
            TimelineModel<?> timelineModel = timeline.getModel();
            TemporalUnit temporalUnit = timelineModel.getSmallestTemporalUnit();

            for (int i = 0; i < model.getScaleCount(); i++) {

                DatelineScale scale = rowScaleMap.get(i);

                TemporalUnit nextUnit = scale.buildScale(temporalUnit);

                if (nextUnit == null) {
                    break;
                }

                temporalUnit = nextUnit;

                resolutions.add(scale.getResolution());
            }

            getDateline().getProperties().put("com.flexganttfx.primaryUnit", getPrimaryTemporalUnit());
        }

        getSkinnable().getScaleResolutions().setAll(resolutions);
    }

    private TemporalUnit getPrimaryTemporalUnit() {
        Resolution<?> resolution = rowScaleMap.get(0).getResolution();
        if (resolution != null) {
            return resolution.getTemporalUnit();
        }

        return null;
    }

    private void updateLasso() {
        Dateline dateline = getSkinnable();
        TimeInterval selectedTimeInterval = dateline.getSelectedTimeInterval();

        if (selectedTimeInterval != null) {
            Instant st = selectedTimeInterval.getStartTime();
            Instant et = selectedTimeInterval.getEndTime();

            TimelineModel<?> timelineModel = dateline.getTimeline().getModel();

            double x1 = timelineModel.calculateLocationForTime(st) + dateline.getDatelineBuffer() - dateline.getTranslateX();
            double x2 = timelineModel.calculateLocationForTime(et) + dateline.getDatelineBuffer() - dateline.getTranslateX();

            Insets insets = dateline.getInsets();

            lasso.setLayoutX(Math.min(x1, x2));
            lasso.setLayoutY(insets.getTop());
            lasso.setPrefWidth(Math.abs(x2 - x1));
            lasso.setPrefHeight(dateline.getHeight() - insets.getTop() - insets.getBottom());
            lasso.setVisible(true);
        } else {
            lasso.setVisible(false);
        }

        dateline.requestLayout();
    }

    @Override
    protected void layoutChildren(double x, double y, double width, double height) {
        if (!initialSetup) {
            initialSetup = true;
            buildCells();
        }

        super.layoutChildren(x, y, width, height);

        lasso.resizeRelocate(lasso.getLayoutX(), lasso.getLayoutY(), lasso.getPrefWidth(), lasso.getPrefHeight());
    }
}
