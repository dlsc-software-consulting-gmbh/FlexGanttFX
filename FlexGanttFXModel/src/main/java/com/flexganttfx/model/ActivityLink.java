/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

/**
 * An activity link can be used to model any kind of dependency between two
 * activities. In project planning applications a link would express a
 * predecessor / successor relationship between two tasks, for example
 * "task A must be finished before task B can begin". In other domains a link
 * might simply express that two or more activities need to be scheduled
 * together and that moving one of them requires all others to be moved, too.
 *
 * @param <A>
 *            the type of the two activities being linked together
 *
 * @since 1.0
 */
public class ActivityLink<A extends Activity> implements Activity {

	private Object userObject;

	/**
	 * An enumerator listing the available link types.
	 *
	 * @see ActivityLink#getType()
	 *
	 * @since 1.0
	 */
	public enum LinkType {

		/**
		 * Used to define a link between the end time of the first activity and
		 * the start time of the second activity.
		 *
		 * @since 1.0
		 */
		END_TO_START,

		/**
		 * Used to define a link between the start time of the first activity
		 * and the end time of the second activity.
		 *
		 * @since 1.0
		 */
		START_TO_END,

		/**
		 * Used to define a link between the start time of the first activity
		 * and the start time of the second activity.
		 *
		 * @since 1.0
		 */
		START_TO_START,

		/**
		 * Used to define a link between the end time of the first activity and
		 * the end time of the second activity.
		 *
		 * @since 1.0
		 */
		END_TO_END
	}

	/**
	 * Constructs a new activity link.
	 *
	 * @param sourceRef
	 *            an activity reference pointing to the link source
	 * @param targetRef
	 *            an activity reference pointing to the link target
	 * @param type
	 *            the type of the link (E-S, S-E, S-S, E-E)
	 * @since 1.0
	 */
	public ActivityLink(ActivityRef<A> sourceRef, ActivityRef<A> targetRef,
			LinkType type) {

		requireNonNull(sourceRef);
		requireNonNull(targetRef);
		requireNonNull(type);

		this.sourceActivityRef = sourceRef;
		this.targetActivityRef = targetRef;
		this.type = type;
	}

	/**
	 * Constructs a new activity link with type {@link LinkType#END_TO_START}.
	 *
	 * @param sourceRef
	 *            an activity reference pointing to the link source
	 * @param targetRef
	 *            an activity reference pointing to the link target
	 * @since 1.0
	 */
	public ActivityLink(ActivityRef<A> sourceRef, ActivityRef<A> targetRef) {
		this(sourceRef, targetRef, LinkType.END_TO_START);
	}

	private ActivityRef<A> sourceActivityRef;

	/**
	 * Sets the activity reference pointing to the source activity of the link.
	 *
	 * @param ref
	 *            the source activity reference
	 * @since 1.0
	 */
	public void setSourceActivityRef(ActivityRef<A> ref) {
		requireNonNull(ref);
		this.sourceActivityRef = ref;
	}

	/**
	 * Returns the activity reference pointing to the source activity of the
	 * link.
	 *
	 * @return the source activity reference
	 * @since 1.0
	 */
	public final ActivityRef<A> getSourceActivityRef() {
		return sourceActivityRef;
	}

	private ActivityRef<A> targetActivityRef;

	/**
	 * Sets the activity reference pointing to the target activity of the link.
	 *
	 * @param ref
	 *            the target activity reference
	 * @since 1.0
	 */
	public void setTargetActivityRef(ActivityRef<A> ref) {
		requireNonNull(ref);
		this.targetActivityRef = ref;
	}

	/**
	 * Returns the activity reference pointing to the target activity of the
	 * link.
	 *
	 * @return the target activity reference
	 * @since 1.0
	 */
	public final ActivityRef<A> getTargetActivityRef() {
		return targetActivityRef;
	}

	private LinkType type;

	/**
	 * Returns the link type (S-S, S-E, E-S, E-E).
	 *
	 * @return the link type
	 * @since 1.0
	 */
	public final LinkType getType() {
		return type;
	}

	/**
	 * Sets the link type (S-S, S-E, E-S, E-E).
	 *
	 * @param type
	 *            the link type
	 * @since 1.0
	 */
	public void setType(LinkType type) {
		requireNonNull(type);
		this.type = type;
	}

	@Override
	public String getName() {
		return getSourceActivityRef().getActivity().getName() + " -> " + getTargetActivityRef().getActivity().getName();
	}

	@Override
	public String getId() {
		return "";
	}

	@Override
	public final Instant getStartTime() {
		return Instant.ofEpochMilli(Math.min(
				getSourceActivityRef().getActivity().getStartTime().toEpochMilli(),
				getTargetActivityRef().getActivity().getStartTime().toEpochMilli()));
	}

	@Override
	public final Instant getEndTime() {
		return Instant.ofEpochMilli(Math.max(
				getSourceActivityRef().getActivity().getEndTime().toEpochMilli(),
				getTargetActivityRef().getActivity().getEndTime().toEpochMilli()));
	}

	/**
	 * An optional user object that might be useful for creating a custom renderer.
	 *
	 * @param userObject an optional user object
	 */
	public final void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	/**
	 * Returns the (optional) user object of the activity link.
	 *
	 * @return the optional user object
	 */
	public final Object getUserObject() {
		return userObject;
	}
}
