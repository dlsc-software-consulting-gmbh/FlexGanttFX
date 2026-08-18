/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.hospital.view;

import atlantafx.base.theme.Styles;
import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarEvent;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.DayViewBase;
import com.calendarfx.view.DetailedDayView;
import com.flexganttfx.extras.GanttChartStatusBar;
import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.hospital.model.HospitalActivity;
import com.flexganttfx.hospital.model.HospitalActivityRole;
import com.flexganttfx.hospital.model.HospitalCase;
import com.flexganttfx.hospital.model.HospitalDataModel;
import com.flexganttfx.hospital.model.HospitalDataModel.ScheduleConflict;
import com.flexganttfx.hospital.model.HospitalRow;
import com.flexganttfx.hospital.renderer.HospitalActivityRenderer;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.container.DualGanttChartContainer;
import com.flexganttfx.view.graphics.ActivityEvent;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.layer.InnerLinesLayer;
import com.flexganttfx.view.util.AutoLinesManager;
import com.flexganttfx.view.util.ThemingUtil;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class HospitalView extends BorderPane {

    private static final String CALENDARFX_ATLANTAFX_STYLESHEET = "/com/calendarfx/view/atlantafx.css";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final double OR_LINE_HEIGHT = 24;

    private final HospitalDataModel model = new HospitalDataModel();

    private final GanttChart<HospitalRow> roomChart = new GanttChart<>(model.getRoomRoot());
    private final GanttChart<HospitalRow> resourceChart = new GanttChart<>(model.getResourceRoot());
    private final DetailedDayView dayView = new DetailedDayView();
    private final Label selectionLabel = new Label("Select a case to inspect assignments and calendar details.");
    private final Label conflictLabel = new Label();
    private final ListView<ScheduleConflict> conflictList = new ListView<>();
    private final Tab conflictsTab = new Tab(conflictTabTitle(0));

    private HospitalCase selectedCase;
    private boolean updatingDayView;

    public HospitalView() {
        getStyleClass().add("hospital-view");
        configureCharts();
        configureDayView();
        configureInteractions();

        DualGanttChartContainer container = new DualGanttChartContainer(roomChart, resourceChart);
        container.getMasterDetailPane().setDividerPosition(.5);
        VBox.setVgrow(container, Priority.ALWAYS);

        SplitPane splitPane = new SplitPane(container, buildDetailPane());
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPositions(0.72);

        VBox topArea = new VBox(new GanttChartToolBar<>(roomChart), buildActionBar());
        setTop(topArea);
        setCenter(splitPane);
        setBottom(new GanttChartStatusBar<>(roomChart));

        roomChart.expandRows();
        resourceChart.expandRows();

        if (!model.getCases().isEmpty()) {
            selectedCase = model.getCases().get(0);
            refreshDetailPane();
        }

        Platform.runLater(() -> {
            roomChart.getTimeline().showTime(model.getCases().get(0).getOccupiedStart());
        });
    }

    private void configureCharts() {
        configureChart(roomChart, true);
        configureRoomAutoLines();
        configureChart(resourceChart, false);
        configureResourceAutoLines();
        resourceChart.getGraphics().setShowLinks(false);
        configureConflictList();
    }

    private void configureChart(GanttChart<HospitalRow> chart, boolean includeLinks) {
        chart.getLayers().add(model.getLayer());
        chart.getTimeline().showTemporalUnit(java.time.temporal.ChronoUnit.HOURS, 10);

        GraphicsBase<HospitalRow> graphics = chart.getGraphics();
        graphics.setActivityRenderer(HospitalActivity.class, GanttLayout.class, new HospitalActivityRenderer(graphics));
        graphics.getSelectedActivities().addListener((ListChangeListener<ActivityRef<?>>) change -> updateSelectedCase());
        graphics.getSystemLayer(InnerLinesLayer.class).setLineWidth(0); // hide the inner lines
        configureTreeIcons(chart);

        if (includeLinks) {
            syncLinks();
        }
    }

    private void configureDayView() {
        dayView.setDate(model.getScheduleDate());
        dayView.setPrefHeight(420);
        dayView.setHoursLayoutStrategy(DayViewBase.HoursLayoutStrategy.FIXED_HOUR_COUNT);
        dayView.setShowAgendaView(false);
        dayView.setShowScrollBar(true);
        dayView.getWeekendDays().clear(); // hide weekend background colors
        dayView.setEntryDetailsCallback(param -> false); // do not show popover
        dayView.setEarlyLateHoursStrategy(DayViewBase.EarlyLateHoursStrategy.SHOW_COMPRESSED);
        dayView.setEntryEditPolicy(param -> param.getEntry() != null && param.getEntry().getUserObject() instanceof HospitalCase);
        applyThemeStylesheet();
    }

    public void applyThemeStylesheet() {
        String stylesheet = Objects.requireNonNull(CalendarView.class.getResource(CALENDARFX_ATLANTAFX_STYLESHEET)).toExternalForm();
        if (ThemingUtil.isAtlantaFXActive(getScene())) {
            if (!dayView.getStylesheets().contains(stylesheet)) {
                dayView.getStylesheets().add(stylesheet);
            }
        } else {
            dayView.getStylesheets().remove(stylesheet);
        }
    }

    private void configureInteractions() {
        roomChart.getGraphics().setOnActivityChangeFinished(this::handleRoomChartChange);
        resourceChart.getGraphics().setOnActivityChangeFinished(this::handleResourceChartChange);

        roomChart.getGraphics().getListView().addEventHandler(MouseEvent.MOUSE_CLICKED, evt -> handleActivityDoubleClick(roomChart.getGraphics(), evt));
        resourceChart.getGraphics().getListView().addEventHandler(MouseEvent.MOUSE_CLICKED, evt -> handleActivityDoubleClick(resourceChart.getGraphics(), evt));
    }

    private void handleActivityDoubleClick(GraphicsBase<HospitalRow> graphics, javafx.scene.input.MouseEvent evt) {
        if (evt.getButton() != MouseButton.PRIMARY || evt.getClickCount() != 2) {
            return;
        }

        ActivityRef<?> ref = graphics.getActivityRefAt(evt.getX(), evt.getY());
        if (ref != null) {
            openEditDialog(ref);
        }
    }

    private Node buildActionBar() {
        Button addCaseButton = new Button("Add Case");
        addCaseButton.setOnAction(evt -> showCaseEditor(null, false));

        Button emergencyButton = new Button("Emergency Admission");
        emergencyButton.setOnAction(evt -> showCaseEditor(null, true));

        Button editCaseButton = new Button("Edit Selected Case");
        editCaseButton.setOnAction(evt -> showCaseEditor(selectedCase, selectedCase != null && selectedCase.isEmergency()));

        Button resolveButton = new Button("Resolve Conflicts");
        resolveButton.setOnAction(evt -> showConflictResolver());

        Button resourceButton = new Button("Resource Details");
        resourceButton.setOnAction(evt -> openResourceDetails(null));

        Button showNowButton = new Button("Show Selected");
        showNowButton.setOnAction(evt -> {
            if (selectedCase != null) {
                roomChart.getTimeline().showTime(selectedCase.getOccupiedStart());
            }
        });

        HBox actions = new HBox(8, addCaseButton, emergencyButton, editCaseButton, resolveButton, resourceButton, showNowButton);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(8, 12, 8, 12));
        actions.getStyleClass().addAll("action-bar", "showcase-gantt-toolbar");
        return actions;
    }

    private Node buildDetailPane() {
        selectionLabel.setWrapText(true);
        selectionLabel.setMinHeight(60);
        selectionLabel.setPadding(new Insets(10));

        conflictList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        dayView.setPadding(new Insets(10));

        Tab dayViewTab = new Tab("Day View", dayView);
        dayViewTab.setClosable(false);

        VBox conflictsBox = new VBox(4, conflictLabel, conflictList);
        VBox.setVgrow(conflictList, Priority.ALWAYS);

        conflictsTab.setClosable(false);
        conflictsTab.setContent(conflictsBox);

        TabPane tabPane = new TabPane(dayViewTab, conflictsTab);
        tabPane.getSelectionModel().select(dayViewTab);
        tabPane.setPrefWidth(420);
        tabPane.getStyleClass().add(Styles.TABS_BORDER_TOP);

        VBox.setVgrow(tabPane, Priority.ALWAYS);

        return tabPane;
    }

    private void handleRoomChartChange(ActivityEvent evt) {
        if (!(evt.getActivityRef().getActivity() instanceof HospitalActivity)) {
            return;
        }
        HospitalActivity activity = (HospitalActivity) evt.getActivityRef().getActivity();

        HospitalCase hospitalCase = activity.getUserObject();
        selectedCase = hospitalCase;

        hospitalCase.setOccupiedInterval(activity.getStartTime(), activity.getEndTime());
        hospitalCase.setRoomName(evt.getNewRow() == null ? evt.getActivityRef().getRow().getName() : evt.getNewRow().getName());
        model.syncCase(hospitalCase);
        refreshAll();
    }

    private void handleResourceChartChange(ActivityEvent evt) {
        if (!(evt.getActivityRef().getActivity() instanceof HospitalActivity)) {
            return;
        }
        HospitalActivity activity = (HospitalActivity) evt.getActivityRef().getActivity();

        HospitalCase hospitalCase = activity.getUserObject();
        selectedCase = hospitalCase;

        switch (activity.getRole()) {
            case SURGEON:
                hospitalCase.setSurgeonName(resolveRowName(evt));
                break;
            case ANESTHESIA:
                hospitalCase.setAnesthesiologistName(resolveRowName(evt));
                break;
            case EQUIPMENT:
                hospitalCase.setEquipmentName(resolveRowName(evt));
                break;
            default:
                model.syncCase(hospitalCase);
                refreshAll();
                return;
        }

        hospitalCase.setSurgeryInterval(activity.getStartTime(), activity.getEndTime());
        model.syncCase(hospitalCase);
        refreshAll();
    }

    private String resolveRowName(ActivityEvent evt) {
        return evt.getNewRow() == null ? evt.getActivityRef().getRow().getName() : evt.getNewRow().getName();
    }

    private void refreshAll() {
        relayoutRoomRows();
        relayoutResourceRows();
        syncLinks();
        refreshDetailPane();
        roomChart.getGraphics().redraw();
        resourceChart.getGraphics().redraw();
    }

    private void configureRoomAutoLines() {
        GraphicsBase<HospitalRow> graphics = roomChart.getGraphics();
        for (HospitalRow row : model.getRoomRows()) {
            row.setLinesManager(new AutoLinesManager<>(row, graphics));
            configureAutoLineRowHeight(row);
        }
        relayoutRoomRows();
    }

    private void configureResourceAutoLines() {
        GraphicsBase<HospitalRow> graphics = resourceChart.getGraphics();
        for (HospitalRow row : model.getResourceRows()) {
            row.setLinesManager(new AutoLinesManager<>(row, graphics));
            configureAutoLineRowHeight(row);
        }
        relayoutResourceRows();
    }

    private void configureAutoLineRowHeight(HospitalRow row) {
        row.lineCountProperty().addListener((obs, oldValue, newValue) -> updateRoomRowHeight(row));
        updateRoomRowHeight(row);
    }

    private void updateRoomRowHeight(HospitalRow row) {
        double rowHeight = row.getLineCount() * OR_LINE_HEIGHT;
        row.setMinHeight(rowHeight);
        row.setMaxHeight(rowHeight);
        row.setHeight(rowHeight);
    }

    private void relayoutRoomRows() {
        for (HospitalRow row : model.getRoomRows()) {
            layoutAutoLines(row);
        }
    }

    private void relayoutResourceRows() {
        for (HospitalRow row : model.getResourceRows()) {
            layoutAutoLines(row);
        }
    }

    private void layoutAutoLines(HospitalRow row) {
        if (row.getLinesManager() instanceof AutoLinesManager) {
            @SuppressWarnings("unchecked")
            AutoLinesManager<HospitalRow, com.flexganttfx.model.Activity> manager =
                    (AutoLinesManager<HospitalRow, com.flexganttfx.model.Activity>) row.getLinesManager();
            manager.layout();
        }
    }

    private void configureTreeIcons(GanttChart<HospitalRow> chart) {
        @SuppressWarnings("unchecked")
        TreeTableColumn<HospitalRow, String> treeColumn = (TreeTableColumn<HospitalRow, String>) chart.getTreeTable().getTreeColumn();
        treeColumn.setCellFactory(column -> new TreeTableCell<HospitalRow, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                HospitalRow row = getTreeTableRow() == null ? null : getTreeTableRow().getItem();
                setText(item);
                setGraphic(createRowIcon(row));
            }
        });
    }

    private void configureConflictList() {
        conflictList.setCellFactory(view -> new ListCell<>() {
            private final FontIcon icon = new FontIcon();
            private final Label titleLabel = new Label();
            private final Label detailLabel = new Label();
            private final Button resolveButton = new Button("Resolve");

            private final VBox textBox = new VBox(4, titleLabel, detailLabel);
            private final HBox content = new HBox(10, icon, textBox, resolveButton);

            {
                titleLabel.setWrapText(true);

                detailLabel.setWrapText(true);
                detailLabel.getStyleClass().add("hospital-conflict-detail-label");
                detailLabel.setOpacity(0.8);

                textBox.setPrefWidth(0);

                HBox.setHgrow(textBox, Priority.ALWAYS);

                content.setAlignment(Pos.CENTER_LEFT);
                content.setPadding(new Insets(6, 0, 6, 0));

                resolveButton.setOnAction(evt -> {
                    ScheduleConflict conflict = getItem();
                    if (conflict != null) {
                        model.applySuggestion(conflict);
                        selectedCase = conflict.getSecond().getUserObject();
                        refreshAll();
                    }
                    evt.consume();
                });
            }

            @Override
            protected void updateItem(ScheduleConflict conflict, boolean empty) {
                super.updateItem(conflict, empty);

                if (empty || conflict == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                MaterialDesign iconCode = iconForConflict(conflict);
                icon.setIconCode(iconCode);
                icon.setIconSize(18);
                icon.setIconColor(colorForConflict(conflict));

                titleLabel.setText(conflict.getRowName() + " conflict");
                detailLabel.setText(buildConflictDetail(conflict));
                setText(null);
                setGraphic(content);
            }
        });
    }

    private void syncLinks() {
        roomChart.getLinks().clear();
        model.getLinks().forEach(roomChart.getLinks()::add);
    }

    private void updateSelectedCase() {
        selectedCase = selectedCaseFrom(roomChart.getGraphics().getSelectedActivities());
        if (selectedCase == null) {
            selectedCase = selectedCaseFrom(resourceChart.getGraphics().getSelectedActivities());
        }
        refreshDetailPane();
    }

    private HospitalCase selectedCaseFrom(List<ActivityRef<?>> refs) {
        if (refs == null || refs.isEmpty()) {
            return null;
        }
        ActivityRef<?> ref = refs.get(0);
        if (ref.getActivity() instanceof HospitalActivity) {
            HospitalActivity activity = (HospitalActivity) ref.getActivity();
            return activity.getUserObject();
        }
        return null;
    }

    private void refreshDetailPane() {
        List<ScheduleConflict> conflicts = model.findConflicts();
        conflictList.getItems().setAll(conflicts);
        conflictsTab.setText(conflictTabTitle(conflicts.size()));

        if (selectedCase == null) {
            selectionLabel.setText("Select a surgery or resource assignment to inspect its room, staff, equipment, and day plan.");
            updatingDayView = true;
            try {
                dayView.getCalendarSources().clear();
                dayView.setDate(model.getScheduleDate());
            } finally {
                updatingDayView = false;
            }
            return;
        }

        selectionLabel.setText(selectedCase.getDisplayName() + " - " + selectedCase.getProcedureName()
                + "\nOccupied: " + formatTime(selectedCase.getOccupiedStart()) + " - " + formatTime(selectedCase.getOccupiedEnd())
                + "\nProcedure: " + formatTime(selectedCase.getSurgeryStart()) + " - " + formatTime(selectedCase.getSurgeryEnd())
                + "\nPrep / Cleanup: " + durationLabel(selectedCase.getPreparationDuration()) + " / " + durationLabel(selectedCase.getCleanupDuration())
                + "\nRoom: " + selectedCase.getRoomName()
                + "\nSurgeon: " + selectedCase.getSurgeonName()
                + "\nAnesthesia: " + selectedCase.getAnesthesiologistName()
                + "\nEquipment: " + selectedCase.getEquipmentName());

        updatingDayView = true;
        try {
            dayView.setDate(selectedCase.getSurgeryStart().atZone(model.getZoneId()).toLocalDate());
            dayView.getCalendarSources().setAll(buildCalendarSource(selectedCase));
        } finally {
            updatingDayView = false;
        }
    }

    private static String conflictTabTitle(int conflictCount) {
        return "Conflicts (" + conflictCount + ")";
    }

    private CalendarSource buildCalendarSource(HospitalCase hospitalCase) {
        CalendarSource source = new CalendarSource("Selection");

        Calendar<DayViewAllocationType> roomCalendar = createDayViewCalendar(hospitalCase.getRoomName(), Calendar.Style.STYLE1, DayViewAllocationType.ROOM);
        Calendar<DayViewAllocationType> surgeonCalendar = createDayViewCalendar(hospitalCase.getSurgeonName(), Calendar.Style.STYLE3, DayViewAllocationType.SURGEON);
        Calendar<DayViewAllocationType> anesthesiaCalendar = createDayViewCalendar(hospitalCase.getAnesthesiologistName(), Calendar.Style.STYLE4, DayViewAllocationType.ANESTHESIA);
        Calendar<DayViewAllocationType> equipmentCalendar = createDayViewCalendar(hospitalCase.getEquipmentName(), Calendar.Style.STYLE5, DayViewAllocationType.EQUIPMENT);

        for (HospitalCase item : model.getCases()) {
            if (item.getRoomName().equals(hospitalCase.getRoomName())) {
                roomCalendar.addEntry(createEntry(item, item.getOccupiedStart(), item.getOccupiedEnd()));
            }
            if (item.getSurgeonName().equals(hospitalCase.getSurgeonName())) {
                surgeonCalendar.addEntry(createEntry(item, item.getSurgeryStart(), item.getSurgeryEnd()));
            }
            if (item.getAnesthesiologistName().equals(hospitalCase.getAnesthesiologistName())) {
                anesthesiaCalendar.addEntry(createEntry(item, item.getSurgeryStart(), item.getSurgeryEnd()));
            }
            if (item.getEquipmentName().equals(hospitalCase.getEquipmentName())) {
                equipmentCalendar.addEntry(createEntry(item, item.getSurgeryStart(), item.getSurgeryEnd()));
            }
        }

        source.getCalendars().addAll(roomCalendar, surgeonCalendar, anesthesiaCalendar, equipmentCalendar);
        return source;
    }

    private Calendar<DayViewAllocationType> createDayViewCalendar(String name, Calendar.Style style, DayViewAllocationType allocationType) {
        Calendar<DayViewAllocationType> calendar = new Calendar<>(name);
        calendar.setStyle(style);
        calendar.setUserObject(allocationType);
        calendar.addEventHandler(event -> handleDayViewCalendarEvent(event, allocationType));
        return calendar;
    }

    private void handleDayViewCalendarEvent(CalendarEvent event, DayViewAllocationType allocationType) {
        if (updatingDayView || event.getEventType() != CalendarEvent.ENTRY_INTERVAL_CHANGED) {
            return;
        }

        Entry<?> entry = event.getEntry();
        if (entry == null || !(entry.getUserObject() instanceof HospitalCase)) {
            return;
        }

        HospitalCase hospitalCase = (HospitalCase) entry.getUserObject();
        selectedCase = hospitalCase;

        Instant start = entry.getStartAsZonedDateTime().toInstant();
        Instant end = entry.getEndAsZonedDateTime().toInstant();

        switch (allocationType) {
            case ROOM:
                hospitalCase.setOccupiedInterval(start, end);
                break;
            case SURGEON:
            case ANESTHESIA:
            case EQUIPMENT:
                hospitalCase.setSurgeryInterval(start, end);
                break;
            default:
                return;
        }

        model.syncCase(hospitalCase);
        refreshAll();
    }

    private Entry<HospitalCase> createEntry(HospitalCase hospitalCase, Instant startTime, Instant endTime) {
        Entry<HospitalCase> entry = new Entry<>(hospitalCase.getPatientName() + " - " + hospitalCase.getProcedureName(), hospitalCase.getId());
        entry.setInterval(
                startTime.atZone(model.getZoneId()).toLocalDateTime(),
                endTime.atZone(model.getZoneId()).toLocalDateTime(),
                model.getZoneId()
        );
        entry.setUserObject(hospitalCase);
        return entry;
    }

    private void showCaseEditor(HospitalCase existingCase, boolean emergencyDefault) {
        Optional<CaseDraft> result = showCaseDialog(existingCase, emergencyDefault);
        result.ifPresent(draft -> {
            if (existingCase == null) {
                HospitalCase created = model.createCase(draft.getPatientName(), draft.getProcedureName(), draft.getRoomName(),
                        draft.getSurgeonName(), draft.getAnesthesiologistName(), draft.getEquipmentName(), draft.getDate(),
                        draft.getHour(), draft.getMinute(), draft.getDurationMinutes(),
                        Duration.ofMinutes(draft.getPreparationMinutes()), Duration.ofMinutes(draft.getCleanupMinutes()),
                        draft.isEmergency());
                model.addCase(created);
                selectedCase = created;
            } else {
                existingCase.setPatientName(draft.getPatientName());
                existingCase.setProcedureName(draft.getProcedureName());
                existingCase.setRoomName(draft.getRoomName());
                existingCase.setSurgeonName(draft.getSurgeonName());
                existingCase.setAnesthesiologistName(draft.getAnesthesiologistName());
                existingCase.setEquipmentName(draft.getEquipmentName());
                existingCase.setEmergency(draft.isEmergency());
                existingCase.setPreparationDuration(Duration.ofMinutes(draft.getPreparationMinutes()));
                existingCase.setCleanupDuration(Duration.ofMinutes(draft.getCleanupMinutes()));
                Instant start = draft.getDate().atTime(draft.getHour(), draft.getMinute()).atZone(model.getZoneId()).toInstant();
                existingCase.setSurgeryInterval(start, start.plus(draft.getDurationMinutes(), java.time.temporal.ChronoUnit.MINUTES));
                model.syncCase(existingCase);
                selectedCase = existingCase;
            }
            refreshAll();
        });
    }

    private Optional<CaseDraft> showCaseDialog(HospitalCase existingCase, boolean emergencyDefault) {
        Dialog<CaseDraft> dialog = new Dialog<>();
        dialog.setTitle(existingCase == null ? (emergencyDefault ? "Emergency Admission" : "Schedule Case") : "Edit Case");
        dialog.initOwner(getScene().getWindow());
        applyCurrentTheme(dialog);

        ButtonType saveButton = new ButtonType(existingCase == null ? "Create" : "Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        TextField patientField = new TextField(existingCase == null ? "" : existingCase.getPatientName());
        TextField procedureField = new TextField(existingCase == null ? "" : existingCase.getProcedureName());
        ComboBox<String> roomBox = new ComboBox<>();
        roomBox.getItems().setAll(model.getRoomNames());
        if (existingCase == null) {
            roomBox.getSelectionModel().select(0);
        } else {
            roomBox.getSelectionModel().select(existingCase.getRoomName());
        }

        ComboBox<String> surgeonBox = new ComboBox<>();
        surgeonBox.getItems().setAll(model.getSurgeonNames());
        if (existingCase == null) {
            surgeonBox.getSelectionModel().select(0);
        } else {
            surgeonBox.getSelectionModel().select(existingCase.getSurgeonName());
        }

        ComboBox<String> anesthesiaBox = new ComboBox<>();
        anesthesiaBox.getItems().setAll(model.getAnesthesiologistNames());
        if (existingCase == null) {
            anesthesiaBox.getSelectionModel().select(0);
        } else {
            anesthesiaBox.getSelectionModel().select(existingCase.getAnesthesiologistName());
        }

        ComboBox<String> equipmentBox = new ComboBox<>();
        equipmentBox.getItems().setAll(model.getEquipmentNames());
        if (existingCase == null) {
            equipmentBox.getSelectionModel().select(0);
        } else {
            equipmentBox.getSelectionModel().select(existingCase.getEquipmentName());
        }

        LocalDate initialDate = existingCase == null
                ? model.getScheduleDate()
                : existingCase.getSurgeryStart().atZone(model.getZoneId()).toLocalDate();
        int initialHour = existingCase == null
                ? (emergencyDefault ? 10 : 8)
                : existingCase.getSurgeryStart().atZone(model.getZoneId()).getHour();
        int initialMinute = existingCase == null
                ? (emergencyDefault ? 45 : 0)
                : existingCase.getSurgeryStart().atZone(model.getZoneId()).getMinute();
        int duration = existingCase == null
                ? (emergencyDefault ? 90 : 120)
                : (int) java.time.Duration.between(existingCase.getSurgeryStart(), existingCase.getSurgeryEnd()).toMinutes();
        int preparationMinutes = existingCase == null
                ? (int) HospitalCase.DEFAULT_PREPARATION_DURATION.toMinutes()
                : (int) existingCase.getPreparationDuration().toMinutes();
        int cleanupMinutes = existingCase == null
                ? (int) HospitalCase.DEFAULT_CLEANUP_DURATION.toMinutes()
                : (int) existingCase.getCleanupDuration().toMinutes();

        DatePicker datePicker = new DatePicker(initialDate);
        Spinner<Integer> hourSpinner = new Spinner<>(6, 20, initialHour);
        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, initialMinute);
        Spinner<Integer> durationSpinner = new Spinner<>(30, 420, duration, 15);
        Spinner<Integer> preparationSpinner = new Spinner<>(0, 180, preparationMinutes, 5);
        Spinner<Integer> cleanupSpinner = new Spinner<>(0, 180, cleanupMinutes, 5);
        CheckBox emergencyBox = new CheckBox("Emergency / high priority");
        emergencyBox.setSelected(existingCase == null ? emergencyDefault : existingCase.isEmergency());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(12));
        grid.addRow(0, new Label("Patient"), patientField);
        grid.addRow(1, new Label("Procedure"), procedureField);
        grid.addRow(2, new Label("Room"), roomBox);
        grid.addRow(3, new Label("Surgeon"), surgeonBox);
        grid.addRow(4, new Label("Anesthesia"), anesthesiaBox);
        grid.addRow(5, new Label("Equipment"), equipmentBox);
        grid.addRow(6, new Label("Date"), datePicker);
        grid.addRow(7, new Label("Start"), new HBox(8, hourSpinner, minuteSpinner));
        grid.addRow(8, new Label("Duration (min)"), durationSpinner);
        grid.addRow(9, new Label("Prep (min)"), preparationSpinner);
        grid.addRow(10, new Label("Cleanup (min)"), cleanupSpinner);
        grid.addRow(11, new Label(""), emergencyBox);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(button -> {
            if (button != saveButton) {
                return null;
            }
            return new CaseDraft(
                    valueOrDefault(patientField.getText(), "Unnamed Patient"),
                    valueOrDefault(procedureField.getText(), "Unspecified Procedure"),
                    roomBox.getValue(),
                    surgeonBox.getValue(),
                    anesthesiaBox.getValue(),
                    equipmentBox.getValue(),
                    datePicker.getValue(),
                    hourSpinner.getValue(),
                    minuteSpinner.getValue(),
                    durationSpinner.getValue(),
                    preparationSpinner.getValue(),
                    cleanupSpinner.getValue(),
                    emergencyBox.isSelected()
            );
        });

        return dialog.showAndWait();
    }

    private void showConflictResolver() {
        List<ScheduleConflict> conflicts = model.findConflicts();
        if (conflicts.isEmpty()) {
            Alert noConflictsAlert = new Alert(Alert.AlertType.INFORMATION, "No conflicts detected right now. Try dragging a case into another booking window.");
            noConflictsAlert.initOwner(getScene().getWindow());
            applyCurrentTheme(noConflictsAlert);
            noConflictsAlert.showAndWait();
            return;
        }

        Dialog<ScheduleConflict> dialog = new Dialog<>();
        dialog.setTitle("Conflict Resolver");
        dialog.initOwner(getScene().getWindow());
        applyCurrentTheme(dialog);
        ButtonType resolveButton = new ButtonType("Apply Suggestion", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(resolveButton, ButtonType.CLOSE);

        ListView<ScheduleConflict> listView = new ListView<>();
        listView.setPlaceholder(new Label("No conflicts detected."));
        listView.getItems().setAll(conflicts);
        listView.getSelectionModel().selectFirst();
        TextArea helpArea = new TextArea("Select a conflict and let the demo shift the later case forward.\n"
                + "This keeps the interaction tangible without hiding the scheduling logic.");
        helpArea.setEditable(false);
        helpArea.setWrapText(true);

        VBox box = new VBox(10, listView, helpArea);
        VBox.setVgrow(listView, Priority.ALWAYS);
        box.setPrefSize(560, 420);
        dialog.getDialogPane().setContent(box);

        dialog.setResultConverter(button -> button == resolveButton ? listView.getSelectionModel().getSelectedItem() : null);
        dialog.showAndWait().ifPresent(conflict -> {
            model.applySuggestion(conflict);
            selectedCase = conflict.getSecond().getUserObject();
            refreshAll();
        });
    }

    private void openResourceDetails(ActivityRef<?> ref) {
        if (ref != null && ref.getActivity() instanceof HospitalActivity) {
            HospitalActivity activity = (HospitalActivity) ref.getActivity();
            selectedCase = activity.getUserObject();
        }

        if (selectedCase == null) {
            Alert noSelectionAlert = new Alert(Alert.AlertType.INFORMATION, "Select a case or resource assignment first.");
            noSelectionAlert.initOwner(getScene().getWindow());
            applyCurrentTheme(noSelectionAlert);
            noSelectionAlert.showAndWait();
            return;
        }

        List<ScheduleConflict> relatedConflicts = model.findConflicts().stream()
                .filter(conflict -> conflict.getFirst().getUserObject().getId().equals(selectedCase.getId())
                        || conflict.getSecond().getUserObject().getId().equals(selectedCase.getId()))
                .collect(Collectors.toList());

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Resource Details");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.initOwner(getScene().getWindow());
        applyCurrentTheme(dialog);

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setText(String.format(
                "Case: %s%n"
                        + "Occupied: %s - %s%n"
                        + "Procedure: %s - %s%n"
                        + "Prep / Cleanup: %s / %s%n"
                        + "Procedure: %s%n"
                        + "Room: %s%n"
                        + "Surgeon: %s%n"
                        + "Anesthesia: %s%n"
                        + "Equipment: %s%n"
                        + "Priority: %s%n%n"
                        + "Related conflicts:%n%s%n",
                selectedCase.getPatientName(),
                formatTime(selectedCase.getOccupiedStart()),
                formatTime(selectedCase.getOccupiedEnd()),
                formatTime(selectedCase.getSurgeryStart()),
                formatTime(selectedCase.getSurgeryEnd()),
                durationLabel(selectedCase.getPreparationDuration()),
                durationLabel(selectedCase.getCleanupDuration()),
                selectedCase.getProcedureName(),
                selectedCase.getRoomName(),
                selectedCase.getSurgeonName(),
                selectedCase.getAnesthesiologistName(),
                selectedCase.getEquipmentName(),
                selectedCase.isEmergency() ? "Emergency" : "Scheduled",
                relatedConflicts.isEmpty()
                        ? "None"
                        : relatedConflicts.stream().map(ScheduleConflict::getMessage).collect(Collectors.joining("\n- ", "- ", ""))
        ));
        textArea.setPrefSize(480, 320);
        dialog.getDialogPane().setContent(textArea);
        dialog.showAndWait();
    }

    private void openEditDialog(ActivityRef<?> ref) {
        if (ref != null && ref.getActivity() instanceof HospitalActivity) {
            HospitalActivity activity = (HospitalActivity) ref.getActivity();
            selectedCase = activity.getUserObject();
            if (activity.getRole() == HospitalActivityRole.SURGERY) {
                showCaseEditor(selectedCase, selectedCase.isEmergency());
            } else {
                openResourceDetails(ref);
            }
        }
    }

    private String valueOrDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private FontIcon createRowIcon(HospitalRow row) {
        FontIcon icon = new FontIcon(iconForRow(row));
        icon.setIconSize(16);
        icon.setIconColor(colorForRow(row));
        return icon;
    }

    private MaterialDesign iconForRow(HospitalRow row) {
        if (row == null) {
            return MaterialDesign.MDI_HOSPITAL;
        }

        String name = row.getName();
        String parentName = row.getParent() == null ? "" : row.getParent().getName();

        if (name != null && name.startsWith("OR-")) {
            return MaterialDesign.MDI_HOSPITAL_BUILDING;
        }
        if ("Theatres".equals(name) || "Operating Rooms".equals(name)) {
            return MaterialDesign.MDI_HOSPITAL;
        }
        if ("Surgeons".equals(name) || "Anesthesia".equals(name) || "Resources".equals(name) || "Equipment".equals(name)) {
            return iconForGroupName(name);
        }
        if ("Surgeons".equals(parentName) || "Anesthesia".equals(parentName)) {
            return MaterialDesign.MDI_STETHOSCOPE;
        }
        if ("Equipment".equals(parentName)) {
            return MaterialDesign.MDI_WRENCH;
        }
        return MaterialDesign.MDI_HOSPITAL;
    }

    private MaterialDesign iconForGroupName(String name) {
        if ("Surgeons".equals(name) || "Anesthesia".equals(name)) {
            return MaterialDesign.MDI_STETHOSCOPE;
        }
        if ("Equipment".equals(name)) {
            return MaterialDesign.MDI_WRENCH;
        }
        return MaterialDesign.MDI_HOSPITAL;
    }

    private Color colorForRow(HospitalRow row) {
        if (row == null) {
            return Color.DARKSLATEBLUE;
        }

        MaterialDesign icon = iconForRow(row);
        if (icon == MaterialDesign.MDI_STETHOSCOPE) {
            return Color.valueOf("#C34A36");
        }
        if (icon == MaterialDesign.MDI_WRENCH) {
            return Color.valueOf("#F9A826");
        }
        return Color.valueOf("#1F6FEB");
    }

    private MaterialDesign iconForConflict(ScheduleConflict conflict) {
        if (conflict.isRoomConflict()) {
            return MaterialDesign.MDI_HOSPITAL_BUILDING;
        }

        HospitalActivityRole role = conflict.getFirst().getRole();
        if (role == HospitalActivityRole.EQUIPMENT) {
            return MaterialDesign.MDI_WRENCH;
        }
        return MaterialDesign.MDI_STETHOSCOPE;
    }

    private Color colorForConflict(ScheduleConflict conflict) {
        MaterialDesign icon = iconForConflict(conflict);
        if (icon == MaterialDesign.MDI_WRENCH) {
            return Color.valueOf("#F9A826");
        }
        if (icon == MaterialDesign.MDI_STETHOSCOPE) {
            return Color.valueOf("#C34A36");
        }
        return Color.valueOf("#1F6FEB");
    }

    private String buildConflictDetail(ScheduleConflict conflict) {
        HospitalCase firstCase = conflict.getFirst().getUserObject();
        HospitalCase secondCase = conflict.getSecond().getUserObject();
        Instant overlapStart = conflict.getFirst().getStartTime().isAfter(conflict.getSecond().getStartTime())
                ? conflict.getFirst().getStartTime()
                : conflict.getSecond().getStartTime();
        Instant overlapEnd = conflict.getFirst().getEndTime().isBefore(conflict.getSecond().getEndTime())
                ? conflict.getFirst().getEndTime()
                : conflict.getSecond().getEndTime();

        String timeWindow = formatTime(overlapStart) + " - " + formatTime(overlapEnd);
        return firstCase.getPatientName() + " overlaps with " + secondCase.getPatientName()
                + " during " + timeWindow
                + " (" + durationLabel(Duration.between(overlapStart, overlapEnd)) + ").";
    }

    private String formatTime(Instant instant) {
        return TIME_FORMATTER.format(instant.atZone(model.getZoneId()));
    }

    private String durationLabel(Duration duration) {
        long minutes = Math.max(duration.toMinutes(), 0);
        return minutes + " min";
    }

    private void applyCurrentTheme(Dialog<?> dialog) {
        String uas = getScene().getUserAgentStylesheet();
        dialog.getDialogPane().sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setUserAgentStylesheet(uas);
            }
        });
    }

    private static final class CaseDraft {

        private final String patientName;
        private final String procedureName;
        private final String roomName;
        private final String surgeonName;
        private final String anesthesiologistName;
        private final String equipmentName;
        private final LocalDate date;
        private final int hour;
        private final int minute;
        private final int durationMinutes;
        private final int preparationMinutes;
        private final int cleanupMinutes;
        private final boolean emergency;

        private CaseDraft(String patientName, String procedureName, String roomName, String surgeonName,
                          String anesthesiologistName, String equipmentName, LocalDate date, int hour,
                          int minute, int durationMinutes, int preparationMinutes, int cleanupMinutes,
                          boolean emergency) {
            this.patientName = patientName;
            this.procedureName = procedureName;
            this.roomName = roomName;
            this.surgeonName = surgeonName;
            this.anesthesiologistName = anesthesiologistName;
            this.equipmentName = equipmentName;
            this.date = date;
            this.hour = hour;
            this.minute = minute;
            this.durationMinutes = durationMinutes;
            this.preparationMinutes = preparationMinutes;
            this.cleanupMinutes = cleanupMinutes;
            this.emergency = emergency;
        }

        public String getPatientName() {
            return patientName;
        }

        public String getProcedureName() {
            return procedureName;
        }

        public String getRoomName() {
            return roomName;
        }

        public String getSurgeonName() {
            return surgeonName;
        }

        public String getAnesthesiologistName() {
            return anesthesiologistName;
        }

        public String getEquipmentName() {
            return equipmentName;
        }

        public LocalDate getDate() {
            return date;
        }

        public int getHour() {
            return hour;
        }

        public int getMinute() {
            return minute;
        }

        public int getDurationMinutes() {
            return durationMinutes;
        }

        public int getPreparationMinutes() {
            return preparationMinutes;
        }

        public int getCleanupMinutes() {
            return cleanupMinutes;
        }

        public boolean isEmergency() {
            return emergency;
        }
    }

    private enum DayViewAllocationType {
        ROOM,
        SURGEON,
        ANESTHESIA,
        EQUIPMENT
    }
}
