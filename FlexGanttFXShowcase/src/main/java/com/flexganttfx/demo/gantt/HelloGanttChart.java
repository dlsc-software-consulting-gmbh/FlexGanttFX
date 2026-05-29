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
package com.flexganttfx.demo.gantt;

import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.demo.HelloActivity;
import com.flexganttfx.demo.HelloRow;
import com.flexganttfx.extras.properties.view.GanttChartConfigurationView;
import com.flexganttfx.model.Layer;
import com.flexganttfx.view.GanttChart;
import javafx.application.Application;
import javafx.scene.Node;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class HelloGanttChart extends FlexGanttFXSample {

    private GanttChart<HelloRow> gc;

    @Override
    public void dispose() {
        super.dispose();
        gc = null;
    }

    @Override
    protected GanttChart<?> createGanttChart() {
        gc = new GanttChart<>();

        HelloRow root = new HelloRow("root");

        Layer layer = new Layer("layer");
        gc.getLayers().add(layer);
        gc.setAutoHideScrollBar(false);

        gc.getTimeline().getModel().setHorizonStartTime(ZonedDateTime.now().minusMonths(3).truncatedTo(ChronoUnit.DAYS).toInstant());
        gc.getTimeline().getModel().setHorizonEndTime(ZonedDateTime.now().plusMonths(3).truncatedTo(ChronoUnit.DAYS).toInstant());

        HelloActivity activity = new HelloActivity();
        activity.setStartTime(Instant.now());
        activity.setEndTime(Instant.now().plus(Duration.ofDays(7)));
        root.addActivity(layer, activity);

        for (int i = 0; i < 200; i++) {
            HelloRow row = new HelloRow("Row " + (i + 1));
            row.setHeight(20 + Math.random() * 100);
            root.getChildren().add(row);
        }

        gc.setRoot(root);

        return gc;
    }

    @Override
    public String getSampleName() {
        return "Gantt Chart";
    }

    @Override
    public String getSampleDescription() {
        return "A simple Gantt chart with a single layer.";
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
