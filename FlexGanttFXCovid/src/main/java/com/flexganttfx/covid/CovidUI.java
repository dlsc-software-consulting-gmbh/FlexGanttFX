package com.flexganttfx.covid;

import com.flexganttfx.core.StringUtils;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.activity.MutableChartActivityBase;
import com.flexganttfx.model.layout.ChartLayout;
import com.flexganttfx.view.GanttChartLite;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.ListViewGraphics;
import com.flexganttfx.view.graphics.ScaleRowHeader;
import com.flexganttfx.view.graphics.renderer.ChartActivityRenderer;
import com.flexganttfx.view.util.Position;
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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
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


    private final Map<String, LocationRow> rowMap = new HashMap<>();

    private final GanttChartLite<LocationRow> ganttChart = new GanttChartLite<>();

    private final File file = new File(System.getProperty("user.home"), "covid.csv");

    public CovidUI(Stage stage) throws Exception {

        boolean success = true;
        if (!file.exists()) {
            success = file.createNewFile();
        }

        if (success) {
            final FileTime lastModifiedTime = Files.getLastModifiedTime(file.toPath(), LinkOption.NOFOLLOW_LINKS);
            final ZonedDateTime fileTimeStamp = ZonedDateTime.ofInstant(lastModifiedTime.toInstant(), ZoneId.systemDefault());
            if (fileTimeStamp.toLocalDate().isBefore(ZonedDateTime.now().toLocalDate())) {
                downloadFile(file);
            } else {
                System.out.println("data file is up-to-date");
            }

            final ListViewGraphics<LocationRow> graphics = ganttChart.getGraphics();
            graphics.setActivityEditingCallback(Cases.class, param -> false);
            graphics.setCanvasBuffer(0);
            graphics.getForegroundSystemLayers().add(new LocationLayer(graphics));
            graphics.setShowRowHeaders(true);
            graphics.setShowCalendarLayer(false);
            graphics.setRowHeadersWidth(90);
            graphics.setRowHeaderFactory(g -> {
                ScaleRowHeader header = new ScaleRowHeader<>(g);
                view.addListener(it -> header.draw());
                return header;
            });

            graphics.setActivityRenderer(TotalCases.class, ChartLayout.class, new CasesRenderer(graphics, "Total Cases", Color.ORANGERED));
            graphics.setActivityRenderer(NewCases.class, ChartLayout.class, new CasesRenderer(graphics, "New Cases", Color.rgb(128, 179, 27)));
            graphics.setActivityRenderer(TotalDeaths.class, ChartLayout.class, new CasesRenderer(graphics, "Total Deaths", Color.rgb(50, 120, 200)));
            graphics.setActivityRenderer(NewDeaths.class, ChartLayout.class, new CasesRenderer(graphics, "New Deaths", Color.rgb(20, 90, 160)));

            graphics.setActivityRenderer(TotalCasesPerMillion.class, ChartLayout.class, new CasesRenderer(graphics, "Total Cases Per Million", Color.ORANGERED));
            graphics.setActivityRenderer(NewCasesPerMillion.class, ChartLayout.class, new CasesRenderer(graphics, "New Cases Per Million", Color.rgb(128, 179, 27)));
            graphics.setActivityRenderer(TotalDeathsPerMillion.class, ChartLayout.class, new CasesRenderer(graphics, "Total Deaths Per Million", Color.rgb(50, 120, 200)));
            graphics.setActivityRenderer(NewDeathsPerMillion.class, ChartLayout.class, new CasesRenderer(graphics, "New Deaths Per Million", Color.rgb(20, 90, 160)));

            graphics.setShowNowLineLayer(false);
            graphics.setShowVerticalCursor(true);
            graphics.setRowControlsFactory(param -> {
                Button remove = new Button("Remove");
                StackPane.setAlignment(remove, Pos.TOP_RIGHT);
                remove.setOnAction(evt -> getSelectedLocations().remove(param.getRow()));
                StackPane stackPane = new StackPane(remove);
                stackPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
                stackPane.getStyleClass().add("controls-panel");
                return stackPane;
            });

            selectedLocations.addListener((Observable it) -> ganttChart.getRows().setAll(getSelectedLocations()));

            CovidToolBar<LocationRow> toolBar = new CovidToolBar<>(ganttChart);

            view.addListener(it -> {

                final View view = getView();
                rowMap.values().forEach(row -> row.updateMaxValue(view));

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
                }

                graphics.redraw();
            });

            SettingsView settingsView = new SettingsView(this);

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

            BorderPane borderPane = new BorderPane();
            borderPane.setTop(headerBox);
            borderPane.setCenter(ganttChart);
            borderPane.setRight(settingsView);
            borderPane.setBottom(statusBar);

            StackPane stackPane = new StackPane(borderPane, glassPane, aboutPane);

            Scene scene = new Scene(stackPane);
            scene.getStylesheets().add(CovidUI.class.getResource("styles.css").toExternalForm());

            CSSFX.start(scene);

            stage.setWidth(1200);
            stage.setHeight(1000);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setTitle("Covid-19 Data");
            stage.show();

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

            /*
             * Use hashmap to ensure we are only creating one row per location.
             */
            LocationRow row = rowMap.computeIfAbsent(location, key -> {
                LocationRow r = new LocationRow(record.get("location"));
                r.updateMaxValue(getView());
                locations.add(r);

                if ((r.getName().equals("United States") || r.getName().equals("Germany") || r.getName().equals("Switzerland")) && !getSelectedLocations().contains(r)) {
                    getSelectedLocations().add(r);
                }

                return r;
            });

            TotalCases totalCases = new TotalCases(record);
            TotalDeaths totalDeaths = new TotalDeaths(record);
            NewCases newCases = new NewCases(record);
            NewDeaths newDeaths = new NewDeaths(record);

            TotalCasesPerMillion totalCasesPerMillion = new TotalCasesPerMillion(record);
            TotalDeathsPerMillion totalDeathsPerMillion = new TotalDeathsPerMillion(record);
            NewCasesPerMillion newCasesPerMillion = new NewCasesPerMillion(record);
            NewDeathsPerMillion newDeathsPerMillion = new NewDeathsPerMillion(record);

            row.setMax(View.TOTAL_DEATHS, Math.max(row.getMax(View.TOTAL_DEATHS), totalDeaths.getChartValue()));
            row.setMax(View.TOTAL_DEATHS_PER_MILLIONS, Math.max(row.getMax(View.TOTAL_DEATHS_PER_MILLIONS), totalDeathsPerMillion.getChartValue()));

            row.setMax(View.NEW_CASES, Math.max(row.getMax(View.NEW_CASES), newCases.getChartValue()));
            row.setMax(View.NEW_CASES_PER_MILLIONS, Math.max(row.getMax(View.NEW_CASES_PER_MILLIONS), newCasesPerMillion.getChartValue()));

            row.setMax(View.TOTAL_CASES, Math.max(row.getMax(View.TOTAL_CASES), totalCases.getChartValue()));
            row.setMax(View.TOTAL_CASES_PER_MILLIONS, Math.max(row.getMax(View.TOTAL_CASES_PER_MILLIONS), totalCasesPerMillion.getChartValue()));

            row.setMax(View.NEW_DEATHS, Math.max(row.getMax(View.NEW_DEATHS), newDeaths.getChartValue()));
            row.setMax(View.NEW_DEATHS_PER_MILLIONS, Math.max(row.getMax(View.NEW_DEATHS_PER_MILLIONS), newDeathsPerMillion.getChartValue()));

            row.addActivity(TOTAL_DEATHS_LAYER, totalDeaths);
            row.addActivity(TOTAL_DEATHS_PER_MILLION_LAYER, totalDeathsPerMillion);

            row.addActivity(NEW_DEATHS_LAYER, newDeaths);
            row.addActivity(NEW_DEATHS_PER_MILLION_LAYER, newDeathsPerMillion);

            row.addActivity(TOTAL_CASES_LAYER, totalCases);
            row.addActivity(TOTAL_CASES_PER_MILLION_LAYER, totalCasesPerMillion);

            row.addActivity(NEW_CASES_LAYER, newCases);
            row.addActivity(NEW_CASES_PER_MILLION_LAYER, newCasesPerMillion);

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

        ganttChart.getTimeline().showRange(
                ZonedDateTime.of(earliestDate, LocalTime.MIN, ZoneId.systemDefault()).toInstant(),
                ZonedDateTime.of(latestDate.plusWeeks(1), LocalTime.MAX, ZoneId.systemDefault()).toInstant());

        System.out.println("finished reading data file");
    }

    class Cases extends MutableChartActivityBase<CSVRecord> {

        public Cases(CSVRecord record, String column) {
            super(StringUtils.isNotBlank(record.get(column)) ? Double.parseDouble(record.get(column)) : 0.0);

            LocalDate date = LocalDate.parse(record.get("date"));
            startTime = ZonedDateTime.of(date, LocalTime.MIN, ZoneId.systemDefault()).toInstant();
            endTime = ZonedDateTime.of(date, LocalTime.MAX, ZoneId.systemDefault()).toInstant();
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

    class CasesRenderer extends ChartActivityRenderer<Cases> {

        public CasesRenderer(GraphicsBase<?> graphics, String name, Color color) {
            super(graphics, name);
            setFill(color);
            setStroke(color.darker());
            setLineWidth(1);
            setAlpha(1);
        }

        @Override
        protected ActivityBounds drawActivity(ActivityRef<Cases> activityRef, Position position, GraphicsContext gc, double x, double y, double w, double h, boolean selected, boolean hover, boolean highlighted, boolean pressed) {
            if (w >= 6) {
                return super.drawActivity(activityRef, position, gc, x + (w - 4) / 2, y, w - 4, h, selected, hover, highlighted, pressed);
            } else {
                return super.drawActivity(activityRef, position, gc, x, y, w, h, selected, hover, highlighted, pressed);
            }
        }
    }
}
