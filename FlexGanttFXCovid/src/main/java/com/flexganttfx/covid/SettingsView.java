package com.flexganttfx.covid;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.apache.commons.csv.CSVRecord;

import java.util.Comparator;

public class SettingsView extends VBox {

    public SettingsView(CovidUI uiInstance) {
        getStyleClass().add("settings-view");

        setFillWidth(true);
        setMinWidth(Region.USE_PREF_SIZE);

        Label titleLabel = new Label("Covid-19");
        titleLabel.getStyleClass().add("title");

        // list data structure, sorted and filtered
        SortedList<LocationRow> sortedCountryList = new SortedList<>(uiInstance.getLocations());
        sortedCountryList.setComparator(Comparator.comparing(LocationRow::getName));
        FilteredList<LocationRow> filteredList = new FilteredList<>(sortedCountryList);

        // list view
        ListView<LocationRow> countryListView = new ListView<>();
        countryListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        countryListView.setPrefHeight(210);
        countryListView.setMinHeight(120);
        countryListView.setItems(filteredList);
        countryListView.setCellFactory(view -> new LocationRowListCell());
        countryListView.setOnMouseClicked(evt -> {
            if (evt.getButton() == MouseButton.PRIMARY && evt.getClickCount() == 2) {
                addSelectedCountries(uiInstance, countryListView);
            }
        });

        TextField searchField = new TextField();
        searchField.setPromptText("Search ...");
        searchField.textProperty().addListener(it -> filteredList.setPredicate(row -> row.getName().toLowerCase().contains(searchField.getText().toLowerCase())));

        // add button
        Button addButton = new Button("Add Country");
        countryListView.getSelectionModel().getSelectedItems().addListener((Observable it) -> {
            if (countryListView.getSelectionModel().getSelectedItems().size() > 1) {
                addButton.setText("Add Countries");
            } else {
                addButton.setText("Add Country");
            }
        });
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.disableProperty().bind(countryListView.getSelectionModel().selectedItemProperty().isNull());
        addButton.setOnAction(evt -> addSelectedCountries(uiInstance, countryListView));
        addButton.setPrefWidth(1);

        // clear button
        Button clearButton = new Button("Remove Charts");
        clearButton.setMaxWidth(Double.MAX_VALUE);
        clearButton.disableProperty().bind(Bindings.createBooleanBinding(() -> uiInstance.getSelectedLocations().isEmpty(), uiInstance.getSelectedLocations()));
        clearButton.setOnAction(evt -> uiInstance.getSelectedLocations().clear());
        clearButton.setPrefWidth(1);

        HBox.setHgrow(addButton, Priority.ALWAYS);
        HBox.setHgrow(clearButton, Priority.ALWAYS);

        HBox buttonBox = new HBox(10, addButton, clearButton);

        RecordView recordView = new RecordView();
        recordView.recordProperty().bind(recordProperty());

        getChildren().setAll(titleLabel, searchField, countryListView, buttonBox, recordView);
    }

    private void addSelectedCountries(CovidUI uiInstance, ListView<LocationRow> countryListView) {
        final ObservableList<LocationRow> selectedLocations = uiInstance.getSelectedLocations();
        countryListView.getSelectionModel().getSelectedItems().forEach(row -> {
            if (!selectedLocations.contains(row)) {
                selectedLocations.add(row);
            }

        });
    }

    private final ObjectProperty<CSVRecord> record = new SimpleObjectProperty<>(this, "record");

    public CSVRecord getRecord() {
        return record.get();
    }

    public ObjectProperty<CSVRecord> recordProperty() {
        return record;
    }

    public void setRecord(CSVRecord record) {
        this.record.set(record);
    }

}
