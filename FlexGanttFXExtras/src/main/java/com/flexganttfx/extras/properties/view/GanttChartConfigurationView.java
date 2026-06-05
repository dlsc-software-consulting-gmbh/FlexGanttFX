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
package com.flexganttfx.extras.properties.view;

import com.flexganttfx.view.GanttChartBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * A tab pane containing four tabs with property sheets for controls, renderers,
 * background, and foreground layers.
 *
 * @since 1.0
 */
public class GanttChartConfigurationView extends TabPane  {

    private final GanttChartPropertySheet controlsSheet = new GanttChartPropertySheet();
    private final GanttChartPropertySheet backgroundLayersSheet = new GanttChartPropertySheet();
    private final GanttChartPropertySheet foregroundLayersSheet = new GanttChartPropertySheet();
    private final GanttChartPropertySheet renderersSheet = new GanttChartPropertySheet();

    /**
     * Constructs a new view.
     */
    public GanttChartConfigurationView() {
        setSide(Side.RIGHT);

        Tab controlsTab = new Tab("Controls", controlsSheet);
        Tab backgroundLayersTab = new Tab("Background Layers", backgroundLayersSheet);
        Tab foregroundLayersTab = new Tab("Foreground Layers", foregroundLayersSheet);
        Tab renderersTab = new Tab("Renderers", renderersSheet);

        getTabs().setAll(controlsTab, backgroundLayersTab, foregroundLayersTab, renderersTab);

        ganttChart.addListener(it -> update());
    }

    /**
     * Constructs a new view for the given gantt chart.
     */
    public GanttChartConfigurationView(GanttChartBase<?> ganttChart) {
        this();
        setGanttChart(ganttChart);
    }

    /**
     * Explicitly updates the view (after some change that the view did not detect automatically).
     */
    public void update() {
        GanttChartBase ganttChart = getGanttChart();
        controlsSheet.getTargets().setAll(ganttChart);
        backgroundLayersSheet.getTargets().setAll(ganttChart.getGraphics().getBackgroundSystemLayers());
        foregroundLayersSheet.getTargets().setAll(ganttChart.getGraphics().getForegroundSystemLayers());
        renderersSheet.getTargets().setAll(ganttChart.getGraphics().getAllActivityRenderers());
    }

    private final ObjectProperty<GanttChartBase> ganttChart = new SimpleObjectProperty<>(this, "ganttChart");

    /**
     * The ganttChart property. Stores the Gantt chart whose controls, layers,
     * and renderers are displayed in this configuration view.
     *
     * @return the ganttChart property
     */
    public final ObjectProperty<GanttChartBase> ganttChartProperty() {
        return ganttChart;
    }

    public final GanttChartBase getGanttChart() {
        return ganttChart.get();
    }

    public final void setGanttChart(GanttChartBase ganttChart) {
        this.ganttChart.set(ganttChart);
    }
}
