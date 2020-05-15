/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 * <p>
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.extras.skin;

import com.flexganttfx.extras.RadarView;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityRepository;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.model.util.TimeInterval;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.Iterator;

public class RadarViewSkin<R extends Row<?, ?, ?>> extends SkinBase<RadarView<R>> {

    private final Canvas canvas;
    private final InvalidationListener redrawListener = evt -> drawRadar();
    private final InvalidationListener weakRedrawListener = new WeakInvalidationListener(redrawListener);

    private Rectangle2D visibleBounds;

    public RadarViewSkin(RadarView<R> view) {
        super(view);

        StackPane stackPane = new StackPane();
        stackPane.getStyleClass().add("radar");

        canvas = new Canvas();
        canvas.widthProperty().bind(view.radarWidthProperty());
        canvas.heightProperty().bind(view.radarHeightProperty());
        stackPane.getChildren().add(canvas);

        getChildren().add(stackPane);

        GraphicsBase<?> graphics = view.getGraphics();
        if (graphics != null) {
            graphics.getTimeline().visibleTimeIntervalProperty().addListener(weakRedrawListener);
            graphics.getRows().addListener(weakRedrawListener);
        }

        view.graphicsProperty().addListener((observable, oldGraphics, newGraphics) -> {

            if (oldGraphics != null) {
                graphics.getTimeline().visibleTimeIntervalProperty().removeListener(weakRedrawListener);
                oldGraphics.getRows().removeListener(weakRedrawListener);
            }

            if (newGraphics != null) {
                graphics.getTimeline().visibleTimeIntervalProperty().addListener(weakRedrawListener);
                newGraphics.getRows().addListener(weakRedrawListener);
            }
        });

        drawRadar();

        canvas.setOnMousePressed(this::mousePressed);
        canvas.setOnMouseDragged(this::mouseDragged);
        canvas.setOnMouseClicked(this::mouseClicked);
    }

    private void mousePressed(MouseEvent e) {
    }

    private void mouseDragged(MouseEvent e) {
        showLocation(e.getX() - visibleBounds.getWidth() / 2);
    }

    private void mouseClicked(MouseEvent e) {
        showLocation(e.getX() - visibleBounds.getWidth() / 2);
    }

    private void showLocation(double location) {
        final double x = Math.min(canvas.getWidth() - visibleBounds.getWidth(), Math.max(0, location));
        Instant time = calculateTimeAt(x);

        Timeline timeline = getSkinnable().getGraphics().getTimeline();
        TimelineModel<?> timelineModel = timeline.getModel();
        timelineModel.setStartTime(time);
    }

    private void drawRadar() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        gc.clearRect(0, 0, width, height);

        GraphicsBase<R> graphics = getSkinnable().getGraphics();

        if (graphics != null) {
            Dateline dateline = graphics.getTimeline().getDateline();
            TemporalUnit temporalUnit = dateline.getPrimaryTemporalUnit();
            ZoneId zoneId = dateline.getZoneId();

            gc.setStroke(Color.RED);
            gc.setLineWidth(.5);

            Instant earliestTimeUsed = graphics.getEarliestTimeUsed();
            Instant latestTimeUsed = graphics.getLatestTimeUsed();

            ObservableList<?> rows = graphics.getRows();
            int numberOfRows = rows.size();

            for (int i = 0; i < numberOfRows; i++) {

                Row<?, ?, ?> row = (Row<?, ?, ?>) rows.get(i);
                ActivityRepository<?> repository = row.getRepository();
                Instant earliestTimeUsedInRow = repository.getEarliestTimeUsed();
                Instant latestTimeUsedInRow = repository.getLatestTimeUsed();

                if (earliestTimeUsedInRow != null && latestTimeUsedInRow != null) {
                    for (Layer layer : graphics.getLayers()) {
                        Iterator<?> activities = repository.getActivities(layer, earliestTimeUsedInRow, latestTimeUsedInRow, temporalUnit, zoneId);

                        while (activities.hasNext()) {
                            Activity activity = (Activity) activities.next();

                            double x1 = calculateX(activity.getStartTime(), width, earliestTimeUsed, latestTimeUsed);
                            double x2 = calculateX(activity.getEndTime(), width, earliestTimeUsed, latestTimeUsed);
                            double y = calculateY(i, numberOfRows, height);

                            gc.strokeLine(x1, y, x2, y);
                        }

                    }
                }
            }

            Timeline timeline = graphics.getTimeline();
            TimeInterval visibleTimeInterval = timeline.getVisibleTimeInterval();

            Instant visibleStartTime = visibleTimeInterval.getStartTime();
            Instant visibleEndTime = visibleTimeInterval.getEndTime();

            if (earliestTimeUsed != null && latestTimeUsed != null) {

                if (visibleStartTime.isBefore(earliestTimeUsed)) {
                    visibleStartTime = earliestTimeUsed;
                }

                if (visibleEndTime.isAfter(latestTimeUsed)) {
                    visibleEndTime = latestTimeUsed;
                }

                double x1 = calculateX(visibleStartTime, width, earliestTimeUsed, latestTimeUsed);
                double x2 = calculateX(visibleEndTime, width, earliestTimeUsed, latestTimeUsed);

                gc.setFill(Color.GREEN.deriveColor(0, 1, 1, .3));
                gc.fillRect(x1, 0, x2 - x1, height);

                visibleBounds = new Rectangle2D(x1, 0, Math.max(0, x2 - x1), Math.max(0, height));
            } else {
                visibleBounds = null;
            }
        }
    }

    private double calculateY(int rowIndex, int totalNumberOfRows, double canvasHeight) {
        return ((int) ((canvasHeight / totalNumberOfRows) * rowIndex)) + .5;
    }

    private double calculateX(Instant time, double width, Instant earliestTimeUsed, Instant latestTimeUsed) {
        double mpp = (double) (latestTimeUsed.toEpochMilli() - earliestTimeUsed.toEpochMilli()) / width;
        return ((double) (time.toEpochMilli() - earliestTimeUsed.toEpochMilli())) / mpp;
    }

    private Instant calculateTimeAt(double x) {
        GraphicsBase<R> graphics = getSkinnable().getGraphics();
        Instant earliestTimeUsed = graphics.getEarliestTimeUsed();
        Instant latestTimeUsed = graphics.getLatestTimeUsed();

        double mpp = (latestTimeUsed.toEpochMilli() - earliestTimeUsed.toEpochMilli()) / canvas.getWidth();
        long millis = (long) (mpp * x);
        return Instant.ofEpochMilli(earliestTimeUsed.toEpochMilli() + millis);
    }
}
