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

import com.flexganttfx.view.container.DualGanttChartContainerBase;
import javafx.beans.binding.Bindings;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.MasterDetailPane;

public class DualGanttChartContainerSkin extends MultiGanttChartContainerSkinBase<DualGanttChartContainerBase> {

    public DualGanttChartContainerSkin(DualGanttChartContainerBase container) {
        super(container);

        MasterDetailPane masterDetailPane = container.getMasterDetailPane();
        masterDetailPane.setMasterNode(new Wrapper(true));
        masterDetailPane.setDetailNode(new Wrapper(false));
        masterDetailPane.setDividerPosition(.6);

        masterDetailPane.setShowDetailNode(container.isShowSecondary());
        Bindings.bindBidirectional(container.showSecondaryProperty(), masterDetailPane.showDetailNodeProperty());

        getChildren().add(masterDetailPane);
    }

    private class Wrapper extends BorderPane {

        public Wrapper(boolean primary) {
            if (primary) {

            	topProperty().bind(getSkinnable().primaryHeaderProperty());
                centerProperty().bind(getSkinnable().primaryGanttChartProperty());
                bottomProperty().bind(getSkinnable().primaryFooterProperty());

            } else {

            	topProperty().bind(getSkinnable().secondaryHeaderProperty());
                centerProperty().bind(getSkinnable().secondaryGanttChartProperty());
                bottomProperty().bind(getSkinnable().secondaryFooterProperty());

                getSkinnable().secondaryGanttChartProperty().addListener(observable -> SplitPane.setResizableWithParent(getSkinnable().getSecondaryGanttChart(), false));

            }
        }
    }
}
