package com.flexganttfx.covid;

import com.flexganttfx.core.StringUtils;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.activity.MutableChartActivityBase;
import com.flexganttfx.model.layout.ChartLayout;
import com.flexganttfx.view.GanttChartLite;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.ListViewGraphics;
import com.flexganttfx.view.graphics.ScaleRowHeader;
import com.flexganttfx.view.graphics.renderer.ChartActivityRenderer;
import com.jpro.webapi.WebAPI;
import fr.brouillard.oss.cssfx.CSSFX;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.controlsfx.control.StatusBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Reader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.FileTime;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;

public class CovidUI {

    private static final Layer TOTAL_CASES_LAYER = new Layer("Total Cases");
    private static final Layer TOTAL_CASES_PER_MILLION_LAYER = new Layer("Total Cases Per Million");

    private static final Layer NEW_CASES_LAYER = new Layer("New Cases");
    private static final Layer NEW_CASES_PER_MILLION_LAYER = new Layer("New Cases Per Million");

    private static final Layer TOTAL_DEATHS_LAYER = new Layer("Total Deaths");
    private static final Layer TOTAL_DEATHS_PER_MILLION_LAYER = new Layer("Total Deaths Per Million");

    private static final Layer NEW_DEATHS_LAYER = new Layer("New Deaths");
    private static final Layer NEW_DEATHS_PER_MILLION_LAYER = new Layer("New Deaths Per Million");

    private static final Layer NEW_TESTS_LAYER = new Layer("New Tests");
    private static final Layer NEW_TESTS_PER_THOUSAND_LAYER = new Layer("New Tests Per Thousand");

    private static final Layer TOTAL_TESTS_LAYER = new Layer("Total Tests");
    private static final Layer TOTAL_TESTS_PER_THOUSAND_LAYER = new Layer("Total Tests Per Million");

    private final Map<String, LocationRow> rowMap = new HashMap<>();

    private final GanttChartLite<LocationRow> ganttChart = new GanttChartLite<>();

    private final File file = new File(System.getProperty("user.home"), "covid.csv");

    public CovidUI(Stage stage) throws Exception {

        boolean success = true;
        boolean newFile = false;

        if (!file.exists()) {
            success = file.createNewFile();
            newFile = true;
        }

        if (success) {
            final FileTime lastModifiedTime = Files.getLastModifiedTime(file.toPath(), LinkOption.NOFOLLOW_LINKS);
            final ZonedDateTime fileTimeStamp = ZonedDateTime.ofInstant(lastModifiedTime.toInstant(), ZoneId.systemDefault());
            if (newFile || fileTimeStamp.toLocalDate().isBefore(ZonedDateTime.now().toLocalDate())) {
                downloadFile(file);
            } else {
                System.out.println("data file is up-to-date");
            }

            final ListViewGraphics<LocationRow> graphics = ganttChart.getGraphics();
            getSelectedLocations().addListener((Observable it) -> {
                graphics.requestLayout();
                graphics.redraw();
            });
            graphics.setActivityEditingCallback(Cases.class, param -> false);
            graphics.setCanvasBuffer(0);
            graphics.getForegroundSystemLayers().add(new LocationLayer(graphics));
            graphics.setShowRowHeaders(true);
            graphics.setShowCalendarLayer(false);
            graphics.setRowHeadersWidth(90);
            graphics.setRowHeaderFactory(g -> {
                ScaleRowHeader header = new ScaleRowHeader<>(g);
                view.addListener(it -> header.draw());
                comparisonMode.addListener(it -> header.draw());
                return header;
            });

            // cases
            graphics.setActivityRenderer(NewCases.class, ChartLayout.class, new CasesRenderer(graphics, "New Cases", Color.rgb(128, 179, 27)));
            graphics.setActivityRenderer(NewCasesPerMillion.class, ChartLayout.class, new CasesRenderer(graphics, "New Cases Per Million", Color.rgb(128, 179, 27)));
            graphics.setActivityRenderer(TotalCases.class, ChartLayout.class, new CasesRenderer(graphics, "Total Cases", Color.rgb(128, 179, 27)));
            graphics.setActivityRenderer(TotalCasesPerMillion.class, ChartLayout.class, new CasesRenderer(graphics, "Total Cases Per Million", Color.rgb(128, 179, 27)));

            // deaths
            graphics.setActivityRenderer(NewDeaths.class, ChartLayout.class, new CasesRenderer(graphics, "New Deaths", Color.rgb(20, 90, 160)));
            graphics.setActivityRenderer(NewDeathsPerMillion.class, ChartLayout.class, new CasesRenderer(graphics, "New Deaths Per Million", Color.rgb(20, 90, 160)));
            graphics.setActivityRenderer(TotalDeaths.class, ChartLayout.class, new CasesRenderer(graphics, "Total Deaths", Color.rgb(20, 90, 160)));
            graphics.setActivityRenderer(TotalDeathsPerMillion.class, ChartLayout.class, new CasesRenderer(graphics, "Total Deaths Per Million", Color.rgb(20, 90, 160)));

            // tests
            graphics.setActivityRenderer(NewTests.class, ChartLayout.class, new CasesRenderer(graphics, "New Tests", Color.BROWN));
            graphics.setActivityRenderer(NewTestsPerThousand.class, ChartLayout.class, new CasesRenderer(graphics, "New Deaths Per Thousand", Color.BROWN));
            graphics.setActivityRenderer(TotalTests.class, ChartLayout.class, new CasesRenderer(graphics, "Total Tests", Color.BROWN.darker()));
            graphics.setActivityRenderer(TotalTestsPerThousand.class, ChartLayout.class, new CasesRenderer(graphics, "Total Tests Per Thousand", Color.BROWN.darker()));

            graphics.setShowNowLineLayer(false);
            graphics.setShowVerticalCursor(true);
            graphics.setRowControlsFactory(param -> {
                Button remove = new Button("Remove");
                StackPane.setMargin(remove, new Insets(20, 0, 0, 0));
                StackPane.setAlignment(remove, Pos.TOP_RIGHT);
                remove.setOnAction(evt -> getSelectedLocations().remove(param.getRow()));
                StackPane stackPane = new StackPane(remove);
                stackPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
                stackPane.getStyleClass().add("controls-panel");
                return stackPane;
            });

            selectedLocations.addListener((Observable it) -> ganttChart.getRows().setAll(getSelectedLocations()));

            CovidToolBar<LocationRow> toolBar = new CovidToolBar<>(this, ganttChart);

            view.addListener(it -> {

                final View view = getView();
                rowMap.values().forEach(row -> {
                    if (isComparisonMode()) {
                        row.updateMaxValueGloballyAndTickLine(view);
                    } else {
                        row.updateMaxValueAndTickLine(view);
                    }
                });

                switch (view) {
                    case TOTAL_CASES:
                        ganttChart.getLayers().setAll(TOTAL_CASES_LAYER);
                        break;
                    case NEW_CASES:
                        ganttChart.getLayers().setAll(NEW_CASES_LAYER);
                        break;
                    case TOTAL_DEATHS:
                        ganttChart.getLayers().setAll(TOTAL_DEATHS_LAYER);
                        break;
                    case NEW_DEATHS:
                        ganttChart.getLayers().setAll(NEW_DEATHS_LAYER);
                        break;
                    case TOTAL_CASES_PER_MILLIONS:
                        ganttChart.getLayers().setAll(TOTAL_CASES_PER_MILLION_LAYER);
                        break;
                    case NEW_CASES_PER_MILLIONS:
                        ganttChart.getLayers().setAll(NEW_CASES_PER_MILLION_LAYER);
                        break;
                    case TOTAL_DEATHS_PER_MILLIONS:
                        ganttChart.getLayers().setAll(TOTAL_DEATHS_PER_MILLION_LAYER);
                        break;
                    case NEW_DEATHS_PER_MILLIONS:
                        ganttChart.getLayers().setAll(NEW_DEATHS_PER_MILLION_LAYER);
                        break;
                    case NEW_TESTS:
                        ganttChart.getLayers().setAll(NEW_TESTS_LAYER);
                        break;
                    case NEW_TESTS_PER_THOUSAND:
                        ganttChart.getLayers().setAll(NEW_TESTS_PER_THOUSAND_LAYER);
                        break;
                    case TOTAL_TESTS:
                        ganttChart.getLayers().setAll(TOTAL_TESTS_LAYER);
                        break;
                    case TOTAL_TESTS_PER_THOUSAND:
                        ganttChart.getLayers().setAll(TOTAL_TESTS_PER_THOUSAND_LAYER);
                        break;
                }

                graphics.redraw();
            });

            SettingsView settingsView = new SettingsView(this);

            graphics.hoverActivityProperty().addListener(it -> {
                final ActivityRef<?> hoverActivity = graphics.getHoverActivity();
                if (hoverActivity != null) {
                    final Activity activity = hoverActivity.getActivity();
                    if (activity instanceof Cases) {
                        settingsView.setRecord(((Cases) activity).getRecord());
                    }
                }
            });

            MenuBar menuBar = new MenuBar();
            Menu mainMenu = new Menu("Explorer");
            menuBar.getMenus().add(mainMenu);

            MenuItem about = new MenuItem("About ...");
            about.setOnAction(evt -> setShowAbout(true));
            mainMenu.getItems().add(about);

            if (!WebAPI.isBrowser()) {
                MenuItem exit = new MenuItem("Quit");
                exit.setOnAction(evt -> Platform.exit());
                mainMenu.getItems().add(exit);
            }

            VBox headerBox = new VBox(menuBar, toolBar);
            headerBox.setFillWidth(true);

            GlassPane glassPane = new GlassPane();
            glassPane.hideProperty().bind(showAboutProperty().not());
            glassPane.setOnMouseClicked(evt -> setShowAbout(false));

            AboutPane aboutPane = new AboutPane(this);
            aboutPane.visibleProperty().bind(showAboutProperty());
            aboutPane.managedProperty().bind(showAboutProperty());

            StatusBar statusBar = new StatusBar();
            statusBar.setText("Dataset file was updated on " + DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).format(fileTimeStamp));

            ScrollPane scrollPane = new ScrollPane(settingsView);
            scrollPane.setPannable(true);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
            scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);

            BorderPane borderPane = new BorderPane();
            borderPane.setTop(headerBox);
            borderPane.setCenter(ganttChart);
            borderPane.setRight(scrollPane);
            borderPane.setBottom(statusBar);

            StackPane stackPane = new StackPane(borderPane, glassPane, aboutPane);

            Scene scene = new Scene(stackPane);
            scene.getStylesheets().add(CovidUI.class.getResource("styles.css").toExternalForm());

            if (!WebAPI.isBrowser()) {
                CSSFX.start(scene);
            }

            stage.setWidth(1200);
            stage.setHeight(1000);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setTitle("Covid-19 Data");
            stage.show();

            selectedLocations.addListener((Observable it) -> updateGlobalMaxValues());

            comparisonMode.addListener(it -> {
                if (isComparisonMode()) {
                    selectedLocations.forEach(row -> row.updateMaxValueGloballyAndTickLine(getView()));
                } else {
                    selectedLocations.forEach(row -> row.updateMaxValueAndTickLine(getView()));
                }
                graphics.redraw();
            });

            Platform.runLater(() -> {
                try {
                    readFile(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setView(View.NEW_CASES_PER_MILLIONS);
            });

        } else {
            System.out.println("error when trying to create csv file in user's home director");
        }
    }

    private void updateGlobalMaxValues() {
        Map<View, Double> maximumValuesMap = new HashMap<>();
        for (View view : View.values()) {
            for (LocationRow row : selectedLocations) {
                maximumValuesMap.put(view, Math.max(row.getMax(view), maximumValuesMap.computeIfAbsent(view, key -> row.getMax(view))));
            }
        }

        for (LocationRow row : selectedLocations) {
            for (View view : View.values()) {
                row.setMaxGlobally(view, maximumValuesMap.get(view));
            }
        }

        for (LocationRow row : selectedLocations) {
            if (isComparisonMode()) {
                row.updateMaxValueGloballyAndTickLine(getView());
            }
        }
    }

    private final BooleanProperty comparisonMode = new SimpleBooleanProperty(this, "comparisonMode", false);

    public boolean isComparisonMode() {
        return comparisonMode.get();
    }

    public BooleanProperty comparisonModeProperty() {
        return comparisonMode;
    }

    public void setComparisonMode(boolean comparisonMode) {
        this.comparisonMode.set(comparisonMode);
    }

    private final BooleanProperty showAbout = new SimpleBooleanProperty(this, "showAbout", true);

    public boolean isShowAbout() {
        return showAbout.get();
    }

    public BooleanProperty showAboutProperty() {
        return showAbout;
    }

    public void setShowAbout(boolean showAbout) {
        this.showAbout.set(showAbout);
    }

    private final ListProperty<LocationRow> locations = new SimpleListProperty<>(this, "locationRows", FXCollections.observableArrayList());

    public ObservableList<LocationRow> getLocations() {
        return locations.get();
    }

    public ListProperty<LocationRow> locationsProperty() {
        return locations;
    }

    public void setLocations(ObservableList<LocationRow> locations) {
        this.locations.set(locations);
    }

    private final ObservableList<LocationRow> selectedLocations = FXCollections.observableArrayList();

    public ObservableList<LocationRow> getSelectedLocations() {
        return selectedLocations;
    }

    private final ObjectProperty<View> view = new SimpleObjectProperty<>(this, "view");

    public View getView() {
        return view.get();
    }

    public ObjectProperty<View> viewProperty() {
        return view;
    }

    public void setView(View view) {
        this.view.set(view);
    }

    private void downloadFile(File file) throws Exception {
        System.out.println("downloading data file to " + file.getAbsolutePath());
        URL url = new URL("https://covid.ourworldindata.org/data/owid-covid-data.csv");
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }

        System.out.println("file size: " + NumberFormat.getIntegerInstance().format(file.length()) + " bytes");
    }

    private void readFile(File file) throws Exception {
        System.out.println("reading data file from " + file.getAbsolutePath());

        Reader in = new FileReader(file);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);

        /*
         * iso_code,continent,
         * location,
         * date,total_cases,
         * new_cases,total_deaths,new_deaths,
         *
         * total_cases_per_million,
         * new_cases_per_million,
         * total_deaths_per_million,
         * new_deaths_per_million,
         *
         * total_tests,
         * new_tests,
         * total_tests_per_thousand,
         * new_tests_per_thousand,
         * new_tests_smoothed,
         * new_tests_smoothed_per_thousand,
         * tests_units,
         *
         * stringency_index,
         * population,
         * population_density,
         * median_age,
         * aged_65_older,
         * aged_70_older,
         * gdp_per_capita,
         * extreme_poverty,
         * cvd_death_rate,
         * diabetes_prevalence,
         * female_smokers,
         * male_smokers,
         * handwashing_facilities,
         * hospital_beds_per_thousand,
         * life_expectancy
         */

        rowMap.clear();

        LocalDate earliestDate = null;
        LocalDate latestDate = null;

        for (CSVRecord record : records) {
            String location = record.get("location");

            if (location.equals("World")) {
                continue;
            }

            /*
             * Use hashmap to ensure we are only creating one row per location.
             */
            LocationRow row = rowMap.computeIfAbsent(location, key -> {
                LocationRow r = new LocationRow(record.get("location"));
                r.setIso3CountryCode(record.get("iso_code"));
                r.updateMaxValueAndTickLine(getView());
                locations.add(r);

                if ((r.getName().equals("United States") || r.getName().equals("Germany") || r.getName().equals("Switzerland")) && !getSelectedLocations().contains(r)) {
                    getSelectedLocations().add(r);
                }

                return r;
            });

            TotalCases totalCases = new TotalCases(record);
            TotalDeaths totalDeaths = new TotalDeaths(record);
            TotalTests totalTests = new TotalTests(record);

            NewCases newCases = new NewCases(record);
            NewDeaths newDeaths = new NewDeaths(record);
            NewTests newTests = new NewTests(record);

            TotalCasesPerMillion totalCasesPerMillion = new TotalCasesPerMillion(record);
            TotalDeathsPerMillion totalDeathsPerMillion = new TotalDeathsPerMillion(record);
            TotalTestsPerThousand totalTestsPerThousand = new TotalTestsPerThousand(record);

            NewCasesPerMillion newCasesPerMillion = new NewCasesPerMillion(record);
            NewDeathsPerMillion newDeathsPerMillion = new NewDeathsPerMillion(record);
            NewTestsPerThousand newTestsPerThousand = new NewTestsPerThousand(record);

            row.setMax(View.TOTAL_DEATHS, Math.max(row.getMax(View.TOTAL_DEATHS), totalDeaths.getChartValue()));
            row.setMax(View.TOTAL_DEATHS_PER_MILLIONS, Math.max(row.getMax(View.TOTAL_DEATHS_PER_MILLIONS), totalDeathsPerMillion.getChartValue()));

            row.setMax(View.NEW_CASES, Math.max(row.getMax(View.NEW_CASES), newCases.getChartValue()));
            row.setMax(View.NEW_CASES_PER_MILLIONS, Math.max(row.getMax(View.NEW_CASES_PER_MILLIONS), newCasesPerMillion.getChartValue()));

            row.setMax(View.TOTAL_CASES, Math.max(row.getMax(View.TOTAL_CASES), totalCases.getChartValue()));
            row.setMax(View.TOTAL_CASES_PER_MILLIONS, Math.max(row.getMax(View.TOTAL_CASES_PER_MILLIONS), totalCasesPerMillion.getChartValue()));

            row.setMax(View.NEW_DEATHS, Math.max(row.getMax(View.NEW_DEATHS), newDeaths.getChartValue()));
            row.setMax(View.NEW_DEATHS_PER_MILLIONS, Math.max(row.getMax(View.NEW_DEATHS_PER_MILLIONS), newDeathsPerMillion.getChartValue()));

            row.setMax(View.NEW_TESTS, Math.max(row.getMax(View.NEW_TESTS), newTests.getChartValue()));
            row.setMax(View.NEW_TESTS_PER_THOUSAND, Math.max(row.getMax(View.NEW_TESTS_PER_THOUSAND), newTestsPerThousand.getChartValue()));

            row.setMax(View.TOTAL_TESTS, Math.max(row.getMax(View.TOTAL_TESTS), totalTests.getChartValue()));
            row.setMax(View.TOTAL_TESTS_PER_THOUSAND, Math.max(row.getMax(View.TOTAL_TESTS_PER_THOUSAND), totalTestsPerThousand.getChartValue()));

            row.addActivity(TOTAL_DEATHS_LAYER, totalDeaths);
            row.addActivity(TOTAL_DEATHS_PER_MILLION_LAYER, totalDeathsPerMillion);

            row.addActivity(NEW_DEATHS_LAYER, newDeaths);
            row.addActivity(NEW_DEATHS_PER_MILLION_LAYER, newDeathsPerMillion);

            row.addActivity(TOTAL_CASES_LAYER, totalCases);
            row.addActivity(TOTAL_CASES_PER_MILLION_LAYER, totalCasesPerMillion);

            row.addActivity(NEW_CASES_LAYER, newCases);
            row.addActivity(NEW_CASES_PER_MILLION_LAYER, newCasesPerMillion);

            row.addActivity(NEW_TESTS_LAYER, newTests);
            row.addActivity(NEW_TESTS_PER_THOUSAND_LAYER, newTestsPerThousand);

            row.addActivity(TOTAL_TESTS_LAYER, totalTests);
            row.addActivity(TOTAL_TESTS_PER_THOUSAND_LAYER, totalTestsPerThousand);

            if (totalCases.getChartValue() > 0) {
                LocalDate date = LocalDate.parse(record.get("date"));
                if (earliestDate == null) {
                    earliestDate = date;
                }

                if (date.isBefore(earliestDate)) {
                    earliestDate = date;
                }

                if (latestDate == null) {
                    latestDate = date;
                }

                if (date.isAfter(latestDate)) {
                    latestDate = date;
                }
            }
        }

        System.out.println("earliest date: " + earliestDate);
        System.out.println("latest date  : " + latestDate);

        if (earliestDate != null && latestDate != null) {
            ganttChart.getTimeline().showRange(
                    ZonedDateTime.of(earliestDate, LocalTime.MIN, ZoneId.systemDefault()).toInstant(),
                    ZonedDateTime.of(latestDate.plusWeeks(1), LocalTime.MAX, ZoneId.systemDefault()).toInstant());
        }

        System.out.println("finished reading data file");

        updateGlobalMaxValues();
    }

    class Cases extends MutableChartActivityBase<CSVRecord> {

        private final CSVRecord record;

        public Cases(CSVRecord record, String column) {
            super(StringUtils.isNotBlank(record.get(column)) ? Double.parseDouble(record.get(column)) : 0.0);

            this.record = record;

            LocalDate date = LocalDate.parse(record.get("date"));
            startTime = ZonedDateTime.of(date, LocalTime.MIN, ZoneId.systemDefault()).toInstant();
            endTime = ZonedDateTime.of(date, LocalTime.MAX, ZoneId.systemDefault()).toInstant();
        }

        public CSVRecord getRecord() {
            return record;
        }
    }

    class TotalCases extends Cases {
        public TotalCases(CSVRecord record) {
            super(record, "total_cases");
        }
    }

    class NewCases extends Cases {
        public NewCases(CSVRecord record) {
            super(record, "new_cases");
        }
    }

    class TotalDeaths extends Cases {
        public TotalDeaths(CSVRecord record) {
            super(record, "total_deaths");
        }
    }

    class NewDeaths extends Cases {
        public NewDeaths(CSVRecord record) {
            super(record, "new_deaths");
        }
    }

    class TotalCasesPerMillion extends Cases {
        public TotalCasesPerMillion(CSVRecord record) {
            super(record, "total_cases_per_million");
        }
    }

    class NewCasesPerMillion extends Cases {
        public NewCasesPerMillion(CSVRecord record) {
            super(record, "new_cases_per_million");
        }
    }

    class TotalDeathsPerMillion extends Cases {
        public TotalDeathsPerMillion(CSVRecord record) {
            super(record, "total_deaths_per_million");
        }
    }

    class NewDeathsPerMillion extends Cases {
        public NewDeathsPerMillion(CSVRecord record) {
            super(record, "new_deaths_per_million");
        }
    }

    class NewTests extends Cases {
        public NewTests(CSVRecord record) {
            super(record, "new_tests");
        }
    }

    class NewTestsPerThousand extends Cases {
        public NewTestsPerThousand(CSVRecord record) {
            super(record, "new_tests_per_thousand");
        }
    }

    class TotalTests extends Cases {
        public TotalTests(CSVRecord record) {
            super(record, "total_tests");
        }
    }

    class TotalTestsPerThousand extends Cases {
        public TotalTestsPerThousand(CSVRecord record) {
            super(record, "total_tests_per_thousand");
        }
    }

    class CasesRenderer extends ChartActivityRenderer<Cases> {

        public CasesRenderer(GraphicsBase<?> graphics, String name, Color color) {
            super(graphics, name);
            setFill(color);
            setStroke(color.darker());
            setLineWidth(1);
            setAlpha(1);
        }
    }
}
