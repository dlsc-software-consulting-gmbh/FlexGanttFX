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

import com.flexganttfx.demo.GanttChartDemoBase;
import com.flexganttfx.demo.DemoActivity;
import com.flexganttfx.demo.DemoRow;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Layout;
import com.flexganttfx.model.LinesManager;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.layout.EqualLinesManager;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.ListViewGraphics;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.util.AutoLinesManager;
import com.flexganttfx.view.util.Position;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class MultiLineDemo extends GanttChartDemoBase {

    private final List<DemoRow> rows = new ArrayList<>();
    private final List<EqualLinesManager<DemoRow, DemoActivity>> equalManagers = new ArrayList<>();
    private final List<AutoLinesManager<DemoRow, DemoActivity>> autoManagers = new ArrayList<>();
    private final List<RandomLinesManager> randomManagers = new ArrayList<>();

    private Layer layer;

    private Slider slider;

    private ComboBox<String> layoutCombo;

    @Override
    public String getName() {
        return "Multi Line";
    }

    @Override
    public String getDescription() {
        return "This demo demonstrates how activities can be placed on multiple lines "
                + "within the same row. Different line managers can be used to place the "
                + "activities with different strategies.";
    }

    @Override
    protected GanttChart<?> createGanttChart() throws Exception {
        GanttChart<DemoRow> gc = new GanttChart<>();
        gc.setDisplayMode(GanttChart.DisplayMode.GRAPHICS_ONLY);

        // the layers
        List<Layer> layers = new ArrayList<>();
        layers.add(layer = new Layer("Layer 1"));
        gc.getLayers().setAll(layers);

        ListViewGraphics<DemoRow> graphics = gc.getGraphics();

        // Create an invisible root and add 5 named rows as children
        DemoRow root = new DemoRow("ROOT");
        String[] rowNames = {"Alpha", "Beta", "Gamma", "Delta", "Epsilon"};
        for (String name : rowNames) {
            DemoRow r = new DemoRow(name);
            r.setHeight(200);
            r.setMaxHeight(2000);
            r.setMinHeight(30);

            AutoLinesManager<DemoRow, DemoActivity> autoMgr = new AutoLinesManager<>(r, graphics);
            EqualLinesManager<DemoRow, DemoActivity> equalMgr = new MyEqualLinesManager(r);
            RandomLinesManager randMgr = new RandomLinesManager(r);

            autoManagers.add(autoMgr);
            equalManagers.add(equalMgr);
            randomManagers.add(randMgr);

            r.setLinesManager(autoMgr);
            rows.add(r);
            root.getChildren().add(r);
        }

        gc.setRoot(root);
        gc.getTreeTable().setShowRoot(false);
        graphics.setAutoGridEnabled(true);
        graphics.setActivityRenderer(DemoActivity.class, GanttLayout.class, new DemoActivityRenderer(graphics, "Demo Activity Renderer"));
        graphics.setOnActivityChangeFinished(evt -> maybePerformLayout());
        graphics.setOnActivityDeleted(evt -> maybePerformLayout());

        applyLineCount(25);

        return gc;
    }

    private void maybePerformLayout() {
        if (layoutCombo == null || "Auto Layout".equals(layoutCombo.getValue())) {
            for (AutoLinesManager<DemoRow, DemoActivity> mgr : autoManagers) {
                mgr.layout();
            }
        }
    }

    @Override
    public Node getControlPanel() {
        layoutCombo = new ComboBox<>();
        layoutCombo.getItems().addAll("Equal (Static)", "Auto Layout", "Random");
        layoutCombo.setValue("Equal (Static)");
        layoutCombo.valueProperty().addListener(it -> {
            switch (layoutCombo.getValue()) {
                case "Equal (Static)": applyEqualLinesManager(); break;
                case "Auto Layout":    applyAutoLinesManager();  break;
                case "Random":         applyRandomLinesManager(); break;
            }
            applyLineCount(rows.isEmpty() ? 25 : rows.get(0).getLineCount());
            getGanttChart().getGraphics().showEarliestActivities();
        });

        Label sliderLabel = new Label("Number of Lines");
        sliderLabel.setMaxWidth(Double.MAX_VALUE);
        sliderLabel.setAlignment(Pos.CENTER);

        slider = new Slider(1, 100, 25);
        slider.setShowTickLabels(false);
        slider.setShowTickMarks(false);
        slider.setOrientation(Orientation.HORIZONTAL);
        slider.setPrefWidth(250);
        slider.valueProperty().addListener(it -> applyLineCount((int) slider.getValue()));

        Label descLabel = new Label(descriptionFor("Equal (Static)"));
        descLabel.setStyle("-fx-font-style: italic; -fx-text-fill: -color-fg-default;");
        layoutCombo.valueProperty().addListener(it -> descLabel.setText(descriptionFor(layoutCombo.getValue())));

        HBox box = new HBox();
        box.setSpacing(10);
        box.setFillHeight(true);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getChildren().addAll(new Label("Layout:"), layoutCombo,
                new Separator(Orientation.VERTICAL),
                sliderLabel, slider);

        VBox vbox = new VBox(4, box, descLabel);

        Platform.runLater(() -> layoutCombo.setValue("Equal (Static)"));

        return vbox;
    }

    private static String descriptionFor(String layout) {
        switch (layout) {
            case "Equal (Static)": return "Distributes the available row height equally to all lines. Activities are placed on manually assigned line indices.";
            case "Auto Layout":    return "Equal line heights with automatic placement — activities are rearranged to avoid overlapping.";
            case "Random":         return "Assigns random heights and positions to lines. Activities are placed on manually assigned line indices.";
            default:               return "";
        }
    }

    private void applyLineCount(int count) {
        for (DemoRow r : rows) {
            r.setLineCount(count);

            LocalDate date = LocalDate.now();

            r.clearActivities();

            for (int i = 0; i < 100; i++) {
                for (int j = 0; j < Math.random() * r.getLineCount() / 2; j++) {
                    int duration = Math.max(1, (int) (Math.random() * 10));

                    LocalTime time = LocalTime.MIN;

                    Instant st = ZonedDateTime.of(date, time, ZoneId.systemDefault()).toInstant();
                    Instant et = ZonedDateTime.of(date.plusDays(duration), time, ZoneId.systemDefault()).toInstant();

                    DemoActivity activity = new DemoActivity();
                    activity.setColor(randomColor());
                    activity.setStartTime(st);
                    activity.setEndTime(et);
                    activity.setLineIndex((int) (Math.random() * r.getLineCount()));

                    date = date.plusDays(Math.max(1, (int) (Math.random() * 3)));

                    r.addActivity(layer, activity);
                }
            }
        }

        if (layoutCombo == null || "Auto Layout".equals(layoutCombo.getValue())) {
            maybePerformLayout();
        }
    }

    private Color randomColor() {
        switch ((int) (Math.random() * 6)) {
            case 0:
                return Color.LIGHTBLUE;
            case 1:
                return Color.LIGHTCYAN;
            case 2:
                return Color.LIGHTCORAL;
            case 3:
                return Color.LIGHTGOLDENRODYELLOW;
            case 4:
                return Color.LIGHTSALMON;
            case 5:
                return Color.LIGHTSEAGREEN;
            case 6:
                return Color.LIGHTSKYBLUE;
            case 7:
                return Color.LIGHTSTEELBLUE;
            case 8:
                return Color.LIGHTYELLOW;
            default:
                return Color.LIGHTGRAY;
        }
    }

    private void applyRandomLinesManager() {
        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).setLinesManager(randomManagers.get(i));
        }
    }

    private void applyEqualLinesManager() {
        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).setLinesManager(equalManagers.get(i));
        }
    }

    private void applyAutoLinesManager() {
        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).setLinesManager(autoManagers.get(i));
        }
    }

    class MyEqualLinesManager extends
            EqualLinesManager<DemoRow, DemoActivity> {

        public MyEqualLinesManager(DemoRow row) {
            super(row);
        }

        @Override
        public int getLineIndex(DemoActivity activity) {
            return activity.getLineIndex();
        }
    }

    class RandomLinesManager implements LinesManager<DemoActivity> {

        private final DemoRow row;

        private double[] locations;
        private double[] heights;

        private final GanttLayout layout;

        public RandomLinesManager(DemoRow row) {
            this.row = row;
            this.layout = new GanttLayout();

            row.lineCountProperty().addListener(it -> update());
        }

        private void update() {
            int count = row.getLineCount();

            heights = new double[count];
            locations = new double[count];

            for (int i = 0; i < count; i++) {
                heights[i] = Math.random() * row.getHeight() / 2;
                locations[i] = Math.min(row.getHeight() - heights[i],
                        Math.random() * row.getHeight());
            }
        }

        @Override
        public int getLineIndex(DemoActivity activity) {
            return activity.getLineIndex();
        }

        @Override
        public double getLineLocation(int lineIndex, double rowHeight) {
            return locations[lineIndex];
        }

        @Override
        public double getLineHeight(int lineIndex, double rowHeight) {
            return heights[lineIndex];
        }

        @Override
        public Layout getLineLayout(int lineIndex) {
            return layout;
        }
    }

    class DemoActivityRenderer extends ActivityBarRenderer<DemoActivity> {

        public DemoActivityRenderer(GraphicsBase<?> graphics, String name) {
            super(graphics, name);
            setCornersRounded(false);
            setBarHeight(Row.DEFAULT_ROW_HEIGHT - 4);
        }

        @Override
        protected ActivityBounds drawActivity(
                ActivityRef<DemoActivity> activityRef, Position position,
                GraphicsContext gc, double x, double y, double w, double h,
                boolean selected, boolean hover, boolean highlighted,
                boolean pressed) {

            DemoActivity activity = activityRef.getActivity();

            /*
             * We are customing the renderer based on the color returned by the
             * activity. This is just one way of coloring activities
             * differently.
             */
            setFill(activity.getColor().darker());
            setStroke(activity.getColor().darker().darker());

            /*
             * We want to use a different bar height depending on the height of
             * the line where the activity is shown. This way we will end up
             * with very large bars on large lines and small ones on small
             * lines. This will only be visible in the randome lines manager use
             * case.
             */
            Row<?, ?, DemoActivity> row = activityRef.getRow();
            LinesManager<DemoActivity> manager = row.getLinesManager();
            int lineIndex = manager.getLineIndex(activity);
            if (lineIndex != -1) {
                double lineHeight = manager.getLineHeight(lineIndex,
                        row.getHeight());
                setBarHeight(lineHeight * .8);
            } else {
                setBarHeight(16);
            }

            return super.drawActivity(activityRef, position, gc, x, y, w, h,
                    selected, hover, highlighted, pressed);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
