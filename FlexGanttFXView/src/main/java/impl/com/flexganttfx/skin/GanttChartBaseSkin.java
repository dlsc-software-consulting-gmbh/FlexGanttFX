/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 * <p>
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin;

import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.GanttChartBase.ScrollBarType;
import com.flexganttfx.view.graphics.ListViewGraphics;
import com.flexganttfx.view.timeline.Timeline;
import com.flexganttfx.view.util.Position;
import com.flexganttfx.view.util.TimelineScrollBar;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SkinBase;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.MasterDetailPane;

public abstract class GanttChartBaseSkin<R extends Row<?, ?, ?>, C extends GanttChartBase<R>> extends SkinBase<C> {

    private final BorderPane timelineGraphicsPane;
    private final Timeline timeline;
    private final MasterDetailPane graphicsMasterDetailPane;
    private final TimelineScrollBar timelineScrollBar;
    private final ScrollBar horizonScrollBar;
    private final Node detailNode;
    private final HiddenSidesPane rightHandSideHiddenSidesPane;
    private final VBox rightHandSideBox;

    public GanttChartBaseSkin(C control) {
        super(control);

        ListViewGraphics<R> graphics = control.getGraphics();

        timeline = control.getTimeline();
        timelineScrollBar = control.getTimelineScrollBar();
        horizonScrollBar = control.getHorizonScrollBar();
        detailNode = control.getDetail();

        control.setMinSize(0, 0);

        timelineGraphicsPane = new BorderPane();
        timelineGraphicsPane.getStyleClass().add("timeline-graphics");
        timelineGraphicsPane.setTop(timeline);
        timelineGraphicsPane.setCenter(graphics);

        rightHandSideHiddenSidesPane = new HiddenSidesPane();
        rightHandSideBox = new VBox();
        graphicsMasterDetailPane = getSkinnable().getGraphicsMasterDetailPane();

        configureMasterNode();
        control.scrollBarTypeProperty().addListener(it -> configureMasterNode());
        control.autoHideScrollBarProperty().addListener(it -> configureMasterNode());

        configureDetailNode();
        control.detailProperty().addListener(it -> configureDetailNode());

        getChildren().add(graphicsMasterDetailPane);

        control.positionProperty().addListener(it -> updatePosition());
        control.graphicsHeaderProperty().addListener(it -> updatePosition());

        updatePosition();
    }

    protected void configureMasterNode() {
        if (getSkinnable().isAutoHideScrollBar()) {
            rightHandSideHiddenSidesPane.setContent(timelineGraphicsPane);
            graphicsMasterDetailPane.setMasterNode(rightHandSideHiddenSidesPane);

            switch (getSkinnable().getScrollBarType()) {
                case NONE:
                    rightHandSideHiddenSidesPane.setBottom(null);
                    break;
                case FIXED_HORIZON:
                    rightHandSideHiddenSidesPane.setBottom(horizonScrollBar);
                    break;
                case INFINITE:
                    rightHandSideHiddenSidesPane.setBottom(timelineScrollBar);
                    break;
            }

        } else {
            VBox.setVgrow(timelineGraphicsPane, Priority.ALWAYS);
            graphicsMasterDetailPane.setMasterNode(rightHandSideBox);

            switch (getSkinnable().getScrollBarType()) {
                case NONE:
                    rightHandSideBox.getChildren().setAll(timelineGraphicsPane);
                    break;
                case FIXED_HORIZON:
                    rightHandSideBox.getChildren().setAll(timelineGraphicsPane, horizonScrollBar);
                    break;
                case INFINITE:
                    rightHandSideBox.getChildren().setAll(timelineGraphicsPane, timelineScrollBar);
                    break;
            }
        }
    }

    protected void configureDetailNode() {
        Node detail = getSkinnable().getDetail();
        if (detail != null) {
            SplitPane.setResizableWithParent(detail, false);
            graphicsMasterDetailPane.setDetailNode(detailNode);
        } else {
            graphicsMasterDetailPane.setDetailNode(new Label());
        }
    }

    /**
     * Returns the {@link HiddenSidesPane} instance that will be used if
     * the scroll bar type is equal to {@link ScrollBarType#INFINITE}. In this
     * case the hidden sides pane will hide the {@link TimelineScrollBar} in its
     * bottom position.
     *
     * @see GanttChartBase#scrollBarTypeProperty()
     * @see GanttChartBase#getTimelineScrollBar()
     *
     * @return the hidden sides pane
     * @since 11.12.3
     */
    public HiddenSidesPane getRightHandSideHiddenSidesPane() {
        return rightHandSideHiddenSidesPane;
    }

    /**
     * Returns the {@link VBox} instance that will be used if
     * the scroll bar type is equal to {@link ScrollBarType#FIXED_HORIZON}. In this
     * case a standard scrollbar will be added at the bottom of the box.
     *
     * @see GanttChartBase#scrollBarTypeProperty()
     * @see GanttChartBase#getHorizonScrollBar()
     *
     * @return the hidden sides pane
     * @since 11.12.3
     */
    public VBox getRightHandSideBox() {
        return rightHandSideBox;
    }

    protected final BorderPane getTimelineGraphicsPane() {
        return timelineGraphicsPane;
    }

    protected final TimelineScrollBar getTimelineScrollBar() {
        return timelineScrollBar;
    }

    private void updatePosition() {
        Position pos = getSkinnable().getPosition();
        switch (pos) {
            case FIRST:
            case ONLY:
                getTimelineGraphicsPane().setTop(getSkinnable().getTimeline());
                break;
            case LAST:
            case MIDDLE:
                getTimelineGraphicsPane().setTop(getSkinnable().getGraphicsHeader());
                break;
            default:
                break;
        }
    }
}
