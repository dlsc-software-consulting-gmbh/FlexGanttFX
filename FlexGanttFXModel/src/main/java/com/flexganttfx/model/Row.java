/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model;

import com.flexganttfx.model.exception.RepositoryException;
import com.flexganttfx.model.layout.EqualLinesManager;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.model.repository.IntervalTreeActivityRepository;
import com.flexganttfx.model.repository.MutableActivityRepository;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * A row object is used to store the activities found on a row of the Gantt
 * chart. These activities are not stored directly on the row but in an activity
 * repository (see {@link #getRepository()}). The default repository is of type
 * {@link IntervalTreeActivityRepository} and can be replaced by calling
 * {@link #setRepository(ActivityRepository)}. Activities can be placed on lines
 * within the row. The row delegates this work to a {@link LinesManager}. The
 * default manager is of type {@link EqualLinesManager}. To replace the manager
 * simply call {@link #setLinesManager(LinesManager)}.
 *
 * <h2>Code Example</h2>
 *
 * <pre>
 * public class Aircraft extends Row&lt;Fleet, CrewMember, Flight&gt; {
 * }
 * </pre>
 *
 * This now allows you to call:
 *
 * <pre>
 * Aircraft aircraft = new Aircraft();
 * ...
 * Fleet fleet = aircraft.getParent();
 * List&lt;CrewMember&gt; crew = aircraft.getChildren();
 * </pre>
 *
 * <h2>Lazy Loading</h2> Simply override the {@link #isLeaf()} method to control
 * whether a row is a parent row or not. Then listen to changes of the expanded
 * property to load the children when the user toggles the expansion state.
 *
 * <pre>
 *
 * static class HelloLazyRow extends
 *         Row&lt;HelloLazyRow, HelloLazyRow, Activity&gt; {
 *
 *     public HelloLazyRow(String name) {
 *         super(name);
 *
 *         expandedProperty().addListener(it -&gt; loadChildrenLazily());
 *     }
 *
 *     &#064;Override
 *     public boolean isLeaf() {
 *         return false;
 *     }
 *
 *     private void loadChildrenLazily() {
 *         getChildren().add(new HelloLazyRow(&quot;Child 1&quot;));
 *         getChildren().add(new HelloLazyRow(&quot;Child 2&quot;));
 *         ...
 *         getChildren().add(new HelloLazyRow(&quot;Child N&quot;));
 *     }
 * }
 * </pre>
 *
 * @param <P>
 *            the type of the parent row (example: row is of type "Building" and
 *            parent type is "Factory" to express that the factory consists of
 *            several buildings).
 * @param <C>
 *            the type of the children rows (example: row is of type "Building"
 *            and children type is "Machine" to express that the building houses
 *            several machines).
 * @param <A>
 *            the type of the activities shown in this row (example: row is of
 *            type "Building", activities are "ProductionOrders" that are
 *            executed in this building).
 * @since 1.0
 */
public abstract class Row<P extends Row<?, ?, ?>, C extends Row<?, ?, ?>, A extends Activity> {

    /**
     * The default height of a row (24 pixels).
     *
     * @see Row#setHeight(double)
     * @see Row#setMinHeight(double)
     * @see Row#setMaxHeight(double)
     */
    public static final double DEFAULT_ROW_HEIGHT = 24;

    /**
     * Constructs a new row with an {@link IntervalTreeActivityRepository} and
     * an {@link EqualLinesManager}.
     *
     * @since 1.0
     */
    public Row() {
        setRepository(new IntervalTreeActivityRepository<>());
        setLinesManager(new EqualLinesManager<>(this));
    }

    /**
     * Constructs a new row with an {@link IntervalTreeActivityRepository} and
     * an {@link EqualLinesManager} and the given name.
     *
     * @param name
     *            the name of the row (e.g. "Building 1")
     * @since 1.0
     */
    public Row(String name) {
        this();

        setName(name);
    }

    private ObservableMap<Object, Object> properties;

    /**
     * Returns an observable map of properties on this row.
     *
     * @return an observable map of properties on this row
     */
    public final ObservableMap<Object, Object> getProperties() {
        if (properties == null) {
            properties = FXCollections
                    .observableMap(new HashMap<>());

            /*
             * We are "abusing" the properties map to pass new values of
             * read-only properties to the row.
             */
            properties.addListener((
                    Change<?, ?> change) -> {
                if (change.getKey().equals("com.flexganttfx.row.showing")) {
                    if (change.getValueAdded() != null) {
                        Boolean shown = (Boolean) change.getValueAdded();
                        showing.set(shown);
                        properties.remove("com.flexganttfx.row.showing");
                    }
                }
            });
        }
        return properties;
    }

    /**
     * Tests if the row has properties.
     *
     * @return true if node has properties.
     */
    public final boolean hasProperties() {
        return properties != null && !properties.isEmpty();
    }

    // Parent support.

    private final ReadOnlyObjectWrapper<P> parent = new ReadOnlyObjectWrapper<>(
            this, "parent");

    private void setParent(P value) {
        parent.setValue(value);
    }

    /**
     * The parent of this row. Each row can have only one parent. If a row has
     * no parent, it represents a root in the tree model.
     *
     * @return the parent of this row, or null if the row has no parent.
     * @since 1.0
     */
    public final P getParent() {
        return parent == null ? null : parent.getValue();
    }

    /**
     * Returns a read-only property used to store the parent row of this row.
     *
     * @return the property used for storing the parent row
     * @since 1.0
     */
    public final ReadOnlyObjectProperty<P> parentProperty() {
        return parent.getReadOnlyProperty();
    }

    // called whenever the contents of the children sequence changes
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private final ListChangeListener<Row> childrenChangeListener = c -> {
        while (c.next()) {
            for (Row row : c.getRemoved()) {
                row.setParent(null);
            }
            for (Row row : c.getAddedSubList()) {
                row.setParent(Row.this);
            }
        }
    };

    // IMPORTANT: we DO need a separate change and an invalidation listener. Read comment below!

    // called whenever the children list has changed. We need to have a separate invalidation
    // listener as those get called before change listeners. The GanttChartTreeItem only uses
    // an invalidation listener and would be executed before the code in this class, which can
    // cause all kinds of issues when updating the list of rows inside the GanttChartSkin class.
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private final InvalidationListener childrenInvalidationListener = c -> {

        /*
         * Do not call getChildren() as this might cause stack overflows when
         * implementing a lazy loading approach and overriding getChildren() and
         * isLeaf() methods.
         */
        setLeaf(Row.this.children == null || Row.this.children.isEmpty());
    };

    /**
     * Returns the path to this row, for example [ROOT, Parent1, Parent2, this].
     *
     * @return the path to this row
     * @since 1.0
     */
    @SuppressWarnings("rawtypes")
    public final Row[] getPath() {
        List<Row> list = new ArrayList<>();
        Row parent = getParent();
        while (parent != null) {
            list.add(0, parent);
            parent = parent.getParent();
        }

        return list.toArray(new Row[list.size()]);
    }

    private ObservableList<C> children;

    /**
     * Returns the list of children of this row.
     *
     * @return the children
     * @since 1.0
     */
    public final ObservableList<C> getChildren() {
        if (children == null) {
            children = FXCollections.observableArrayList();
            children.addListener(childrenInvalidationListener);
            children.addListener(childrenChangeListener);
        }

        return children;
    }

    /**
     * Checks whether the given row has any child rows (no matter how deep) that fulfill the given
     * predicate.
     *
     * @param predicate the test to perform
     * @return true if the row has any children where the predicate returns true
     */
    public boolean hasChildren(Predicate predicate) {
        return doHasChildren(this, predicate);
    }

    private boolean doHasChildren(Row row, Predicate predicate) {
        boolean foundChild = false;

        FilteredList<Row> filteredList = new FilteredList<>(row.getChildren());
        filteredList.setPredicate(predicate);

        if (!filteredList.isEmpty()) {
            foundChild = true;
        }

        if (!foundChild) {
            for (Object child : row.getChildren()) {
                foundChild = doHasChildren((Row) child, predicate);
                if (foundChild) {
                    break;
                }
            }
        }

        return foundChild;
    }

    // --- Leaf
    private ReadOnlyBooleanWrapper leaf;

    private void setLeaf(boolean value) {
        if (value && leaf == null) {
            return;
        } else if (leaf == null) {
            leaf = new ReadOnlyBooleanWrapper(this, "leaf", true);
        }
        leaf.setValue(value);
    }

    /**
     * A row is a leaf in the tree table view on the left-hand side of the Gantt
     * chart if it has no children. This method may of course be overridden by
     * subclasses to support alternate means of defining how a row may be a
     * leaf, but the general premise is the same: a leaf can not be expanded by
     * the user, and as such will not show a disclosure node or respond to
     * expansion requests.
     *
     * @return true if the row is a leaf (has no children rows)
     * @since 1.3
     */
    public boolean isLeaf() {
        return leaf == null || leaf.getValue();
    }

    /**
     * Represents the TreeItem leaf property, which is true if the TreeItem has
     * no children.
     *
     * @return a property for determining if the row is a leaf row
     * @since 1.3
     */
    public final ReadOnlyBooleanProperty leafProperty() {
        if (leaf == null) {
            leaf = new ReadOnlyBooleanWrapper(this, "leaf", true);
        }
        return leaf.getReadOnlyProperty();
    }

    // expansion support

    private final BooleanProperty expanded = new SimpleBooleanProperty(this,
            "expanded", false);

    /**
     * The property used to store the expansion state of the row. The value of
     * this property is needed for controlling the state of the tree items that
     * will be created for the tree table view control on the left-hand side of
     * the Gantt chart.
     *
     * @return the expanded property
     * @since 1.0
     */
    public final BooleanProperty expandedProperty() {
        return expanded;
    }

    /**
     * Sets the value of the {@link #expandedProperty()}.
     *
     * @param expanded
     *            the new value of the expanded property
     * @since 1.0
     */
    public final void setExpanded(boolean expanded) {
        expandedProperty().set(expanded);
    }

    /**
     * Returns the value of the {@link #expandedProperty()}.
     *
     * @return true if the row is expanded
     * @since 1.0
     */
    public final boolean isExpanded() {
        return expandedProperty().get();
    }

    // Showing support

    private final ReadOnlyBooleanWrapper showing = new ReadOnlyBooleanWrapper(this,
            "showing", false);

    /**
     * The property used to express whether a row is currently showing in the
     * view or not. This information can be useful when deciding whether a row
     * needs to update its activities or not, for example in a lazy-loading
     * scenario.
     *
     * @return the showing property
     * @since 1.0
     */
    public final ReadOnlyBooleanProperty showingProperty() {
        return showing.getReadOnlyProperty();
    }

    /**
     * Returns the value of {@link #showingProperty()}.
     *
     * @return true if the row is currently showing in the UI
     * @since 1.0
     */
    public final boolean isShowing() {
        return showing.get();
    }

    // Layout support.

    private final ObjectProperty<Layout> layout = new SimpleObjectProperty<>(
            this, "layout", new GanttLayout());

    /**
     * The property used to store the layout used for laying out the activities
     * that are directly associated with the row (and not on an inner line).
     *
     * @see #getLineLayout(int)
     * @return the row layer property
     * @since 1.0
     */
    public final ObjectProperty<Layout> layoutProperty() {
        return layout;
    }

    /**
     * Returns the value of the {@link #layoutProperty()}.
     *
     * @return the layout of the row
     * @since 1.0
     */
    public final Layout getLayout() {
        return layoutProperty().get();
    }

    /**
     * Sets the value of the {@link #layoutProperty()}.
     *
     * @param layout
     *            the new row layout
     * @since 1.0
     */
    public final void setLayout(Layout layout) {
        requireNonNull(layout);
        layoutProperty().set(layout);
    }

    // Activity repository support.

    private final ObjectProperty<ActivityRepository<A>> repository = new SimpleObjectProperty<>(
            this, "repository");

    /**
     * The property used to store the activity repository for the row. A
     * repository is used to lookup the activities for a given time interval
     * that needs to be shown on the row.
     *
     * @return the repository property
     * @since 1.0
     */
    public final ObjectProperty<ActivityRepository<A>> repositoryProperty() {
        return repository;
    }

    /**
     * Returns the value of the {@link #repositoryProperty()}.
     *
     * @return the activity repository of the row
     * @since 1.0
     */
    public final ActivityRepository<A> getRepository() {
        return repositoryProperty().get();
    }

    /**
     * Sets the value of the {@link #repositoryProperty()}.
     *
     * @param repository
     *            the new repository to use
     * @since 1.0
     */
    public final void setRepository(ActivityRepository<A> repository) {
        requireNonNull(repository);

        repositoryProperty().set(repository);
    }

    /**
     * Returns the earliest time used by the row. This is a convenience method
     * delegating to {@link ActivityRepository#getEarliestTimeUsed()}.
     *
     * @return the earliest time used by the row / by the activities of the row
     *         / earliest start time of any activity on the row
     * @since {@link #getLatestTimeUsed()}
     * @since 1.0
     */
    public final Instant getEarliestTimeUsed() {
        return getRepository().getEarliestTimeUsed();
    }

    /**
     * Returns the latest time used by the row. This is a convenience method
     * delegating to {@link ActivityRepository#getLatestTimeUsed()}.
     *
     * @return the latest time used by the row / by the activities of the row /
     *         earliest start time of any activity on the row
     * @since {@link #getEarliestTimeUsed()}
     * @since 1.0
     */
    public final Instant getLatestTimeUsed() {
        return getRepository().getLatestTimeUsed();
    }

    // Name property support.

    private final StringProperty name = new SimpleStringProperty(this, "name",
            "Default");

    /**
     * The property used to store the name of the row.
     *
     * @return the name property
     * @since 1.0
     */
    public final StringProperty nameProperty() {
        return name;
    }

    /**
     * Returns the value of the {@link #nameProperty()}.
     *
     * @return the name of the row
     * @since 1.0
     */
    public final String getName() {
        return nameProperty().get();
    }

    /**
     * Sets the value of the {@link #nameProperty()}.
     *
     * @param name
     *            the new name of the row
     * @since 1.0
     */
    public final void setName(String name) {
        requireNonNull(name);
        nameProperty().set(name);
    }

    // Height support.

    private final DoubleProperty height = new SimpleDoubleProperty(this,
            "height", DEFAULT_ROW_HEIGHT);

    /**
     * The property used to store the height of the row.
     *
     * @return the row height property
     * @since 1.0
     */
    public final DoubleProperty heightProperty() {
        return height;
    }

    /**
     * Sets the value of the {@link #heightProperty()}.
     *
     * @param height
     *            the new height of the row
     * @since 1.0
     */
    public final void setHeight(double height) {
        heightProperty().set(height);
    }

    /**
     * Returns the value of the {@link #heightProperty()}.
     *
     * @return the value of the height property
     * @since 1.0
     */
    public final double getHeight() {
        return heightProperty().get();
    }

    // Min height support.

    private final DoubleProperty minHeight = new SimpleDoubleProperty(this,
            "minHeight", DEFAULT_ROW_HEIGHT);

    /**
     * The property used to store the minimum height of the row. The user will
     * not be able to resize the row to anything smaller than the value of this
     * property.
     *
     * @return the minimum height property
     * @since 1.0
     */
    public final DoubleProperty minHeightProperty() {
        return minHeight;
    }

    /**
     * Sets a new value for the {@link #minHeightProperty()}.
     *
     * @param height
     *            the new minimum height
     * @since 1.0
     */
    public final void setMinHeight(double height) {
        minHeightProperty().set(height);
    }

    /**
     * Returns the value of the {@link #minHeightProperty()}.
     *
     * @return the minimum height of the row
     * @since 1.0
     */
    public final double getMinHeight() {
        return minHeightProperty().get();
    }

    // Min height support.

    private final DoubleProperty maxHeight = new SimpleDoubleProperty(this,
            "maxHeight", 500);

    /**
     * The property used to store the maximum height of the row. The user will
     * not be able to resize the row to anything larger than the value of this
     * property.
     *
     * @return the maximum height property
     * @since 1.0
     */
    public final DoubleProperty maxHeightProperty() {
        return maxHeight;
    }

    /**
     * Sets the value of the {@link #maxHeightProperty()}.
     *
     * @param height
     *            the maximum height of the row
     * @since 1.0
     */
    public final void setMaxHeight(double height) {
        maxHeightProperty().set(height);
    }

    /**
     * Returns the value of {@link #maxHeightProperty()}.
     *
     * @return the maximum height of the row
     * @since 1.0
     */
    public final double getMaxHeight() {
        return maxHeightProperty().get();
    }

    // User object support.

    private ObjectProperty<Object> userObject;

    /**
     * The property used to store a row-specific user object.
     *
     * @return the user object property
     * @since 1.0
     */
    public final ObjectProperty<Object> userObjectProperty() {
        if (userObject == null) {
            userObject = new SimpleObjectProperty<>(this, "userObject");
        }

        return userObject;
    }

    /**
     * Returns the value of {@link #userObjectProperty()}.
     *
     * @return the user object associated with this row
     * @since 1.0
     */
    public final Object getUserObject() {
        return userObject == null ? null : userObject.get();
    }

    /**
     * Sets the value of {@link #userObjectProperty()}.
     *
     * @param obj
     *            the new user object
     * @since 1.0
     */
    public final void setUserObject(Object obj) {
        if (userObject == null && obj == null) {
            return;
        }
        userObjectProperty().set(obj);
    }

    // Lines manager support.

    private final ObjectProperty<LinesManager<A>> linesManager = new SimpleObjectProperty<>(
            this, "linesLayout");

    /**
     * The property used to store the {@link LinesManager} instance for this
     * row. The lines manager is used to control the layout of inner lines and
     * the placement of activities on these lines.
     *
     * @return the lines manager property
     * @since 1.0
     */
    public final ObjectProperty<LinesManager<A>> linesManagerProperty() {
        return linesManager;
    }

    /**
     * Returns the value of {@link #linesManagerProperty()}.
     *
     * @return the lines manager for this row
     * @since 1.0
     */
    public final LinesManager<A> getLinesManager() {
        return linesManagerProperty().get();
    }

    /**
     * Sets the value of {@link #linesManagerProperty()}.
     *
     * @param manager
     *            the new lines manager
     * @since 1.0
     */
    public final void setLinesManager(LinesManager<A> manager) {
        requireNonNull(manager);
        linesManagerProperty().set(manager);
    }

    // ZoneId support.

    private ObjectProperty<ZoneId> zoneId;

    /**
     * The property used to store the zone ID for this row. Each row can be
     * placed in a different time zone.
     *
     * @return the zone ID property
     * @since 1.0
     */
    public final ObjectProperty<ZoneId> zoneIdProperty() {
        if (zoneId == null) {
            zoneId = new SimpleObjectProperty<>(this, "zoneId",
                    ZoneId.systemDefault());
        }

        return zoneId;
    }

    /**
     * Returns the value of the {@link #zoneIdProperty()}.
     *
     * @return the zone ID of this row
     * @since 1.0
     */
    public final ZoneId getZoneId() {
        return zoneId == null ? ZoneId.systemDefault() : zoneId.get();
    }

    /**
     * Sets the value of the {@link #zoneIdProperty()}.
     *
     * @param zoneId
     *            the new zone ID for this row
     * @since 1.0
     */
    public final void setZoneId(ZoneId zoneId) {
        requireNonNull(zoneId);
        zoneIdProperty().set(zoneId);
    }

    // Calendar support

    private final ObservableList<Calendar<?>> calendars = FXCollections
            .observableArrayList();

    /**
     * Returns a list of calendars attached to this row. Calendars directly
     * attached to a row can be used to visualize row-specific information, for
     * example "resource availability".
     *
     * @return a list of row-specific calendars
     * @since 1.0
     */
    public final ObservableList<Calendar<?>> getCalendars() {
        return calendars;
    }

    private final IntegerProperty lineCount = new SimpleIntegerProperty(this,
            "lineCount", 0);

    /**
     * The property used to keep track of the number of inner lines shown by the
     * row.
     *
     * @return the line count property
     * @since 1.0
     */
    public final IntegerProperty lineCountProperty() {
        return lineCount;
    }

    /**
     * Sets the value of the {@link #lineCountProperty()}.
     *
     * @param count
     *            the new line count for the row
     * @since 1.0
     */
    public final void setLineCount(int count) {
        lineCountProperty().set(count);
    }

    /**
     * Returns the value of the {@link #lineCountProperty()}.
     *
     * @return the line count of the row
     * @since 1.0
     */
    public final int getLineCount() {
        return lineCount.get();
    }

    private MutableActivityRepository<A> getMutableRepository() {
        ActivityRepository<A> repository = getRepository();
        if (repository instanceof MutableActivityRepository) {
            return (MutableActivityRepository<A>) repository;
        }

        throw new RepositoryException(
                MessageFormat
                        .format("the repository used by the row with the name {0} is immutable and of type {1}, "
                                + "activities can not be added or removed to the row / repository",
                                getName(), repository.getClass().getName()));
    }

    /**
     * Adds the given activity for the given layer to this row. This method
     * delegates to {@link MutableActivityRepository#addActivity(ActivityRef)}.
     *
     * @param layer
     *            the layer where the activity will be displayed
     * @param activity
     *            the activity that will be added
     * @see MutableActivityRepository#addActivity(ActivityRef)
     * @since 1.0
     */
    public final void addActivity(Layer layer, A activity) {
        getMutableRepository().addActivity(
                new ActivityRef<>(this, layer, activity));
    }

    /**
     * Removes the given activity from the given layer from this row.
     *
     * @param layer
     *            the layer from which to remove the activity
     * @param activity
     *            the activity to remove
     * @see MutableActivityRepository#removeActivity(ActivityRef)
     * @since 1.0
     */
    public final void removeActivity(Layer layer, A activity) {
        getMutableRepository().removeActivity(
                new ActivityRef<>(this, layer, activity));
    }

    /**
     * Removes all activities from the row.
     *
     * @see MutableActivityRepository#clearActivities()
     * @since 1.0
     */
    public final void clearActivities() {
        getMutableRepository().clearActivities();
    }

    /**
     * Removes all activities on the given layer from the row.
     *
     * @param layer
     *            the layer from which to remove all activities
     * @see MutableActivityRepository#clearActivities(Layer)
     * @since 1.0
     */
    public final void clearActivities(Layer layer) {
        getMutableRepository().clearActivities(layer);
    }

    // Lines manager support.

    /**
     * Returns the line index for the given activity. This is a convenience
     * method delegating to {@link LinesManager#getLineIndex(Activity)}.
     *
     * @param activity
     *            the activity for which to return a line index
     * @return the line index for the given activity
     * @since 1.0
     */
    public final int getLineIndex(A activity) {
        return getLinesManager().getLineIndex(activity);
    }

    /**
     * Returns the location of the given inner line. The value returned is
     * usually between 0 and {@link #getHeight()}.
     *
     * @param lineIndex
     *            the index of the line for which to return a location
     * @return the line location y-coordinate
     * @since 1.0
     */
    public final double getLineLocation(int lineIndex) {
        return getLinesManager().getLineLocation(lineIndex, getHeight());
    }

    /**
     * Returns the height of the given inner line. The value returned is usually
     * between 0 and {@link #getHeight()}.
     *
     * @param lineIndex
     *            the index of the line for which to return a height
     * @return the height of the line
     * @since 1.0
     */
    public final double getLineHeight(int lineIndex) {
        return getLinesManager().getLineHeight(lineIndex, getHeight());
    }

    /**
     * Returns a line-specific layout for the given line.
     *
     * @param lineIndex
     *            the index of the line
     * @return the line layout
     * @since 1.0
     */
    public final Layout getLineLayout(int lineIndex) {
        return getLinesManager().getLineLayout(lineIndex);
    }

    @Override
    public String toString() {
        return getName();
    }
}
