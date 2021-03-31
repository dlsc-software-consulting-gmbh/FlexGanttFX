/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.emirates.view;

import com.flexganttfx.emirates.model.Group;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.layer.SystemLayer;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.time.Instant;

/**
 * Created by dirk on 08/07/16.
 */
public class GroupSystemLayer extends SystemLayer {

    public GroupSystemLayer(GraphicsBase<?> graphics) {
        super("Group Layer", graphics);
    }

    @Override
    public void drawLayer(RowCanvas canvas, Instant startTime, Instant endTime) {
        Row<?,?,?> row = canvas.getRow();
        if (row instanceof Group) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setFill(Color.GRAY);
            gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        }
    }
}
