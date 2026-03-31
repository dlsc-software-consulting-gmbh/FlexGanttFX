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
package com.flexganttfx.weather.view;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Layout;
import com.flexganttfx.model.LinesManager;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.MutableActivity;
import com.flexganttfx.model.activity.MutableChartActivityBase;
import com.flexganttfx.model.activity.MutableHighLowChartActivityBase;
import com.flexganttfx.model.layout.ChartLayout;
import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.view.GanttChartLite;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityRenderer;
import com.flexganttfx.view.util.Position;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Root UI for the World Climate Explorer demo.
 *
 * <p>Uses a {@link GanttChartLite} (flat row list, no tree table) with custom
 * row headers that double as a visual legend for each city's data.
 *
 * <p>Each row is split into two chart bands via a {@link LinesManager}:
 * <ol>
 *   <li><b>Temperature band (72%)</b> — daily high/low rendered as a vertical gradient bar
 *       using {@link MutableHighLowChartActivityBase}.</li>
 *   <li><b>Precipitation band (28%)</b> — rainy-day amounts in mm using
 *       {@link MutableChartActivityBase}.</li>
 * </ol>
 *
 * <p>Each row header ({@link CityRowHeader}) shows:
 * <ul>
 *   <li>A colour-coded swatch that mirrors the row's band split (temperature gradient /
 *       precipitation blue), giving an instant visual key to the chart.</li>
 *   <li>A numeric temperature scale with tick marks at min, 0 °C, and max.</li>
 *   <li>The city name and its Köppen climate classification.</li>
 * </ul>
 */
public class WeatherView extends BorderPane {

    /**
     * City climate normals.
     * Columns: name, winterLow°C, winterHigh°C, summerLow°C, summerHigh°C,
     *          winterPrecip mm/day, summerPrecip mm/day, southernHemisphere.
     */
    private static final Object[][] CITIES = {
        //  City                    wLow  wHigh  sLow  sHigh  wPmm  sPmm  south
        {"London, UK",              2.0,   8.0, 14.0,  23.0,  1.9,  1.5,  false},
        {"Dubai, UAE",             15.0,  25.0, 30.0,  43.0,  0.4,  0.0,  false},
        {"Tokyo, Japan",            2.0,  10.0, 25.0,  33.0,  1.7,  5.0,  false},
        {"Sydney, Australia",       9.0,  17.0, 19.0,  28.0,  3.5,  2.3,  true },
        {"Moscow, Russia",        -13.0,  -6.0, 16.0,  24.0,  1.3,  3.0,  false},
        {"Singapore",              24.0,  31.0, 24.0,  32.0,  5.8,  4.7,  false},
        {"Reykjavik, Iceland",     -3.0,   2.0,  9.0,  14.0,  2.5,  1.7,  false},
        {"New Delhi, India",        7.0,  21.0, 28.0,  40.0,  0.6,  6.0,  false},
    };

    // Fraction of row height allocated to each band
    static final double TEMP_FRACTION   = 0.72;
    static final double PRECIP_FRACTION = 1.0 - TEMP_FRACTION;

    public WeatherView() {
        GanttChartLite<CityRow> chart = buildChart();
        setTop(new GanttChartToolBar<>(chart));
        setCenter(chart);
        setBottom(buildLegend());
    }

    // ─────────────────────────── Chart construction ───────────────────────────

    private GanttChartLite<CityRow> buildChart() {
        GanttChartLite<CityRow> gc = new GanttChartLite<>();

        Layer layer = new Layer("Weather");
        gc.getLayers().add(layer);

        LocalDate startDate = LocalDate.of(2018, 1, 1);
        LocalDate endDate   = LocalDate.of(2024, 12, 31);

        List<CityRow> cityRows = new ArrayList<>();

        for (Object[] city : CITIES) {
            String  name     = (String)  city[0];
            double  wLow     = (double)  city[1];
            double  wHigh    = (double)  city[2];
            double  sLow     = (double)  city[3];
            double  sHigh    = (double)  city[4];
            double  wPrecip  = (double)  city[5];
            double  sPrecip  = (double)  city[6];
            boolean southern = (boolean) city[7];

            double minTemp   = wLow  - 5.0;
            double maxTemp   = sHigh + 5.0;
            double maxPrecip = Math.max(wPrecip, sPrecip) * 2.2;

            CityRow row = new CityRow(name);
            cityRows.add(row);

            ChartLayout tempLayout = new ChartLayout();
            tempLayout.setPadding(4);
            tempLayout.setMinValue(minTemp);
            tempLayout.setMaxValue(maxTemp);
            if (minTemp < 0 && maxTemp > 0) {
                tempLayout.getMajorTicks().add(0.0);
            }

            ChartLayout precipLayout = new ChartLayout();
            precipLayout.setPadding(2);
            precipLayout.setMinValue(0.0);
            precipLayout.setMaxValue(maxPrecip);

            row.setTempLayout(tempLayout);
            row.setPrecipLayout(precipLayout);

            Random rng = new Random((long) name.hashCode() * 31L);

            for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
                double sf = seasonFactor(d, southern);

                double noise = rng.nextGaussian() * 2.2;
                double lo = wLow  + (sLow  - wLow)  * sf + noise;
                double hi = wHigh + (sHigh - wHigh)  * sf + noise + rng.nextDouble() * 3.0;
                hi = Math.max(hi, lo + 0.5);

                row.addActivity(layer, new DailyWeather(d, lo, hi));

                double prob = precipProbability(name, sf, wPrecip, sPrecip);
                if (rng.nextDouble() < prob) {
                    double base   = wPrecip + (sPrecip - wPrecip) * sf;
                    double amount = base * (0.3 + rng.nextDouble() * 2.2);
                    amount = Math.min(amount, maxPrecip);
                    if (amount > 0.2) {
                        row.addActivity(layer, new DailyPrecip(d, amount));
                    }
                }
            }
        }

        gc.getRows().setAll(cityRows);
        gc.getCalendars().clear();

        gc.getTimeline().showTime(
            ZonedDateTime.of(startDate, LocalTime.MIN, ZoneId.systemDefault()).toInstant());
        gc.getTimeline().showTemporalUnit(ChronoUnit.MONTHS, 48);

        gc.getGraphics().setShowVerticalCursor(true);
        gc.getGraphics().setShowHorizontalCursor(true);

        // Row headers replace the tree table for GanttChartLite
        gc.getGraphics().setShowRowHeaders(true);
        gc.getGraphics().setRowHeadersWidth(220);
        gc.getGraphics().setRowHeaderFactory(g -> new CityRowHeader(g));

        gc.getGraphics().setActivityRenderer(
            DailyWeather.class, ChartLayout.class, new TemperatureRenderer(gc.getGraphics()));
        gc.getGraphics().setActivityRenderer(
            DailyPrecip.class, ChartLayout.class, new PrecipitationRenderer(gc.getGraphics()));

        return gc;
    }

    /** Bottom legend bar showing the temperature colour ramp and precipitation key. */
    private HBox buildLegend() {
        HBox legend = new HBox(20);
        legend.setPadding(new Insets(6, 14, 6, 14));
        legend.setAlignment(Pos.CENTER_LEFT);

        double[] tempStops = {-25, -10, 0, 10, 20, 30, 40};
        for (int i = 0; i < tempStops.length - 1; i++) {
            Color c0 = TemperatureRenderer.tempColor(tempStops[i]);
            Color c1 = TemperatureRenderer.tempColor(tempStops[i + 1]);
            LinearGradient grad = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, c0), new Stop(1, c1));
            Rectangle swatch = new Rectangle(36, 14, grad);
            swatch.setArcWidth(2);
            swatch.setArcHeight(2);
            legend.getChildren().add(swatch);
        }
        Label tempLabel = new Label("Temperature  (−25 °C … +40 °C)");
        tempLabel.setPadding(new Insets(0, 20, 0, 4));
        legend.getChildren().add(tempLabel);

        Color rainLow  = Color.hsb(200, 0.45, 1.0, 0.6);
        Color rainHigh = Color.hsb(220, 1.0,  0.55, 1.0);
        LinearGradient rainGrad = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
            new Stop(0, rainLow), new Stop(1, rainHigh));
        Rectangle rainSwatch = new Rectangle(50, 14, rainGrad);
        rainSwatch.setArcWidth(2);
        rainSwatch.setArcHeight(2);
        legend.getChildren().add(rainSwatch);
        legend.getChildren().add(new Label("Precipitation  (light → heavy)"));

        return legend;
    }

    // ──────────────────────── Seasonal helpers ────────────────────────────────

    private static double seasonFactor(LocalDate date, boolean southern) {
        double doy    = date.getDayOfYear();
        double length = date.isLeapYear() ? 366.0 : 365.0;
        double phase  = southern ? Math.PI : 0.0;
        return (Math.sin(2.0 * Math.PI * doy / length - Math.PI / 2.0 + phase) + 1.0) / 2.0;
    }

    private static double precipProbability(String city, double sf, double wP, double sP) {
        if ("Dubai, UAE".equals(city)) {
            return 0.04 * (1.0 - sf * 0.9);
        } else if ("Singapore".equals(city)) {
            return 0.55 - 0.12 * sf;
        } else if ("New Delhi, India".equals(city)) {
            return 0.04 + 0.72 * sf * sf;
        } else if ("Moscow, Russia".equals(city)) {
            return 0.22 + 0.22 * sf;
        } else {
            double ratio = wP / (wP + sP + 0.001);
            return 0.14 + 0.42 * (ratio * (1.0 - sf) + (1.0 - ratio) * sf);
        }
    }

    // ───────────────────────── Inner model classes ────────────────────────────

    class CityRow extends Row<CityRow, CityRow, MutableActivity> {
        private ChartLayout tempLayout;
        private ChartLayout precipLayout;

        CityRow(String name) {
            super(name);
            setLineCount(2);
            setHeight(220);
            setMaxHeight(600);
            tempLayout   = new ChartLayout();
            precipLayout = new ChartLayout();
            setLinesManager(new CityLinesManager(this));
        }

        ChartLayout getTempLayout()               { return tempLayout; }
        void        setTempLayout(ChartLayout l)  { tempLayout   = l; }
        ChartLayout getPrecipLayout()             { return precipLayout; }
        void        setPrecipLayout(ChartLayout l){ precipLayout = l; }
    }

    class CityLinesManager implements LinesManager<MutableActivity> {
        private final CityRow row;

        CityLinesManager(CityRow row) { this.row = row; }

        @Override
        public int getLineIndex(MutableActivity activity) {
            return (activity instanceof DailyPrecip) ? 1 : 0;
        }

        @Override
        public double getLineLocation(int lineIndex, double rowHeight) {
            return lineIndex == 0 ? 0 : rowHeight - getLineHeight(1, rowHeight);
        }

        @Override
        public double getLineHeight(int lineIndex, double rowHeight) {
            return lineIndex == 0 ? rowHeight * TEMP_FRACTION : rowHeight * PRECIP_FRACTION;
        }

        @Override
        public Layout getLineLayout(int lineIndex) {
            return lineIndex == 0 ? row.getTempLayout() : row.getPrecipLayout();
        }
    }

    static class DailyWeather extends MutableHighLowChartActivityBase<Object> {
        DailyWeather(LocalDate date, double low, double high) {
            setLow(low);
            setHigh(high);
            setStartTime(ZonedDateTime.of(date, LocalTime.MIN, ZoneId.systemDefault()).toInstant());
            setEndTime(ZonedDateTime.of(date, LocalTime.MAX, ZoneId.systemDefault()).toInstant());
        }
    }

    static class DailyPrecip extends MutableChartActivityBase<Object> {
        DailyPrecip(LocalDate date, double mmPerDay) {
            setChartValue(mmPerDay);
            setStartTime(ZonedDateTime.of(date, LocalTime.MIN, ZoneId.systemDefault()).toInstant());
            setEndTime(ZonedDateTime.of(date, LocalTime.MAX, ZoneId.systemDefault()).toInstant());
        }
    }

    // ─────────────────────────── Row header ──────────────────────────────────

    /**
     * Custom row header drawn entirely on a {@link Canvas}. For each city row it shows:
     * <ol>
     *   <li><b>Left swatch (0–18 px)</b>: a temperature gradient strip (top 72 %) that uses
     *       exactly the same colour function as the chart bars, so the header acts as a
     *       continuous colour key. Below it a precipitation-blue strip (bottom 28 %).</li>
     *   <li><b>Temperature scale (18–90 px)</b>: a vertical axis with tick marks and °C labels
     *       at the max value, 0 °C (when in range), every 10 °C, and the min value.
     *       Positions match pixel-for-pixel with the chart band.</li>
     *   <li><b>Info area (90 px – right)</b>: city name in bold, Köppen climate type,
     *       and the temperature range. The precipitation band area shows a "Precip." label.</li>
     * </ol>
     *
     * <p>A horizontal rule at 72 % of the height visually separates the two bands and
     * mirrors the band boundary in the chart.
     */
    class CityRowHeader extends GraphicsBase.RowHeader<CityRow> {

        // Layout constants (pixels)
        private static final double SWATCH_W  = 18;   // colour swatch width
        private static final double SCALE_X   = 22;   // start of tick marks
        private static final double TICK_LEN  = 7;    // tick mark length
        private static final double LABEL_X   = 88;   // right-align limit for scale labels
        private static final double INFO_X    = 95;   // left edge of city info text

        private final Canvas canvas;

        CityRowHeader(GraphicsBase<CityRow> graphics) {
            super(graphics);

            canvas = new Canvas() {
                @Override public boolean isResizable()           { return true; }
                @Override public double prefWidth(double h)      { return getWidth(); }
                @Override public double prefHeight(double w)     { return getHeight(); }
            };

            canvas.widthProperty().bind(widthProperty());
            canvas.heightProperty().bind(heightProperty());
            canvas.widthProperty().addListener(it -> draw());
            canvas.heightProperty().addListener(it -> draw());

            setGraphic(canvas);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setAlignment(Pos.CENTER_LEFT);

            itemProperty().addListener(it -> draw());
        }

        private void draw() {
            double w = canvas.getWidth();
            double h = canvas.getHeight();
            if (w <= 0 || h <= 0) return;

            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, w, h);

            CityRow row = getItem();
            if (row == null || row.getTempLayout() == null) return;

            ChartLayout tempLayout = row.getTempLayout();

            double tempH   = h * TEMP_FRACTION;
            double precipH = h - tempH;
            double precipY = tempH;

            double minT   = tempLayout.getMinValue();
            double maxT   = tempLayout.getMaxValue();
            double tRange = maxT - minT;

            // ── Dark semi-transparent background ──────────────────────────
            gc.setFill(Color.color(0.06, 0.07, 0.10, 0.93));
            gc.fillRect(0, 0, w, h);

            // ── Temperature gradient swatch ───────────────────────────────
            // Build a multi-stop gradient matching the exact colour function
            List<Stop> tStops = new ArrayList<>();
            int N = 32;
            for (int i = 0; i <= N; i++) {
                double t    = i / (double) N;
                double temp = maxT - tRange * t;   // top=hottest, bottom=coldest
                tStops.add(new Stop(t, TemperatureRenderer.tempColor(temp)));
            }
            gc.setFill(new LinearGradient(0, 0, 0, tempH, false, CycleMethod.NO_CYCLE, tStops));
            gc.fillRect(0, 0, SWATCH_W, tempH);

            // ── Precipitation swatch ──────────────────────────────────────
            gc.setFill(new LinearGradient(0, precipY, 0, precipY + precipH, false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.hsb(200, 0.40, 1.00, 0.30)),
                new Stop(1.0, Color.hsb(220, 0.90, 0.65, 0.90))));
            gc.fillRect(0, precipY, SWATCH_W, precipH);

            // ── Swatch right border ───────────────────────────────────────
            gc.setStroke(Color.color(1, 1, 1, 0.18));
            gc.setLineWidth(0.5);
            gc.strokeLine(SWATCH_W, 0, SWATCH_W, h);

            // ── Band separator rule ───────────────────────────────────────
            gc.setStroke(Color.color(1, 1, 1, 0.30));
            gc.setLineWidth(1.0);
            gc.strokeLine(0, precipY, w, precipY);

            // ── Temperature scale ─────────────────────────────────────────
            gc.setFont(Font.font("System", 9.5));
            gc.setTextAlign(TextAlignment.RIGHT);
            gc.setTextBaseline(VPos.CENTER);

            // Vertical scale axis line
            gc.setStroke(Color.color(1, 1, 1, 0.25));
            gc.setLineWidth(0.5);
            gc.strokeLine(SCALE_X, 0, SCALE_X, tempH);

            // Determine a nice tick interval
            double tickStep = niceTickStep(tRange);
            double firstTick = Math.ceil(minT / tickStep) * tickStep;

            // Collect tick values to draw, avoiding crowding
            List<Double> ticks = new ArrayList<>();
            ticks.add(maxT);
            for (double v = firstTick; v <= maxT + 0.01; v += tickStep) {
                if (v > minT + tickStep * 0.4 && v < maxT - tickStep * 0.4) {
                    ticks.add(v);
                }
            }
            ticks.add(minT);

            double prevLabelY = -999;
            for (double tick : ticks) {
                double y = tempH * (maxT - tick) / tRange;
                if (y < 0 || y > tempH) continue;

                boolean isZero    = Math.abs(tick) < 0.01;
                boolean isExtreme = Math.abs(tick - maxT) < 0.01 || Math.abs(tick - minT) < 0.01;

                // Tick mark — longer for extremes and zero
                double extraLen = (isExtreme || isZero) ? 4 : 0;
                gc.setStroke(isZero
                    ? Color.color(0.6, 0.85, 1.0, 0.8)
                    : Color.color(1, 1, 1, isExtreme ? 0.55 : 0.35));
                gc.setLineWidth(isZero ? 0.8 : 0.5);
                gc.strokeLine(SCALE_X, y, SCALE_X + TICK_LEN + extraLen, y);

                // Label — skip if too close to previous one
                if (y - prevLabelY > 11 || isExtreme) {
                    String label = isZero ? "0°C"
                        : (tick == Math.floor(tick))
                            ? String.format("%.0f°", tick)
                            : String.format("%.1f°", tick);

                    gc.setFill(isZero
                        ? Color.color(0.6, 0.85, 1.0, 0.85)
                        : Color.color(1, 1, 1, isExtreme ? 0.80 : 0.55));
                    gc.fillText(label, LABEL_X, y);
                    prevLabelY = y;
                }
            }

            // ── City name ─────────────────────────────────────────────────
            gc.setTextAlign(TextAlignment.LEFT);
            gc.setFill(Color.color(1.0, 1.0, 1.0, 0.95));
            gc.setFont(Font.font("System", FontWeight.BOLD, 13));
            gc.setTextBaseline(VPos.TOP);
            gc.fillText(row.getName(), INFO_X, 5, w - INFO_X - 4);

            // ── Köppen climate classification ─────────────────────────────
            gc.setFont(Font.font("System", 10.5));
            gc.setFill(Color.color(0.65, 0.85, 1.0, 0.80));
            gc.fillText(climateType(row.getName()), INFO_X, 22, w - INFO_X - 4);

            // ── Temperature range summary ─────────────────────────────────
            gc.setFont(Font.font("System", 9.5));
            gc.setFill(Color.color(1, 1, 1, 0.45));
            gc.setTextBaseline(VPos.BOTTOM);
            gc.fillText(String.format("%.0f °C  …  %.0f °C", minT + 5, maxT - 5),
                INFO_X, tempH - 5, w - INFO_X - 4);

            // ── Precipitation band label ──────────────────────────────────
            gc.setFont(Font.font("System", FontWeight.BOLD, 10));
            gc.setFill(Color.color(0.55, 0.78, 1.0, 0.75));
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText("Precipitation", INFO_X, precipY + precipH * 0.4, w - INFO_X - 4);

            gc.setFont(Font.font("System", 9.5));
            gc.setFill(Color.color(1, 1, 1, 0.40));
            gc.fillText("(mm / day)", INFO_X, precipY + precipH * 0.72, w - INFO_X - 4);
        }

        /** Choose a tick interval that keeps the scale readable without crowding. */
        private double niceTickStep(double range) {
            if (range <= 15)  return 5;
            if (range <= 30)  return 5;
            if (range <= 60)  return 10;
            if (range <= 100) return 20;
            return 25;
        }

        private String climateType(String city) {
            if ("London, UK".equals(city))          return "Cfb · Temperate oceanic";
            if ("Dubai, UAE".equals(city))          return "BWh · Hot desert";
            if ("Tokyo, Japan".equals(city))        return "Cfa · Humid subtropical";
            if ("Sydney, Australia".equals(city))   return "Cfb · Temperate oceanic (S)";
            if ("Moscow, Russia".equals(city))      return "Dfb · Humid continental";
            if ("Singapore".equals(city))           return "Af · Tropical rainforest";
            if ("Reykjavik, Iceland".equals(city))  return "Cfc · Subarctic oceanic";
            if ("New Delhi, India".equals(city))    return "BSh · Semi-arid / Monsoon";
            return "";
        }
    }

    // ─────────────────────────── Renderers ───────────────────────────────────

    /**
     * Renders the daily temperature range as a vertical gradient bar.
     * The top colour maps to the high temperature, the bottom to the low.
     */
    static class TemperatureRenderer extends ActivityRenderer<DailyWeather> {

        TemperatureRenderer(GraphicsBase<?> graphics) {
            super(graphics, "Temperature");
            setCornersRounded(false);
        }

        @Override
        protected ActivityBounds drawActivity(
                ActivityRef<DailyWeather> ref, Position position,
                GraphicsContext gc, double x, double y, double w, double h,
                boolean selected, boolean hover, boolean highlighted, boolean pressed) {

            double bw = Math.max(1.0, w - 0.5);
            double bx = x + (w - bw) / 2.0;

            if (selected || hover || pressed || highlighted) {
                gc.setFill(getFill(selected, hover, highlighted, pressed));
                gc.fillRect(bx, y, bw, h);
            } else {
                DailyWeather weather = ref.getActivity();
                LinearGradient gradient = new LinearGradient(
                    0, y, 0, y + h, false, CycleMethod.NO_CYCLE,
                    new Stop(0.0, tempColor(weather.getHigh())),
                    new Stop(1.0, tempColor(weather.getLow())));
                gc.setFill(gradient);
                gc.fillRect(bx, y, bw, h);

                if (bw > 2) {
                    Color mid = tempColor((weather.getHigh() + weather.getLow()) / 2.0);
                    gc.setStroke(mid.deriveColor(0, 1, 0.6, 0.5));
                    gc.setLineWidth(0.5);
                    gc.strokeLine(bx, y + h * 0.5, bx + bw, y + h * 0.5);
                }
            }

            return new ActivityBounds(ref, bx, y, bw, h);
        }

        /**
         * Maps a temperature in °C to a HSB colour.
         * ≤ −30 °C → deep blue, 0 °C → cyan, 20 °C → yellow, ≥ 45 °C → red.
         * Package-visible so {@link CityRowHeader} can reuse it for the swatch.
         */
        static Color tempColor(double celsius) {
            double hue, sat, bright;
            if (celsius <= 0.0) {
                double t = Math.max(0.0, Math.min(1.0, (celsius + 30.0) / 30.0));
                hue    = 240.0 - 60.0 * t;
                sat    = 0.80;
                bright = 0.60 + 0.35 * t;
            } else if (celsius <= 20.0) {
                double t = celsius / 20.0;
                hue    = 180.0 - 120.0 * t;
                sat    = 0.72 + 0.18 * t;
                bright = 0.88 + 0.07 * t;
            } else {
                double t = Math.min(1.0, (celsius - 20.0) / 25.0);
                hue    = 60.0 - 60.0 * t;
                sat    = 0.88 + 0.12 * t;
                bright = 0.95 - 0.10 * t;
            }
            return Color.hsb(
                Math.max(0, Math.min(240, hue)),
                Math.min(1.0, sat),
                Math.min(1.0, bright));
        }
    }

    /**
     * Renders daily precipitation as a top-fade bar coded by intensity.
     */
    static class PrecipitationRenderer extends ActivityRenderer<DailyPrecip> {

        PrecipitationRenderer(GraphicsBase<?> graphics) {
            super(graphics, "Precipitation");
            setCornersRounded(false);
        }

        @Override
        protected ActivityBounds drawActivity(
                ActivityRef<DailyPrecip> ref, Position position,
                GraphicsContext gc, double x, double y, double w, double h,
                boolean selected, boolean hover, boolean highlighted, boolean pressed) {

            double bw = Math.max(1.0, w - 0.5);
            double bx = x + (w - bw) / 2.0;

            if (selected || hover || pressed || highlighted) {
                gc.setFill(getFill(selected, hover, highlighted, pressed));
                gc.fillRect(bx, y, bw, h);
            } else {
                double mm        = ref.getActivity().getChartValue();
                double intensity = Math.min(1.0, mm / 10.0);
                Color rainColor = Color.hsb(
                    200.0 + 20.0 * intensity,
                    0.45  + 0.55 * intensity,
                    1.0   - 0.45 * intensity,
                    0.55  + 0.45 * intensity);
                gc.setFill(new LinearGradient(
                    0, y, 0, y + h, false, CycleMethod.NO_CYCLE,
                    new Stop(0.0, rainColor.deriveColor(0, 1, 1, 0.25)),
                    new Stop(1.0, rainColor)));
                gc.fillRect(bx, y, bw, h);
            }

            return new ActivityBounds(ref, bx, y, bw, h);
        }
    }
}
