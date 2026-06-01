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
package com.flexganttfx.factory.view;

import com.flexganttfx.factory.model.DataModel;
import com.flexganttfx.factory.model.Job;
import com.flexganttfx.factory.model.Machine;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.timeline.Timeline;
import javafx.scene.paint.Color;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * A pre-configured {@link GanttChart} for the factory demo. It wires the
 * {@link DataModel} to the chart and attaches the {@link JobRenderer}.
 * The visible time range starts one week before today so all status types
 * (past, current, and future jobs) are visible on launch.
 */
public class FactoryGanttChart extends GanttChart<Machine> {

    public FactoryGanttChart(DataModel dataModel) {
        super(dataModel.getRoot());

        getGraphics().setActivityTextFill(Color.WHITE);

        getLayers().add(dataModel.getLayer());
        getRoot().getChildren().setAll(dataModel.getProductionLines());

        GraphicsBase<Machine> graphics = getGraphics();
        graphics.setActivityRenderer(Job.class, GanttLayout.class, new JobRenderer(graphics));

        Timeline timeline = getTimeline();
        timeline.showTemporalUnit(ChronoUnit.HOURS, 10);

        // Position the view at the start of the data window (one week before today)
        Instant windowStart = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(7, ChronoUnit.DAYS);
        timeline.showTime(windowStart, false);
    }
}
