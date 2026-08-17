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
package com.flexganttfx.extras;

import com.flexganttfx.extras.util.Messages;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.dateline.VirtualGrid;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.util.ThemingUtil;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import org.controlsfx.control.StatusBar;

import java.text.MessageFormat;

import static java.util.Objects.requireNonNull;

/**
 * A status bar for rapid prototyping with FlexGanttFX. It observes a
 * {@link GanttChartBase} and displays the name of the currently hovered
 * activity together with the active virtual grid.
 *
 * <h2>Example</h2>
 * <pre>{@code
 * GanttChart<MyRow> gantt = new GanttChart<>(new MyRow("Root"));
 *
 * GanttChartStatusBar<MyRow> statusBar = new GanttChartStatusBar<>(gantt);
 *
 * BorderPane pane = new BorderPane();
 * pane.setCenter(gantt);
 * pane.setBottom(statusBar);
 * }</pre>
 *
 * @see GanttChartToolBar
 *
 * @param <R> the type of the rows in the Gantt chart
 * @since 1.0
 */
public class GanttChartStatusBar<R extends Row<?, ?, ?>> extends StatusBar {

    private final Label gridLabel;

    /**
     * Constructs a new statusbar control. The Gantt chart has to be set later
     * by calling {@link #setGanttChart(GanttChartBase)}.
     *
     * @since 1.0
     */
    public GanttChartStatusBar() {
        getStyleClass().add("gantt-chart-status-bar");

        gridLabel = new Label();
        gridLabel.getStyleClass().add("grid-label");

        getRightItems().add(gridLabel);

        updateGridLabel();

        ganttChartProperty().addListener((observable, oldValue, newValue) -> {

            if (oldValue != null) {
                GraphicsBase<?> graphicsView = oldValue.getGraphics();
                graphicsView.hoverActivityProperty().removeListener(weakFocusedActivityListener);
                graphicsView.virtualGridProperty().removeListener(weakVirtualGridListener);

            }

            if (newValue != null) {
                GraphicsBase<?> graphicsView = newValue.getGraphics();
                graphicsView.hoverActivityProperty().addListener(weakFocusedActivityListener);
                graphicsView.virtualGridProperty().addListener(weakVirtualGridListener);
            }

            updateGridLabel();
        });
    }

    @Override
    public String getUserAgentStylesheet() {
        if (ThemingUtil.isAtlantaFXActive(getScene())) {
           return requireNonNull(GanttChartStatusBar.class.getResource("statusbar-atlantafx.css")).toExternalForm();
        } else {
            return requireNonNull(GanttChartStatusBar.class.getResource("statusbar.css")).toExternalForm();
        }
    }

    /**
     * Constructs a new statusbar control.
     *
     * @param ganttChart the Gantt chart for which the statusbar will be used
     * @since 1.0
     */
    public GanttChartStatusBar(GanttChartBase<R> ganttChart) {
        this();

        setGanttChart(ganttChart);
    }

    private final InvalidationListener focusedActivityListener = observable -> {
        GraphicsBase<?> graphicsView = getGanttChart().getGraphics();
        ActivityRef<?> focusedActivity = graphicsView.getHoverActivity();
        if (focusedActivity != null) {
            Activity activity = focusedActivity.getActivity();
            setText(activity.getName());
        }
    };

    private final WeakInvalidationListener weakFocusedActivityListener = new WeakInvalidationListener(focusedActivityListener);

    private final InvalidationListener virtualGridListener = observable -> updateGridLabel();

    private final WeakInvalidationListener weakVirtualGridListener = new WeakInvalidationListener(virtualGridListener);

    private void updateGridLabel() {
        GanttChartBase<R> gc = getGanttChart();
        if (gc == null) {
            return;
        }
        GraphicsBase<?> graphicsView = gc.getGraphics();
        VirtualGrid<?> grid = graphicsView.getVirtualGrid();
        if (grid != null) {
            gridLabel.setText(MessageFormat.format(Messages.getString("GanttChartStatusBar.MESSAGE_GRID_NAME"), grid.getName()));
        } else {
            gridLabel.setText(Messages.getString("GanttChartStatusBar.MESSAGE_GRID_OFF"));
        }
    }

    private final ObjectProperty<GanttChartBase<R>> ganttChart = new SimpleObjectProperty<>(this, "ganttChart");

    /**
     * The ganttChart property. Stores the Gantt chart watched by this status
     * bar.
     *
     * @return the ganttChart property
     * @since 1.0
     */
    public final ObjectProperty<GanttChartBase<R>> ganttChartProperty() {
        return ganttChart;
    }

    public final GanttChartBase<R> getGanttChart() {
        return ganttChartProperty().get();
    }

    public final void setGanttChart(GanttChartBase<R> ganttChart) {
        requireNonNull(ganttChart);
        ganttChartProperty().set(ganttChart);
    }
}
