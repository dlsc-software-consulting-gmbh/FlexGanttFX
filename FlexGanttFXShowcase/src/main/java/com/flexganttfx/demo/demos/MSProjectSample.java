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

import com.flexganttfx.demo.FlexGanttFXSampleBase;
import com.flexganttfx.extras.GanttChartStatusBar;
import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.extras.properties.view.GanttChartConfigurationView;
import com.flexganttfx.model.ActivityLink;
import com.flexganttfx.msproject.SampleProject;
import com.flexganttfx.msproject.SampleProjectFactory;
import com.flexganttfx.msproject.view.MSProjectGanttChart;
import com.flexganttfx.view.graphics.renderer.CurvedLinkRenderer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Sampler wrapper for the standalone MSProject reader demo.
 */
public class MSProjectSample extends FlexGanttFXSampleBase {

    private MSProjectGanttChart gantt;

    @Override
    public String getSampleName() {
        return "MS Project Reader";
    }

    @Override
    public String getSampleDescription() {
        return "Reads Microsoft Project (.mpp / .xml) files and displays the task hierarchy " +
               "as a Gantt chart with curved link renderers showing finish-to-start dependencies. " +
               "Choose from several built-in sample projects or open your own file.";
    }

    @Override
    public Node getPanel(Stage stage) {
        gantt = new MSProjectGanttChart();
        gantt.getTreeTableMasterDetailPane().setDividerPosition(.41);
        gantt.getGraphics().setLinkRenderer(ActivityLink.class, new CurvedLinkRenderer<>(gantt.getGraphics(), "Link Renderer") {
            @Override
            public void draw(ActivityLink<?> link, GraphicsContext gc, Rectangle2D sourceBounds, Rectangle2D targetBounds) {
                if (link.getTargetActivityRef().getActivity().getStartTime()
                        .isBefore(link.getSourceActivityRef().getActivity().getEndTime())) {
                    setStrokeColor(Color.CRIMSON);
                    setArrowHeadColor(Color.CRIMSON);
                } else {
                    setStrokeColor(Color.SLATEGRAY);
                    setArrowHeadColor(Color.SLATEGRAY);
                }
                super.draw(link, gc, sourceBounds, targetBounds);
            }
        });

        // Load default project
        SampleProject defaultProject = SampleProjectFactory.ALL.get(0);
        gantt.load(defaultProject.getFactory().get());
        VBox.setVgrow(gantt, Priority.ALWAYS);

        GanttChartToolBar<?>   toolBar   = new GanttChartToolBar<>(gantt);
        GanttChartStatusBar<?> statusBar = new GanttChartStatusBar<>(gantt);

        HBox selectorBar = buildSelectorBar();

        return new VBox(0, toolBar, selectorBar, gantt, statusBar);
    }

    @Override
    public void dispose() {
        super.dispose();
        gantt = null;
    }

    private HBox buildSelectorBar() {
        Label label = new Label("Project:");
        label.setStyle("-fx-font-weight: bold;");

        ComboBox<SampleProject> projectBox = new ComboBox<>();
        projectBox.getItems().setAll(SampleProjectFactory.ALL);
        projectBox.getSelectionModel().selectFirst();
        projectBox.setPrefWidth(260);
        projectBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && gantt != null) {
                gantt.load(newVal.getFactory().get());
            }
        });

        HBox bar = new HBox(10, label, projectBox);
        bar.getStyleClass().add("showcase-gantt-toolbar");
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(6, 12, 6, 12));
        return bar;
    }
}
