This demo focuses on model-side changes in the lite chart variant. It mirrors the standard model demo but uses `GanttChartLite` to show how the lighter control responds to the same kinds of updates.

```java
public class GanttChartLiteModelDemo extends GanttChartDemoBase {

    private int layerCounter;
    private GanttChartLite<DemoRow> gantt;
    private final Layer layer = new Layer("Default");

    public GanttChartLiteModelDemo() {
        DemoRow root = new DemoRow("Initial Root");
        root.setExpanded(true);
    }
    
    class DemoRow extends Row<DemoRow, DemoRow, Activity> {
        public DemoRow(String name) {
            super(name);
        }
    }
    
    @Override
    protected GanttChartBase<?> createGanttChart() throws Exception {
        gantt = new GanttChartLite<>();
        gantt.getLayers().add(layer);
        return gantt;
    }

    @Override
    public Node getControlPanel() {
        HBox box = new HBox();
        box.setSpacing(10);

        Button newModel = new Button("Set New List");
        newModel.setOnAction(event -> setNewList());
        newModel.setMaxWidth(Double.MAX_VALUE);

        Button addLayer = new Button("Add New Layer");
        addLayer.setOnAction(event -> addLayer());
        addLayer.setMaxWidth(Double.MAX_VALUE);

        Button addSingleRow = new Button("Add Single Row");
        addSingleRow.setOnAction(event -> addSingleRow());
        addSingleRow.setMaxWidth(Double.MAX_VALUE);

        Button addRows = new Button("Add Rows");
        addRows.setOnAction(event -> addRows());
        addRows.setMaxWidth(Double.MAX_VALUE);

        Button setRows = new Button("Set Rows");
        setRows.setOnAction(event -> setRows());
        setRows.setMaxWidth(Double.MAX_VALUE);

        Button clearRows = new Button("Clear Rows");
        clearRows.setOnAction(event -> clearRows());
        clearRows.setMaxWidth(Double.MAX_VALUE);

        Button removeFirstTen = new Button("Remove First 10 Rows");
        removeFirstTen.setOnAction(event -> removeFirstTen());
        removeFirstTen.setMaxWidth(Double.MAX_VALUE);

        Button loadTest = new Button("Load Test 150K Rows");
        loadTest.setOnAction(event -> loadTest());
        loadTest.setMaxWidth(Double.MAX_VALUE);

        box.getChildren().addAll(newModel, addLayer, addSingleRow, addRows, setRows, removeFirstTen, clearRows, loadTest);

        return box;
    }
    
    private void addLayer() {
        layerCounter++;
        Layer layer = new Layer("Layer " + layerCounter);
        gantt.getLayers().add(layer);
    }

    private int rowCounter = 0;

    private void clearRows() {
        gantt.getRows().clear();
    }

    private void setNewList() {
        gantt.setRows(FXCollections.observableArrayList());
    }

    private void removeFirstTen() {
        int counter = 0;
        List<DemoRow> rows = new ArrayList<>();
        for (DemoRow row : gantt.getRows()) {
            rows.add(row);
            counter++;
            if (counter == 10) {
                break;
            }
        }
        gantt.getRows().removeAll(rows);
    }

    private void addRows() {
        List<DemoRow> rows = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            rows.add(new DemoRow("Row " + rowCounter++));
        }
        gantt.getRows().addAll(rows);
    }

    private void setRows() {
        List<DemoRow> rows = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            rows.add(new DemoRow("Row " + rowCounter++));
        }
        gantt.getRows().setAll(rows);
    }

    private void addSingleRow() {
        DemoRow row = new DemoRow("Row " + rowCounter++);
        gantt.getRows().add(row);
    }

    private void loadTest() {
        List<DemoRow> topLevelRows = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            DemoRow topLevelRow = new DemoRow("Top level row " + i);
            topLevelRows.add(topLevelRow);

            DemoActivity activity = new DemoActivity();
            activity.setStartTime(ZonedDateTime.now().plusDays(3).toInstant());
            activity.setEndTime(activity.getStartTime().plus(Duration.ofDays(7)));

            topLevelRow.addActivity(layer, activity);

            for (int j = 0; j < 13; j++) {
                DemoRow child = new DemoRow("child " + i + "/" + j);
                topLevelRow.getChildren().add(child);
            }
        }

        gantt.getRows().setAll(topLevelRows);
    }
}
```
