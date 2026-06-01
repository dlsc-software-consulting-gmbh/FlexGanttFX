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
package impl.com.flexganttfx.skin.container;

import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.container.QuadGanttChartContainerBase;
import com.flexganttfx.view.util.Position;
import impl.com.flexganttfx.skin.util.Binder;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.MasterDetailPane;

/**
 * Skin for a container that shows four Gantt charts. It arranges the charts in upper and
 * lower master-detail panes and synchronizes their split positions and shared state.
 */
public class QuadGanttChartContainerSkin extends ContainerSkinBase<QuadGanttChartContainerBase> {

    private enum Corner {
        UPPER_LEFT,
        UPPER_RIGHT,
        LOWER_LEFT,
        LOWER_RIGHT
    }

    public QuadGanttChartContainerSkin(QuadGanttChartContainerBase container) {
        super(container);

        MasterDetailPane upperPane = container.getUpperMasterDetailPane();
        upperPane.setMasterNode(new Wrapper(Corner.UPPER_LEFT));
        upperPane.setDetailNode(new Wrapper(Corner.UPPER_RIGHT));

        MasterDetailPane lowerPane = container.getLowerMasterDetailPane();
        lowerPane.setMasterNode(new Wrapper(Corner.LOWER_LEFT));
        lowerPane.setDetailNode(new Wrapper(Corner.LOWER_RIGHT));

        Bindings.bindBidirectional(upperPane.showDetailNodeProperty(), lowerPane.showDetailNodeProperty());
        Bindings.bindBidirectional(upperPane.dividerPositionProperty(), lowerPane.dividerPositionProperty());

        MasterDetailPane masterDetailPane = container.getUpperLowerMasterDetailPane();
        masterDetailPane.setMasterNode(upperPane);
        masterDetailPane.setDetailNode(lowerPane);
        masterDetailPane.setShowDetailNode(container.isShowLower());

        Bindings.bindBidirectional(container.showLowerProperty(), masterDetailPane.showDetailNodeProperty());

        getChildren().add(masterDetailPane);

        InvalidationListener updateListener = it -> updatePositions();

        container.upperLeftGanttChartProperty().addListener(updateListener);
        container.upperRightGanttChartProperty().addListener(updateListener);
        container.lowerLeftGanttChartProperty().addListener(updateListener);
        container.lowerRightGanttChartProperty().addListener(updateListener);

        updatePositions();
    }

    private void updatePositions() {
        GanttChartBase<?> upperLeft = getSkinnable().getUpperLeftGanttChart();
        GanttChartBase<?> upperRight = getSkinnable().getUpperRightGanttChart();
        GanttChartBase<?> lowerLeft = getSkinnable().getLowerLeftGanttChart();
        GanttChartBase<?> lowerRight = getSkinnable().getLowerRightGanttChart();

        clearStyles(upperLeft);
        clearStyles(upperRight);
        clearStyles(lowerLeft);
        clearStyles(lowerRight);

        upperLeft.setPosition(Position.FIRST);
        upperRight.setPosition(Position.FIRST);
        lowerLeft.setPosition(Position.LAST);
        lowerRight.setPosition(Position.LAST);

        if (upperLeft instanceof GanttChart) {
            ((GanttChart) upperLeft).getTreeTable().getStyleClass().add(GANTT_TREE_TABLE_VIEW_FIRST);
        }

        if (upperRight instanceof GanttChart) {
            ((GanttChart) upperRight).getTreeTable().getStyleClass().add(GANTT_TREE_TABLE_VIEW_FIRST);
        }

        if (lowerLeft instanceof GanttChart) {
            ((GanttChart) lowerLeft).getTreeTable().getStyleClass().add(GANTT_TREE_TABLE_VIEW_LAST);
        }

        if (lowerRight instanceof GanttChart) {
            ((GanttChart) lowerRight).getTreeTable().getStyleClass().add(GANTT_TREE_TABLE_VIEW_LAST);
        }

        upperLeft.getTimeline().getStyleClass().add(TIMELINE_FIRST);
        upperRight.getTimeline().getStyleClass().add(TIMELINE_FIRST);
        lowerLeft.getTimeline().getStyleClass().add(TIMELINE_LAST);
        lowerRight.getTimeline().getStyleClass().add(TIMELINE_LAST);

        lowerLeft.setMasterTimeline(upperLeft.getTimeline());
        lowerRight.setMasterTimeline(upperRight.getTimeline());

        Binder.bind(upperLeft, lowerLeft, true);
        Binder.bind(upperRight, lowerRight, true);
    }

    private void clearStyles(GanttChartBase<?> ganttChart) {
        if (ganttChart instanceof GanttChart) {
            ((GanttChart) ganttChart).getTreeTable().getStyleClass().remove(GANTT_TREE_TABLE_VIEW_FIRST);
            ((GanttChart) ganttChart).getTreeTable().getStyleClass().remove(GANTT_TREE_TABLE_VIEW_MIDDLE);
            ((GanttChart) ganttChart).getTreeTable().getStyleClass().remove(GANTT_TREE_TABLE_VIEW_LAST);
        }

        ganttChart.getTimeline().getStyleClass().remove(TIMELINE_FIRST);
        ganttChart.getTimeline().getStyleClass().remove(TIMELINE_MIDDLE);
        ganttChart.getTimeline().getStyleClass().remove(TIMELINE_LAST);
    }

    private class Wrapper extends BorderPane {

        public Wrapper(Corner corner) {
            switch (corner) {
                case UPPER_LEFT:
                    topProperty().bind(getSkinnable().upperLeftHeaderProperty());
                    centerProperty().bind(getSkinnable().upperLeftGanttChartProperty());
                    bottomProperty().bind(getSkinnable().upperLeftFooterProperty());
                    break;
                case UPPER_RIGHT:
                    topProperty().bind(getSkinnable().upperRightHeaderProperty());
                    centerProperty().bind(getSkinnable().upperRightGanttChartProperty());
                    bottomProperty().bind(getSkinnable().upperRightFooterProperty());
                    getSkinnable().upperRightGanttChartProperty().addListener(observable -> SplitPane.setResizableWithParent(getSkinnable().getUpperRightGanttChart(), false));
                    break;
                case LOWER_LEFT:
                    topProperty().bind(getSkinnable().lowerLeftHeaderProperty());
                    centerProperty().bind(getSkinnable().lowerLeftGanttChartProperty());
                    bottomProperty().bind(getSkinnable().lowerLeftFooterProperty());
                    break;
                case LOWER_RIGHT:
                    topProperty().bind(getSkinnable().lowerRightHeaderProperty());
                    centerProperty().bind(getSkinnable().lowerRightGanttChartProperty());
                    bottomProperty().bind(getSkinnable().lowerRightFooterProperty());
                    getSkinnable().lowerRightGanttChartProperty().addListener(observable -> SplitPane.setResizableWithParent(getSkinnable().getLowerRightGanttChart(), false));
                    break;
            }
        }
    }
}
