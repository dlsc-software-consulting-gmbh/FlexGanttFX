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
package com.flexganttfx.demo.demos;

import com.flexganttfx.demo.DemoBase;
import com.flexganttfx.hospital.view.HospitalView;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Showcase wrapper for the standalone hospital operating room scheduling demo.
 */
public class HospitalDemo extends DemoBase {

    @Override
    public String getName() {
        return "Hospital OR Scheduler";
    }

    @Override
    public String getDescription() {
        return "An operating room scheduling demo with linked room and resource charts, "
                + "a CalendarFX detailed day view, conflict resolution, and drag-and-resize editing "
                + "across a month-long surgery schedule.";
    }

    @Override
    public Node getPanel(Stage stage) {
        return new HospitalView();
    }

    @Override
    public Node getControlPanel() {
        return null;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
