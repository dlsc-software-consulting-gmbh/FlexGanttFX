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
/**
 * The view layer containing the various custom controls for creating Gantt charts.
 */
module com.flexganttfx.view {

    requires java.logging;
    requires java.prefs;

    requires transitive org.controlsfx.controls;
    requires transitive com.flexganttfx.model;

    exports com.flexganttfx.view;
    exports com.flexganttfx.view.container;
    exports com.flexganttfx.view.graphics;
    exports com.flexganttfx.view.graphics.layer;
    exports com.flexganttfx.view.graphics.renderer;
    exports com.flexganttfx.view.timeline;
    exports com.flexganttfx.view.util;

    exports impl.com.flexganttfx.skin;
    exports impl.com.flexganttfx.skin.container;
    exports impl.com.flexganttfx.skin.graphics;
    exports impl.com.flexganttfx.skin.timeline;
    exports impl.com.flexganttfx.skin.treetable;
    exports impl.com.flexganttfx.skin.util;

    opens com.flexganttfx.view;
    opens com.flexganttfx.view.graphics;
    opens com.flexganttfx.view.timeline;
    opens com.flexganttfx.view.util;
}