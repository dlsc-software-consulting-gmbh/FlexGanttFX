/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 * <p>
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.timeline;

import com.flexganttfx.model.dateline.ChronoUnitResolution;
import com.flexganttfx.model.dateline.DatelineModel;
import com.flexganttfx.model.dateline.Resolution;
import com.flexganttfx.model.dateline.Resolution.Position;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.model.util.TimeInterval;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.DatelineCell;
import com.flexganttfx.view.timeline.Timeline;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.util.Callback;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static javafx.scene.control.SelectionMode.SINGLE;
import static javafx.scene.input.MouseButton.PRIMARY;

final class DatelineScale extends Region {

    private static final String DEFAULT_STYLE_CLASS = "scale";

    private static final String SELECTED_STYLE_CLASS = "dateline-cell-selected";

    private Resolution<?> resolution;

    private final Position position;

    private final Dateline dateline;

    DatelineScale(Dateline dateline, Position position) {
        getStyleClass().add(DEFAULT_STYLE_CLASS);

        this.position = position;
        this.dateline = dateline;

        switch (position) {
            case ONLY:
                getStyleClass().add("scale-only");
                break;
            case BOTTOM:
                getStyleClass().add("scale-bottom");
                break;
            case TOP:
                getStyleClass().add("scale-top");
                break;
            case MIDDLE:
                getStyleClass().add("scale-middle");
                break;
        }

        EventHandler<MouseEvent> clickSelectHandler = evt -> {
            if (evt.getButton().equals(PRIMARY) && evt.getClickCount() == 1) {

                if (evt.isShortcutDown()) {
                    DatelineCell<?> cell = getCellAt(evt.getX());

                    if (cell != null) {
                        TimeInterval interval = cell.getInterval();

                        if (evt.getClickCount() == 1) {
                            if (dateline.getSelectionMode().equals(SINGLE)) {
                                dateline.getSelectedIntervals().clear();
                            }

                            if (cell.getStyleClass()
                                    .contains(SELECTED_STYLE_CLASS)) {
                                cell.getStyleClass()
                                        .remove(SELECTED_STYLE_CLASS);
                                dateline.getSelectedIntervals()
                                        .remove(interval);
                            } else {
                                cell.getStyleClass().add(SELECTED_STYLE_CLASS);
                                dateline.getSelectedIntervals().add(interval);
                            }
                        }

                        evt.consume();
                    }
                } else {
                    dateline.getSelectedIntervals().clear();
                }
            }
        };

        addEventHandler(MouseEvent.MOUSE_CLICKED, clickSelectHandler);
        EventHandler<MouseEvent> clickZoomHandler = evt -> {
            if (evt.getButton().equals(PRIMARY) && evt.getClickCount() == 2) {
                DatelineCell<?> cell = getCellAt(evt.getX());
                if (cell != null) {
                    TimeInterval interval = cell.getInterval();
                    dateline.getTimeline().showRange(interval);
                }
            }
        };

        addEventHandler(MouseEvent.MOUSE_CLICKED, clickZoomHandler);
        EventHandler<MouseEvent> updateFocusedTimeIntervalHandler = evt -> {
            DatelineCell<?> cell = getCellAt(evt.getX());
            if (cell != null) {
                TimeInterval interval = cell.getInterval();
                dateline.getProperties().put("com.flexganttfx.dateline.hover.interval", interval);
            } else {
                dateline.getProperties().put("com.flexganttfx.dateline.hover.interval", null);
            }
        };

        addEventHandler(MouseEvent.MOUSE_MOVED, updateFocusedTimeIntervalHandler);
        addEventHandler(MouseEvent.MOUSE_ENTERED, updateFocusedTimeIntervalHandler);

        EventHandler<MouseEvent> clearFocusedTimeIntervalHandler = evt -> dateline.getProperties().put("com.flexganttfx.dateline.hover.interval", null);
        addEventHandler(MouseEvent.MOUSE_EXITED, clearFocusedTimeIntervalHandler);

        heightProperty().addListener(it -> {
            if (resolution != null) {
                buildCells(resolution, true);
            }
        });
    }

    final Position getPosition() {
        return position;
    }

    final void setResolution(Resolution<?> resolution) {
        this.resolution = resolution;
    }

    final Resolution<? extends TemporalUnit> getResolution() {
        return resolution;
    }

    // Cell padding support
    private StyleableDoubleProperty cellPadding;

    private StyleableDoubleProperty cellPaddingProperty() {
        if (cellPadding == null) {
            cellPadding = new StyleableDoubleProperty() {

                @Override
                public CssMetaData<DatelineScale, Number> getCssMetaData() {
                    return StyleableProperties.CELL_PADDING;
                }

                @Override
                public Object getBean() {
                    return DatelineScale.this;
                }

                @Override
                public String getName() {
                    return "cellPadding";
                }
            };
        }
        return cellPadding;
    }

    public double getCellPadding() {
        return cellPaddingProperty().get();
    }


    private static class StyleableProperties {

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        private static final CssMetaData<DatelineScale, Number> CELL_PADDING = new CssMetaData<DatelineScale, Number>(
                "-fx-cell-padding", SizeConverter.getInstance(), 5) {

            @Override
            public Double getInitialValue(DatelineScale node) {
                return node.getCellPadding();
            }

            @Override
            public boolean isSettable(DatelineScale n) {
                return n.cellPadding == null || !n.cellPadding.isBound();
            }

            @Override
            public StyleableProperty<Number> getStyleableProperty(
                    DatelineScale n) {
                return n.cellPaddingProperty();
            }
        };

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(
                    Region.getClassCssMetaData());
            styleables.add(CELL_PADDING);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    @Override
    public final List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    private DatelineCell<?> getCellAt(double x) {
        for (Node node : getChildren()) {
            if (node.getLayoutX() <= x && (node.getLayoutX() + node.prefWidth(-1) > x)) {
                return (DatelineCell<?>) node;
            }
        }

        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    final TemporalUnit buildScale(TemporalUnit unit) {

        DatelineModel datelineModel = dateline.getModel();

        boolean success = false;

        do {
            if (getResolution() != null) {
                success = buildCells(getResolution(), true);
            } else {

                getChildren().clear();

                Iterator<Resolution> resolutions = datelineModel.getResolutions(unit);

                while (resolutions.hasNext()) {
                    Resolution resolution = resolutions.next();

                    if (resolution.isSupportingPosition(getPosition())) {
                        success = buildCells(resolution, false);
                    }

                    if (success) {
                        break;
                    }
                }

                if (!success) {
                    TemporalUnit nextUnit = datelineModel.nextTemporalUnit(unit);
                    if (nextUnit == null) {
                        return null;
                    }

                    unit = nextUnit;
                }
            }
        } while (!success);

        return datelineModel.nextTemporalUnit(unit);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private boolean buildCells(final Resolution<? extends TemporalUnit> resolution, boolean cached) {
        getChildren().forEach(node -> {
            node.setVisible(false);
            node.setManaged(false);
        });

        Timeline timeline = dateline.getTimeline();
        TimelineModel timelineModel = timeline.getModel();

        TemporalUnit temporalUnit = resolution.getTemporalUnit();

        ObservableList<TimeInterval> selections = dateline.getSelectedIntervals();

        ZoneId zoneId = dateline.getZoneId();

        double scaleTopInsets = getInsets().getTop();
        double scaleHeight = getHeight() - scaleTopInsets - getInsets().getBottom();

        boolean success = true;

        Instant st = resolution.decrement(timelineModel.calculateTimeForLocation(-dateline.getDatelineBuffer() + dateline.getTranslateX()), zoneId);
        st = resolution.decrement(st, zoneId);

        DayOfWeek firstDayOfWeek = dateline.getFirstDayOfWeek();
        Instant startTime = resolution.truncate(st, zoneId, firstDayOfWeek);

        double x1 = timelineModel.calculateLocationForTime(startTime) + dateline.getDatelineBuffer() - dateline.getTranslateX();
        int index = 0;

        while (x1 < dateline.getWidth()) {
            Instant endTime = resolution.increment(startTime, zoneId);

            int dstCorrectionInHours = 0;

            if (resolution.getTemporalUnit().equals(ChronoUnit.HOURS)) {
                if (resolution instanceof ChronoUnitResolution) {
                    ChronoUnitResolution chronoUnitResolution = (ChronoUnitResolution) resolution;
                    if (chronoUnitResolution.isDSTEndIncrement()) {
                        dstCorrectionInHours = 1;
                    } else if (chronoUnitResolution.isDSTStartIncrement()) {
                        dstCorrectionInHours = -1;
                        endTime = resolution.increment(endTime, zoneId);
                    }
                }
            }

            if (dstCorrectionInHours > 0) {
                endTime = endTime.plus(1, ChronoUnit.HOURS);
            } else if (dstCorrectionInHours < 0) {
                endTime = endTime.minus(1, ChronoUnit.HOURS);
            }

            x1 = timelineModel.calculateLocationForTime(startTime) + dateline.getDatelineBuffer() - dateline.getTranslateX();

            if (x1 < dateline.getWidth()) {

                DatelineCell cell = getOrCreateDatelineCell(temporalUnit, index++);

                cell.getStyleClass().removeAll("dst-end", "dst-start", "dateline-cell-first", "dateline-cell-last", SELECTED_STYLE_CLASS);
                cell.update(startTime, endTime, resolution, dateline, getPosition());

                double x2 = timelineModel.calculateLocationForTime(endTime) + dateline.getDatelineBuffer() - dateline.getTranslateX();

                double padding = getCellPadding();

                if (dstCorrectionInHours > 0) {
                    cell.getStyleClass().add("dst-end");
                } else if (dstCorrectionInHours < 0) {
                    cell.getStyleClass().add("dst-start");
                }

                if (!selections.isEmpty() && selections.contains(cell.getInterval())) {
                    cell.getStyleClass().add(SELECTED_STYLE_CLASS);
                }

                cell.applyCss();

                if (cached || x1 + cell.prefWidth(scaleHeight) + 2 * padding <= x2) {

                    double cellWidth = x2 - x1;

                    cell.resizeRelocate(x1, scaleTopInsets, cellWidth, scaleHeight);
                    cell.setPrefSize(cellWidth, scaleHeight);
                    cell.setVisible(true);
                    cell.setManaged(true);

                } else {
                    success = false;
                    break;
                }

                startTime = resolution.increment(startTime, zoneId);

                if (dstCorrectionInHours > 0) {
                    startTime = startTime.plus(dstCorrectionInHours, ChronoUnit.HOURS);
                } else if (dstCorrectionInHours < 0) {
                    startTime = endTime;
                }
            }
        }

        if (success) {
            setResolution(resolution);
        }

        return success;
    }

    private DatelineCell getOrCreateDatelineCell(TemporalUnit temporalUnit, int index) {
        if (index < getChildren().size()) {
            return (DatelineCell) getChildren().get(index);
        }

        Callback<TemporalUnit, DatelineCell> cellFactory = dateline.getCellFactory(temporalUnit.getClass());
        DatelineCell cell = cellFactory.call(temporalUnit);
        cell.setManaged(false);
        getChildren().add(cell);

        return cell;
    }
}
