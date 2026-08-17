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
package com.flexganttfx.extras.properties;

import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.container.QuadGanttChartContainerBase;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.PropertySheet.Item;

import java.util.Optional;

/**
 * Provides ControlsFX {@link org.controlsfx.control.PropertySheet.Item}
 * instances for configuring a {@link QuadGanttChartContainerBase}.
 *
 * @param <T> the chart type managed by the container
 * @since 1.0
 */
public class QuadGanttChartContainerBaseItemProvider<T extends GanttChartBase<?>> implements ItemProvider<QuadGanttChartContainerBase<T>> {

    /**
     * Constructs a new provider.
     */
    public QuadGanttChartContainerBaseItemProvider() {
    }

    private static final String QUAD_GANTT_CHART_CONTAINER_PROPERTIES_CATEGORY = "Control: Quad Gantt Chart Container";

    public final ObservableList<Item> getPropertySheetItems(QuadGanttChartContainerBase<T> container) {
        ObservableList<Item> items = FXCollections.observableArrayList();

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(container.showLowerProperty());
            }

            @Override
            public void setValue(Object value) {
                container.setShowLower((boolean) value);
            }

            @Override
            public Object getValue() {
                return container.isShowLower();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Lower";
            }

            @Override
            public String getDescription() {
                return "Show lower charts";
            }

            @Override
            public String getCategory() {
                return QUAD_GANTT_CHART_CONTAINER_PROPERTIES_CATEGORY;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(container.animatedProperty());
            }

            @Override
            public void setValue(Object value) {
                container.setAnimated((boolean) value);
            }

            @Override
            public Object getValue() {
                return container.isAnimated();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Animated";
            }

            @Override
            public String getDescription() {
                return "Open / close animations";
            }

            @Override
            public String getCategory() {
                return QUAD_GANTT_CHART_CONTAINER_PROPERTIES_CATEGORY;
            }
        });

        return items;
    }
}
