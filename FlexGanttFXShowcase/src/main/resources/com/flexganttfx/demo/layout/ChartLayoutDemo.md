This demo focuses on the `ChartLayout` with high-low style activities. Instead of standard Gantt bars, it uses chart-oriented rendering to combine time-based positioning with numeric values inside the same timeline.

```java
public class ChartLayoutDemo extends GanttChartDemoBase {

    private final DateTimeFormatter formatter = ofPattern("yyyy-MM-dd");
    
    @Override
    protected GanttChart<?> createGanttChart() {
        Properties props = new Properties();
        try {
            props.load(ChartLayoutDemo.class.getResourceAsStream("text.properties"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Symbol root = new Symbol("Root");
        GanttChart<Symbol> gc = new GanttChart<>(root);

        Layer layer = new Layer("Daily Trading");
        gc.getLayers().add(layer);

        String[] ticker = new String[]{"aapl", "amzn", "ebay", "intc", "msft", "orcl", "yhoo"};

        int DAYS = 1000;

        LocalDate earliestDate = null;

        for (String symbol : ticker) {
            Symbol symbolRow = new Symbol(symbol.toUpperCase());
            root.getChildren().add(symbolRow);

            String fileName = "stocks-" + symbol + ".csv";
            try (CSVReader reader = new CSVReader(new InputStreamReader(ChartLayoutDemo.class.getResourceAsStream(fileName)), ',', '"', 1)) {

                List<String[]> entries = reader.readAll();

                double min = Double.MAX_VALUE;
                double max = 0;

                double minVolume = Double.MAX_VALUE;
                double maxVolume = 0;

                int counter = 0;

                for (String[] csvLine : entries) {
                    LocalDate date = LocalDate.from(formatter.parse(csvLine[0]));

                    earliestDate = date;

                    Double open = Double.parseDouble(csvLine[1]);
                    Double high = Double.parseDouble(csvLine[2]);
                    Double low = Double.parseDouble(csvLine[3]);
                    Double close = Double.parseDouble(csvLine[4]);

                    Integer volume = Integer.parseInt(csvLine[5]);

                    DailyTrading dailyTrading = new DailyTrading(date, open, low, high, close, volume);

                    symbolRow.getTrades().add(dailyTrading);
                    symbolRow.addActivity(layer, dailyTrading);
                    symbolRow.addActivity(layer, new Volume(date, volume));

                    /*
                     * The min and max values are based on the last 500 days.
                     * Otherwise the scale will be too large and all high / low
                     * entries will be really tiny.
                     */
                    counter++;

                    min = Math.min(min, low);
                    max = Math.max(max, high);

                    minVolume = Math.min(minVolume, volume);
                    maxVolume = Math.max(maxVolume, volume);

                    if (counter > DAYS) {
                        break;
                    }
                }

                ChartLayout highLowChartLayout = new ChartLayout();
                highLowChartLayout.setPadding(10);
                highLowChartLayout.setMinValue(min);
                highLowChartLayout.setMaxValue(max);
                highLowChartLayout.getMajorTicks().add(min);
                highLowChartLayout.getMajorTicks().add(min + ((max - min) / 2));
                highLowChartLayout.getMajorTicks().add(max);

                ChartLayout volumeChartLayout = new ChartLayout();
                volumeChartLayout.setPadding(10);
                volumeChartLayout.setMinValue(maxVolume);
                volumeChartLayout.setMaxValue(maxVolume);
                volumeChartLayout.getMajorTicks().add(maxVolume);

                symbolRow.setHighLowLayout(highLowChartLayout);
                symbolRow.setVolumeLayout(volumeChartLayout);
                symbolRow.setMaxHeight(2000);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        root.setExpanded(true);

        gc.setShowTreeTable(false);
        gc.getCalendars().clear();
        gc.getTreeTable().setShowRoot(false);
        gc.getTimeline().showTime(Instant.from(ZonedDateTime.of(earliestDate, LocalTime.MIN, ZoneId.systemDefault())));
        gc.getTimeline().showTemporalUnit(ChronoUnit.MONTHS, 80);
        gc.getGraphics().setShowVerticalCursor(true);
        gc.getGraphics().setShowHorizontalCursor(true);
        gc.getGraphics().setShowRowHeaders(true);
        gc.getGraphics().setRowHeadersWidth(100);
        gc.getGraphics().setActivityRenderer(DailyTrading.class, ChartLayout.class, new DailyTradingRenderer(gc.getGraphics()));
        gc.getGraphics().setRowEditorFactory(param -> {
            GridPane pane = new GridPane();
            pane.setPrefHeight(450);
            pane.setGridLinesVisible(false);
            pane.setStyle("-fx-background-color: white; -fx-border-color: transparent transparent lightgray transparent; -fx-border-width: .5;");

            String fileName = null;

            switch (param.getRow().getName()) {
                case "AAPL":
                    fileName = "aapl.png";
                    break;
                case "ORCL":
                    fileName = "orcl.png";
                    break;
                case "MSFT":
                    fileName = "msft.png";
                    break;
                case "EBAY":
                    fileName = "ebay.jpg";
                    break;
                case "INTC":
                    fileName = "intc.jpeg";
                    break;
                case "AMZN":
                    fileName = "amzn.jpg";
                    break;
                case "YHOO":
                    fileName = "yhoo.jpeg";
                    break;
            }

            ImageView logo = new ImageView(ChartLayoutDemo.class.getResource(fileName).toExternalForm());
            logo.setSmooth(true);
            logo.setFitHeight(64);

            if (param.getRow().getName().equals("ORCL")) {
                logo.setFitWidth(250);
            }

            logo.setPreserveRatio(true);

            Label companyLabel = new Label("", logo);
            companyLabel.setFont(Font.font(24));
            companyLabel.setPrefWidth(300);
            GridPane.setMargin(companyLabel, new Insets(10));
            pane.add(companyLabel, 0, 0);

            Text text = new Text(props.getProperty(param.getRow().getName().toLowerCase()));
            text.setWrappingWidth(300);
            GridPane.setMargin(text, new Insets(10));
            pane.add(text, 0, 1);

            TableView<DailyTrading> table = new TableView<>();

            TableColumn<DailyTrading, LocalDate> dateColumn = new TableColumn<>("Date");
            TableColumn<DailyTrading, Double> openColumn = new TableColumn<>("Open");
            TableColumn<DailyTrading, Double> lowColumn = new TableColumn<>("Low");
            TableColumn<DailyTrading, Double> highColumn = new TableColumn<>("High");
            TableColumn<DailyTrading, Double> closeColumn = new TableColumn<>("Close");
            TableColumn<DailyTrading, Integer> volumeColumn = new TableColumn<>("Volume");
            table.getColumns().addAll(dateColumn, openColumn, lowColumn, highColumn, closeColumn, volumeColumn);

            dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            openColumn.setCellValueFactory(new PropertyValueFactory<>("stockOpen"));
            lowColumn.setCellValueFactory(new PropertyValueFactory<>("stockLow"));
            highColumn.setCellValueFactory(new PropertyValueFactory<>("stockHigh"));
            closeColumn.setCellValueFactory(new PropertyValueFactory<>("stockClose"));
            volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));

            table.getItems().addAll(param.getRow().getTrades());
            GridPane.setHgrow(table, Priority.ALWAYS);
            GridPane.setVgrow(table, Priority.ALWAYS);
            GridPane.setMargin(table, new Insets(10));
            GridPane.setRowSpan(table, 3);

            pane.add(table, 1, 0);

            Button closeButton = new Button("Close Details");
            closeButton.setStyle("-fx-background-color: #ecebe9, rgba(0,0,0,0.05),linear-gradient(#dcca8a, #c7a740),linear-gradient(#f9f2d6 0%, #f4e5bc 20%, #e6c75d 80%, #e2c045 100%),linear-gradient(#f6ebbe, #e6c34d);-fx-background-insets: 0,9 9 8 9,9,10,11;-fx-background-radius: 50;-fx-padding: 15 30 15 30;-fx-font-family: Helvetica;-fx-font-size: 18px;-fx-text-fill: #311c09;-fx-effect: innershadow( three-pass-box , rgba(0,0,0,0.1) , 2, 0.0 , 0 , 1);");
            closeButton.setOnAction(evt -> param.stopEditing());
            pane.add(closeButton, 0, 2);
            GridPane.setHalignment(closeButton, HPos.CENTER);

            return pane;
        });

        gc.getGraphics().setRowControlsFactory(param -> new RowControls<>(param.getGraphics(), param.getRow()));

        TreeTableColumn<Symbol, String> nameColumn = new TreeTableColumn<>("Name");
        nameColumn.setPrefWidth(150);
        nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        nameColumn.setCellFactory(column -> new SymbolTreeTableCell());

        gc.getTreeTable().getColumns().clear();
        gc.getTreeTable().getColumns().add(nameColumn);

        return gc;
    }
    
    @Override
    public Node getControlPanel() {
        ComboBox<RowEditingMode> box = new ComboBox<>();
        box.getItems().addAll(RowEditingMode.values());
        box.setValue(getGanttChart().getGraphics().getRowEditingMode());
        box.setConverter(new StringConverter<>() {
            @Override
            public String toString(RowEditingMode object) {
                switch (object) {
                    case NONE:
                        return "Disabled";
                    case SINGLE_ROW:
                        return "Single Row";
                    case MULTIPLE_ROWS:
                        return "Multiple Rows";
                }
                return "";
            }

            @Override
            public RowEditingMode fromString(String string) {
                return null;
            }
        });
        Bindings.bindBidirectional(box.valueProperty(), getGanttChart().getGraphics().rowEditingModeProperty());
        return box;
    }
    
    class SymbolTreeTableCell extends TreeTableCell<Symbol, String> {
        public SymbolTreeTableCell() {
            setStyle("-fx-font-size: 32;");
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(item);
            setTextAlignment(TextAlignment.CENTER);
            setAlignment(Pos.CENTER);
        }
    }

    class Symbol extends Row<Symbol, Symbol, MutableActivity> {
        private ChartLayout highLowLayout;
        private ChartLayout volumeLayout;
        private final List<DailyTrading> trades;

        public Symbol(String symbol) {
            super(symbol);

            trades = new ArrayList<>();

            setLinesManager(new SymbolLinesManager(this));
            setLineCount(2);
            setHeight(300);
        }

        public List<DailyTrading> getTrades() {
            return trades;
        }

        public ChartLayout getHighLowLayout() {
            return highLowLayout;
        }

        public void setHighLowLayout(ChartLayout highLowLayout) {
            this.highLowLayout = highLowLayout;
        }

        public ChartLayout getVolumeLayout() {
            return volumeLayout;
        }

        public void setVolumeLayout(ChartLayout volumeLayout) {
            this.volumeLayout = volumeLayout;
        }
    }

    class SymbolLinesManager implements LinesManager<MutableActivity> {

        private final Symbol symbol;

        public SymbolLinesManager(Symbol symbol) {
            this.symbol = symbol;
        }

        @Override
        public int getLineIndex(MutableActivity activity) {
            if (activity instanceof DailyTrading) {
                return 0;
            }

            return 1;
        }

        @Override
        public double getLineLocation(int lineIndex, double rowHeight) {
            switch (lineIndex) {
                case 0:
                    return 0;
                case 1:
                    return rowHeight - getLineHeight(lineIndex, rowHeight);
            }

            return 0;
        }

        @Override
        public double getLineHeight(int lineIndex, double rowHeight) {
            switch (lineIndex) {
                case 0:
                    return rowHeight - getLineHeight(1, rowHeight);
                case 1:
                    // 25% for the volume chart
                    return rowHeight * .25;
            }

            return 0;
        }

        @Override
        public Layout getLineLayout(int lineIndex) {
            if (lineIndex == 0) {
                return symbol.getHighLowLayout();
            }

            return symbol.getVolumeLayout();
        }

    }

    class Volume extends MutableChartActivityBase<Object> {
        public Volume(LocalDate date, int volume) {
            setChartValue(volume);
            setStartTime(Instant.from(ZonedDateTime.of(date, LocalTime.MIN, ZoneId.systemDefault())));
            setEndTime(Instant.from(ZonedDateTime.of(date, LocalTime.MAX, ZoneId.systemDefault())));
        }
    }

    public class DailyTrading extends MutableHighLowChartActivityBase<Object> {
        private final LocalDate date;
        private final double stockOpen;
        private final double stockClose;
        private final double stockLow;
        private final double stockHigh;
        private final int volume;

        public DailyTrading(LocalDate date, double open, double low,
                            double high, double close, int volume) {
            super();

            this.date = date;

            setStartTime(Instant.from(ZonedDateTime.of(date, LocalTime.MIN, ZoneId.systemDefault())));
            setEndTime(Instant.from(ZonedDateTime.of(date, LocalTime.MAX, ZoneId.systemDefault())));

            setLow(low);
            setHigh(high);

            stockLow = low;
            stockHigh = high;
            stockOpen = open;
            stockClose = close;
            this.volume = volume;
        }

        public LocalDate getDate() {
            return date;
        }

        public double getStockLow() {
            return stockLow;
        }

        public double getStockHigh() {
            return stockHigh;
        }

        public double getStockOpen() {
            return stockOpen;
        }

        public double getStockClose() {
            return stockClose;
        }

        public int getVolume() {
            return volume;
        }
    }

    class DailyTradingRenderer extends ActivityRenderer<DailyTrading> {

        public DailyTradingRenderer(GraphicsBase<?> view) {
            super(view, "Daily Trading");
            setCornersRounded(false);
        }

        @Override
        protected ActivityBounds drawActivity(
                ActivityRef<DailyTrading> activityRef, Position position,
                GraphicsContext gc, double x, double y, double w, double h,
                boolean selected, boolean hover, boolean highlighted,
                boolean pressed) {

            double xx = x + w / 2;
            double ww = Math.min(w, 6);

            gc.setFill(Color.BLACK);
            gc.fillRect(xx - 1, y, 2, h);

            DailyTrading trading = activityRef.getActivity();

            double yy1 = calculateLocation(trading.getStockOpen(), trading, h);
            double yy2 = calculateLocation(trading.getStockClose(), trading, h);

            double y1 = y + Math.min(yy1, yy2);
            double y2 = y + Math.max(yy1, yy2);

            if (selected || hover || pressed || highlighted) {
                gc.setFill(getFill(selected, hover, highlighted, pressed));
            } else {
                if (trading.getStockClose() < trading.getStockOpen()) {
                    gc.setFill(Color.RED);
                } else {
                    gc.setFill(Color.GREEN);
                }
            }

            gc.fillRect(xx - ww / 2, y1, ww, y2 - y1);

            return new ActivityBounds(activityRef, xx - 3, y, ww, h);
        }

        private double calculateLocation(double value, DailyTrading trading,
                                         double h) {

            double ppv = h / (trading.getStockHigh() - trading.getStockLow());

            return h - (value - trading.getStockLow()) * ppv;
        }
    }
}
```
