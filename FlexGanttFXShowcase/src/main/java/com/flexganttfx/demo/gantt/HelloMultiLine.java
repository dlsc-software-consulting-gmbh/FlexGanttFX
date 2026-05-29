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
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
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

public class HelloMultiLine extends FlexGanttFXSample {

    private final List<HelloRow> rows = new ArrayList<>();
    private final List<EqualLinesManager<HelloRow, HelloActivity>> equalManagers = new ArrayList<>();
    private final List<AutoLinesManager<HelloRow, HelloActivity>> autoManagers = new ArrayList<>();
    private final List<RandomLinesManager> randomManagers = new ArrayList<>();

    private Layer layer;

    private Slider slider;

    private RadioButton equalButton;

    private RadioButton autoButton;

    @Override
    public String getSampleName() {
        return "Multi Line";
    }

    @Override
    public String getSampleDescription() {
        return "This sample demonstrates how activities can be placed on multiple lines "
                + "within the same row. Different line managers can be used to place the "
                + "activities with different strategies.";
    }

    @Override
    protected GanttChart<?> createGanttChart() throws Exception {
        GanttChart<HelloRow> gc = new GanttChart<>();
        gc.setDisplayMode(GanttChart.DisplayMode.GRAPHICS_ONLY);

        // the layers
        List<Layer> layers = new ArrayList<>();
        layers.add(layer = new Layer("Layer 1"));
        gc.getLayers().setAll(layers);

        ListViewGraphics<HelloRow> graphics = gc.getGraphics();

        // Create an invisible root and add 5 named rows as children
        HelloRow root = new HelloRow("ROOT");
        String[] rowNames = {"Alpha", "Beta", "Gamma", "Delta", "Epsilon"};
        for (String name : rowNames) {
            HelloRow r = new HelloRow(name);
            r.setHeight(200);
            r.setMaxHeight(2000);
            r.setMinHeight(30);

            AutoLinesManager<HelloRow, HelloActivity> autoMgr = new AutoLinesManager<>(r, graphics);
            EqualLinesManager<HelloRow, HelloActivity> equalMgr = new MyEqualLinesManager(r);
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
        graphics.setActivityRenderer(HelloActivity.class, GanttLayout.class, new HelloActivityRenderer(graphics, "Hello Activity Renderer"));
        graphics.setOnActivityChangeFinished(evt -> maybePerformLayout());
        graphics.setOnActivityDeleted(evt -> maybePerformLayout());

        applyLineCount(25);

        return gc;
    }

    private void maybePerformLayout() {
        if (autoButton == null || autoButton.isSelected()) {
            for (AutoLinesManager<HelloRow, HelloActivity> mgr : autoManagers) {
                mgr.layout();
            }
        }
    }

    @Override
    public Node getControlPanel() {
        equalButton = new RadioButton("Equal Lines");
        autoButton = new RadioButton("Equal Lines (Auto Layout)");
        RadioButton randomButton = new RadioButton("Random Lines");

        equalButton.setStyle("-fx-background-color: transparent;");
        autoButton.setStyle("-fx-background-color: transparent;");
        randomButton.setStyle("-fx-background-color: transparent;");

        equalButton.setTooltip(new Tooltip("Distribute available row height equally to all lines"));
        autoButton.setTooltip(new Tooltip("Equal line height, activities non overlapping"));
        randomButton.setTooltip(new Tooltip("Randomly place lines and allocate line height"));

        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(equalButton, autoButton, randomButton);
        toggleGroup.selectedToggleProperty().addListener(it -> applyLineCount(rows.isEmpty() ? 25 : rows.get(0).getLineCount()));
        toggleGroup.selectedToggleProperty().addListener(it -> getGanttChart().getGraphics().showEarliestActivities());

        equalButton.setOnAction(evt -> applyEqualLinesManager());
        autoButton.setOnAction(evt -> applyAutoLinesManager());
        randomButton.setOnAction(evt -> applyRandomLinesManager());

        Label sliderLabel = new Label("Number of Lines");
        sliderLabel.setMaxWidth(Double.MAX_VALUE);
        sliderLabel.setAlignment(Pos.CENTER);

        slider = new Slider(1, 100, 25);
        slider.setShowTickLabels(false);
        slider.setShowTickMarks(false);
        slider.setOrientation(Orientation.HORIZONTAL);
        slider.setPrefWidth(250);
        slider.valueProperty().addListener(it -> applyLineCount((int) slider.getValue()));

        Button apply = new Button("Apply");
        apply.setMaxWidth(Double.MAX_VALUE);
        apply.setOnAction(evt -> applyLineCount((int) slider.getValue()));

        HBox box = new HBox();
        box.setSpacing(10);
        box.setFillHeight(true);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getChildren().addAll(equalButton, autoButton,
                randomButton, new Separator(Orientation.VERTICAL),
                sliderLabel, slider, apply);

        Platform.runLater(() -> equalButton.fire());

        return box;
    }

    private void applyLineCount(int count) {
        for (HelloRow r : rows) {
            r.setLineCount(count);

            LocalDate date = LocalDate.now();

            r.clearActivities();

            for (int i = 0; i < 100; i++) {
                for (int j = 0; j < Math.random() * r.getLineCount() / 2; j++) {
                    int duration = Math.max(1, (int) (Math.random() * 10));

                    LocalTime time = LocalTime.MIN;

                    Instant st = ZonedDateTime.of(date, time, ZoneId.systemDefault()).toInstant();
                    Instant et = ZonedDateTime.of(date.plusDays(duration), time, ZoneId.systemDefault()).toInstant();

                    HelloActivity activity = new HelloActivity();
                    activity.setColor(randomColor());
                    activity.setStartTime(st);
                    activity.setEndTime(et);
                    activity.setLineIndex((int) (Math.random() * r.getLineCount()));

                    date = date.plusDays(Math.max(1, (int) (Math.random() * 3)));

                    r.addActivity(layer, activity);
                }
            }
        }

        if (autoButton == null || autoButton.isSelected()) {
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
            EqualLinesManager<HelloRow, HelloActivity> {

        public MyEqualLinesManager(HelloRow row) {
            super(row);
        }

        @Override
        public int getLineIndex(HelloActivity activity) {
            return activity.getLineIndex();
        }
    }

    class RandomLinesManager implements LinesManager<HelloActivity> {

        private final HelloRow row;

        private double[] locations;
        private double[] heights;

        private final GanttLayout layout;

        public RandomLinesManager(HelloRow row) {
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
        public int getLineIndex(HelloActivity activity) {
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

    class HelloActivityRenderer extends ActivityBarRenderer<HelloActivity> {

        public HelloActivityRenderer(GraphicsBase<?> graphics, String name) {
            super(graphics, name);
            setCornersRounded(false);
            setBarHeight(Row.DEFAULT_ROW_HEIGHT - 4);
        }

        @Override
        protected ActivityBounds drawActivity(
                ActivityRef<HelloActivity> activityRef, Position position,
                GraphicsContext gc, double x, double y, double w, double h,
                boolean selected, boolean hover, boolean highlighted,
                boolean pressed) {

            HelloActivity activity = activityRef.getActivity();

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
            Row<?, ?, HelloActivity> row = activityRef.getRow();
            LinesManager<HelloActivity> manager = row.getLinesManager();
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
