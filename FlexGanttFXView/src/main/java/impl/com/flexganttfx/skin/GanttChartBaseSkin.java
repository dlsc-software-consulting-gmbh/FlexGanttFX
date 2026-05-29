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
    private final MasterDetailPane graphicsMasterDetailPane;
    private final TimelineScrollBar timelineScrollBar;
    private final ScrollBar horizonScrollBar;
    private final HiddenSidesPane rightHandSideHiddenSidesPane;
    private final VBox rightHandSideBox;

    public GanttChartBaseSkin(C control) {
        super(control);

        ListViewGraphics<R> graphics = control.getGraphics();

        Timeline timeline = control.getTimeline();
        timelineScrollBar = control.getTimelineScrollBar();
        horizonScrollBar = control.getHorizonScrollBar();

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
            graphicsMasterDetailPane.setMasterNode(new Label(""));
            graphicsMasterDetailPane.setMasterNode(rightHandSideHiddenSidesPane);

            rightHandSideHiddenSidesPane.setContent(new Label());
            rightHandSideHiddenSidesPane.setContent(timelineGraphicsPane);

            rightHandSideHiddenSidesPane.setBottom(new Label());

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
            horizonScrollBar.setManaged(true);
            horizonScrollBar.setVisible(true);
            timelineScrollBar.setManaged(true);
            timelineScrollBar.setVisible(true);

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
            graphicsMasterDetailPane.setDetailNode(detail);
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
