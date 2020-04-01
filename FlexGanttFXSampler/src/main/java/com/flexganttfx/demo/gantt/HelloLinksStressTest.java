package com.flexganttfx.demo.gantt;

import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.model.ActivityLink;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.ActivityBase;
import com.flexganttfx.model.activity.CompletableActivity;
import com.flexganttfx.model.activity.MutableCompletableActivityBase;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.ListViewGraphics;
import com.flexganttfx.view.graphics.renderer.CompletableActivityRenderer;
import com.flexganttfx.view.graphics.renderer.StraightLinkRenderer;
import com.flexganttfx.view.util.Position;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class HelloLinksStressTest extends FlexGanttFXSample {

    private final ArrayList<ActivityLink<?>> links = new ArrayList<>();

    public HelloLinksStressTest() {
//        final GanttChartToolBar<?> bar = getToolbar();
//        ToggleButton b = new ToggleButton("Show Links");
//        b.selectedProperty().addListener(it -> {
//            if (b.isSelected()) {
//                getGanttChart().getLinks().setAll(links);
//            } else {
//                getGanttChart().getLinks().clear();
//            }
//        });
//        bar.getItems().add(0, b);
    }

    @Override
    protected GanttChartBase<?> createGanttChart() throws Exception {
        GanttChart<ActivityRow> gantt = new GanttChart();
        List<ActivityRow> roots = new ArrayList<>();

        int TOTAL = 1000;

        for (int i = 0; i < TOTAL; i++) {
            final ActivityRow row = new ActivityRow("row " + i, i);
            roots.add(row);
            for (int j = 0; j < 5; j++) {
                row.getChildren().add(new ActivityRow("sub Row" + i + " : " + j, row));
            }
        }
        ActivityRow root = new ActivityRow(null, -1);
        root.setExpanded(true);
        root.getChildren().addAll(roots);
        gantt.setRoot(root);
        gantt.getLayers().add(layer);


        // source set ensures that only one link will come "out of" an activity.
        Set<ActivityRow> sourceSet = new HashSet<>();

        for (int i = 0; i < 100000; i++) {
            int s = -1, e = -1;
            while (s >= e) {
                s = (int) (Math.random() * TOTAL);
                e = Math.min(TOTAL - 1, s +  (int) (Math.random() * 5));
            }

            ActivityRow rsChild = roots.get(s);
            ActivityRow reChild = roots.get(e);

            ActivityRow predecessor = rsChild.getChildren().get((int) (Math.random() * rsChild.getChildren().size()));
            ActivityRow successor = reChild.getChildren().get((int) (Math.random() * reChild.getChildren().size()));

            //    if (!sourceSet.contains(predecessor)) {
            sourceSet.add(predecessor);
            links.add(new ActivityLink<>(
                    new ActivityRef<>(predecessor, layer, predecessor.act),
                    new ActivityRef<>(successor, layer, successor.act)
            ));

            predecessor.setLinksOut(predecessor.getLinksOut() + 1);
            successor.setLinksIn(successor.getLinksOut() + 1);
            //   }
        }

        ListViewGraphics graphics = gantt.getGraphics();
        // Note default renderer will improve the performance however it's not a valid case as we MUST use the custom renderer
//        graphics.setActivityRenderer(MutableCompletableActivityBase.class, GanttLayout.class, new CompletableActivityRenderer(graphics, "DEFAULT"));
        graphics.setActivityRenderer(MutableCompletableActivityBase.class, GanttLayout.class, new CompletableActivityRendererBase(graphics, "DEFAULT"));
        graphics.setLinkRenderer(ActivityLink.class, new StraightLinkRenderer<>(graphics, "Straight Link Renderer"));

        TreeTableView<ActivityRow> table = gantt.getTreeTable();
        table.getSelectionModel().getSelectedItems().addListener((InvalidationListener) observable -> {
            TreeItem<ActivityRow> item = table.getSelectionModel().getSelectedItem();
            if (item != null && item.getValue().act != null) {
                gantt.getGraphics().getTimeline().showTime(item.getValue().act.getStartTime());
            }
        });

        TreeTableColumn<ActivityRow, Integer> columnA = new TreeTableColumn<>("in");
        columnA.setMinWidth(100);
        columnA.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue().getLinksIn()));

        TreeTableColumn<ActivityRow, Integer> columnB = new TreeTableColumn<>("out");
        columnB.setMinWidth(100);
        columnB.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue().getLinksOut()));

        TreeTableColumn<ActivityRow, Instant> column = new TreeTableColumn<>("start");
        column.setMinWidth(100);
        column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue().data.start));

        TreeTableColumn<ActivityRow, Instant> column1 = new TreeTableColumn<>("end");
        column1.setMinWidth(100);
        column1.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue().data.end));

        TreeTableColumn<ActivityRow, String> column2 = new TreeTableColumn<>("name");
        column2.setMinWidth(100);
        column2.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue().data.name));
        table.getColumns().addAll(columnA, columnB, column, column1, column2);

        links.forEach(link -> gantt.getLinks().add(link));

        gantt.getGraphics().showEarliestActivities();
        return gantt;
    }

    @Override
    public String getSampleName() {
        return "Links Stress Test";
    }

    class Data {
        Instant start;
        Instant end;
        String name;

    }

    Layer layer = new Layer("Activities");
    int shift = 1;

    class ActivityRow extends Row<ActivityRow, ActivityRow, ActivityBase<Data>> {
        Data data;
        int linksIn;
        int linksOut;

        protected MutableCompletableActivityBase<Data> act;

        public ActivityRow(String name, int i) {
            data = new Data();
            data.start = generateRandomInstant(2002 + (i * shift), 2004 + (i * shift));
            data.end = generateRandomInstant(2006 + (i * shift), 2008 + (i * shift));
            data.name = name;
            setExpanded(true);
            if (data != null && name != null) {
                createActivity(data);
                setName(data.name);
            }
        }

        public ActivityRow(String name, ActivityRow parent) {
            data = new Data();
            int s = Date.from(parent.data.start).getYear() + 1 + 1900;
            int e = Date.from(parent.data.end).getYear() + 1 + 1900;
            int mid = (int) Math.floor((s + e) / 2);
            data.start = generateRandomInstant(s, mid);
            data.end = generateRandomInstant(mid + 1 > e ? e : mid + 1, e);
            data.name = name;
            setExpanded(true);
            if (data != null && name != null) {
                createActivity(data);
                setName(data.name);
            }
        }

        public int getLinksIn() {
            return linksIn;
        }

        public void setLinksIn(int linksIn) {
            this.linksIn = linksIn;
        }

        public int getLinksOut() {
            return linksOut;
        }

        public void setLinksOut(int linksOut) {
            this.linksOut = linksOut;
        }

        protected void createActivity(Data data) {
//            System.out.println("st: " + data.start + ", et: " + data.end);
            act = new MutableCompletableActivityBase<>(data.name, data.start, data.end);
            act.setUserObject(data);
            addActivity(layer, act);
        }
    }

    Instant generateRandomInstant(int startYear, int endYear) {
        LocalDate startDate = LocalDate.of(startYear, 1, 1); //start date
        long start = startDate.toEpochDay();
        LocalDate endDate = LocalDate.of(endYear, 1, 1); //end date
        long end = endDate.toEpochDay();
        long randomEpochDay = ThreadLocalRandom.current().longs(start, end).findAny().getAsLong();
        return LocalDate.ofEpochDay(randomEpochDay).atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    class CompletableActivityRendererBase<A extends CompletableActivity> extends CompletableActivityRenderer<A> {

        private double widthPerChar = -1;

        public CompletableActivityRendererBase(GraphicsBase<?> graphics, String name) {
            super(graphics, name);
            setGlossy(false);
            setBarHeight(40);
            setCornerRadius(5);
            setCornersRounded(true);
            setFont(new Font("Roboto", 15));
            setFillCompletion(Color.valueOf("#2899B0"));
            setFill(Color.valueOf("#3CB5CE"));
            setTextFillHover(Color.rgb(255, 255, 255, .87));
            setTextFillPressed(getTextFillHover());
            setTextFillHighlight(getTextFillHover());
            setTextFillSelected(getTextFillHover());
        }

        @Override
        public void drawCompletion(ActivityRef<A> activityRef, GraphicsContext gc, double x, double y, double w, double h, boolean selected, boolean hover, boolean highlighted, boolean pressed) {
            super.drawCompletion(activityRef, gc, x, y, w, h, selected, hover, highlighted, pressed);
        }

        @Override
        public void drawBorder(ActivityRef activityRef, Position position, GraphicsContext gc, double x, double y, double w, double h, boolean selected, boolean hover, boolean highlighted, boolean pressed) {
            // do nothing
        }

        @Override
        public ActivityBounds drawActivity(ActivityRef<A> path, Position position, GraphicsContext gc, double x, double y, double w, double h, boolean selected, boolean hover, boolean highlighted, boolean pressed) {
            return super.drawActivity(path, position, gc, x, y, w, h, selected, hover, highlighted, pressed);
        }

        @Override
        public void drawBackground(ActivityRef<A> activityRef, Position position, GraphicsContext gc, double x, double y, double w, double h, boolean selected, boolean hover, boolean highlighted, boolean pressed) {
            super.drawBackground(activityRef, position, gc, x, y, w, h, selected, hover, highlighted, pressed);
        }
    }


}
