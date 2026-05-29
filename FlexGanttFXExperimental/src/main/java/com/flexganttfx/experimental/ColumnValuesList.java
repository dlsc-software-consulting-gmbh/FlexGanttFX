/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing/>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://github.com/dlemmermann/FlexGanttFX/blob/master/LICENSE>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.experimental;

import static java.util.Objects.requireNonNull;
import static javafx.scene.control.SelectionMode.MULTIPLE;

import java.util.function.Predicate;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.Skin;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class ColumnValuesList<S, T> extends Control {

	private final TableColumn<S, T> column;

	private final ListView<T> listView;

	private final ColumnBrowser<S> browser;

	public ColumnValuesList(ColumnBrowser<S> browser, TableColumn<S, T> column) {
		requireNonNull(browser);
		requireNonNull(column);

		this.browser = browser;
		this.column = column;
		this.listView = new ListView<>();
		this.listView.getSelectionModel().setSelectionMode(MULTIPLE);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new ColumnValuesListSkin<>(this);
	}

	public final ColumnBrowser<S> getColumnBrowser() {
		return browser;
	}

	public final TableColumn<S, T> getColumn() {
		return column;
	}

	public final ListView<T> getListView() {
		return listView;
	}

	public final Predicate<S> getPredicate() {
		Predicate<S> predicate = new Predicate<S>() {
			@Override
			public boolean test(S item) {
				TableView<S> table = column.getTableView();
				Callback<CellDataFeatures<S, T>, ObservableValue<T>> valueFactory = column
						.getCellValueFactory();
				ObservableValue<T> value = valueFactory
						.call(new CellDataFeatures<S, T>(table, column, item));

				MultipleSelectionModel<T> selectionModel = listView
						.getSelectionModel();

				if (selectionModel.isEmpty()) {
					return true;
				}

				return selectionModel.getSelectedItems().contains(
						value.getValue());
			}
		};

		return predicate;
	}
}
