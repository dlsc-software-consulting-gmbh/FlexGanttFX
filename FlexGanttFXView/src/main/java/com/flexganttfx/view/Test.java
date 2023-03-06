package com.flexganttfx.view;

import com.flexganttfx.view.util.VirtualFlowUtil;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Test extends Application {

    private ListView<Entry> listView = new ListView<>();
    private TreeTableView<Entry> treeTableView = new TreeTableView<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
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

        treeTableView.getColumns().add(nameColumn);
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(true);
        treeTableView.setPrefWidth(300);
        treeTableView.getStyleClass().add("gantt-tree-table-view");

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

        HBox hBox = new HBox(treeTableView, rightHandSide);
        HBox.setHgrow(treeTableView, Priority.NEVER);
        HBox.setHgrow(rightHandSide, Priority.ALWAYS);

        InvalidationListener l = it -> {
            if (treeTableView.getSkin() != null && listView.getSkin() != null) {
                bindVerticalListViewScrollBarWithVerticalTreeTableScrollBar(treeTableView, listView);
            }
        };

        treeTableView.skinProperty().addListener(l);
        listView.skinProperty().addListener(l);

        Scene scene = new Scene(hBox);
        scene.getStylesheets().add(Test.class.getResource("test.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setWidth(900);
        primaryStage.setHeight(700);
        primaryStage.centerOnScreen();
        primaryStage.show();

        root.setExpanded(true);
    }

    private TreeItem<Entry> createRows() {
        TreeItem<Entry> root = new TreeItem<>(new Entry("Root"));
        for (int i = 0; i < 100; i++) {
            TreeItem<Entry> treeItem = new TreeItem<>(new Entry("Child " + i));
            root.getChildren().add(treeItem);
        }
        root.expandedProperty().addListener(it -> updateRows());
        return root;
    }

    private void updateRows() {
        List<Entry> rows = new ArrayList<>();
        TreeItem<Entry> root = treeTableView.getRoot();
        rows.add(root.getValue());
        if (root.isExpanded()) {
            for (int i = 0; i< root.getChildren().size(); i++) {
                rows.add(root.getChildren().get(i).getValue());
            }
        }
        listView.getItems().setAll(rows);
    }

    public static class Entry {
        private String name;
        private double height = 40;

        public Entry(String name) {
            this.name = name;

            switch ((int)(Math.random() * 4)) {
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

    private ScrollBar findScrollBar(Parent parent, Orientation orientation) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof ScrollBar) {
                ScrollBar b = (ScrollBar) node;
                if (b.getOrientation().equals(orientation)) {
                    return b;
                }
            }

            if (node instanceof Parent) {
                ScrollBar b = findScrollBar((Parent) node, orientation);
                if (b != null) {
                    return b;
                }
            }
        }

        return null;
    }

    protected void bindVerticalListViewScrollBarWithVerticalTreeTableScrollBar(TreeTableView treeTableView, ListView listView) {
        protected void bindVerticalListViewScrollBarWithVerticalTreeTableScrollBar(TreeTableView treeTableView, ListView listView)
        {
            VirtualFlowUtil.bindVirtualFlows(treeTableView, listView);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
