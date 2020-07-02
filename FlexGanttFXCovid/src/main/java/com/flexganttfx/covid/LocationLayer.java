package com.flexganttfx.covid;

import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.layer.SystemLayer;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.text.NumberFormat;
import java.time.Instant;

public class LocationLayer extends SystemLayer<LocationRow> {

    private static final Color FILL_COLOR = Color.BLACK;

    private final NumberFormat formatter = NumberFormat.getIntegerInstance();

    public LocationLayer(GraphicsBase<LocationRow> graphics) {
        super("Location Layer", graphics);
    }

    @Override
    public void drawLayer(RowCanvas<LocationRow> canvas, Instant startTime, Instant endTime) {
        final GraphicsContext gc = canvas.getGraphicsContext2D();

        final LocationRow row = canvas.getRow();
        if (row != null) {
            gc.setFill(FILL_COLOR);
            gc.setGlobalAlpha(.5);
            gc.fillRect(0, 0, canvas.getWidth(), 20);
            gc.setGlobalAlpha(1);

            gc.setFill(Color.WHITE);
            gc.setTextAlign(TextAlignment.LEFT);
            gc.setTextBaseline(VPos.BASELINE);
            gc.fillText(row.getName() + ", Total Infected: " + formatter.format(row.getMax(View.TOTAL_CASES)) + ", Total Deaths: " + formatter.format(row.getMax(View.TOTAL_DEATHS)), 20, 16);
        }
    }
}
