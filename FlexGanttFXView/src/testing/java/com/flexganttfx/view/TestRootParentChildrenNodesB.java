package com.flexganttfx.view;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class TestRootParentChildrenNodesB extends Application {
    private ListView<Entry> listView = new ListView<>();
    private TreeTableView<Entry> treeTableView = new TreeTableView<>();
    private VirtualFlow<ListCell<Entry>> listViewVirtualFlow;

    @Override
    public void start(Stage primaryStage) throws Exception {
        listView.setSkin(new ListViewSkin<>(listView) {

            @Override
            protected VirtualFlow<ListCell<Entry>> createVirtualFlow() {
                listViewVirtualFlow = super.createVirtualFlow();
                return listViewVirtualFlow;
            }
        });

        treeTableView.addEventHandler(TreeItem.TreeModificationEvent.ANY, evt -> {
            System.out.println(evt);
            treeTableView.layout();
            listView.layout();
        });

        treeTableView.setRowFactory(treeTableView -> new TreeTableRow<>() {
            @Override
            protected void updateItem(Entry item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setMinHeight(item.getHeight());
                    setPrefHeight(item.getHeight());
                    setMaxHeight(item.getHeight());
                } else {
                    setMinHeight(Region.USE_COMPUTED_SIZE);
                    setPrefHeight(Region.USE_COMPUTED_SIZE);
                    setMaxHeight(Region.USE_COMPUTED_SIZE);
                }
            }

        });

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
                    setText(item);
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

        treeTableView.skinProperty().addListener(it -> bindVirtualFlowPositionProperties());
        listView.skinProperty().addListener(it -> bindVirtualFlowPositionProperties());

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
            treeTableView.scrollTo(listView.getItems().size() - 1);
        });
        Button buttonScrollUpTv = new Button("Scroll up TreeTableView");
        buttonScrollUpTv.setOnAction((e) -> {
            treeTableView.scrollTo(0);
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
        HBox hBox = new HBox(treeTableView, rightHandSide, flowPane);
        HBox.setHgrow(treeTableView, Priority.NEVER);
        HBox.setHgrow(rightHandSide, Priority.ALWAYS);

        Scene scene = new Scene(hBox);
        scene.getStylesheets().add(TestRootParentChildrenNodesB.class.getResource("test.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setWidth(900);
        primaryStage.setHeight(700);
        primaryStage.centerOnScreen();
        primaryStage.show();

        root.setExpanded(true);
    }

    private VirtualFlow findVirtualFlow(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof VirtualFlow) {
                return (VirtualFlow) node;
            }

            if (node instanceof Parent) {
                VirtualFlow b = findVirtualFlow((Parent) node);
                if (b != null) {
                    return b;
                }
            }
        }

        return null;
    }

    private boolean bound;

    protected void bindVirtualFlowPositionProperties() {
        if (!bound) {
            VirtualFlow<?> leftFlow = findVirtualFlow(treeTableView);
            VirtualFlow<?> rightFlow = findVirtualFlow(listView);

            if (leftFlow != null && rightFlow != null) {
                Bindings.bindBidirectional(leftFlow.positionProperty(), rightFlow.positionProperty());
                bound = true;
            }
        }
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
        TreeItem<Entry> root = treeTableView.getRoot();
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
        System.out.println("v: " + System.getProperty("javafx.version") + ", position property binding");
        launch(args);
    }
}