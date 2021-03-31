/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.treetable;

import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChart;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * A specialization of tree item for use with the {@link GanttChart} tree table
 * view.
 *
 * @param <R>
 *            the type of the rows shown by the tree table view
 */
public final class GanttChartTreeItem<R extends Row<?, ?, ?>> extends TreeItem<R> {

	/**
	 * Constructs a new tree item.
	 *
	 * @param row
	 *            the wrapped row
	 * @param graphic
	 *            a graphic node (e.g. an icon)
	 */
	public GanttChartTreeItem(R row, Node graphic) {
		super(row, graphic);

		init();
	}

	/**
	 * Constructs a new tree item.
	 *
	 * @param row
	 *            the wrapped row
	 */
	public GanttChartTreeItem(R row) {
		super(row);

		init();
	}

	/**
	 * Constructs a new tree item.
	 */
	public GanttChartTreeItem() {
		super();

		init();
	}

	private final ObjectProperty<Predicate> filter = new SimpleObjectProperty<>(this, "filter", row -> true);

	/**
	 * A predicate used to filter the children of this tree item.
	 *
	 * @return the filter predicate
	 */
	public final ObjectProperty<Predicate> filterProperty() {
		return filter;
	}

	private Predicate getFilter() {
		return filter.get();
	}

	@Override
	public boolean isLeaf() {
		return getValue().isLeaf();
	}

	private final InvalidationListener updateChildrenListener = it -> updateChildren();

	private final WeakInvalidationListener weakUpdateChildrenListener = new WeakInvalidationListener(updateChildrenListener);

	private void init() {
		final R row = getRow();

		Bindings.bindBidirectional(expandedProperty(), row.expandedProperty());

		ObservableList<? extends Row<?, ?, ?>> children = row.getChildren();
		children.addListener(weakUpdateChildrenListener);
		filterProperty().addListener(weakUpdateChildrenListener);

		buildChildren(this);
	}

	private void updateChildren() {
		buildChildren(GanttChartTreeItem.this);
	}

	@SuppressWarnings("unchecked")
	private void buildChildren(GanttChartTreeItem<R> item) {
		R row = item.getRow();

		final Predicate filter = getFilter();

		List<GanttChartTreeItem<R>> newChildren = new ArrayList<>();
		for (Row<?, ?, ?> child : row.getChildren()) {
			if (filter == null || filter.test(child) || child.hasChildren(filter)) {
				GanttChartTreeItem<R> childItem = new GanttChartTreeItem<>((R) child);
				newChildren.add(childItem);
			}
		}

		newChildren.forEach(this::buildChildren);

		/*
		 * Add the children at the very last, otherwise we are causing too many
		 * tree events and the code that creates the list of rows for the graphics
		 * area will be called too often, causing very bad performance.
         *
		 * For more information see issue FLEXFX-301.
		 */
		item.getChildren().setAll(newChildren);
	}

	/**
	 * Returns the row shown by this tree item.
	 *
	 * @return the row
	 */
	public final R getRow() {
		return getValue();
	}

	/**
	 * Returns the level number string for the tree item, e.g. "2.1.7").
	 *
	 * @return the level number
	 */
	public String getLevelNumber() {
		GanttChartTreeItem<R> parent = (GanttChartTreeItem<R>) getParent();
		if (parent != null) {
			String parentLevelNumber = parent.getLevelNumber();
			if (parentLevelNumber != null) {
				return parentLevelNumber + "."
						+ (parent.getChildren().indexOf(this) + 1);
			}

			return Integer.toString(parent.getChildren().indexOf(this) + 1);
		}

		return null;
	}
}
