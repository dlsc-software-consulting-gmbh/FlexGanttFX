/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras.properties;

import com.flexganttfx.extras.properties.layer.*;
import com.flexganttfx.extras.properties.renderer.ActivityBarRendererItemProvider;
import com.flexganttfx.extras.properties.renderer.ActivityRendererItemProvider;
import com.flexganttfx.extras.properties.renderer.CompletableActivityRendererItemProvider;
import com.flexganttfx.extras.properties.renderer.RendererItemProvider;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.layer.*;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.graphics.renderer.CompletableActivityRenderer;
import com.flexganttfx.view.graphics.renderer.Renderer;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.BeanPropertyUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This factory creates property sheet items for a given object. In previous versions of
 * FlexGanttFX these items were returned by the custom controls themselves but since this
 * was more a feature used at evaluation time of the product it did not make sense to keep
 * them in production code. Hence they were refactored to this place.
 * <p>
 * Supported classes / classes with an item provider implementation
 * <ul>
 *     <li>GanttChartBase</li>
 *     <li>GanttChart</li>
 *     <li>GraphicsBase</li>
 *     <li>AgendaLinesLayer</li>
 *     <li>ChartLinesLayer</li>
 *     <li>DSTLineLayer</li>
 *     <li>GridLinesLayer</li>
 *     <li>HoverTimeIntervalLayer</li>
 *     <li>InnerLinesLayer</li>
 *     <li>LayoutLayer</li>
 *     <li>NowLineLayer</li>
 *     <li>ScaleLayer</li>
 *     <li>SelectedTimeIntervalsLayer</li>
 *     <li>SystemLayer</li>
 *     <li>ZoomTimeIntervalLayer</li>
 *     <li>ActivityBarRenderer</li>
 *     <li>ActivityRendererItemProvider</li>
 *     <li>CompletableActivityRenderer</li>
 *     <li>Renderer</li>
 * </ul>
 * </p>
 */
public class ItemFactory {

    private final static Map<Class, ItemProvider<?>> PROVIDER_MAP = new HashMap<>();

    static {
        PROVIDER_MAP.put(GanttChartBase.class, new GanttChartBaseItemProvider());
        PROVIDER_MAP.put(GanttChart.class, new GanttChartItemProvider());
        PROVIDER_MAP.put(GraphicsBase.class, new GraphicsBaseItemProvider());

        PROVIDER_MAP.put(AgendaLinesLayer.class, new AgendaLinesLayerItemProvider());
        PROVIDER_MAP.put(ChartLinesLayer.class, new ChartLinesLayerItemProvider());
        PROVIDER_MAP.put(DSTLineLayer.class, new DSTLineLayerItemProvider());
        PROVIDER_MAP.put(GridLinesLayer.class, new GridLinesLayerItemProvider());
        PROVIDER_MAP.put(HoverTimeIntervalLayer.class, new HoverTimeIntervalLayerItemProvider());
        PROVIDER_MAP.put(InnerLinesLayer.class, new InnerLinesLayerItemProvider());
        PROVIDER_MAP.put(LayoutLayer.class, new LayoutLayerItemProvider());
        PROVIDER_MAP.put(NowLineLayer.class, new NowLineLayerItemProvider());
        PROVIDER_MAP.put(ScaleLayer.class, new ScaleLayerItemProvider());
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
