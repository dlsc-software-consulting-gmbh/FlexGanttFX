package com.flexganttfx.covid;

import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.core.StringUtils;
import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.ChartActivityBase;
import com.flexganttfx.model.layout.ChartLayout;
import com.flexganttfx.view.GanttChartLite;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.ScaleRowHeader;
import com.flexganttfx.view.graphics.renderer.ChartActivityRenderer;
import com.flexganttfx.view.util.Position;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.fxmisc.cssfx.CSSFX;

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
import java.util.HashMap;
import java.util.Map;

public class CovidApp extends Application {

    private static final Layer TOTAL_CASES_LAYER = new Layer("Total Cases");
    private static final Layer NEW_CASES_LAYER = new Layer("New Cases");
    private static final Layer TOTAL_DEATHS_LAYER = new Layer("Total Deaths");

    private final Map<String, LocationRow> rowMap = new HashMap<>();

    @Override
    public void start(Stage stage) throws Exception {
        final File file = new File(System.getProperty("user.home"), "covid.csv");

        boolean success = true;
        if (!file.exists()) {
            success = file.createNewFile();
        }

        if (success) {
            final FileTime lastModifiedTime = Files.getLastModifiedTime(file.toPath(), LinkOption.NOFOLLOW_LINKS);
            final ZonedDateTime time = ZonedDateTime.ofInstant(lastModifiedTime.toInstant(), ZoneId.systemDefault());
            if (time.toLocalDate().isBefore(ZonedDateTime.now().toLocalDate())) {
                downloadFile(file);
            } else {
                System.out.println("data file is up-to-date");
            }

            if (!FlexGanttFX.isLicenseKeySet()) {
                FlexGanttFX.setLicenseKey("LIC=DLSC;VEN=DLSC;VER=12;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302C02142BD7F914E6633D7DBA0B8564D8FC20EC249BCFD702142558B5C6FF46325A0A698A1E8036828E54D6FEC8");
            }

            GanttChartLite<LocationRow> ganttChart = new GanttChartLite<>();
            ganttChart.getGraphics().setShowRowHeaders(true);
            ganttChart.getGraphics().setRowHeaderFactory(graphics -> new ScaleRowHeader<>(ganttChart.getGraphics()));

            ganttChart.getGraphics().setActivityRenderer(TotalCases.class, ChartLayout.class, new CasesRenderer(ganttChart.getGraphics(), "Total Cases", Color.CADETBLUE));
            ganttChart.getGraphics().setActivityRenderer(NewCases.class, ChartLayout.class, new CasesRenderer(ganttChart.getGraphics(), "New Cases", Color.BLUE));
            ganttChart.getGraphics().setActivityRenderer(TotalDeaths.class, ChartLayout.class, new CasesRenderer(ganttChart.getGraphics(), "Total Deaths", Color.BLACK));

            ganttChart.getGraphics().setShowNowLineLayer(false);
            ganttChart.getGraphics().setRowControlsFactory(row -> {
                Button remove = new Button("Remove");
                remove.setOnAction(evt -> getSelectedLocations().remove(row));
                return remove;
            });

            selectedLocations.addListener((Observable it) -> ganttChart.getRows().setAll(getSelectedLocations()));

            GanttChartToolBar<LocationRow> toolBar = new GanttChartToolBar<>(ganttChart);

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
                }

                ganttChart.getGraphics().redraw();
            });

            SettingsView settingsView = new SettingsView(this);

            BorderPane borderPane = new BorderPane();
            borderPane.setTop(toolBar);
            borderPane.setCenter(ganttChart);
            borderPane.setRight(settingsView);

            Scene scene = new Scene(borderPane);
            scene.getStylesheets().add(CovidApp.class.getResource("styles.css").toExternalForm());

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
                setView(View.NEW_CASES);
                ganttChart.getGraphics().showAllActivities();
            });
        } else {
            System.out.println("error when trying to create csv file in user's home director");
        }
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
         * new_cases,total_deaths,new_deaths,total_cases_per_million,new_cases_per_million,total_deaths_per_million,new_deaths_per_million,total_tests,new_tests,total_tests_per_thousand,new_tests_per_thousand,new_tests_smoothed,new_tests_smoothed_per_thousand,tests_units,stringency_index,population,population_density,median_age,aged_65_older,aged_70_older,gdp_per_capita,extreme_poverty,cvd_death_rate,diabetes_prevalence,female_smokers,male_smokers,handwashing_facilities,hospital_beds_per_thousand,life_expectancy
         */

        rowMap.clear();

        for (CSVRecord record : records) {
            String location = record.get("location");

            /*
             * Use hashmap to ensure we are only creating one row per location.
             */
            LocationRow row = rowMap.computeIfAbsent(location, key -> {
                LocationRow r = new LocationRow(record.get("location"));
                r.updateMaxValue(getView());
                locations.add(r);

                if (r.getName().equals("United States") && !getSelectedLocations().contains(r)) {
                    getSelectedLocations().add(r);
                }

                return r;
            });

            TotalCases totalCases = new TotalCases(record);
            NewCases newCases = new NewCases(record);
            TotalDeaths totalDeaths = new TotalDeaths(record);

            row.setMaxTotalCases(Math.max(row.getMaxTotalCases(), totalCases.getChartValue()));
            row.setMaxNewCases(Math.max(row.getMaxNewCases(), newCases.getChartValue()));
            row.setMaxDeaths(Math.max(row.getMaxDeaths(), totalDeaths.getChartValue()));

            row.addActivity(TOTAL_CASES_LAYER, totalCases);
            row.addActivity(NEW_CASES_LAYER, newCases);
            row.addActivity(TOTAL_DEATHS_LAYER, totalDeaths);
        }

        System.out.println("finished reading data file");
    }

    class LocationRow extends Row<LocationRow, LocationRow, Cases> {

        private double maxTotalCases;
        private double maxNewCases;
        private double maxDeaths;

        private final ChartLayout chartLayout = new ChartLayout();

        public LocationRow(String name) {
            super(name);

            setHeight(200);
            setMinHeight(50);

            chartLayout.setPadding(0);
            setLayout(chartLayout);
        }

        private void updateMaxValue(View view) {
            final double max = getMax(view);
            chartLayout.setMaxValue(max * 1.25);
            chartLayout.getMajorTicks().setAll(max);
        }

        private double getMax(View view) {
            if (view == null) {
                return 0;
            }

            switch (view) {
                case TOTAL_CASES:
                    return maxTotalCases;
                case NEW_CASES:
                    return maxNewCases;
                case TOTAL_DEATHS:
                    return maxDeaths;
                default:
                    return 0;
            }
        }

        public double getMaxTotalCases() {
            return maxTotalCases;
        }

        public void setMaxTotalCases(double maxTotalCases) {
            this.maxTotalCases = maxTotalCases;
        }

        public double getMaxNewCases() {
            return maxNewCases;
        }

        public void setMaxNewCases(double maxNewCases) {
            this.maxNewCases = maxNewCases;
        }

        public double getMaxDeaths() {
            return maxDeaths;
        }

        public void setMaxDeaths(double maxDeaths) {
            this.maxDeaths = maxDeaths;
        }
    }

    class Cases extends ChartActivityBase<CSVRecord> {

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

    class CasesRenderer extends ChartActivityRenderer<Cases> {

        public CasesRenderer(GraphicsBase<?> graphics, String name, Color color) {
            super(graphics, name);
            setFill(color);
        }

        @Override
        protected ActivityBounds drawActivity(ActivityRef<Cases> activityRef, Position position, GraphicsContext gc, double x, double y, double w, double h, boolean selected, boolean hover, boolean highlighted, boolean pressed) {
            if (w >= 6) {
                return super.drawActivity(activityRef, position, gc, x + (w - 4) / 2, y, w - 4 , h, selected, hover, highlighted, pressed);
            } else {
                return super.drawActivity(activityRef, position, gc, x, y, w, h, selected, hover, highlighted, pressed);
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
