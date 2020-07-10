package com.flexganttfx.covid;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.time.LocalDate;

public class RecordView extends GridPane {

    public RecordView() {
        getStyleClass().add("record-view");

        setMinWidth(RecordView.USE_PREF_SIZE);

        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();

        col1.setMinWidth(Region.USE_PREF_SIZE);
        col2.setMinWidth(Region.USE_PREF_SIZE);

        col1.setHgrow(Priority.ALWAYS);
        col2.setHgrow(Priority.ALWAYS);

        getColumnConstraints().setAll(col1, col2);

        Label title = new Label("");
        add(title, 0, 0);
        GridPane.setColumnSpan(title, 2);
        GridPane.setMargin(title, new Insets(5, 0, 0, 0));

        recordProperty().addListener(it -> {
            final CSVRecord record = getRecord();
            if (record != null) {
                title.setText("Location: " + record.get("location"));
            } else {
                title.setText("");
            }
        });

        add(new CasesView("Date", "date", LocalDate.class), 0, 1);

        Label populationLabel = new Label("Population");
        add(populationLabel, 0, 2);
        GridPane.setColumnSpan(populationLabel, 2);
        GridPane.setMargin(populationLabel, new Insets(5, 0, 0, 0));

        add(new CasesView("Total", "population", Integer.class), 0, 3);
        add(new CasesView("Density", "population_density", Integer.class), 1, 3);

        final Label casesHeader = new Label("Cases");
        add(casesHeader, 0, 4);
        GridPane.setColumnSpan(casesHeader, 2);
        GridPane.setMargin(casesHeader, new Insets(5, 0, 0, 0));

        add(new CasesView("New Cases", "new_cases", Integer.class), 0, 5);
        add(new CasesView("Total Cases", "total_cases", Integer.class), 1, 5);

        add(new CasesView("New Cases / Million", "new_cases_per_million", Integer.class), 0, 6);
        add(new CasesView("Total Cases / Million", "total_cases_per_million", Integer.class), 1, 6);

        final Label deathsHeader = new Label("Deaths");
        add(deathsHeader, 0, 7);
        GridPane.setColumnSpan(deathsHeader, 2);
        GridPane.setMargin(deathsHeader, new Insets(5, 0, 0, 0));

        add(new CasesView("New Deaths", "new_deaths", Integer.class), 0, 8);
        add(new CasesView("Total Deaths", "total_deaths", Integer.class), 1, 8);

        add(new CasesView("New Deaths / Million", "new_deaths_per_million", Integer.class), 0, 9);
        add(new CasesView("Total Deaths / Million", "total_deaths_per_million", Integer.class), 1, 9);

        final Label testsHeader = new Label("Tests");
        add(testsHeader, 0, 10);
        GridPane.setColumnSpan(testsHeader, 2);
        GridPane.setMargin(testsHeader, new Insets(5, 0, 0, 0));

        add(new CasesView("New Tests", "new_tests", Integer.class), 0, 11);
        add(new CasesView("Total Tests", "total_tests", Integer.class), 1, 11);

        add(new CasesView("New Tests / Thousand", "new_tests_per_thousand", Integer.class), 0, 12);
        add(new CasesView("Total Tests / Thousand", "total_tests_per_thousand", Integer.class), 1, 12);
    }

    class CasesView extends VBox {

        private final Label nameLabel = new Label();
        private final Label valueLabel = new Label();

        public CasesView(String name, String value, Class type) {
            getStyleClass().add("cases-view");
            setFillWidth(true);
            setMinWidth(Region.USE_PREF_SIZE);
            // make sure they all get the same width
            //setPrefWidth(1);

            nameLabel.getStyleClass().add("name-label");
            valueLabel.getStyleClass().add("value-label");

            nameLabel.setMinWidth(Region.USE_PREF_SIZE);
            valueLabel.setMinWidth(Region.USE_PREF_SIZE);

            valueLabel.setMaxWidth(Double.MAX_VALUE);
            valueLabel.setAlignment(Pos.CENTER_RIGHT);
            getChildren().setAll(nameLabel, valueLabel);

            nameLabel.setText(name);

            record.addListener(it -> {
                final CSVRecord record = getRecord();
                if (record != null) {
                    final String valueString = record.get(value);
                    if (type.equals(Integer.class) && StringUtils.isNotBlank(valueString)) {
                        valueLabel.setText(NumberFormat.getIntegerInstance().format(Double.parseDouble(valueString)));
                    } else {
                        valueLabel.setText(valueString);
                    }
                } else {
                    valueLabel.setText("");
                }
            });
        }
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
