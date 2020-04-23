/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin;

import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.graphics.ListViewGraphics;
import com.flexganttfx.view.timeline.Timeline;
import com.flexganttfx.view.util.Position;
import com.flexganttfx.view.util.TimelineScrollBar;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.MasterDetailPane;

public abstract class GanttChartBaseSkin<R extends Row<?, ?, ?>, C extends GanttChartBase<R>> extends
        SkinBase<C> {

    private final BorderPane timelineGraphicsPane;
    private Timeline timeline;
    private final MasterDetailPane graphicsMasterDetailPane;
    private final TimelineScrollBar timelineScrollBar;
    private final Node detailNode;
    private final HiddenSidesPane hiddenSidesPane;

    public GanttChartBaseSkin(C control) {
        super(control);

        ListViewGraphics<R> graphics = control.getGraphics();

        timeline = control.getTimeline();
        timelineScrollBar = control.getTimelineScrollBar();
        detailNode = control.getDetail();

        control.setMinSize(0, 0);

        timelineGraphicsPane = new BorderPane();
        timelineGraphicsPane.getStyleClass().add("timeline-graphics");
        timelineGraphicsPane.setTop(timeline);
        timelineGraphicsPane.setCenter(graphics);

        hiddenSidesPane = new HiddenSidesPane();
        hiddenSidesPane.setContent(timelineGraphicsPane);
        hiddenSidesPane.setBottom(timelineScrollBar);

        graphicsMasterDetailPane = control.getGraphicsMasterDetailPane();
        graphicsMasterDetailPane.setMasterNode(hiddenSidesPane);

        control.detailProperty().addListener(it -> configureDetailNode());
        configureDetailNode();

        getChildren().add(graphicsMasterDetailPane);

        control.positionProperty().addListener(it -> updatePosition());
        control.graphicsHeaderProperty().addListener(it -> updatePosition());

        updatePosition();
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

    public HiddenSidesPane getHiddenSidesPane() {
        return hiddenSidesPane;
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
                getTimelineScrollBar().toBack();
                break;
            case LAST:
            case MIDDLE:
                getTimelineGraphicsPane().setTop(getSkinnable().getGraphicsHeader());
                getTimelineScrollBar().toFront();
                break;
            default:
                break;
        }
    }}
