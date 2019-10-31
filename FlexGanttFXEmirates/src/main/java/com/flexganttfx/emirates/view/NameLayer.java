/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.emirates.view;

import com.flexganttfx.emirates.model.Group;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.layer.SystemLayer;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.time.Instant;

/**
 * Created by dirk on 21.06.16.
 */
public class NameLayer<R extends Row<?, ?, ?>> extends SystemLayer<R> {

    private Color backgroundColor = Color.color(0, 0, 0, .5);
    private Color foregroundColor = Color.WHITE;

    public NameLayer(GraphicsBase graphics) {
        super("Name Layer", graphics);
    }

    @Override
    public void drawLayer(RowCanvas<R> canvas, Instant startTime, Instant endTime) {
        R row = canvas.getRow();
        if (row != null && !(row instanceof Group)) {

            String name = row.getName();
            GraphicsContext gc = canvas.getGraphicsContext2D();

            double textWidth = 80;
            double textHeight = 20;

            gc.setTextAlign(TextAlignment.LEFT);
            gc.setTextBaseline(VPos.BOTTOM);
            gc.setFill(backgroundColor);
            gc.fillRect(0, 0, textWidth + 4, textHeight);
            gc.setFill(foregroundColor);
            gc.fillText(name, 2, textHeight);
        }
    }
}
