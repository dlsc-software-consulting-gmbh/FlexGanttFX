package com.flexganttfx.covid;

import com.flexganttfx.covid.CovidApp.LocationRow;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Comparator;

public class SettingsView extends VBox {

    public SettingsView(CovidApp app) {
        getStyleClass().add("settings-view");

        setFillWidth(true);

        Label titleLabel = new Label("Covid-19");
        titleLabel.getStyleClass().add("title");

        MenuButton datasetButton = new MenuButton("Select Dataset");
        datasetButton.setMaxWidth(Double.MAX_VALUE);

        for (View v : View.values()) {
            MenuItem item = new MenuItem(v.getDisplayName());
            item.setOnAction(evt -> app.setView(v));
            datasetButton.getItems().add(item);
        }

        app.viewProperty().addListener(it -> datasetButton.setText(app.getView().getDisplayName()));

        ListView<LocationRow> countryListView = new ListView<>();
        countryListView.setPrefHeight(210);
        SortedList<LocationRow> sortedCountryList = new SortedList<>(app.getLocations());
        sortedCountryList.setComparator(Comparator.comparing(LocationRow::getName));
        FilteredList<LocationRow> filteredList = new FilteredList<>(sortedCountryList);
        countryListView.setItems(filteredList);
        countryListView.setCellFactory(view -> new LocationRowListCell());

        TextField searchField = new TextField();
        searchField.setPromptText("Search ...");
        searchField.textProperty().addListener(it -> filteredList.setPredicate(row -> row.getName().toLowerCase().contains(searchField.getText().toLowerCase())));

        // add button
        Button addButton = new Button("Add Country");
        addButton.disableProperty().bind(countryListView.getSelectionModel().selectedItemProperty().isNull());
        addButton.setOnAction(evt -> {

            final ObservableList<LocationRow> selectedLocations = app.getSelectedLocations();
            final LocationRow selectedItem = countryListView.getSelectionModel().getSelectedItem();

            if (!selectedLocations.contains(selectedItem)) {
                selectedLocations.add(selectedItem);
            }
        });
        addButton.setMaxWidth(Double.MAX_VALUE);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        getChildren().setAll(titleLabel, searchField, countryListView, addButton, spacer, datasetButton);
    }
}
