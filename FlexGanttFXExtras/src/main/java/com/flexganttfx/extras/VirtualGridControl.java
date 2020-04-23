/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras;

import static java.util.Objects.requireNonNull;

import com.flexganttfx.extras.util.Messages;
import com.flexganttfx.model.dateline.VirtualGrid;
import com.flexganttfx.view.util.FlexGanttFXControl;

import impl.com.flexganttfx.extras.skin.VirtualGridControlSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;

/**
 * A control used to select a {@link VirtualGrid} from a list of possible
 * virtual grids.
 *
 * @since 1.0
 */
public class VirtualGridControl extends FlexGanttFXControl {

	/**
	 * Constructs a new virtual grid control.
	 *
	 * @since 1.0
	 */
	public VirtualGridControl() {
		getStyleClass().add("virtual-grid-control");
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new VirtualGridControlSkin(this);
	}

	@Override
	public String getUserAgentStylesheet() {
		return super.getUserAgentStylesheet(VirtualGridControl.class, "virtualgrid.css");
	}

	private final StringProperty noGridText = new SimpleStringProperty(this,
			"noGridText", Messages.getString("VirtualGridControl.NO_GRID"));

	/**
	 * Returns the property used for managing the text shown by the "no grid"
	 * button.
	 *
	 * @return the "no grid" text
	 * @since 1.3
	 */
	public final StringProperty noGridTextProperty() {
		return noGridText;
	}

	/**
	 * Sets the value of {@link #noGridTextProperty()}.
	 *
	 * @param text
	 *            the text shown by the "no grid" option.
	 * @since 1.3
	 */
	public final void setNoGridText(String text) {
		requireNonNull(text);
		noGridText.set(text);
	}

	/**
	 * Returns the value of {@link #noGridTextProperty()}.
	 *
	 * @return the text shown by the "no grid" option
	 * @since 1.3
	 */
	public final String getNoGridText() {
		return noGridText.get();
	}

	private final BooleanProperty showNoGridOption = new SimpleBooleanProperty(
			this, "showNoGridOption", true);

	/**
	 * Controls whether the control should present an option to the user to
	 * disable the grid completely.
	 *
	 * @return a property used for controlling the visibility of the "no grid"
	 *         option.
	 *
	 * @since 1.3
	 */
	public final BooleanProperty showNoGridOptionProperty() {
		return showNoGridOption;
	}

	/**
	 * Returns the value of {@link #showNoGridOptionProperty()}.
	 *
	 * @return true if the "no grid" option will be shown
	 */
	public final boolean isShowNoGridOption() {
		return showNoGridOption.get();
	}

	/**
	 * Sets the value of {@link #showNoGridOptionProperty()}.
	 *
	 * @param show
	 *            if true the option will be shown
	 */
	public final void setShowNoGridOption(boolean show) {
		showNoGridOption.set(show);
	}

	private final ObjectProperty<VirtualGrid<?>> value = new SimpleObjectProperty<>(
			this, "value");

	/**
	 * The property used to store the currently selected {@link VirtualGrid}.
	 *
	 * @return the property for the selected grid
	 * @since 1.0
	 */
	public final ObjectProperty<VirtualGrid<?>> valueProperty() {
		return value;
	}

	/**
	 * Returns the value of {@link #valueProperty()}.
	 *
	 * @return the selected virtual grid
	 * @since 1.0
	 */
	public final VirtualGrid<?> getValue() {
		return valueProperty().get();
	}

	/**
	 * Sets the value of {@link #valueProperty()}.
	 *
	 * @param grid
	 *            the grid to select
	 * @since 1.0
	 */
	public final void setValue(VirtualGrid<?> grid) {
		valueProperty().set(grid);
	}

	private ObservableList<VirtualGrid<?>> grids = FXCollections
			.observableArrayList();

	/**
	 * Returns the list of possible virtual grid values.
	 *
	 * @return the possible virtual grids
	 * @since 1.0
	 */
	public final ObservableList<VirtualGrid<?>> getGrids() {
		return grids;
	}
}
