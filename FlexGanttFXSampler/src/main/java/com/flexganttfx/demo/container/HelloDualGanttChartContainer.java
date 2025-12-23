/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.container;

import com.flexganttfx.demo.FlexGanttFXSampleBase;
import com.flexganttfx.demo.HelloRow;
import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.extras.properties.DualGanttChartContainerBaseItemProvider;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.container.DualGanttChartContainer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.controlsfx.control.PropertySheet;

public class HelloDualGanttChartContainer extends FlexGanttFXSampleBase {

    private DualGanttChartContainer dual;

    @Override
    public String getSampleName() {
        return "Dual";
    }

    @Override
    public void dispose() {
        super.dispose();
        dual = null;
    }

    @Override
    public Node getPanel(Stage panel) {
        GanttChart<HelloRow> gc1 = new GanttChart<>();
        gc1.setRoot(new HelloRow("Root"));
        gc1.setAutoHideScrollBar(false);

        GanttChart<HelloRow> gc2 = new GanttChart<>();
        gc2.setRoot(new HelloRow("Root"));

        dual = new DualGanttChartContainer(gc1, gc2);
        dual.getPrimaryGanttChart().getGraphics().setShowRowHeaders(true);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(dual);
        borderPane.setTop(new GanttChartToolBar<>(gc1));

        return borderPane;
    }

    @Override
    public Node getControlPanel() {
        DualGanttChartContainerBaseItemProvider provider = new DualGanttChartContainerBaseItemProvider();
        return new PropertySheet(FXCollections.observableArrayList(provider.getPropertySheetItems(dual)));
    }

    @Override
    public String getSampleDescription() {
        return "A special multi Gantt chart container that is capable of displaying "
                + "exactly two Gantt charts and keeping their layouts (same "
                + "table width, same timeline) and their scrolling and zooming behavior in "
                + "synch. The container distinguishes between a primary and a secondary Gantt "
                + "chart, where the secondary Gantt chart is located in the detail node section "
                + "of a MasterDetailPane. It can be hidden or shown on demand. Each one "
                + "of the two Gantt charts can have its own header and footer.";
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}