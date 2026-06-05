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

import com.flexganttfx.extras.properties.timeline.DatelineItemProvider;
import com.flexganttfx.extras.properties.timeline.EventlineItemProvider;
import com.flexganttfx.extras.properties.timeline.TimelineItemProvider;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.GanttChartBase.ScrollBarType;
import com.flexganttfx.view.graphics.ListViewGraphics;
import com.flexganttfx.view.timeline.Timeline;
import com.flexganttfx.view.util.Position;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.PropertySheet.Item;

import java.util.List;
import java.util.Optional;

/**
 * A property sheet item provider for {@link GanttChartBase}.
 *
 * @param <R> the row type
 * @since 1.0
 */
public class GanttChartBaseItemProvider<R extends Row<?, ?, ?>> implements ItemProvider<GanttChartBase<R>> {

    private static final String GANTT_CHART_BASE_PROPERTIES_CATEGORY = "Control: Gantt Chart Base";

    @Override
    public List<Item> getPropertySheetItems(GanttChartBase<R> gc) {
        ObservableList<Item> items = FXCollections.observableArrayList();

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(gc.scrollBarTypeProperty());
            }

            @Override
            public void setValue(Object value) {
                gc.setScrollBarType((ScrollBarType) value);
            }

            @Override
            public Object getValue() {
                return gc.getScrollBarType();
            }

            @Override
            public Class<?> getType() {
                return ScrollBarType.class;
            }

            @Override
            public String getName() {
                return "Scroll Bar Type";
            }

            @Override
            public String getDescription() {
                return "Selects the type of scrollbar to use for scrolling in time.";
            }

            @Override
            public String getCategory() {
                return GANTT_CHART_BASE_PROPERTIES_CATEGORY;
            }
        });


        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(gc.fixedCellSizeProperty());
            }

            @Override
            public void setValue(Object value) {
                gc.setFixedCellSize((Double) value);
            }

            @Override
            public Object getValue() {
                return gc.getFixedCellSize();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Fixed Cell Size";
            }

            @Override
            public String getDescription() {
                return "Controls whether cells have a fixed or varying row height.";
            }

            @Override
            public String getCategory() {
                return GANTT_CHART_BASE_PROPERTIES_CATEGORY;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(gc.positionProperty());
            }

            @Override
            public void setValue(Object value) {
                gc.setPosition((Position) value);
            }

            @Override
            public Object getValue() {
                return gc.getPosition();
            }

            @Override
            public Class<?> getType() {
                return Position.class;
            }

            @Override
            public String getName() {
                return "Position";
            }

            @Override
            public String getDescription() {
                return "The position of the Gantt chart within a dual / multi Gantt chart container.";
            }

            @Override
            public String getCategory() {
                return GANTT_CHART_BASE_PROPERTIES_CATEGORY;
            }
        });

        Timeline timeline = gc.getTimeline();
        TimelineItemProvider timelineItemProvider = new TimelineItemProvider();
        items.addAll(timelineItemProvider.getPropertySheetItems(timeline));

        DatelineItemProvider datelineItemProvider = new DatelineItemProvider();
        items.addAll(datelineItemProvider.getPropertySheetItems(timeline.getDateline()));

        EventlineItemProvider eventlineItemProvider = new EventlineItemProvider();
        items.addAll(eventlineItemProvider.getPropertySheetItems(timeline.getEventline()));

        ListViewGraphics<R> graphics = gc.getGraphics();
        GraphicsBaseItemProvider graphicsBasePropertySheetSupport = new GraphicsBaseItemProvider<>();
        items.addAll(graphicsBasePropertySheetSupport.getPropertySheetItems(graphics));

        for (final Layer layer : graphics.getLayers()) {
            items.add(new Item() {

                @Override
                public Optional<ObservableValue<?>> getObservableValue() {
                    return Optional.of(layer.visibleProperty());
                }

                @Override
                public void setValue(Object value) {
                    layer.setVisible((Boolean) value);
                }

                @Override
                public Object getValue() {
                    return layer.isVisible();
                }

                @Override
                public Class<?> getType() {
                    return Boolean.class;
                }

                @Override
                public String getName() {
                    return "Visible";
                }

                @Override
                public String getDescription() {
                    return "Show / hide the model layer (its activities)";
                }

                @Override
                public String getCategory() {
                    return "Model Layer: " + layer.getName();
                }
            });

            items.add(new Item() {

                @Override
                public Optional<ObservableValue<?>> getObservableValue() {
                    return Optional.of(layer.deletableProperty());
                }

                @Override
                public void setValue(Object value) {
                    layer.setDeletable((Boolean) value);
                }

                @Override
                public Object getValue() {
                    return layer.isDeletable();
                }

                @Override
                public Class<?> getType() {
                    return Boolean.class;
                }

                @Override
                public String getName() {
                    return "Deletable";
                }

                @Override
                public String getDescription() {
                    return "Determines if the layer can be deleted by the user.";
                }

                @Override
                public String getCategory() {
                    return "Model Layer: " + layer.getName();
                }
            });

            items.add(new Item() {

                @Override
                public Optional<ObservableValue<?>> getObservableValue() {
                    return Optional.of(layer.nameProperty());
                }

                @Override
                public void setValue(Object value) {
                    layer.setName((String) value);
                }

                @Override
                public Object getValue() {
                    return layer.getName();
                }

                @Override
                public Class<?> getType() {
                    return String.class;
                }

                @Override
                public String getName() {
                    return "Name";
                }

                @Override
                public String getDescription() {
                    return "The name of the model layer";
                }

                @Override
                public String getCategory() {
                    return "Model Layer: " + layer.getName();
                }
            });

            items.add(new Item() {

                @Override
                public Optional<ObservableValue<?>> getObservableValue() {
                    return Optional.of(layer.opacityProperty());
                }

                @Override
                public void setValue(Object value) {
                    layer.setOpacity((Double) value);
                }

                @Override
                public Object getValue() {
                    return layer.getOpacity();
                }

                @Override
                public Class<?> getType() {
                    return Double.class;
                }

                @Override
                public String getName() {
                    return "Opacity";
                }

                @Override
                public String getDescription() {
                    return "Layer opacity / transparency.";
                }

                @Override
                public String getCategory() {
                    return "Model Layer: " + layer.getName();
                }
            });
        }

        return items;
    }
}
