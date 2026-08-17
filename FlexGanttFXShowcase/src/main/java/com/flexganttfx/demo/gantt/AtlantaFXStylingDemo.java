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

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import com.flexganttfx.demo.DemoRow;
import com.flexganttfx.demo.GanttChartDemoBase;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.GanttChartBase;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.HBox;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Demonstrates AtlantaFX style tweaks ({@code striped}, {@code bordered},
 * {@code alt-icon}) applied to the {@link TreeTableView} inside a
 * {@link GanttChart}, and the {@code striped} tweak applied to the
 * {@link ListView} used by the internal {@code ListViewGraphics}.
 */
public class AtlantaFXStylingDemo extends GanttChartDemoBase {

    private GanttChart<DemoRow> gantt;

    @Override
    public boolean requiresAtlantaFX() {
        return true;
    }

    @Override
    public void dispose() {
        super.dispose();
        gantt = null;
    }

    @Override
    protected GanttChartBase<?> createGanttChart() {
        gantt = new GanttChart<>();
        gantt.getLayers().add(DemoRow.layer);

        DemoRow root = new DemoRow("root");
        root.setExpanded(true);
        for (int i = 0; i < 20; i++) {
            DemoRow task = new DemoRow("Task " + (i + 1), 3);
            if (i == 0 || i == 4 || i == 9 || i == 14) {
                int subtaskCount = (i == 4) ? 3 : 2;
                for (int j = 1; j <= subtaskCount; j++) {
                    task.getChildren().add(new DemoRow("Subtask " + (i + 1) + "." + j, 2));
                }
                task.setExpanded(true);
            }
            root.getChildren().add(task);
        }
        gantt.setRoot(root);
        gantt.getTreeTable().setShowRoot(false);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());

        TreeTableColumn<DemoRow, String> startCol = new TreeTableColumn<>("Start");
        startCol.setPrefWidth(100);
        startCol.setCellValueFactory(p -> {
            Instant t = p.getValue().getValue().getEarliestTimeUsed();
            return new ReadOnlyObjectWrapper<>(t != null ? dateFormatter.format(t) : "");
        });

        TreeTableColumn<DemoRow, String> endCol = new TreeTableColumn<>("End");
        endCol.setPrefWidth(100);
        endCol.setCellValueFactory(p -> {
            Instant t = p.getValue().getValue().getLatestTimeUsed();
            return new ReadOnlyObjectWrapper<>(t != null ? dateFormatter.format(t) : "");
        });

        gantt.getTreeTable().getColumns().addAll(startCol, endCol);

        return gantt;
    }

    @Override
    public Node getControlPanel() {
        TreeTableView<?> treeTable = gantt.getTreeTable();
        ListView<?> listView = gantt.getGraphics().getListView();

        CheckBox stripedTree = styleCheckBox("Striped", Styles.STRIPED, treeTable.getStyleClass());
        CheckBox borderedTree = styleCheckBox("Bordered", Styles.BORDERED, treeTable.getStyleClass());
        CheckBox altIconTree = styleCheckBox("Alt Icons", Tweaks.ALT_ICON, treeTable.getStyleClass());
        CheckBox stripedList = styleCheckBox("Striped", Styles.STRIPED, listView.getStyleClass());
        CheckBox borderedList = styleCheckBox("Bordered", Styles.BORDERED, listView.getStyleClass());

        stripedTree.setSelected(gantt.getTreeTable().getStyleClass().contains(Styles.STRIPED));
        stripedList.setSelected(gantt.getTreeTable().getStyleClass().contains(Styles.STRIPED));

        HBox box = new HBox(8,
                new Label("Tree Table View:"),
                stripedTree,
                borderedTree,
                altIconTree,
                new Separator(Orientation.VERTICAL),
                new Label("List View (Graphics):"),
                stripedList,
                borderedList
        );
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(4, 8, 4, 8));
        return box;
    }

    private static CheckBox styleCheckBox(String label, String styleClass, ObservableList<String> styleClasses) {
        CheckBox cb = new CheckBox(label);
        cb.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                if (!styleClasses.contains(styleClass)) {
                    styleClasses.add(styleClass);
                }
            } else {
                styleClasses.remove(styleClass);
            }
        });
        return cb;
    }

    @Override
    public String getName() {
        return "AtlantaFX Styling";
    }

    @Override
    public String getDescription() {
        return "Demonstrates AtlantaFX style tweaks on GanttChart sub-controls. "
                + "Toggle 'striped' and 'bordered' rows and 'alt-icon' disclosure arrows "
                + "on the tree table view, and 'striped' and 'bordered' rows on the internal list view "
                + "used by ListViewGraphics.";
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
