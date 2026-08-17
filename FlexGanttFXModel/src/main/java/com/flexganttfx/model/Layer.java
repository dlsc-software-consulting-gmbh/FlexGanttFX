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
package com.flexganttfx.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import static java.util.Objects.requireNonNull;

/**
 * Layers are used to group activities together. Activities on the same layer
 * are drawn at the same time (z-order). A layer has a name, an ID, it can be
 * turned on / off, and their opacity can be changed. These changes have an
 * impact on all activities on that layer.
 * <p>
 * The ID of the layer is used for drag and drop operations of activities
 * between different Gantt charts. Dropped activities will be added to the layer
 * with the same ID. The layer name will be used as the default ID for newly
 * created layers. The ID only needs to be changed if the same layer type will
 * be used with different names in different Gantt charts.
 *
 * <h2>Code Example</h2>
 *
 * <pre>
 * Layer flights = new Layer("Flights");
 * Layer maintenance = new Layer("Maintenance");
 * maintenance.setOpacity(.5);
 *
 * ganttChart.getLayers().addAll(flights, maintenance);
 *
 * aircraft.addActivity(flights, new Flight(flightData));
 * </pre>
 *
 * @see Row#addActivity(Layer, Activity)
 * @see ActivityRef#getLayer()
 * @see ActivityRepository#getActivities(Layer, java.time.Instant, java.time.Instant, java.time.temporal.TemporalUnit, java.time.ZoneId)
 * @since 1.0
 */
public class Layer {

	/**
	 * Constructs a new layer with the given name.
	 * 
	 * @param name
	 *            the name of the layer
	 * @throws NullPointerException if the given name is {@code null}
	 * @since 1.0
	 */
	public Layer(String name) {
		requireNonNull(name);

		setName(name);
		setId(getName());
	}

	/**
	 * Constructs a new layer.
	 * 
	 * @since 1.0
	 */
	public Layer() {
		setId(getName());
	}

	private final StringProperty name = new SimpleStringProperty(this, "name",
			"Untitled");

	/**
	 * The property used to store the name of the layer.
	 * 
	 * <p>
	 * Setting the name to {@code null} will cause a {@link NullPointerException}.
	 *
	 * @return the name of the layer
	 * @since 1.0
	 */
	public final StringProperty nameProperty() {
		return name;
	}

	/**
	 * Sets the value of the {@link #nameProperty()}.
	 * 
	 * @param name
	 *            the new name of the layer
	 * @since 1.0
	 */
	public final void setName(String name) {
		requireNonNull(name);
		nameProperty().set(name);
	}

	/**
	 * Returns the value of the {@link #nameProperty()}.
	 * 
	 * @return the layer name
	 * @since 1.0
	 */
	public final String getName() {
		return nameProperty().get();
	}

	private final StringProperty id = new SimpleStringProperty(this, "id");

	/**
	 * The property used to store the id of the layer.
	 * 
	 * <p>
	 * Setting the ID to {@code null} will cause a {@link NullPointerException}.
	 *
	 * @return the id of the layer
	 * @since 1.0
	 */
	public final StringProperty idProperty() {
		return id;
	}

	/**
	 * Sets the value of the {@link #idProperty()}.
	 * 
	 * @param id
	 *            the new id of the layer
	 * @since 1.0
	 */
	public final void setId(String id) {
		requireNonNull(id);
		idProperty().set(id);
	}

	/**
	 * Returns the value of the {@link #idProperty()}.
	 * 
	 * @return the layer id
	 * @since 1.0
	 */
	public final String getId() {
		return idProperty().get();
	}

	private final DoubleProperty opacity = new SimpleDoubleProperty(this, "opacity",
			1);

	/**
	 * The property used to store the opacity of the layer.
	 * 
	 * @return the layer opacity
	 * @since 1.0
	 */
	public final DoubleProperty opacityProperty() {
		return opacity;
	}

	/**
	 * Sets the value of the {@link #opacityProperty()}.
	 * 
	 * @param opacity
	 *            the new opacity of the layer
	 * @since 1.0
	 */
	public final void setOpacity(double opacity) {
		opacityProperty().set(opacity);
	}

	/**
	 * Returns the value of {@link #opacityProperty()}.
	 * 
	 * @return the opacity of the layer
	 * @since 1.0
	 */
	public final double getOpacity() {
		return opacityProperty().get();
	}

	private final DoubleProperty fadeInOutOpacity = new SimpleDoubleProperty(this, "fadeInOutOpacity", 1);

	/**
	 * The property used to store the temporary opacity of the layer while the
	 * layer is being made visible or hidden. This property is needed for the
	 * animation that takes place while this is done.
	 * 
	 * @return the fade in / fade out temporary opacity of the layer
	 * @since 1.0
	 */
	public final DoubleProperty fadeInOutOpacityProperty() {
		return fadeInOutOpacity;
	}

	/**
	 * Returns the value of {@link #fadeInOutOpacityProperty()}.
	 * 
	 * @return the fade in / fade out opacity value
	 * @since 1.0
	 */
	public final double getFadeInOutOpacity() {
		return fadeInOutOpacity.get();
	}

	/**
	 * Sets the value of {@link #fadeInOutOpacityProperty()}.
	 * 
	 * @param opacity
	 *            the new opacity
	 * @since 1.0
	 */
	public final void setFadeInOutOpacity(double opacity) {
		fadeInOutOpacityProperty().set(opacity);
	}

	private final BooleanProperty visible = new SimpleBooleanProperty(this,
			"visible", true);

	/**
	 * The property used to store the visibility of the layer.
	 * 
	 * @return the visibility of the layer
	 * @since 1.0
	 */
	public final BooleanProperty visibleProperty() {
		return visible;
	}

	/**
	 * Returns the value of {@link #visibleProperty()}.
	 * 
	 * @return true if the layer is visible
	 * @since 1.0
	 */
	public final boolean isVisible() {
		return visible.get();
	}

	/**
	 * Sets the value of {@link #visibleProperty()}.
	 * 
	 * @param visible
	 *            the new visibility of the layer
	 */
	public final void setVisible(boolean visible) {
		visibleProperty().set(visible);
	}

	private final BooleanProperty deletable = new SimpleBooleanProperty(this,
			"deletable", true);

	/**
	 * The property used to define whether the layer can be deleted by the user
	 * or not.
	 * 
	 * @return true if the layer can be deleted
	 * @since 1.0
	 */
	public final BooleanProperty deletableProperty() {
		return deletable;
	}

	/**
	 * Returns the value of {@link #deletableProperty()}.
	 * 
	 * @return true if the layer can be deleted
	 * @since 1.0
	 */
	public final boolean isDeletable() {
		return deletable.get();
	}

	/**
	 * Sets the value of {@link #deletableProperty()}.
	 * 
	 * @param deletable
	 *            the new deletable value
	 * @since 1.0
	 */
	public final void setDeletable(boolean deletable) {
		deletableProperty().set(deletable);
	}

	@Override
	public String toString() {
		return getName();
	}
}
