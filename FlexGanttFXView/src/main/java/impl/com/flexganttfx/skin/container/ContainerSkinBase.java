/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.container;

import com.flexganttfx.view.container.ContainerBase;
import javafx.scene.control.SkinBase;

/**
 * Created by dirk on 11/07/16.
 */
public abstract class ContainerSkinBase<T extends ContainerBase> extends SkinBase<T> {

    protected static final String GANTT_TREE_TABLE_VIEW_LAST = "gantt-tree-table-view-last";
    protected static final String GANTT_TREE_TABLE_VIEW_MIDDLE = "gantt-tree-table-view-middle";
    protected static final String GANTT_TREE_TABLE_VIEW_FIRST = "gantt-tree-table-view-first";

    protected static final String TIMELINE_LAST = "timeline-last";
    protected static final String TIMELINE_MIDDLE = "timeline-middle";
    protected static final String TIMELINE_FIRST = "timeline-first";

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected ContainerSkinBase(T control) {
        super(control);
    }
}
