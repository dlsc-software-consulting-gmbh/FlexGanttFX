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
 * Contains all model classes used for the various Gantt chart controls.
 */
module com.flexganttfx.model {

    requires java.logging;
    requires transitive javafx.base;

    requires transitive com.flexganttfx.core;

    exports com.flexganttfx.model;
    exports com.flexganttfx.model.activity;
    exports com.flexganttfx.model.calendar;
    exports com.flexganttfx.model.dateline;
    exports com.flexganttfx.model.exception;
    exports com.flexganttfx.model.layout;
    exports com.flexganttfx.model.repository;
    exports com.flexganttfx.model.timeline;
    exports com.flexganttfx.model.util;
}