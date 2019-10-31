/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.editor;

import static com.flexganttfx.editor.AgendaEntry.PushDirection.DOWN;
import static com.flexganttfx.editor.AgendaEntry.PushDirection.NONE;
import static com.flexganttfx.editor.AgendaEntry.PushDirection.UP;
import static com.flexganttfx.view.graphics.ActivityEvent.END_TIME_CHANGE_FINISHED;
import static com.flexganttfx.view.graphics.ActivityEvent.HORIZONTAL_DRAG_FINISHED;
import static com.flexganttfx.view.graphics.ActivityEvent.START_TIME_CHANGE_FINISHED;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.flexganttfx.editor.AgendaEntry.PushDirection;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.repository.MutableActivityRepository;
import com.flexganttfx.model.util.ActivityHelper;

/**
 * The conflict resolver is responsible for fixing an agenda schedule while the
 * user is performing editing operations, e.g. dragging an agenda entry,
 * changing its start time, changing its end time. The resolver has three entry
 * points: fix after the user performed a drag, fix after the user changed the
 * start time of an entry, fix after the user has changed the end time of an
 * entry.
 *
 * @param <R>
 *            the type of rows used
 *
 * @since 1.0
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class AgendaConflictResolver<R extends Row<?, ?, ?>> {

	/**
	 * The context where this resolver will be used. Using an interface allows
	 * us to use the same resolver in different controls. The context interface
	 * is currently implemented by the {@link AgendaEditor} and the
	 * {@link AgendaGanttChart} classes.
	 */
	private AgendaEditorContext<R> context;

	public AgendaConflictResolver(AgendaEditorContext<R> context) {
		this.context = context;

		/*
		 * Listen to edit events finishing so that the map of fixed boxes /
		 * entries can be cleared.
		 */
		context.getGraphics().addEventHandler(START_TIME_CHANGE_FINISHED,
				evt -> clearFixedBoxes());

		context.getGraphics().addEventHandler(END_TIME_CHANGE_FINISHED,
				evt -> clearFixedBoxes());

		context.getGraphics().addEventHandler(HORIZONTAL_DRAG_FINISHED,
				evt -> clearFixedBoxes());
	}

	/*
	 * A data structure used to keep track of fixed boxes / entries. The key of
	 * the map is also the primary entry of the box.
	 */
	private Map<AgendaEntry, Box> fixedBoxes = new HashMap<>();

	/**
	 * Clears all boxes in the "fixedBoxes" map.
	 */
	private void clearFixedBoxes() {
		fixedBoxes.values().forEach(it -> clearFixedBox(it));
		fixedBoxes.clear();
	}

	/**
	 * Clearing a box means that we are clearing the fields
	 * "original start time", "original end time", "push direction", and
	 * "pusher" of all entries in the box and all related entries.
	 */
	private void clearFixedBox(Box box) {
		clearFixedEntry(box.getEntry());
		box.getEntries().forEach(it -> clearFixedEntry(it));
		getGroupMembers(box).forEach(it -> clearFixedEntry(it));
	}

	/**
	 * Clearing all fields that were required while fixing and restoring the
	 * given entry.
	 *
	 * @param entry
	 *            the entry to be cleared
	 */
	private void clearFixedEntry(AgendaEntry entry) {
		entry.setOriginalStartTime(null);
		entry.setOriginalEndTime(null);
		entry.setPushDirection(NONE);
		entry.setPusher(null);
	}

	/**
	 * The method used to fix the schedule after the user has performed a drag
	 * operation. It first resolves all conflicts of the edited / dragged
	 * activities and then tries to restore all previously fixed entries / box.
	 * This process goes on and on: fix, restore, fix, restore, fix, restore,
	 * etc...
	 *
	 * @param horizontalMove
	 *            a flag used to indicate whether the user has performed a
	 *            horizontal drag (moved an entry from one day to another).
	 */
	public final void fixScheduleAfterDrag(boolean horizontalMove) {

		/*
		 * If overlapping is allowed then we do not have to do anything.
		 */
		if (context.allowOverlappingProperty().get()) {
			return;
		}

		/*
		 * For each selected / edited activity try to resolve all new conflicts
		 * that exist after the last drag event.
		 */
		for (ActivityRef<?> ref : context.getGraphics().getSelectedActivities()) {
			AgendaEntry entry = (AgendaEntry) ref.getActivity();
			Row row = ref.getRow();
			Layer layer = ref.getLayer();

			resolveConflictsAfterDrag(row, layer, row.getLineIndex(entry),
					entry, horizontalMove);
		}

		/*
		 * We normally always restore but the context might have this option for
		 * debugging so that it is easier to distinguish between the behaviour
		 * of the conflict resolution and the behaviour of the restore code.
		 */
		if (context.restoreProperty().get()) {
			boolean fixedSomething = false;
			do {
				fixedSomething = restoreEntriesAfterDrag();
			} while (fixedSomething);
		}

		/*
		 * A redraw is required every time the user has moved the mouse.
		 */
		context.getGraphics().redraw();
	}

	/**
	 * Performs the actual conflict resolution after the user has performed a
	 * drag on an agenda entry.
	 *
	 * @param row
	 *            the row where the conflict resolution will run
	 * @param layer
	 *            the layer on which the conflict resolution will run
	 * @param lineIndex
	 *            the index of the line where the conflict resolution will run
	 * @param draggedEntry
	 *            the dragged agenda entry that might be causing conflicts
	 * @param horizontalMove
	 *            a flag signaling whether the drag was passing day boundaries
	 */
	private void resolveConflictsAfterDrag(Row row, Layer layer, int lineIndex,
			AgendaEntry draggedEntry, boolean horizontalMove) {

		MutableActivityRepository<AgendaEntry> repository = (MutableActivityRepository<AgendaEntry>) row
				.getRepository();

		Set<AgendaEntry> investigatedEntries = new HashSet<>();
		AgendaEntry entryB = null;

		/*
		 * We are looping until we find no more conflicts caused by the dragged
		 * entry. We can not collect all conflicts first and then iterate over
		 * them because fixing one conflict might have solved or caused other
		 * conflicts.
		 */
		do {
			Iterator<AgendaEntry> iter = repository.getActivities(layer,
					draggedEntry.getStartTime(), draggedEntry.getEndTime(),
					DAYS, row.getZoneId());

			entryB = null;
			while (iter.hasNext()) {
				AgendaEntry entry = iter.next();
				ActivityRef<AgendaEntry> ref = new ActivityRef<>(row, layer,
						entry);

				/*
				 * Do not investigate if the entry is .... a) the dragged entry
				 * itself b) one of the currently edited / selected activities
				 * (they never move) c) an already investigated entry
				 */
				if (!draggedEntry.equals(entry)
						&& !context.getGraphics().getSelectedActivities()
								.contains(ref)
						&& !investigatedEntries.contains(entry)) {
					entryB = entry;

					resolveConflictAfterDrag(row, layer, lineIndex,
							draggedEntry, entryB, horizontalMove);

					/*
					 * Add entry to set of investigated entries, so we do not
					 * try to fix over and over again ... prevents infinite
					 * loops.
					 */
					investigatedEntries.add(entryB);
				}
			}
		} while (entryB != null);
	}

	/**
	 * Performs the actual conflict resolution after the user has performed a
	 * drag on an agenda entry.
	 *
	 * @param row
	 *            the row where the conflict resolution will run
	 * @param layer
	 *            the layer on which the conflict resolution will run
	 * @param lineIndex
	 *            the index of the line where the conflict resolution will run
	 * @param draggedEntry
	 *            the dragged agenda entry that might be causing conflicts
	 * @param pushedEntry
	 *            an agenda entry which is in conflict with the currently
	 *            dragged entry
	 * @param horizontalMove
	 *            a flag signaling whether the drag was passing day boundaries
	 */
	private void resolveConflictAfterDrag(Row row, Layer layer, int lineIndex,
			AgendaEntry draggedEntry, AgendaEntry pushedEntry,
			boolean horizontalMove) {

		/*
		 * The algorithm chooses to push down an entry (make it start later /
		 * after the dragged entry) if the current start time of the entry is
		 * after (later than) the start time of the dragged entry.
		 */

		if (pushedEntry.getStartTime().isAfter(draggedEntry.getStartTime())) {
			pushDown(row, layer, lineIndex, draggedEntry, pushedEntry, null,
					horizontalMove);
		} else {
			pushUp(row, layer, lineIndex, draggedEntry, pushedEntry, null,
					horizontalMove);
		}
	}

	/**
	 * Performs a recursive push-up for the given pusher. The algorithm starts
	 * with the given pushed entry, then looks for follow-up conflicts caused by
	 * the pushed entry, then for conflicts caused by entries that are members
	 * of the same group as the pusehd entry.
	 *
	 * @param row
	 *            the row where the push-up will run
	 * @param layer
	 *            the layer where the push-up will run
	 * @param lineIndex
	 *            the index of the line where the push-up will run
	 * @param pusher
	 *            the entry that pushes other entries out of the way (either it
	 *            was edited by the user or moved as part of the conflict
	 *            resolution)
	 * @param pushedEntry
	 *            the entry that needs to be pushed out of the way as it is in
	 *            conflict with the pushed entry
	 * @param moveDuration
	 *            an optional duration that will be used when the move duration
	 *            is already known (e.g. for entries that are moved because of
	 *            their membership to a group)
	 * @param horizontalMove
	 *            a flag indicating whether the drag was crossing day boundaries
	 */
	private void pushUp(Row row, Layer layer, int lineIndex,
			AgendaEntry pusher, AgendaEntry pushedEntry, Duration moveDuration,
			boolean horizontalMove) {

		Box box = new Box(pushedEntry, row, layer);

		if (horizontalMove) {

			/*
			 * When dragging from one day to another we have to perform a more
			 * thorough check when looking for the last conflicting entry. Other
			 * entries might be above that have already been pushed.
			 */
			pusher = findLastConflictingEntryWhenPushingUp(row, layer, pusher,
					pushedEntry);

		} else {

			/*
			 * When dragging vertically (within the same day) we only care about
			 * all the selected entries.
			 */
			pusher = findLastConflictingEntryWhenPushingUp(pusher, pushedEntry);

		}

		if (moveDuration == null) {

			/*
			 * The move duration depends on the horizontal move flag. If it is a
			 * horizontal move then the move duration is computed based on the
			 * end time of the box, which contains all entries that belong to
			 * the pushed entry. If the move does not cross day boundaries then
			 * we only care for the end time of the pushed entry when
			 * calculating the move duration.
			 */
			moveDuration = Duration.between(
					pusher.getStartTime(),
					horizontalMove ? box.getEndTime() : pushedEntry
							.getEndTime());
		}

		markBox(box, UP, pusher);

		for (AgendaEntry member : getGroupMembers(box)) {
			if (!box.contains(member) && !member.equals(pusher)) {

				if (member.getPushDirection().equals(NONE)) {
					member.setPushDirection(pushedEntry.getPushDirection());
					member.setOriginalStartTime(member.getStartTime());
					member.setOriginalEndTime(member.getEndTime());
				}
			}
		}

		moveBox(box, moveDuration, UP);

		/*
		 * We have moved the box (one or more entries). Now we have to check if
		 * the move caused any follow-up conflicts.
		 */

		MutableActivityRepository<AgendaEntry> repository = (MutableActivityRepository<AgendaEntry>) row
				.getRepository();

		Iterator<AgendaEntry> iter = repository.getActivities(layer,
				repository.getEarliestTimeUsed(),
				repository.getLatestTimeUsed(), DAYS, row.getZoneId());

		while (iter.hasNext()) {
			AgendaEntry followEntry = iter.next();

			/*
			 * Keep pushing other entries if they are not already contained in
			 * the moved box, they are not the pusher itself, they intersect
			 * with the box, and they are not selected (we never move selected
			 * entries).
			 */
			if (!box.contains(followEntry) && !followEntry.equals(pusher)
					&& box.intersectsWithEntry(followEntry)
					&& !isSelected(row, layer, followEntry)) {

				pushUp(row, layer, lineIndex, box.getEarliestEntry(),
						followEntry, null, horizontalMove);
			}
		}

		/*
		 * When we moved the box we also moved group members that were not
		 * located on the same day. We now have to check wether that caused any
		 * additional conflicts.
		 */
		for (AgendaEntry member : getGroupMembers(box)) {

			/*
			 * Group members on the same day are already included in the box, so
			 * we do not have to do anything in this case.
			 */
			if (!box.contains(member) && !member.equals(pusher)) {

				iter = repository.getActivities(layer, member.getStartTime(),
						member.getEndTime(), DAYS, row.getZoneId());

				while (iter.hasNext()) {
					AgendaEntry nextEntry = iter.next();

					if (!nextEntry.equals(member) && !nextEntry.equals(pusher)
							&& !isSelected(row, layer, nextEntry)) {
						pushUp(row, layer, lineIndex, member, nextEntry,
								moveDuration, horizontalMove);
					}
				}
			}
		}
	}

	/*
	 * An entry might have to get pushed up because of the given pusher but
	 * ultimately it might end up with another entry as the actual pusher. Here
	 * we are searching upwards to see if the pusher has to be replaced with
	 * another entry. This approach is needed when an entry gets dragged
	 * horizontally / crossing day borders.
	 */
	private AgendaEntry findLastConflictingEntryWhenPushingUp(Row row,
			Layer layer, AgendaEntry pusher, AgendaEntry pushedEntry) {

		Instant time = pusher.getStartTime();

		MutableActivityRepository<AgendaEntry> repository = (MutableActivityRepository<AgendaEntry>) row
				.getRepository();

		Instant searchEnd = pusher.getStartTime();
		Instant searchStart = repository.getEarliestTimeUsed();

		if (!searchEnd.isAfter(searchStart)) {
			searchEnd = searchStart;
		}

		Iterator<AgendaEntry> iter = repository.getActivities(layer,
				searchStart, searchEnd, DAYS, row.getZoneId());

		/*
		 * We need a list in reverse order, entries sorted based on their end
		 * time. Latest end first.
		 */
		List<AgendaEntry> backwardsList = createBackwardsIterationList(iter);

		iter = backwardsList.iterator();

		while (iter.hasNext()) {
			AgendaEntry nextEntry = iter.next();

			if (!nextEntry.equals(pusher) && !nextEntry.equals(pushedEntry)
					&& nextEntry.getPusher() != null) {
				Instant nextStartTime = nextEntry.getStartTime();
				Instant nextEndTime = nextEntry.getEndTime();

				Instant possibleNewEndTime = time;
				Instant possibleNewStartTime = time.minus(pushedEntry
						.getDuration());

				if (ActivityHelper.intersect(nextStartTime, nextEndTime,
						possibleNewStartTime, possibleNewEndTime)) {

					if (nextStartTime.isBefore(time)) {
						time = nextStartTime;
						pusher = nextEntry;
					}
				}
			}
		}

		return pusher;
	}

	/*
	 * Creates a list with the elements returned by the given iterator. The
	 * elements are then sorted based on their end times. Latest end time first.
	 */
	private List<AgendaEntry> createBackwardsIterationList(
			Iterator<AgendaEntry> iter) {
		List<AgendaEntry> list = new ArrayList<>();
		while (iter.hasNext()) {
			list.add(iter.next());
		}

		Collections.sort(list, new Comparator<AgendaEntry>() {
			@Override
			public int compare(AgendaEntry o1, AgendaEntry o2) {
				if (o1.getEndTime().isAfter(o2.getEndTime())) {
					return -1;
				}

				return +1;
			}
		});
		return list;
	}

	/*
	 * When several entries are dragged at the same time it might happen that a
	 * pushed entry is in conflict with one of them but might actually end up
	 * being pushed up by another one after it has moved out of the way of the
	 * first one.
	 */
	private AgendaEntry findLastConflictingEntryWhenPushingUp(
			AgendaEntry pusher, AgendaEntry pushedEntry) {

		Instant time = pusher.getStartTime();

		for (ActivityRef<?> ref : context.getGraphics().getSelectedActivities()) {
			AgendaEntry nextEntry = (AgendaEntry) ref.getActivity();
			if (!nextEntry.equals(pusher)) {
				Instant nextStartTime = nextEntry.getStartTime();
				Instant nextEndTime = nextEntry.getEndTime();

				Instant possibleNewStartTime = time.minus(pushedEntry
						.getDuration());
				Instant possibleNewEndTime = time;

				if (ActivityHelper.intersect(nextStartTime, nextEndTime,
						possibleNewStartTime, possibleNewEndTime)) {
					if (nextStartTime.isBefore(time)) {
						time = nextStartTime;
						pusher = nextEntry;
					}
				}
			}
		}

		return pusher;
	}

	/**
	 * Performs a recursive push-down for the given pusher. The algorithm starts
	 * with the given pushed entry, then looks for follow-up conflicts caused by
	 * the pushed entry, then for conflicts caused by entries that are members
	 * of the same group as the pusehd entry.
	 *
	 * @param row
	 *            the row where the push-down will run
	 * @param layer
	 *            the layer where the push-down will run
	 * @param lineIndex
	 *            the index of the line where the push-down will run
	 * @param pusher
	 *            the entry that pushes other entries out of the way (either it
	 *            was edited by the user or moved as part of the conflict
	 *            resolution)
	 * @param pushedEntry
	 *            the entry that needs to be pushed out of the way as it is in
	 *            conflict with the pushed entry
	 * @param moveDuration
	 *            an optional duration that will be used when the move duration
	 *            is already known (e.g. for entries that are moved because of
	 *            their membership to a group)
	 * @param horizontalMove
	 *            a flag indicating whether the drag was crossing day boundaries
	 */
	private void pushDown(Row row, Layer layer, int lineIndex,
			AgendaEntry pusher, AgendaEntry pushedEntry, Duration moveDuration,
			boolean horizontalMove) {

		Box box = new Box(pushedEntry, row, layer);

		if (horizontalMove) {

			/*
			 * When dragging from one day to another we have to perform a more
			 * thorough check when looking for the last conflicting entry. Other
			 * entries might be above that have already been pushed.
			 */
			pusher = findLastConflictingEntryWhenPushingDown(row, layer,
					pusher, pushedEntry);
		} else {

			/*
			 * When dragging vertically (within the same day) we only care about
			 * all the selected entries.
			 */
			pusher = findLastConflictingEntryWhenPushingDown(pusher,
					pushedEntry);
		}

		if (moveDuration == null) {

			/*
			 * The move duration depends on the horizontal move flag. If it is a
			 * horizontal move then the move duration is computed based on the
			 * end time of the box, which contains all entries that belong to
			 * the pushed entry. If the move does not cross day boundaries then
			 * we only care for the end time of the pushed entry when
			 * calculating the move duration.
			 */
			moveDuration = Duration.between(horizontalMove ? box.getStartTime()
					: pushedEntry.getStartTime(), pusher.getEndTime());
		}

		markBox(box, DOWN, pusher);

		for (AgendaEntry member : getGroupMembers(box)) {
			if (!box.contains(member) && !member.equals(pusher)) {
				if (member.getPushDirection().equals(NONE)) {
					member.setPushDirection(pushedEntry.getPushDirection());
					member.setOriginalStartTime(member.getStartTime());
					member.setOriginalEndTime(member.getEndTime());
				}
			}
		}

		moveBox(box, moveDuration, DOWN);

		/*
		 * We have moved the box (one or more entries). Now we have to check if
		 * the move caused any follow-up conflicts.
		 */

		MutableActivityRepository<AgendaEntry> repository = (MutableActivityRepository<AgendaEntry>) row
				.getRepository();

		Iterator<AgendaEntry> iter = repository.getActivities(layer,
				repository.getEarliestTimeUsed(),
				repository.getLatestTimeUsed(), DAYS, row.getZoneId());

		while (iter.hasNext()) {
			AgendaEntry followEntry = iter.next();

			/*
			 * Keep pushing other entries if they are not already contained in
			 * the moved box, they are not the pusher itself, they intersect
			 * with the box, and they are not selected (we never move selected
			 * entries).
			 */
			if (!box.contains(followEntry) && !followEntry.equals(pusher)
					&& box.intersectsWithEntry(followEntry)
					&& !isSelected(row, layer, followEntry)) {

				pushDown(row, layer, lineIndex, box.getLatestEntry(),
						followEntry, null, horizontalMove);
			}
		}

		/*
		 * When we moved the box we also moved group members that were not
		 * located on the same day. We now have to check wether that caused any
		 * additional conflicts.
		 */
		for (AgendaEntry member : getGroupMembers(box)) {

			/*
			 * Group members on the same day are already included in the box, so
			 * we do not have to do anything in this case.
			 */
			if (!box.contains(member) && !member.equals(pusher)) {

				iter = repository.getActivities(layer, member.getStartTime(),
						member.getEndTime(), DAYS, row.getZoneId());

				while (iter.hasNext()) {
					AgendaEntry nextEntry = iter.next();

					if (!nextEntry.equals(member) && !nextEntry.equals(pusher)
							&& !isSelected(row, layer, nextEntry)) {
						pushDown(row, layer, lineIndex, member, nextEntry,
								moveDuration, horizontalMove);
					}
				}
			}
		}
	}

	/*
	 * When several entries are dragged at the same time it might happen that a
	 * pushed entry is in conflict with one of them but might actually end up
	 * being pushed up by another one after it has moved out of the way of the
	 * first one.
	 */
	private AgendaEntry findLastConflictingEntryWhenPushingDown(
			AgendaEntry pusher, AgendaEntry pushedEntry) {

		Instant time = pusher.getEndTime();

		for (ActivityRef<?> ref : context.getGraphics().getSelectedActivities()) {
			AgendaEntry nextEntry = (AgendaEntry) ref.getActivity();
			if (!nextEntry.equals(pusher)) {
				Instant nextStartTime = nextEntry.getStartTime();
				Instant nextEndTime = nextEntry.getEndTime();

				Instant possibleNewEndTime = time.plus(pushedEntry
						.getDuration());
				Instant possibleNewStartTime = time;

				if (ActivityHelper.intersect(nextStartTime, nextEndTime,
						possibleNewStartTime, possibleNewEndTime)) {
					if (nextEndTime.isAfter(time)) {
						time = nextEndTime;
						pusher = nextEntry;
					}
				}
			}
		}

		return pusher;
	}

	/**
	 * An entry might have to get pushed down because of the given pusher but
	 * ultimately it might end up with another entry as the actual pusher. Here
	 * we are searching downwards to see if the pusher has to be replaced with
	 * another entry.
	 */
	private AgendaEntry findLastConflictingEntryWhenPushingDown(Row row,
			Layer layer, AgendaEntry pusher, AgendaEntry pushedEntry) {

		Instant time = pusher.getEndTime();

		MutableActivityRepository<AgendaEntry> repository = (MutableActivityRepository<AgendaEntry>) row
				.getRepository();

		Instant searchStart = pusher.getEndTime();
		Instant searchEnd = repository.getLatestTimeUsed();
		if (!searchEnd.isAfter(searchStart)) {
			searchEnd = searchStart;
		}

		Iterator<AgendaEntry> iter = repository.getActivities(layer,
				searchStart, searchEnd, DAYS, row.getZoneId());

		while (iter.hasNext()) {
			AgendaEntry nextEntry = iter.next();
			if (!nextEntry.equals(pusher) && !nextEntry.equals(pushedEntry)
					&& nextEntry.getPusher() != null) {
				Instant nextStartTime = nextEntry.getStartTime();
				Instant nextEndTime = nextEntry.getEndTime();

				Instant possibleNewStartTime = time;
				Instant possibleNewEndTime = time.plus(pushedEntry
						.getDuration());

				if (ActivityHelper.intersect(nextStartTime, nextEndTime,
						possibleNewStartTime, possibleNewEndTime)) {
					if (nextEndTime.isAfter(time)) {
						time = nextEndTime;
						pusher = nextEntry;

					}
				}
			}
		}

		return pusher;
	}

	/*
	 * A convenience method to quickly find out whether a given entry is one of
	 * the selected activities.
	 */
	private boolean isSelected(Row row, Layer layer, AgendaEntry entry) {
		ActivityRef<AgendaEntry> ref = new ActivityRef<AgendaEntry>(row, layer,
				entry);
		return context.getGraphics().getSelectedActivities().contains(ref);
	}

	/**
	 * This method restores the position of fixed entries "as much as possible",
	 * depending on whether the conflict with the pushing entry still exists or
	 * not.
	 */
	private boolean restoreEntriesAfterDrag() {
		Map<AgendaEntry, Box> copyOfFixedEntries = new HashMap<>(fixedBoxes);
		Iterator<Box> iter = copyOfFixedEntries.values().iterator();

		boolean fixedSomething = false;

		while (iter.hasNext()) {

			Box box = iter.next();

			/*
			 * Ideally this should never happen but it makes the code more fault
			 * tolerant.
			 */
			if (box.getEntry().getOriginalStartTime() == null) {
				continue;
			}

			AgendaEntry entry = box.getEntry();

			Instant newStartTime = box.getStartTime();
			Instant newEndTime = box.getEndTime();

			Duration moveDuration = null;

			/*
			 * Some fixed entries only have their original start and end times
			 * set but they do not have a pusher assigned to them. This is often
			 * the case for group members where one of the members was pushed
			 * and the others just follow along.
			 */
			if (entry.getPusher() != null) {

				boolean backInOriginalLocation = false;

				if (entry.getPushDirection().equals(PushDirection.UP)) {

					Instant pusherStartTime = entry.getPusher().getStartTime();
					if (pusherStartTime.isAfter(box.getEndTime())) {

						/*
						 * The fixed box was pushed up but now the pusher is
						 * starting after the current end time of the pushed
						 * box. This means we can move the pushed box down
						 * again, maybe to the start time of the pusher, maybe
						 * to the original location.
						 */
						newEndTime = pusherStartTime;

						if (newEndTime.isAfter(box.getLatestEntry()
								.getOriginalEndTime())) {

							/*
							 * Great, we can move back to the original location.
							 * The original end time is the end time of the
							 * "latest" entry inside the box.
							 */
							newEndTime = box.getLatestEntry()
									.getOriginalEndTime();

							backInOriginalLocation = true;
						}
					}

					/*
					 * We have found the new end time, but for moving purposes
					 * we actually need the new start time, so here we go.
					 */
					newStartTime = newEndTime.minus(box.getDuration());

				} else if (entry.getPushDirection().equals(PushDirection.DOWN)) {
					Instant pusherEndTime = entry.getPusher().getEndTime();
					if (pusherEndTime.isBefore(box.getStartTime())) {

						/*
						 * The fixed box was pushed down but now the pusher is
						 * finishing before the current start time of the pushed
						 * box. This means we can move the pushed box up again,
						 * maybe to the end time of the pusher, maybe to the
						 * original location.
						 */

						newStartTime = pusherEndTime;

						if (newStartTime.isBefore(box.getEarliestEntry()
								.getOriginalStartTime())) {

							/*
							 * Great, we can move back to the original location.
							 * The original start time is the start time of the
							 * "earliest" entry inside the box.
							 */

							newStartTime = box.getEarliestEntry()
									.getOriginalStartTime();

							backInOriginalLocation = true;
						}
					}
				}

				if (backInOriginalLocation) {
					fixedBoxes.remove(box);
				}
			}

			/*
			 * We need the opposite direction now to move the entries back to
			 * their new position, maybe their original location.
			 */
			PushDirection pushDirection = UP;
			if (entry.getPushDirection().equals(UP)) {
				pushDirection = DOWN;
			}

			if (pushDirection.equals(PushDirection.UP)) {
				moveDuration = Duration.between(newStartTime,
						box.getStartTime());
			} else {
				moveDuration = Duration.between(box.getStartTime(),
						newStartTime);
			}

			if (moveDuration != null && !moveDuration.equals(Duration.ZERO)) {
				fixedSomething = true;

				moveBox(box, moveDuration, pushDirection);
			}
		}

		return fixedSomething;
	}

	/**
	 * A box groups agenda entries together that are members of the same group
	 * on the same day.
	 */
	private class Box {

		/*
		 * The primary entry for which the box was created in the first place.
		 */
		private AgendaEntry entry;

		/*
		 * Entries that are in the same group as the primary entry and located
		 * on the same day.
		 */
		private List<AgendaEntry> entries = new ArrayList<>();

		private Row row;

		private Layer layer;

		/**
		 * Constructs a new box for the given entry. Automatically adds all
		 * entries of the same group (if applicable) that are located on the
		 * same day.
		 */
		public Box(AgendaEntry entry, Row row, Layer layer) {
			this.entry = requireNonNull(entry);
			this.row = requireNonNull(row);
			this.layer = requireNonNull(layer);

			for (AgendaEntry e : getGroupMembers(this)) {
				if (isSameDay(entry.getStartTime(), e.getStartTime())) {
					entries.add(e);
				}
			}
		}

		public Row getRow() {
			return row;
		}

		public Layer getLayer() {
			return layer;
		}

		private boolean isSameDay(Instant time1, Instant time2) {
			LocalDate day1 = ZonedDateTime.ofInstant(time1, row.getZoneId())
					.toLocalDate();
			LocalDate day2 = ZonedDateTime.ofInstant(time2, row.getZoneId())
					.toLocalDate();
			return day1.equals(day2);
		}

		/**
		 * Convenience method to quickly check if the given entry intersects
		 * with the time bounds of the box.
		 */
		public boolean intersectsWithEntry(AgendaEntry entry) {
			if (ActivityHelper.intersect(this.entry, entry)) {
				return true;
			}

			for (AgendaEntry e : getEntries()) {
				if (ActivityHelper.intersect(e, entry)) {
					return true;
				}
			}

			return false;
		}

		public AgendaEntry getEntry() {
			return entry;
		}

		public List<AgendaEntry> getEntries() {
			return entries;
		}

		/**
		 * Returns the start time of the box. This time is equivalent to the
		 * start time of the earliest entry in the box.
		 */
		public Instant getStartTime() {
			Instant time = entry.getStartTime();

			for (AgendaEntry e : getEntries()) {
				if (e.getStartTime().isBefore(time)) {
					time = e.getStartTime();
				}
			}

			return time;
		}

		/**
		 * Returns the end time of the box. This time is equivalent to the end
		 * time of the latest entry in the box.
		 */
		public Instant getEndTime() {
			Instant time = entry.getEndTime();

			for (AgendaEntry e : getEntries()) {
				if (e.getEndTime().isAfter(time)) {
					time = e.getEndTime();
				}
			}

			return time;
		}

		public Duration getDuration() {
			return Duration.between(getStartTime(), getEndTime());
		}

		public AgendaEntry getEarliestEntry() {
			AgendaEntry earliest = entry;
			for (AgendaEntry e : getEntries()) {
				if (e.getStartTime().isBefore(earliest.getStartTime())) {
					earliest = e;
				}
			}
			return earliest;
		}

		public AgendaEntry getLatestEntry() {
			AgendaEntry latest = entry;
			for (AgendaEntry e : getEntries()) {
				if (e.getEndTime().isAfter(latest.getEndTime())) {
					latest = e;
				}
			}
			return latest;
		}

		public boolean contains(AgendaEntry entry) {
			if (this.entry.equals(entry)) {
				return true;
			}

			return getEntries().contains(entry);
		}
	}

	/**
	 * Marks the entries inside the given box with the given push direction and
	 * sets the original start and end times to the current start and end times
	 * of the entries.
	 *
	 * @param box
	 *            the box to mark
	 * @param direction
	 *            the push direction used for the entries of the box
	 * @param pusher
	 *            the entry pushing the box
	 */
	private void markBox(Box box, PushDirection direction, AgendaEntry pusher) {
		fixedBoxes.put(box.getEntry(), box);
		markEntry(box.getEntry(), direction, pusher);
		for (AgendaEntry entry : box.getEntries()) {
			markEntry(entry, direction, pusher);
		}

		for (AgendaEntry entry : getGroupMembers(box)) {
			if (!box.contains(entry)) {
				markEntry(entry, direction, pusher);
			}
		}
	}

	/**
	 * Marks the given entry with the given push direction and sets the original
	 * start and end times to the current start and end times.
	 *
	 * @param pushedEntry
	 *            the entry to mark
	 * @param direction
	 *            the push direction used for the entries of the box
	 * @param pusher
	 *            the entry pushing the box
	 */
	private void markEntry(AgendaEntry pushedEntry, PushDirection direction,
			AgendaEntry pusher) {
		if (pushedEntry.getPushDirection().equals(NONE)) {
			pushedEntry.setOriginalStartTime(pushedEntry.getStartTime());
			pushedEntry.setOriginalEndTime(pushedEntry.getEndTime());
		}
		pushedEntry.setPushDirection(direction);
		pushedEntry.setPusher(pusher);
	}

	/**
	 * Moves all entries inside the given box by the given temporal amount.
	 *
	 * @param box
	 *            the box to move
	 * @param moveDuration
	 *            the time to move it by
	 * @param pushDirection
	 *            the direction into which to move
	 */
	private void moveBox(Box box, Duration moveDuration,
			PushDirection pushDirection) {

		ActivityRef<AgendaEntry> ref = new ActivityRef<AgendaEntry>(
				box.getRow(), box.getLayer(), box.entry);
		moveEntry(ref, moveDuration, pushDirection);

		for (AgendaEntry entry : box.getEntries()) {
			ref = new ActivityRef<AgendaEntry>(box.getRow(), box.getLayer(),
					entry);
			moveEntry(ref, moveDuration, pushDirection);
		}

		for (AgendaEntry entry : getGroupMembers(box)) {
			if (!box.contains(entry)) {
				ref = new ActivityRef<AgendaEntry>(box.getRow(),
						box.getLayer(), entry);
				moveEntry(ref, moveDuration, pushDirection);
			}
		}
	}

	/**
	 * Moves the given entry by the given temporal amount.
	 *
	 * @param entryRef
	 *            an activity reference pointing to the entry
	 * @param moveDuration
	 *            the time to move it by
	 * @param pushDirection
	 *            the direction into which to move
	 */
	private void moveEntry(ActivityRef<AgendaEntry> entryRef,
			Duration moveDuration, PushDirection pushDirection) {

		AgendaEntry entry = entryRef.getActivity();

		/*
		 * Always remove an entry first before making any changes to it.
		 */
		entryRef.detachFromRow();

		switch (pushDirection) {
		case DOWN:
			entry.setStartTime(entry.getStartTime().plus(moveDuration));
			entry.setEndTime(entry.getEndTime().plus(moveDuration));
			break;
		case UP:
			entry.setStartTime(entry.getStartTime().minus(moveDuration));
			entry.setEndTime(entry.getEndTime().minus(moveDuration));
			break;
		case NONE:
		default:
			break;
		}

		/*
		 * Reattach the entry to its repository. Give the repository a chance to
		 * insert the entry at the correct location.
		 */
		entryRef.attachToRow();
	}

	/*
	 * A convenience method to lookup all group members for the given box.
	 */
	private List<AgendaEntry> getGroupMembers(Box box) {
		return getGroupMembers(box.getRow(), box.getLayer(), box.getEntry());
	}

	/*
	 * A convenience method to lookup all group members for the given entry.
	 */
	private List<AgendaEntry> getGroupMembers(Row row, Layer layer,
			AgendaEntry entry) {

		Object groupId = entry.getGroupId();
		if (groupId == null) {
			return Collections.emptyList();
		}

		MutableActivityRepository<AgendaEntry> repository = (MutableActivityRepository<AgendaEntry>) row
				.getRepository();
		Instant earliest = repository.getEarliestTimeUsed();
		Instant latest = repository.getLatestTimeUsed();

		/*
		 * Calculate the earliest date used.
		 */
		Iterator<AgendaEntry> activities = repository.getActivities(layer,
				earliest, latest, DAYS, row.getZoneId());

		List<AgendaEntry> members = new ArrayList<>();
		while (activities.hasNext()) {
			AgendaEntry e = activities.next();
			if (!e.equals(entry) && groupId.equals(e.getGroupId())) {
				members.add(e);
			}
		}

		return members;
	}

	/**
	 * Sets the start and end time for all entries in the given box and for the
	 * members of the same group to their original values.
	 */
	private void resetToOriginalLocation(Box box) {
		resetToOriginalLocation(box.getRow(), box.getLayer(), box.getEntry());
		box.getEntries()
				.forEach(
						it -> resetToOriginalLocation(box.getRow(),
								box.getLayer(), it));
		getGroupMembers(box)
				.forEach(
						it -> resetToOriginalLocation(box.getRow(),
								box.getLayer(), it));
	}

	/**
	 * Sets the start and end time for the given entry to their original values.
	 */
	private final void resetToOriginalLocation(Row row, Layer layer,
			AgendaEntry entry) {
		ActivityRef<AgendaEntry> ref = new ActivityRef<>(row, layer, entry);

		if (entry.getOriginalStartTime() != null) {
			ref.detachFromRow();
			entry.setStartTime(entry.getOriginalStartTime());
			entry.setEndTime(entry.getOriginalEndTime());
			ref.attachToRow();
		}
	}

	/**
	 * Sets the start and end time for all entries in all fixed boxes to their
	 * original values.
	 */
	public final void reset(boolean originalLocations) {
		if (originalLocations) {
			fixedBoxes.values().forEach(it -> resetToOriginalLocation(it));
		}
		clearFixedBoxes();
	}

	/**
	 * One of the fix methods provided by the conflict resolver. Gets invoked
	 * when the uer changes the end time of an agenda entry.
	 *
	 * @param horizontalMove
	 *            a flag used to indicate whether the user has performed a
	 *            horizontal drag (new end time is on next day).
	 */
	public final void fixScheduleAfterEndTimeChange(boolean horizontal) {

		/*
		 * If overlapping is allowed then we do not have to do anything.
		 */
		if (context.allowOverlappingProperty().get()) {
			return;
		}

		if (horizontal) {
			reset(true);
		}

		/*
		 * For each selected / edited activity try to resolve all new conflicts
		 * that exist after the last drag event.
		 */
		for (ActivityRef<?> ref : context.getGraphics().getSelectedActivities()) {
			AgendaEntry entry = (AgendaEntry) ref.getActivity();
			Row row = ref.getRow();
			Layer layer = ref.getLayer();

			resolveConflictsAfterEndTimeChange(row, layer,
					row.getLineIndex(entry), entry, horizontal);
		}

		/*
		 * We normally always restore but the context might have this option for
		 * debugging so that it is easier to distinguish between the behaviour
		 * of the conflict resolution and the behaviour of the restore code.
		 */
		if (context.restoreProperty().get()) {
			boolean fixedSomething = false;
			do {
				fixedSomething = restoreEntriesAfterEndTimeChange();
			} while (fixedSomething);
		}

		context.getGraphics().redraw();
	}

	/**
	 * Performs the actual conflict resolution after the user has performed an
	 * end time change on an agenda entry.
	 *
	 * @param row
	 *            the row where the conflict resolution will run
	 * @param layer
	 *            the layer on which the conflict resolution will run
	 * @param lineIndex
	 *            the index of the line where the conflict resolution will run
	 * @param editedEntry
	 *            the edited agenda entry that might be causing conflicts
	 * @param pushedEntry
	 *            an agenda entry which is in conflict with the currently edited
	 *            entry
	 * @param horizontalMove
	 *            a flag signalling wether the new end time was passing day
	 *            boundaries
	 */
	private void resolveConflictsAfterEndTimeChange(Row row, Layer layer,
			int lineIndex, AgendaEntry editedEntry, boolean horizontal) {
		Set<AgendaEntry> ignoredEntries = new HashSet<>();
		resolveConflictsAfterEndTimeChange(row, layer, lineIndex,
				ignoredEntries, editedEntry, editedEntry.getStartTime(),
				editedEntry.getEndTime(), horizontal);
	}

	/**
	 * Performs the actual conflict resolution after the user has performed an
	 * end time change on an agenda entry. Basically this algorithm finds all
	 * entries with which the edited entry has a conflict and pushes them to the
	 * end of the edited entry (stacking them, one after the other). The area
	 * used for the moved entries will then be checked next for conflicts, and
	 * so on....
	 *
	 * @param row
	 *            the row where the conflict resolution will run
	 * @param layer
	 *            the layer on which the conflict resolution will run
	 * @param lineIndex
	 *            the index of the line where the conflict resolution will run
	 * @param ignoredEntries
	 *            a set of entries that no longer need to be fixed
	 * @param editedEntry
	 *            the edited agenda entry that might be causing conflicts
	 * @param pushedEntry
	 *            an agenda entry which is in conflict with the currently edited
	 *            entry
	 * @param horizontalMove
	 *            a flag signalling wether the new end time was passing day
	 *            boundaries
	 */
	private void resolveConflictsAfterEndTimeChange(Row row, Layer layer,
			int lineIndex, Set<AgendaEntry> ignoredEntries,
			AgendaEntry editedEntry, Instant startTime, Instant endTime,
			boolean horizontal) {

		MutableActivityRepository<AgendaEntry> repository = (MutableActivityRepository<AgendaEntry>) row
				.getRepository();

		Iterator<AgendaEntry> iter = repository.getActivities(layer, startTime,
				endTime, DAYS, row.getZoneId());

		Instant time = editedEntry.getEndTime();
		Instant earliest = null;
		Instant latest = null;

		Box latestBox = null;

		boolean movedStuff = false;

		Set<AgendaEntry> newIgnoredEntries = new HashSet<>(ignoredEntries);

		while (iter.hasNext()) {

			AgendaEntry pushedEntry = iter.next();

			ActivityRef<AgendaEntry> ref = new ActivityRef<AgendaEntry>(row,
					layer, pushedEntry);

			if (!pushedEntry.equals(editedEntry)
					&& !newIgnoredEntries.contains(pushedEntry)
					&& !context.getGraphics().getSelectedActivities()
							.contains(ref)) {

				Box box = new Box(pushedEntry, row, layer);

				if (horizontal && pushedEntry.getGroupId() != null) {
					List<AgendaEntry> groupMembers = getGroupMembers(row,
							layer, pushedEntry);
					AgendaEntry firstGroupMember = groupMembers.get(0);
					if (firstGroupMember.getStartTime().isBefore(
							pushedEntry.getStartTime())) {
						pushedEntry = firstGroupMember;
						box = new Box(pushedEntry, row, layer);
						newIgnoredEntries.addAll(groupMembers);
					}
				}

				if (pushedEntry.equals(editedEntry)) {
					continue;
				}

				Duration duration = Duration.between(
						pushedEntry.getStartTime(), time);

				markBox(box, DOWN, editedEntry);
				moveBox(box, duration, DOWN);

				time = box.getEndTime();
				editedEntry = pushedEntry;

				if (earliest == null || box.getStartTime().isBefore(earliest)) {
					earliest = box.getStartTime();
				}

				if (latest == null || box.getEndTime().isAfter(latest)) {
					latest = box.getEndTime();
					latestBox = box; // last entry of box is new pusher
				}

				movedStuff = true;

				newIgnoredEntries.add(pushedEntry);

				List<AgendaEntry> groupMembers = getGroupMembers(row, layer,
						pushedEntry);
				for (AgendaEntry member : groupMembers) {
					resolveConflictsAfterEndTimeChange(row, layer, lineIndex,
							newIgnoredEntries, member, member.getStartTime(),
							member.getEndTime(), horizontal);
				}
			}
		}

		if (movedStuff && latestBox != null) {
			resolveConflictsAfterEndTimeChange(row, layer, lineIndex,
					newIgnoredEntries, latestBox.getLatestEntry(), earliest,
					latest, horizontal);
		}
	}

	/**
	 * This method restores the position of fixed entries after an end time
	 * change event "as much as possible", depending on whether the conflict
	 * with the pushing entry still exists or not.
	 */
	private boolean restoreEntriesAfterEndTimeChange() {
		Map<AgendaEntry, Box> copyOfFixedEntries = new HashMap<>(fixedBoxes);
		Iterator<Box> iter = copyOfFixedEntries.values().iterator();

		// sort fixed boxes based on start time
		List<Box> sortedList = new ArrayList<>();
		while (iter.hasNext()) {
			Box box = iter.next();
			sortedList.add(box);
		}

		Collections.sort(sortedList, new Comparator<Box>() {
			@Override
			public int compare(AgendaConflictResolver<R>.Box o1,
					AgendaConflictResolver<R>.Box o2) {
				return o1.getEntry().getStartTime()
						.compareTo(o2.getEntry().getStartTime());
			}
		});

		boolean fixedSomething = false;

		for (Box box : sortedList) {

			/*
			 * Ideally this should never happen but it makes the code more fault
			 * tolerant.
			 */
			if (box.getEntry().getOriginalStartTime() == null) {
				continue;
			}

			AgendaEntry entry = box.getEntry();

			Instant newStartTime = box.getStartTime();

			Duration moveDuration = null;

			/*
			 * Some fixed entries only have their original start and end times
			 * set but they do not have a pusher assigned to them. This is often
			 * the case for group members where one of the members was pushed
			 * and the others just follow along.
			 */
			if (entry.getPusher() != null) {

				boolean backInOriginalLocation = false;

				Instant pusherEndTime = entry.getPusher().getEndTime();
				if (pusherEndTime.isBefore(box.getStartTime())) {

					/*
					 * The fixed box was pushed down but now the pusher is
					 * finishing before the current start time of the pushed
					 * box. This means we can move the pushed box up again,
					 * maybe to the end time of the pusher, maybe to the
					 * original location.
					 */

					newStartTime = pusherEndTime;

					if (newStartTime.isBefore(box.getEarliestEntry()
							.getOriginalStartTime())) {

						/*
						 * Great, we can move back to the original location. The
						 * original start time is the start time of the
						 * "earliest" entry inside the box.
						 */

						newStartTime = box.getEarliestEntry()
								.getOriginalStartTime();

						backInOriginalLocation = true;
					}
				}

				if (backInOriginalLocation) {
					fixedBoxes.remove(box);
				}
			}

			moveDuration = Duration.between(newStartTime, box.getStartTime());

			if (moveDuration != null && !moveDuration.equals(Duration.ZERO)) {
				fixedSomething = true;
				moveBox(box, moveDuration, UP);
			}
		}

		return fixedSomething;
	}

	/**
	 * One of the fix methods provided by the conflict resolver. Gets invoked
	 * when the uer changes the start time of an agenda entry.
	 *
	 * @param horizontalMove
	 *            a flag used to indicate whether the user has performed a
	 *            horizontal drag (new start time is on previous day).
	 */
	public final void fixScheduleAfterStartTimeChange(boolean horizontal) {

		/*
		 * If overlapping is allowed then we do not have to do anything.
		 */
		if (context.allowOverlappingProperty().get()) {
			return;
		}

		if (horizontal) {
			reset(true);
		}

		/*
		 * For each selected / edited activity try to resolve all new conflicts
		 * that exist after the last drag event.
		 */
		for (ActivityRef<?> ref : context.getGraphics().getSelectedActivities()) {
			AgendaEntry entry = (AgendaEntry) ref.getActivity();
			Row row = ref.getRow();
			Layer layer = ref.getLayer();

			resolveConflictsAfterStartTimeChange(row, layer,
					row.getLineIndex(entry), entry, horizontal);
		}

		/*
		 * We normally always restore but the context might have this option for
		 * debugging so that it is easier to distinguish between the behaviour
		 * of the conflict resolution and the behaviour of the restore code.
		 */
		if (context.restoreProperty().get()) {
			boolean fixedSomething = false;
			do {
				fixedSomething = restoreEntriesAfterStartTimeChange();
			} while (fixedSomething);
		}

		context.getGraphics().redraw();
	}

	/**
	 * Performs the actual conflict resolution after the user has performed a
	 * start time change on an agenda entry.
	 *
	 * @param row
	 *            the row where the conflict resolution will run
	 * @param layer
	 *            the layer on which the conflict resolution will run
	 * @param lineIndex
	 *            the index of the line where the conflict resolution will run
	 * @param editedEntry
	 *            the edited agenda entry that might be causing conflicts
	 * @param pushedEntry
	 *            an agenda entry which is in conflict with the currently edited
	 *            entry
	 * @param horizontalMove
	 *            a flag signalling wether the new start time was passing day
	 *            boundaries
	 */
	private void resolveConflictsAfterStartTimeChange(Row row, Layer layer,
			int lineIndex, AgendaEntry pusher, boolean horizontal) {
		Set<AgendaEntry> ignoredEntries = new HashSet<>();
		resolveConflictsAfterStartTimeChange(row, layer, lineIndex,
				ignoredEntries, pusher, pusher.getStartTime(),
				pusher.getEndTime(), horizontal);
	}

	/**
	 * Performs the actual conflict resolution after the user has performed a
	 * start time change on an agenda entry. Basically this algorithm finds all
	 * entries with which the edited entry has a conflict and pushes them to the
	 * start of the edited entry (stacking them, one before the other). The area
	 * used for the moved entries will then be checked next for conflicts, and
	 * so on....
	 *
	 * @param row
	 *            the row where the conflict resolution will run
	 * @param layer
	 *            the layer on which the conflict resolution will run
	 * @param lineIndex
	 *            the index of the line where the conflict resolution will run
	 * @param ignoredEntries
	 *            a set of entries that no longer need to be fixed
	 * @param editedEntry
	 *            the edited agenda entry that might be causing conflicts
	 * @param pushedEntry
	 *            an agenda entry which is in conflict with the currently edited
	 *            entry
	 * @param horizontalMove
	 *            a flag signalling wether the new start time was passing day
	 *            boundaries
	 */
	private void resolveConflictsAfterStartTimeChange(Row row, Layer layer,
			int lineIndex, Set<AgendaEntry> ignoredEntries, AgendaEntry pusher,
			Instant startTime, Instant endTime, boolean horizontal) {

		MutableActivityRepository<AgendaEntry> repository = (MutableActivityRepository<AgendaEntry>) row
				.getRepository();

		Iterator<AgendaEntry> iter = repository.getActivities(layer, startTime,
				endTime, DAYS, row.getZoneId());

		List<AgendaEntry> list = createBackwardsIterationList(iter);

		iter = list.iterator();

		Instant time = pusher.getStartTime();
		Instant earliest = null;
		Instant latest = null;

		Box earliestBox = null;

		boolean movedStuff = false;

		Set<AgendaEntry> newIgnoredEntries = new HashSet<>(ignoredEntries);

		while (iter.hasNext()) {

			AgendaEntry pushedEntry = iter.next();

			ActivityRef<AgendaEntry> ref = new ActivityRef<AgendaEntry>(row,
					layer, pushedEntry);

			if (!pushedEntry.equals(pusher)
					&& !newIgnoredEntries.contains(pushedEntry)
					&& !context.getGraphics().getSelectedActivities()
							.contains(ref)) {

				Box box = new Box(pushedEntry, row, layer);

				if (horizontal && pushedEntry.getGroupId() != null) {
					List<AgendaEntry> groupMembers = getGroupMembers(row,
							layer, pushedEntry);
					AgendaEntry lastGroupMember = groupMembers.get(groupMembers
							.size() - 1);
					if (lastGroupMember.getEndTime().isAfter(
							pushedEntry.getEndTime())) {
						pushedEntry = lastGroupMember;
						box = new Box(pushedEntry, row, layer);
						newIgnoredEntries.addAll(groupMembers);
					}
				}

				if (pushedEntry.equals(pusher)) {
					continue;
				}

				Duration duration = Duration.between(time,
						pushedEntry.getEndTime());

				markBox(box, UP, pusher);
				moveBox(box, duration, UP);

				time = box.getStartTime();
				pusher = pushedEntry;

				if (earliest == null || box.getStartTime().isBefore(earliest)) {
					earliest = box.getStartTime();
					earliestBox = box; // first entry of box is new pusher
				}

				if (latest == null || box.getEndTime().isAfter(latest)) {
					latest = box.getEndTime();
				}

				movedStuff = true;

				newIgnoredEntries.add(pushedEntry);

				List<AgendaEntry> groupMembers = getGroupMembers(row, layer,
						pushedEntry);

				Collections.sort(groupMembers, new Comparator<AgendaEntry>() {
					@Override
					public int compare(AgendaEntry o1, AgendaEntry o2) {
						return -o1.getEndTime().compareTo(o2.getEndTime());
					}
				});

				for (AgendaEntry member : groupMembers) {
					resolveConflictsAfterStartTimeChange(row, layer, lineIndex,
							newIgnoredEntries, member, member.getStartTime(),
							member.getEndTime(), horizontal);
				}
			}
		}

		if (movedStuff && earliestBox != null) {
			resolveConflictsAfterStartTimeChange(row, layer, lineIndex,
					newIgnoredEntries, earliestBox.getEarliestEntry(),
					earliest, latest, horizontal);
		}
	}

	/**
	 * This method restores the position of fixed entries after a start time
	 * change event "as much as possible", depending on whether the conflict
	 * with the pushing entry still exists or not.
	 */
	private boolean restoreEntriesAfterStartTimeChange() {
		Map<AgendaEntry, Box> copyOfFixedEntries = new HashMap<>(fixedBoxes);
		Iterator<Box> iter = copyOfFixedEntries.values().iterator();

		// sort fixed boxes based on end time
		List<Box> sortedList = new ArrayList<>();
		while (iter.hasNext()) {
			Box box = iter.next();
			sortedList.add(box);
		}

		Collections.sort(sortedList, new Comparator<Box>() {
			@Override
			public int compare(AgendaConflictResolver<R>.Box o1,
					AgendaConflictResolver<R>.Box o2) {
				return -o1.getEntry().getStartTime()
						.compareTo(o2.getEntry().getStartTime());
			}
		});

		boolean fixedSomething = false;

		for (Box box : sortedList) {

			/*
			 * Ideally this should never happen but it makes the code more fault
			 * tolerant.
			 */
			if (box.getEntry().getOriginalStartTime() == null) {
				continue;
			}

			AgendaEntry entry = box.getEntry();

			Instant newEndTime = box.getEndTime();

			Duration moveDuration = null;

			/*
			 * Some fixed entries only have their original start and end times
			 * set but they do not have a pusher assigned to them. This is often
			 * the case for group members where one of the members was pushed
			 * and the others just follow along.
			 */
			if (entry.getPusher() != null) {

				boolean backInOriginalLocation = false;

				Instant pusherStartTime = entry.getPusher().getStartTime();
				if (pusherStartTime.isAfter(box.getEndTime())) {

					/*
					 * The fixed box was pushed up but now the pusher is
					 * starting after the current end time of the pushed box.
					 * This means we can move the pushed box down again, maybe
					 * to the start time of the pusher, maybe to the original
					 * location.
					 */
					newEndTime = pusherStartTime;

					if (newEndTime.isAfter(box.getLatestEntry()
							.getOriginalEndTime())) {

						/*
						 * Great, we can move back to the original location. The
						 * original end time is the end time of the "latest"
						 * entry inside the box.
						 */
						newEndTime = box.getLatestEntry().getOriginalEndTime();

						backInOriginalLocation = true;
					}
				}

				if (backInOriginalLocation) {
					fixedBoxes.remove(box);
				}
			}

			moveDuration = Duration.between(box.getEndTime(), newEndTime);

			if (moveDuration != null && !moveDuration.equals(Duration.ZERO)) {
				fixedSomething = true;
				moveBox(box, moveDuration, DOWN);
			}
		}

		return fixedSomething;
	}
}
