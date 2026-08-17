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

import com.flexganttfx.extras.properties.layer.AgendaLinesLayerItemProvider;
import com.flexganttfx.extras.properties.layer.ChartLinesLayerItemProvider;
import com.flexganttfx.extras.properties.layer.DSTLineLayerItemProvider;
import com.flexganttfx.extras.properties.layer.GridLinesLayerItemProvider;
import com.flexganttfx.extras.properties.layer.HoverTimeIntervalLayerItemProvider;
import com.flexganttfx.extras.properties.layer.InnerLinesLayerItemProvider;
import com.flexganttfx.extras.properties.layer.LayoutLayerItemProvider;
import com.flexganttfx.extras.properties.layer.NowLineLayerItemProvider;
import com.flexganttfx.extras.properties.layer.SelectedTimeIntervalsLayerItemProvider;
import com.flexganttfx.extras.properties.layer.SystemLayerItemProvider;
import com.flexganttfx.extras.properties.layer.ZoomIntervalLayerItemProvider;
import com.flexganttfx.extras.properties.renderer.ActivityBarRendererItemProvider;
import com.flexganttfx.extras.properties.renderer.ActivityRendererItemProvider;
import com.flexganttfx.extras.properties.renderer.CompletableActivityRendererItemProvider;
import com.flexganttfx.extras.properties.renderer.RendererItemProvider;
import com.flexganttfx.extras.properties.timeline.DatelineItemProvider;
import com.flexganttfx.extras.properties.timeline.EventlineItemProvider;
import com.flexganttfx.extras.properties.timeline.TimelineItemProvider;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.layer.AgendaLinesLayer;
import com.flexganttfx.view.graphics.layer.ChartLinesLayer;
import com.flexganttfx.view.graphics.layer.DSTLineLayer;
import com.flexganttfx.view.graphics.layer.GridLinesLayer;
import com.flexganttfx.view.graphics.layer.HoverTimeIntervalLayer;
import com.flexganttfx.view.graphics.layer.InnerLinesLayer;
import com.flexganttfx.view.graphics.layer.LayoutLayer;
import com.flexganttfx.view.graphics.layer.NowLineLayer;
import com.flexganttfx.view.graphics.layer.SelectedTimeIntervalsLayer;
import com.flexganttfx.view.graphics.layer.SystemLayer;
import com.flexganttfx.view.graphics.layer.ZoomTimeIntervalLayer;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.graphics.renderer.CompletableActivityRenderer;
import com.flexganttfx.view.graphics.renderer.Renderer;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.Eventline;
import com.flexganttfx.view.timeline.Timeline;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.BeanPropertyUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This factory creates property sheet items for a given object. In previous
 * versions of FlexGanttFX these items were returned by the custom controls
 * themselves, but since this was more a feature used at evaluation time of the
 * product it did not make sense to keep them in production code. Hence they
 * were refactored to this place.
 * <p>
 * Supported classes / classes with an item provider implementation
 * <ul>
 *     <li>GanttChartBase</li>
 *     <li>GanttChart</li>
 *     <li>GraphicsBase</li>
 *     <li>Dateline</li>
 *     <li>Timeline</li>
 *     <li>Eventline</li>
 *     <li>AgendaLinesLayer</li>
 *     <li>ChartLinesLayer</li>
 *     <li>DSTLineLayer</li>
 *     <li>GridLinesLayer</li>
 *     <li>HoverTimeIntervalLayer</li>
 *     <li>InnerLinesLayer</li>
 *     <li>LayoutLayer</li>
 *     <li>NowLineLayer</li>
 *     <li>SelectedTimeIntervalsLayer</li>
 *     <li>SystemLayer</li>
 *     <li>ZoomTimeIntervalLayer</li>
 *     <li>ActivityBarRenderer</li>
 *     <li>ActivityRenderer</li>
 *     <li>CompletableActivityRenderer</li>
 *     <li>Renderer</li>
 * </ul>
 * Custom item providers can be registered via
 * {@link #registerItemProvider(Class, ItemProvider)}.
 *
 * @since 1.0
 */
public class ItemFactory {

    /**
     * Constructs a new factory.
     */
    public ItemFactory() {
    }

    private final static Map<Class, ItemProvider<?>> PROVIDER_MAP = new HashMap<>();

    static {
        PROVIDER_MAP.put(GanttChartBase.class, new GanttChartBaseItemProvider());
        PROVIDER_MAP.put(GanttChart.class, new GanttChartItemProvider());
        PROVIDER_MAP.put(GraphicsBase.class, new GraphicsBaseItemProvider());

        PROVIDER_MAP.put(Dateline.class, new DatelineItemProvider());
        PROVIDER_MAP.put(Timeline.class, new TimelineItemProvider());
        PROVIDER_MAP.put(Eventline.class, new EventlineItemProvider());

        PROVIDER_MAP.put(AgendaLinesLayer.class, new AgendaLinesLayerItemProvider());
        PROVIDER_MAP.put(ChartLinesLayer.class, new ChartLinesLayerItemProvider());
        PROVIDER_MAP.put(DSTLineLayer.class, new DSTLineLayerItemProvider());
        PROVIDER_MAP.put(GridLinesLayer.class, new GridLinesLayerItemProvider());
        PROVIDER_MAP.put(HoverTimeIntervalLayer.class, new HoverTimeIntervalLayerItemProvider());
        PROVIDER_MAP.put(InnerLinesLayer.class, new InnerLinesLayerItemProvider());
        PROVIDER_MAP.put(LayoutLayer.class, new LayoutLayerItemProvider());
        PROVIDER_MAP.put(NowLineLayer.class, new NowLineLayerItemProvider());
        PROVIDER_MAP.put(SelectedTimeIntervalsLayer.class, new SelectedTimeIntervalsLayerItemProvider());
        PROVIDER_MAP.put(SystemLayer.class, new SystemLayerItemProvider());
        PROVIDER_MAP.put(ZoomTimeIntervalLayer.class, new ZoomIntervalLayerItemProvider());

        PROVIDER_MAP.put(ActivityBarRenderer.class, new ActivityBarRendererItemProvider());
        PROVIDER_MAP.put(ActivityRendererItemProvider.class, new ActivityRendererItemProvider());
        PROVIDER_MAP.put(CompletableActivityRenderer.class, new CompletableActivityRendererItemProvider());
        PROVIDER_MAP.put(Renderer.class, new RendererItemProvider());
    }

    /**
     * Returns a list of property sheet items for the given object. The method will try to find
     * a dedicated item provider based on the class of the object. If none can be found it will
     * utilise {@link BeanPropertyUtils#getProperties}.
     *
     * @param object the object for which to return property sheet items
     * @return the resulting property sheet items
     */
    public List<Item> getItems(Object object) {
        ItemProvider<Object> provider = findItemProvider(object);
        if (provider == null) {
            return BeanPropertyUtils.getProperties(object);
        } else {
            return provider.getPropertySheetItems(object);
        }
    }

    /**
     * Registers a custom item provider for the given class, overriding any
     * previously registered provider.
     *
     * @param <T> the target type
     * @param clazz the class for which the provider should be used
     * @param itemProvider the provider to register
     */
    public static <T> void registerItemProvider(Class<T> clazz, ItemProvider<T> itemProvider) {
        PROVIDER_MAP.put(clazz, itemProvider);
    }

    private ItemProvider<Object> findItemProvider(Object object) {
        return doFindItemProvider(object.getClass());
    }

    private ItemProvider<Object> doFindItemProvider(Class clazz) {
        do {
            ItemProvider<Object> provider = (ItemProvider<Object>) PROVIDER_MAP.get(clazz);
            if (provider != null) {
                return provider;
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null && clazz != Object.class);

        return null;
    }
}
