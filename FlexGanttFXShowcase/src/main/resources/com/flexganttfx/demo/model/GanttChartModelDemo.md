This demo focuses on model-side changes and how the standard `GanttChart` reacts to them. It is useful for exploring how row, activity, and structural updates propagate through the control.

```java
public class GanttChartModelDemo extends GanttChartDemoBase {

    private DemoRow root;
    private int rootCounter;
    private int layerCounter;
    private GanttChart<DemoRow> gantt;
    private final Layer layer = new Layer("Default");
    
    public GanttChartModelDemo() {
        root = new DemoRow("Initial Root");
        root.setExpanded(true);
    }

    class DemoRow extends Row<DemoRow, DemoRow, Activity> {
        public DemoRow(String name) {
            super(name);
        }
    }
    
    @Override
    protected GanttChart<?> createGanttChart() {
        gantt = new GanttChart<>(root);
        gantt.getLayers().add(layer);
        gantt.getTreeTable().getSelectionModel().setSelectionMode(MULTIPLE);
        return gantt;
    }

    @Override
    public Node getControlPanel() {
        HBox box = new HBox();
        box.setSpacing(10);

        Button newModel = new Button("Set New Root");
        newModel.setOnAction(event -> setNewRoot());
        newModel.setMaxWidth(Double.MAX_VALUE);
        newModel.setMinWidth(Region.USE_PREF_SIZE);

        Button addLayer = new Button("Add New Layer");
        addLayer.setOnAction(event -> addLayer());
        addLayer.setMaxWidth(Double.MAX_VALUE);
        addLayer.setMinWidth(Region.USE_PREF_SIZE);

        ToggleButton showRoot = new ToggleButton("Show Root");
        showRoot.setMaxWidth(Double.MAX_VALUE);
        showRoot.setMinWidth(Region.USE_PREF_SIZE);
        Bindings.bindBidirectional(showRoot.selectedProperty(), gantt.getTreeTable().showRootProperty());

        Button addSingleRow = new Button("Add Single Row");
        addSingleRow.setMinWidth(Region.USE_PREF_SIZE);
        addSingleRow.setOnAction(event -> addSingleRow());
        addSingleRow.setMaxWidth(Double.MAX_VALUE);

        Button addRows = new Button("Add Rows");
        addRows.setMinWidth(Region.USE_PREF_SIZE);
        addRows.setOnAction(event -> addRows());
        addRows.setMaxWidth(Double.MAX_VALUE);

        Button setRows = new Button("Set Rows");
        setRows.setMinWidth(Region.USE_PREF_SIZE);
        setRows.setOnAction(event -> setRows());
        setRows.setMaxWidth(Double.MAX_VALUE);

        Button clearRows = new Button("Clear Rows");
        clearRows.setMinWidth(Region.USE_PREF_SIZE);
        clearRows.setOnAction(event -> clearRows());
        clearRows.setMaxWidth(Double.MAX_VALUE);

        Button removeRows = new Button("Remove Selected Rows");
        removeRows.setMinWidth(Region.USE_PREF_SIZE);
        removeRows.setOnAction(event -> removeRows());
        removeRows.setMaxWidth(Double.MAX_VALUE);

        Button removeFirstTen = new Button("Remove First 10 Rows");
        removeFirstTen.setMinWidth(Region.USE_PREF_SIZE);
        removeFirstTen.setOnAction(event -> removeFirstTen());
        removeFirstTen.setMaxWidth(Double.MAX_VALUE);

        Button loadTest = new Button("Load Test 150K Rows");
        loadTest.setMinWidth(Region.USE_PREF_SIZE);
        loadTest.setOnAction(event -> loadTest());
        loadTest.setMaxWidth(Double.MAX_VALUE);

        box.getChildren().addAll(newModel, addLayer, showRoot, addSingleRow,
                addRows, setRows, removeRows, removeFirstTen, clearRows, loadTest);

        return box;
    }
    
    private void setNewRoot() {
        rootCounter++;
        root = new DemoRow("Root #" + rootCounter);
        root.setExpanded(true);
        gantt.setRoot(root);
    }

    private void addLayer() {
        layerCounter++;
        Layer layer = new Layer("Layer " + layerCounter);
        gantt.getLayers().add(layer);
    }

    private int rowCounter = 0;

    private void clearRows() {
        gantt.getRoot().getChildren().clear();
    }

    private void removeRows() {
        List<DemoRow> rows = new ArrayList<>();
        for (TreeItem<DemoRow> item : gantt.getTreeTable().getSelectionModel()
                .getSelectedItems()) {
            rows.add(item.getValue());
        }
        gantt.getRoot().getChildren().removeAll(rows);
    }

    private void removeFirstTen() {
        int counter = 0;
        List<DemoRow> rows = new ArrayList<>();
        for (DemoRow row : gantt.getRoot().getChildren()) {
            rows.add(row);
            counter++;
            if (counter == 10) {
                break;
            }
        }
        gantt.getRoot().getChildren().removeAll(rows);
    }

    private void addRows() {
        List<DemoRow> rows = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            rows.add(new DemoRow("Row " + rowCounter++));
        }
        gantt.getRoot().getChildren().addAll(rows);
    }

    private void setRows() {
        List<DemoRow> rows = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            rows.add(new DemoRow("Row " + rowCounter++));
        }
        gantt.getRoot().getChildren().setAll(rows);
    }

    private void addSingleRow() {
        DemoRow row = new DemoRow("Row " + rowCounter++);
        gantt.getRoot().getChildren().add(row);
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private void loadTest() {
        Task<Object> task = new Task<>() {
            @Override
            protected Object call() throws Exception {
                List<DemoRow> topLevelRows = new ArrayList<>();
                for (int i = 0; i < 10000; i++) {
                    updateMessage("Creating row " + (i + 1));
                    updateProgress(i, 9999);
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

                gantt.getRoot().getChildren().setAll(topLevelRows);

                return null;
            }
        };

        ProgressDialog progressDialog = new ProgressDialog(task);
        progressDialog.initOwner(gantt.getScene().getWindow());
        progressDialog.setTitle("Loading Test");
        progressDialog.setHeaderText("Loading 150K rows.");
        progressDialog.show();

        executor.execute(task);
    }
}
```
