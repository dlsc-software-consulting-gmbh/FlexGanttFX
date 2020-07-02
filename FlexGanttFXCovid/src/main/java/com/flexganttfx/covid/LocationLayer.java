package com.flexganttfx.covid;

import com.flexganttfx.covid.CovidApp.LocationRow;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.layer.SystemLayer;
import impl.com.flexganttfx.skin.graphics.RowCanvas;

import java.time.Instant;

public class LocationLayer extends SystemLayer<LocationRow> {

    public LocationLayer(GraphicsBase<LocationRow> graphics) {
        super("Location Layer", graphics);
    }

    @Override
    public void drawLayer(RowCanvas<LocationRow> canvas, Instant startTime, Instant endTime) {
        
    }
}
