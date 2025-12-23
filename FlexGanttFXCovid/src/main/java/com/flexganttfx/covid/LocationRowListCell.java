/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.covid;

import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;

public class LocationRowListCell extends ListCell<LocationRow> {

    private final ImageView imageView = new ImageView();

    public LocationRowListCell() {
        setGraphic(imageView);
        imageView.setFitHeight(24);
        imageView.setPreserveRatio(true);
    }

    @Override
    protected void updateItem(LocationRow locationRow, boolean empty) {
        super.updateItem(locationRow, empty);

        if (!empty && locationRow != null) {
            setText(locationRow.getName());
            imageView.setImage(Flags.getFlag(Iso.convertIso3CountryCodeToIso2CountryCode(locationRow.getIso3CountryCode())));
        } else {
            setText("");
            imageView.setImage(null);
        }
    }
}
