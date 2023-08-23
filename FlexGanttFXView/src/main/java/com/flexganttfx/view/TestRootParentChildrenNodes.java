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
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringJoiner;

public class TestRootParentChildrenNodes extends Application
{

    private ListView<Entry> listView = new ListView<>();
    private TreeTableView<Entry> treeTableView = new TreeTableView<>();

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        TreeTableColumn<Entry, String> nameColumn = new TreeTableColumn<>("Name");
        nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(170);
        nameColumn.setCellFactory(column -> new TreeTableCell<>()
        {
            {
                setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(String item, boolean empty)
            {
                super.updateItem(item, empty);
                if (item != null)
                {
                    TreeItem<Entry> treeItem = getTreeTableRow().getTreeItem();
                    if (treeItem != null)
                    {
                        Entry entry = treeItem.getValue();
                        setMinHeight(entry.getHeight());
                        setPrefHeight(entry.getHeight());
                        setMaxHeight(entry.getHeight());
                        setText(item);
                    }
                }
                else
                {
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
        listView.setCellFactory(listView -> new ListCell<>()
        {

            @Override
            protected void updateItem(Entry item, boolean empty)
            {
                super.updateItem(item, empty);
                if (item != null)
                {
                    setMinHeight(item.getHeight());
                    setPrefHeight(item.getHeight());
                    setMaxHeight(item.getHeight());
                    setText(item.getName());
                }
                else
                {
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

        String javaFxRuntimeVersion = "JavaFX Version: " + System.getProperty("javafx.runtime.version");
        String javaFxVersion = "Java Version: " + System.getProperty("java.version");
        String osName = "OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version");

        flowPane.getChildren().addAll(buttonScrollDownTv, buttonScrollUpTv, buttonScrollDownLv, buttonScrollUpLv, new Label(javaFxVersion), new Label(javaFxRuntimeVersion), new Label(osName));
        HBox hBox = new HBox(treeTableView, rightHandSide, flowPane);
        HBox.setHgrow(treeTableView, Priority.NEVER);
        HBox.setHgrow(rightHandSide, Priority.ALWAYS);

        InvalidationListener l = it -> {
            if (treeTableView.getSkin() != null && listView.getSkin() != null)
            {
                bindVerticalListViewScrollBarWithVerticalTreeTableScrollBar(treeTableView, listView);
            }
        };

        treeTableView.skinProperty().addListener(l);
        listView.skinProperty().addListener(l);

        Scene scene = new Scene(hBox);
        scene.getStylesheets().add(TestRootParentChildrenNodes.class.getResource("test.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setWidth(900);
        primaryStage.setHeight(700);
        primaryStage.centerOnScreen();
        primaryStage.show();

        root.setExpanded(true);
    }

    private TreeItem<Entry> createRows()
    {
        Entry entry = new Entry("Root", 25);
        TreeItem<Entry> root = new TreeItem<>(entry);
        entry.setTreeItem(root);
        for (int i = 0; i < 10; i++)
        {
            entry = new Entry("Parent " + i, 25);
            TreeItem<Entry> parent = new TreeItem<>(entry);
            entry.setTreeItem(parent);
            parent.setExpanded(true);
            root.getChildren().add(parent);
            parent.expandedProperty().addListener(it -> updateRows());
            for (int j = 0; j < 10 + i; j++)
            {
                entry = new Entry("Child " + i + "/" + j);
                TreeItem<Entry> treeItem = new TreeItem<>(entry);
                entry.setTreeItem(treeItem);
                parent.getChildren().add(treeItem);
            }
        }
        root.expandedProperty().addListener(it -> updateRows());
        return root;
    }

    private void updateRows()
    {
        List<Entry> rows = new ArrayList<>();
        TreeItem<Entry> root = treeTableView.getRoot();

        rows.add(root.getValue());
        if (root.isExpanded())
        {
            for (int i = 0; i < root.getChildren().size(); i++)
            {
                TreeItem<Entry> parent = root.getChildren().get(i);
                rows.add(parent.getValue());
                if (parent.isExpanded())
                {
                    for (int j = 0; j < parent.getChildren().size(); j++)
                    {
                        rows.add(parent.getChildren().get(j).getValue());
                    }
                }
            }
        }
        listView.getItems().setAll(rows);
    }

    public static class Entry
    {
        public static final LinkedHashMap<String, Entry> instances = new LinkedHashMap<>();
        static int index = -1;
        private String name;
        private double height = 40;
        TreeItem<Entry> treeItem;

        public Entry(String name, int height)
        {
            this.name = name + " h: " + height;
            this.height = height;
            instances.put(name, this);
        }

        public Entry(String name)
        {

            switch (++index % 4)
            {
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

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public double getHeight()
        {
            return height;
        }

        public void setHeight(double height)
        {
            this.height = height;
        }

        public TreeItem<Entry> getTreeItem()
        {
            return treeItem;
        }

        public void setTreeItem(TreeItem<Entry> treeItem)
        {
            this.treeItem = treeItem;
        }

        @Override
        public String toString()
        {
            return new StringJoiner(", ", Entry.class.getSimpleName() + "[", "]").add("name='" + name + "'").add("height=" + height).toString();
        }
    }

    private ScrollBar findScrollBar(Parent parent, Orientation orientation)
    {
        for (Node node : parent.getChildrenUnmodifiable())
        {
            if (node instanceof ScrollBar)
            {
                ScrollBar b = (ScrollBar) node;
                if (b.getOrientation().equals(orientation))
                {
                    return b;
                }
            }

            if (node instanceof Parent)
            {
                ScrollBar b = findScrollBar((Parent) node, orientation);
                if (b != null)
                {
                    return b;
                }
            }
        }

        return null;
    }

    protected void bindVerticalListViewScrollBarWithVerticalTreeTableScrollBar(TreeTableView treeTableView, ListView listView)
    {
        VirtualFlowUtil.bindVirtualFlows(treeTableView, listView);
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}