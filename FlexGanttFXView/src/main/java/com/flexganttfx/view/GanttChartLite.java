/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.view;

import com.flexganttfx.model.Row;
import com.flexganttfx.view.container.DualGanttChartLiteContainer;
import com.flexganttfx.view.container.MultiGanttChartLiteContainer;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.Eventline;
import com.flexganttfx.view.timeline.Timeline;
import com.flexganttfx.view.util.Position;
import impl.com.flexganttfx.skin.GanttChartLiteSkin;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.control.Skin;

import java.util.Objects;

/**
 * A control used to visualize any kind of scheduling data along a
 * timeline. The model data needed by the control consists of rows with
 * activities, links between activities, and layers to group activities
 * together.
 * <ul>
 * <li>{@link #getRows()} - returns the list of rows</li>
 * <li>{@link #getLayers()} - returns the list of layers</li>
 * <li>{@link #getLinks()} - returns the list of links between activities</li>
 * </ul>
 * <br>
 * The control consists of several children controls:
 * <ul>
 * <li>{@link GraphicsBase}: shown on the right-hand side to display a graphical
 * representation of the model data</li>
 * <li>{@link Timeline}: shown above the graphics view. The timeline itself
 * consists of two child controls.</li>
 * <li>{@link Dateline}: displays days, weeks, months, years, etc...</li>
 * <li>{@link Eventline}: displays various time markers</li>
 * </ul>
 * <p>
 * Visually the lite Gantt chart is very similar to a regular Gantt chart that is using the display mode GRAPHICS_ONLY.
 * The big difference is that the lite Gantt chart does not create a tree table view and that the rows can be
 * added as a simple list instead of a hierarchy of rows.
 * </p>
 * <p>
 * The screenshot belows shows the initial appearance of an empty Gantt chart (lite)
 * control.
 * </p>
 *
 *     <img src="doc-files/gantt-chart-lite.png" alt="Gantt Chart Lite Control" width="100%">
 *
 * <h2>Master / Detail Panes</h2> The Gantt chart uses a single MasterDetailPane
 * instance from ControlsFX for the high-level layout. The pane initially displays a property sheet as
 * its detail node. The property sheet is used at development time and can be
 * replaced with any node by calling {@link #setDetail(Node)}. The property
 * sheet displays a lot of properties that are used by the controls, the
 * renderers, the system layers to fine-tune the appearance of the control. Many
 * of them can be changed at runtime.
 * <h2>Standalone vs. Multi- / Dual Gantt Chart</h2> A Gantt chart can be used
 * standalone or inside a {@link MultiGanttChartLiteContainer} or
 * {@link DualGanttChartLiteContainer}. When used in one of these containers the
 * {@link Position} of the Gantt chart becomes important. The control can be the
 * first chart, the last chart, the only chart, or a chart somewhere in the
 * middle. A "first" or "only" chart always displays a timeline. A "middle" or
 * "last" displays an (optional) special header (see {@link #setGraphicsHeader(Node)}). The
 * containers are also the reason why the control distinguishes between a
 * timeline ({@link #getTimeline()}) and a master timeline (
 * {@link #getMasterTimeline()}). The master timeline is the one shown by the
 * "first" chart, while the regular timeline is the one that belongs directly to
 * an individual Gantt chart instance.
 * <h2>Code Example</h2>
 * <pre>
 * import java.time.Duration;
 * import java.time.Instant;
 * import java.time.temporal.ChronoUnit;
 *
 * import javafx.application.Application;
 * import javafx.scene.Scene;
 * import javafx.stage.Stage;
 *
 * import com.flexganttfx.model.GanttChartModel;
 * import com.flexganttfx.model.Layer;
 * import com.flexganttfx.model.Row;
 * import com.flexganttfx.model.activity.MutableActivityBase;
 * import com.flexganttfx.model.layout.GanttLayout;
 * import com.flexganttfx.view.GanttChart;
 * import com.flexganttfx.view.graphics.GraphicsView;
 * import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
 * import com.flexganttfx.view.timeline.Timeline;
 *
 * public class TutorialAircraftFlight extends Application {
 *
 *
 *  class FlightData {
 *  	String flightNo;
 *  	Instant departureTime = Instant.now();
 *  	Instant arrivalTime = Instant.now().plus(Duration.ofHours(6));
 *
 *  	public FlightData(String flightNo, int day) {
 *  		this.flightNo = flightNo;
 *  		departureTime = departureTime.plus(Duration.ofDays(day));
 *  		arrivalTime = arrivalTime.plus(Duration.ofDays(day));
 *    }
 *  }
 *
 *  class Flight extends MutableActivityBase&lt;FlightData&gt; {
 *  	public Flight(FlightData data) {
 *  		setUserObject(data);
 *  		setName(data.flightNo);
 *  		setStartTime(data.departureTime);
 *  		setEndTime(data.arrivalTime);
 *    }
 *  }
 *
 *  class Aircraft extends Row&lt;Aircraft, Aircraft, Flight&gt; {
 *  	public Aircraft(String name) {
 *  		super(name);
 *    }
 *  }
 *
 *  public void start(Stage stage) {
 *  	// Create the Gantt chart
 *  	GanttChartLite&lt;Aircraft&gt; gantt = new GanttChartLite&lt;&gt;(new FlightSchedule(new Aircraft(&quot;ROOT&quot;)));
 *
 *  	Layer flightsLayer = new Layer(&quot;Flights&quot;);
 *  	gantt.getLayers().add(flightsLayer);
 *
 *  	Aircraft b747 = new Aircraft(&quot;B747&quot;);
 *  	b747.addActivity(flightsLayer, new Flight(new FlightData(&quot;flight1&quot;, 1)));
 *  	b747.addActivity(flightsLayer, new Flight(new FlightData(&quot;flight2&quot;, 2)));
 *  	b747.addActivity(flightsLayer, new Flight(new FlightData(&quot;flight3&quot;, 3)));
 *
 *  	Aircraft a380 = new Aircraft(&quot;A380&quot;);
 *  	a380.addActivity(flightsLayer, new Flight(new FlightData(&quot;flight1&quot;, 1)));
 *  	a380.addActivity(flightsLayer, new Flight(new FlightData(&quot;flight2&quot;, 2)));
 *  	a380.addActivity(flightsLayer, new Flight(new FlightData(&quot;flight3&quot;, 3)));
 *
 *  	gantt.getRows().setAll(b747, a380);
 *
 *  	Timeline timeline = gantt.getTimeline();
 *  	timeline.showTemporalUnit(ChronoUnit.HOURS, 10);
 *
 *  	GraphicsView&lt;Aircraft&gt; graphics = gantt.getGraphics();
 *  	graphics.setActivityRenderer(Flight.class, GanttLayout.class,
 *  			new ActivityBarRenderer&lt;&gt;(graphics, &quot;Flight Renderer&quot;));
 *  	graphics.showEarliestActivities();
 *
 *  	Scene scene = new Scene(gantt);
 *  	stage.setScene(scene);
 *  	stage.sizeToScene();
 *  	stage.centerOnScreen();
 *  	stage.show();
 *  }
 *
 *  public static void main(String[] args) {
 *  	launch(args);
 *  }
 * </pre>
 *
 * @param <R> the type of the rows shown by the Gantt chart (e.g. "Aircraft")
 * @since 1.6
 */
public class GanttChartLite<R extends Row<?, ?, ?>> extends GanttChartBase<R> {

    /**
     * Constructs a new Gantt Chart control.
     *
     * @since 1.6
     */
    public GanttChartLite() {
        FilteredList<R> filteredList = new FilteredList<>(rowsProperty());
        filteredList.predicateProperty().bind(rowFilterProperty());
        getGraphics().setRows(filteredList);
    }

    /**
     * Creates the default skin for this lite Gantt chart.
     *
     * @return the default skin
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new GanttChartLiteSkin<>(this);
    }

    private final ListProperty<R> rows = new SimpleListProperty<>(this, "rows", FXCollections.observableArrayList());

    /**
     * Returns the property used to store the list of rows.
     *
     * @return the list of rows
     * @since 1.6
     */
    public final ListProperty<R> rowsProperty() {
        return rows;
    }

    /**
     * Sets the value of the {@link #rowsProperty()}.
     *
     * @param rows the new rows to display
     * @since 1.6
     */
    public final void setRows(ObservableList<R> rows) {
        Objects.requireNonNull(rows);
        this.rows.set(rows);
    }

    /**
     * Returns the list that is used to store all rows of the model.
     *
     * @return a list of rows
     * @since 1.6
     */
    public final ObservableList<R> getRows() {
        return rows.get();
    }
}
