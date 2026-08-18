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
open module com.flexganttfx.earthquake {
    requires com.flexganttfx.view;
    requires com.flexganttfx.extras;
    requires atlantafx.base;
    requires java.prefs;
    requires java.net.http;
    requires com.google.gson;
    exports com.flexganttfx.earthquake;
    exports com.flexganttfx.earthquake.model;
    exports com.flexganttfx.earthquake.view;
    exports com.flexganttfx.earthquake.renderer;
}
