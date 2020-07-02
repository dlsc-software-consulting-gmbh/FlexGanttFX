package com.flexganttfx.covid;

import javafx.scene.control.ListCell;

public class LocationRowListCell extends ListCell<LocationRow> {

    public LocationRowListCell() {
    }

    @Override
    protected void updateItem(LocationRow locationRow, boolean empty) {
        super.updateItem(locationRow, empty);

        if (!empty && locationRow != null) {
            setText(locationRow.getName());
        } else {
            setText("");
        }
    }
}
