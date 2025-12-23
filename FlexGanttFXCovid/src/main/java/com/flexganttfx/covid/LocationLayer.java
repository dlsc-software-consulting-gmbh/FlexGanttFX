/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.covid;

import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.layer.SystemLayer;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.text.NumberFormat;
import java.time.Instant;

public class LocationLayer extends SystemLayer<LocationRow> {

    private static final Color FILL_COLOR = Color.BLACK;

    private static final NumberFormat formatter = NumberFormat.getIntegerInstance();

    public LocationLayer(GraphicsBase<LocationRow> graphics) {
        super("Location Layer", graphics);
    }

    @Override
    public void drawLayer(RowCanvas<LocationRow> canvas, Instant startTime, Instant endTime) {
        final GraphicsContext gc = canvas.getGraphicsContext2D();

        final LocationRow row = canvas.getRow();
        if (row != null) {
            final String iso2 = Iso.convertIso3CountryCodeToIso2CountryCode(row.getIso3CountryCode());

            double bannerHeight = 20;

            gc.setFill(FILL_COLOR);
            gc.setGlobalAlpha(.5);
            gc.fillRect(0, 0, canvas.getWidth(), bannerHeight);
            gc.setGlobalAlpha(1);

            gc.setFill(Color.WHITE);
            gc.setTextAlign(TextAlignment.LEFT);
            gc.setTextBaseline(VPos.BASELINE);

            Image image = Flags.getFlag(iso2);
            if (image != null) {
                final double width = image.getWidth();
                final double height = image.getHeight();
                final double scale = Math.min(16 / width, 16 / height);
                final double w = width * scale;
                final double h = height * scale;
                gc.drawImage(image, 10, (bannerHeight - h) / 2, w, h);
            }

            gc.fillText(row.getName() + ", Total Infected: " + formatter.format(row.getMax(View.TOTAL_CASES)) + ", Total Deaths: " + formatter.format(row.getMax(View.TOTAL_DEATHS)), 40, 16);
        }
    }
}
