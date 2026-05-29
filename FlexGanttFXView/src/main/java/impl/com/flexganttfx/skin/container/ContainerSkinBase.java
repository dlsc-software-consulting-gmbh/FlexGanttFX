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

import com.flexganttfx.view.container.ContainerBase;
import javafx.scene.control.SkinBase;

/**
 * Created by dirk on 11/07/16.
 */
public abstract class ContainerSkinBase<T extends ContainerBase> extends SkinBase<T> {

    protected static final String GANTT_TREE_TABLE_VIEW_LAST = "gantt-tree-table-view-last";
    protected static final String GANTT_TREE_TABLE_VIEW_MIDDLE = "gantt-tree-table-view-middle";
    protected static final String GANTT_TREE_TABLE_VIEW_FIRST = "gantt-tree-table-view-first";

    protected static final String TIMELINE_LAST = "timeline-last";
    protected static final String TIMELINE_MIDDLE = "timeline-middle";
    protected static final String TIMELINE_FIRST = "timeline-first";

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected ContainerSkinBase(T control) {
        super(control);
    }
}
