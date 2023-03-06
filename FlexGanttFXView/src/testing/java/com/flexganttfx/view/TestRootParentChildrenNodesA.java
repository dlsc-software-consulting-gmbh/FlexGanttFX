package com.flexganttfx.view;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Cell;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.TreeTableViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class TestRootParentChildrenNodesA extends Application {
    private ListView<Entry> listView = new ListView<>();
    private TreeTableView<Entry> tableView = new TreeTableView<>();

    private VirtualFlow<ListCell<Entry>> listVirtualFlow;
    private VirtualFlow<TreeTableRow<Entry>> tableVirtualFlow;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(this.getClass().getName());
        TreeTableColumn<Entry, String> nameColumn = new TreeTableColumn<>("Name");
        nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(170);
        nameColumn.setCellFactory(column -> new TreeTableCell<>() {
            {
                setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    TreeItem<Entry> treeItem = getTreeTableRow().getTreeItem();
                    if (treeItem != null) {
                        Entry entry = treeItem.getValue();
                        setMinHeight(entry.getHeight());
                        setPrefHeight(entry.getHeight());
                        setMaxHeight(entry.getHeight());
                        setText(item);
                    }
                } else {
                    setText("");
                }
            }
        });

        TreeItem<Entry> root = createRows();

        tableView.setSkin(new TreeTableViewSkin<>(tableView) {
            @Override
            protected VirtualFlow<TreeTableRow<Entry>> createVirtualFlow() {
                tableVirtualFlow = new VirtualFlow<>() {
                };
                return tableVirtualFlow;
            }
        });
        tableView.getColumns().add(nameColumn);
        tableView.setRoot(root);
        tableView.setShowRoot(true);
        tableView.setPrefWidth(300);
        tableView.getStyleClass().add("gantt-tree-table-view");

        listView.setSkin(new ListViewSkin<>(listView) {
                             @Override
                             protected VirtualFlow<ListCell<Entry>> createVirtualFlow() {
                                 listVirtualFlow = new VirtualFlow<>() {
                                 };

                                 listVirtualFlow.positionProperty().addListener(o -> {
                                     int ls = listView.getItems().size();
                                     int i = 0;
                                     Cell me = null;
                                     while ((me == null) && (i < ls)) {
                                         me = listVirtualFlow.getVisibleCell(i);
                                         i++;
                                     }
                                     if (me != null) {
                                         i = i - 1;
                                         double delta = -1 * me.getLayoutY();
                                         tableView.scrollTo(i);
                                         tableView.layout();
                                         tableVirtualFlow.scrollPixels(delta);
                                         tableView.layout();
                                     }
                                 });
                                 return listVirtualFlow;
                             }
                         }
        );
        listView.getStyleClass().add("graphics-list-view");
        listView.setCellFactory(listView -> new ListCell<>() {

            @Override
            protected void updateItem(Entry item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setMinHeight(item.getHeight());
                    setPrefHeight(item.getHeight());
                    setMaxHeight(item.getHeight());
                    setText(item.getName());
                } else {
                    setText("");
                }
            }
        });
        Region spacer = new Region();
        spacer.getStyleClass().add("spacer");
        spacer.setStyle("-fx-background-color: lightgrey;");
        spacer.setPrefHeight(24);

        VBox rightHandSide = new VBox(spacer, listView);
        VBox.setVgrow(listView, Priority.ALWAYS);

        FlowPane flowPane = new FlowPane();
        flowPane.setMaxWidth(30);
        Button buttonScrollDownTv = new Button("Scroll down TreeTableView");
        buttonScrollDownTv.setOnAction((e) -> {
            tableView.scrollTo(listView.getItems().size() - 1);
        });
        Button buttonScrollUpTv = new Button("Scroll up TreeTableView");
        buttonScrollUpTv.setOnAction((e) -> {
            tableView.scrollTo(0);
        });

        Button buttonScrollDownLv = new Button("Scroll down ListView");
        buttonScrollDownLv.setOnAction((e) -> {
            listView.scrollTo(listView.getItems().size() - 1);
        });
        Button buttonScrollUpLv = new Button("Scroll up ListView");
        buttonScrollUpLv.setOnAction((e) -> {
            listView.scrollTo(0);
        });

        flowPane.getChildren().addAll(buttonScrollDownTv, buttonScrollUpTv, buttonScrollDownLv, buttonScrollUpLv);
        HBox hBox = new HBox(tableView, rightHandSide, flowPane);
        HBox.setHgrow(tableView, Priority.NEVER);
        HBox.setHgrow(rightHandSide, Priority.ALWAYS);

        Scene scene = new Scene(hBox);
        //scene.getStylesheets().add(TestRootParentChildrenNodesA.class.getResource("test.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setWidth(900);
        primaryStage.setHeight(700);
        primaryStage.centerOnScreen();
        primaryStage.show();

        root.setExpanded(true);
    }

    private TreeItem<Entry> createRows() {
        TreeItem<Entry> root = new TreeItem<>(new Entry("Root", 25));
        for (int i = 0; i < 10; i++) {
            TreeItem<Entry> parent = new TreeItem<>(new Entry("Parent " + i, 25));
            parent.setExpanded(true);
            root.getChildren().add(parent);
            parent.expandedProperty().addListener(it -> updateRows());
            for (int j = 0; j < 10 + i; j++) {
                TreeItem<Entry> treeItem = new TreeItem<>(new Entry("Child " + i + "/" + j));
                parent.getChildren().add(treeItem);
            }
        }
        root.expandedProperty().addListener(it -> updateRows());
        return root;
    }

    private void updateRows() {
        List<Entry> rows = new ArrayList<>();
        TreeItem<Entry> root = tableView.getRoot();
        rows.add(root.getValue());
        if (root.isExpanded()) {
            for (int i = 0; i < root.getChildren().size(); i++) {
                TreeItem<Entry> parent = root.getChildren().get(i);
                rows.add(parent.getValue());
                if (parent.isExpanded()) {
                    for (int j = 0; j < parent.getChildren().size(); j++) {
                        rows.add(parent.getChildren().get(j).getValue());
                    }
                }
            }
        }
        listView.getItems().setAll(rows);
    }

    public static class Entry {
        static int index = -1;
        private String name;
        private double height = 40;

        public Entry(String name, int height) {
            this.name = name + " h: " + height;
            this.height = height;
        }

        public Entry(String name) {

            switch (++index % 4) {
                case 0:
                    setHeight(50);
                    break;
                case 1:
                    setHeight(30);
                    break;
                case 2:
                    setHeight(70);
                    break;
                case 3:
                    setHeight(100);
                    break;
            }
            this.name = name + " h: " + getHeight();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getHeight() {
            return height;
        }

        public void setHeight(double height) {
            this.height = height;
        }
    }

    public static void main(String[] args) {
        System.out.println("v: " + System.getProperty("javafx.version") + ", position property with calculation");
        launch(args);
    }
}