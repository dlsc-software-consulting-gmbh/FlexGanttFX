/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
/**
 * A couple of non-essential custom controls designed to work with the Gantt charts.
 */
module com.flexganttfx.extras {

    requires transitive com.flexganttfx.view;
    requires com.dlsc.gemsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign;
    requires java.prefs;
    exports com.flexganttfx.extras;
    exports com.flexganttfx.extras.properties;
    exports com.flexganttfx.extras.properties.layer;
    exports com.flexganttfx.extras.properties.renderer;
    exports com.flexganttfx.extras.properties.view;
    exports com.flexganttfx.extras.util;
    exports impl.com.flexganttfx.extras.skin;
    opens com.flexganttfx.extras;
    opens com.flexganttfx.extras.util;
}