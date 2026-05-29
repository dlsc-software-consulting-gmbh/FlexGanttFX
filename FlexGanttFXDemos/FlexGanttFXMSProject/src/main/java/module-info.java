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
module com.flexganttfx.msproject {

    requires transitive mpxj;
    requires transitive com.flexganttfx.extras;
//    requires org.scenicview.scenicview;

    requires jpro.webapi;
    requires javafx.web;
    requires javafx.swing;
    requires javafx.fxml;

    requires atlantafx.base;
    requires java.prefs;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign;
    requires org.scenicview.scenicview;

    exports com.flexganttfx.msproject;
    exports com.flexganttfx.msproject.model;
    exports com.flexganttfx.msproject.view;
}