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

import static javafx.geometry.VPos.TOP;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class PopOverHeader<T extends Node> extends GridPane {

	private final TextField titleField = new TextField();

	private final TextField subtitleField = new TextField();

	public PopOverHeader() {
		add(titleField, 0, 0);
		add(subtitleField, 0, 1);

		getStyleClass().add("header");

		titleField.getStyleClass().add("title");
		titleField.setEditable(true);
		titleField.setPromptText("Title");
		titleField.setMaxWidth(500);

		subtitleField.getStyleClass().add("subtitle");
		subtitleField.setEditable(true);
		subtitleField.setPromptText("Subtitle");
		subtitleField.setMaxWidth(500);

		Bindings.bindBidirectional(titleField.textProperty(), titleProperty());
		Bindings.bindBidirectional(subtitleField.textProperty(),
				subtitleProperty());

		if (getExtra() != null) {
			add(getExtra(), 1, 0, 1, 2);
			GridPane.setValignment(getExtra(), TOP);
		}

		GridPane.setValignment(titleField, TOP);
		GridPane.setValignment(subtitleField, TOP);
		GridPane.setHgrow(titleField, Priority.ALWAYS);
		GridPane.setHgrow(subtitleField, Priority.ALWAYS);
		GridPane.setFillWidth(titleField, true);
		GridPane.setFillWidth(subtitleField, true);

		extraProperty().addListener((value, oldNode, newNode) -> {
			if (oldNode != null) {
				getChildren().remove(oldNode);
			}
			if (newNode != null) {
				add(newNode, 1, 0, 1, 2);
				GridPane.setValignment(getExtra(), TOP);
			}

			if (newNode != null) {
				String style = "header-extra";
				if (!newNode.getStyleClass().contains(style)) {
					newNode.getStyleClass().add(style);
				}
			}
		});

		@SuppressWarnings("unchecked")
		T picker = (T) new ColorPicker();
		setExtra(picker);
	}

	public final TextField getTitleField() {
		return titleField;
	}

	public final TextField getSubtitleField() {
		return subtitleField;
	}

	// title support

	private final StringProperty title = new SimpleStringProperty(this, "title");

	public final StringProperty titleProperty() {
		return title;
	}

	public final void setTitle(String title) {
		titleProperty().set(title);
	}

	public final String getTitle() {
		return titleProperty().get();
	}

	// subtitle support

	private final StringProperty subtitle = new SimpleStringProperty(this,
			"subtitle");

	public final StringProperty subtitleProperty() {
		return subtitle;
	}

	public final void setSubtitle(String subtitle) {
		subtitleProperty().set(subtitle);
	}

	public final String getSubtitle() {
		return subtitleProperty().get();
	}

	// extras support

	private final ObjectProperty<T> extra = new SimpleObjectProperty<T>(this,
			"extra");

	public final ObjectProperty<T> extraProperty() {
		return extra;
	}

	public final void setExtra(T extra) {
		extraProperty().set(extra);
	}

	public final Node getExtra() {
		return extraProperty().get();
	}
}