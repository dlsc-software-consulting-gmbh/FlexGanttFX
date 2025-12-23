/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
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
import com.flexganttfx.view.graphics.ActivityEvent;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SkinBase;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.Iterator;

public class RadarViewSkin<R extends Row<?, ?, ?>> extends SkinBase<RadarView<R>> {

    private final Canvas activitiesCanvas;
    private final Canvas visibleTimeIntervalCanvas;

    private final InvalidationListener redrawTimeIntervalListener = evt -> drawVisibleTimeInterval();
    private final InvalidationListener weakRedrawTimeIntervalListener = new WeakInvalidationListener(redrawTimeIntervalListener);

    private Rectangle2D visibleBounds;

    public RadarViewSkin(RadarView<R> view) {
        super(view);

        StackPane stackPane = new StackPane();
        stackPane.getStyleClass().add("radar");

        activitiesCanvas = new Canvas();
        activitiesCanvas.widthProperty().bind(view.radarWidthProperty());
        activitiesCanvas.heightProperty().bind(view.radarHeightProperty());
        stackPane.getChildren().add(activitiesCanvas);

        visibleTimeIntervalCanvas = new Canvas();
        visibleTimeIntervalCanvas.widthProperty().bind(view.radarWidthProperty());
        visibleTimeIntervalCanvas.heightProperty().bind(view.radarHeightProperty());
        stackPane.getChildren().add(visibleTimeIntervalCanvas);

        getChildren().add(stackPane);

        GraphicsBase<?> graphics = view.getGraphics();
        final EventHandler<InputEvent> activityEventHandler = evt -> drawActivities();

        if (graphics != null) {
            graphics.getTimeline().visibleTimeIntervalProperty().addListener(weakRedrawTimeIntervalListener);
            graphics.getRows().addListener(weakRedrawTimeIntervalListener);
            graphics.addEventHandler(ActivityEvent.ANY, activityEventHandler);
        }

        view.graphicsProperty().addListener((observable, oldGraphics, newGraphics) -> {

            if (oldGraphics != null) {
                graphics.getTimeline().visibleTimeIntervalProperty().removeListener(weakRedrawTimeIntervalListener);
                oldGraphics.getRows().removeListener(weakRedrawTimeIntervalListener);
                oldGraphics.removeEventHandler(ActivityEvent.ANY, activityEventHandler);
            }

            if (newGraphics != null) {
                graphics.getTimeline().visibleTimeIntervalProperty().addListener(weakRedrawTimeIntervalListener);
                newGraphics.getRows().addListener(weakRedrawTimeIntervalListener);
                newGraphics.addEventHandler(ActivityEvent.ANY, activityEventHandler);
            }
        });

        drawAll();

        visibleTimeIntervalCanvas.setOnMousePressed(this::mousePressed);
        visibleTimeIntervalCanvas.setOnMouseDragged(this::mouseDragged);
        visibleTimeIntervalCanvas.setOnMouseClicked(this::mouseClicked);
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
        final double x = Math.min(activitiesCanvas.getWidth() - visibleBounds.getWidth(), Math.max(0, location));
        Instant time = calculateTimeAt(x);

        Timeline timeline = getSkinnable().getGraphics().getTimeline();
        TimelineModel<?> timelineModel = timeline.getModel();
        timelineModel.setStartTime(time);
    }

    private void drawAll() {
        drawActivities();
        drawVisibleTimeInterval();
    }

    private void drawActivities() {
        GraphicsContext gc = activitiesCanvas.getGraphicsContext2D();

        double width = activitiesCanvas.getWidth();
        double height = activitiesCanvas.getHeight();

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
        }
    }

    private void drawVisibleTimeInterval() {
        GraphicsContext gc = visibleTimeIntervalCanvas.getGraphicsContext2D();

        double width = visibleTimeIntervalCanvas.getWidth();
        double height = visibleTimeIntervalCanvas.getHeight();

        gc.clearRect(0, 0, width, height);

        GraphicsBase<R> graphics = getSkinnable().getGraphics();

        if (graphics != null) {
            Instant earliestTimeUsed = graphics.getEarliestTimeUsed();
            Instant latestTimeUsed = graphics.getLatestTimeUsed();

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

                gc.setFill(getVisibleTimeIntervalColor());
                gc.fillRect(x1, 0, x2 - x1, height);

                visibleBounds = new Rectangle2D(x1, 0, Math.max(0, x2 - x1), Math.max(0, height));
            } else {
                visibleBounds = null;
            }
        }
    }

    private final ObjectProperty<Paint> visibleTimeIntervalColor = new SimpleObjectProperty<>(this, "visibleTimeIntervalColor", Color.GREEN.deriveColor(0, 1, 1, .3));

    /**
     * The color used to fill the rectangle that represents the currently visible time interval.
     *
     * @return the color used for the visible time interval
     */
    public final ObjectProperty<Paint> visibleTimeIntervalColorProperty() {
        return visibleTimeIntervalColor;
    }

    public final Paint getVisibleTimeIntervalColor() {
        return visibleTimeIntervalColor.get();
    }

    public final void setVisibleTimeIntervalColor(Paint visibleTimeIntervalColor) {
        this.visibleTimeIntervalColor.set(visibleTimeIntervalColor);
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

        double mpp = (latestTimeUsed.toEpochMilli() - earliestTimeUsed.toEpochMilli()) / activitiesCanvas.getWidth();
        long millis = (long) (mpp * x);
        return Instant.ofEpochMilli(earliestTimeUsed.toEpochMilli() + millis);
    }
}
