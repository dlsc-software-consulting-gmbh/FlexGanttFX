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
package com.flexganttfx.demo.demos;

import com.flexganttfx.demo.DemoBase;
import com.flexganttfx.earthquake.view.EarthquakeView;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Showcase wrapper for the USGS Earthquake demo.
 */
public class EarthquakeDemo extends DemoBase {

    @Override
    public String getName() {
        return "USGS Earthquakes";
    }

    @Override
    public String getDescription() {
        return "Live seismic event data from the USGS Earthquake Hazards Program. Earthquakes are "
                + "grouped by magnitude band and color-coded by severity. Adjust the date range and "
                + "minimum magnitude from the toolbar to filter events fetched in real time.";
    }

    @Override
    public Node getPanel(Stage stage) {
        return new EarthquakeView();
    }

    @Override
    public Node getControlPanel() {
        return null;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
