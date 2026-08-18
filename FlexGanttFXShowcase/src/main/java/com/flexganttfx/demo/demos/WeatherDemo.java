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
import com.flexganttfx.weather.view.WeatherView;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Showcase wrapper for the standalone World Climate Explorer demo.
 */
public class WeatherDemo extends DemoBase {

    @Override
    public String getName() {
        return "World Climate";
    }

    @Override
    public String getDescription() {
        return "Seven years (2018–2024) of synthetic daily weather data for 8 iconic world cities "
            + "spanning tropical, desert, oceanic, continental and sub-arctic climate zones. "
            + "Temperature bars are colour-coded from icy blue (cold) through teal, green, "
            + "yellow and orange to fiery red (hot) using the HighLowChartActivity and ChartLayout. "
            + "The lower band shows precipitation intensity. ~28 000 chart activities in total.";
    }

    @Override
    public Node getPanel(Stage stage) {
        return new WeatherView();
    }

    @Override
    public Node getControlPanel() {
        return null;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
