/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.editor;

import static javafx.scene.input.KeyEvent.KEY_PRESSED;
import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;
import static javafx.scene.input.MouseEvent.MOUSE_MOVED;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import com.flexganttfx.editor.AgendaEntryBase.Type;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.ActivityRepository;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.MutableActivity;
import com.flexganttfx.model.dateline.VirtualGrid;
import com.flexganttfx.model.repository.MutableActivityRepository;
import com.flexganttfx.model.util.ActivityHelper;
import com.flexganttfx.view.graphics.ActivityEvent;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.GraphicsBase.EditingCallbackParameter;
import com.flexganttfx.view.graphics.LassoEvent;
import com.flexganttfx.view.graphics.LassoEvent.LassoInfo;
import com.flexganttfx.view.timeline.Dateline;

/**
 * The agenda controller is used to handle the various editing operations that
 * the user can perform inside an agenda and to trigger the conflict resolution.
 * The controller also enables the multi-entry editing: a change to one entry
 * will be applied to all currently selected entries.
 *
 * @param <R>
 *            the type of rows used by the context
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class AgendaController<R extends Row<?, ?, ?>> {

	private final Stack<Snapshot> undoStack = new Stack<>();

	private final Stack<Snapshot> redoStack = new Stack<>();

	private final AgendaConflictResolver<R> conflictResolver;

	private final AgendaEditorContext<R> context;

	private final GraphicsBase<R> graphics;

	private final Layer layer;

	private MouseEvent lastMouseEvent;

	/**
	 * Constructs a new controller for the given context and layer.
	 *
	 * @param context
	 *            the editor context for which the controller will be used
	 * @param layer
	 *            the layer where the editing will take place
	 */
	public AgendaController(AgendaEditorContext<R> context, Layer layer) {

		this.context = context;
		this.layer = layer;

		this.conflictResolver = new AgendaConflictResolver<>(context);
		this.graphics = context.getGraphics();

		/*
		 * We are using the lasso to add activities.
		 */
		graphics.addEventHandler(LassoEvent.SELECTION_FINISHED,
				evt -> addActivity(evt));

		graphics.addEventHandler(ActivityEvent.START_TIME_CHANGE_ONGOING,
				evt -> activityStartTimeChanging(evt));
		graphics.addEventHandler(ActivityEvent.END_TIME_CHANGE_ONGOING,
				evt -> activityEndTimeChanging(evt));
		graphics.addEventHandler(ActivityEvent.HORIZONTAL_DRAG_ONGOING,
				evt -> activityDragging(evt));

		/*
		 * A double click with the mouse also creates a new activity.
		 */
		graphics.addEventHandler(MOUSE_CLICKED, evt -> addActivity(evt));

		graphics.addEventHandler(MOUSE_MOVED, evt -> lastMouseEvent = evt);

		/*
		 * Undo, redo, copy, paste.
		 */
		graphics.addEventHandler(KEY_PRESSED, evt -> handleKeyEvent(evt));

		/*
		 * Cleanups after editing has finished.
		 */
		graphics.addEventHandler(ActivityEvent.START_TIME_CHANGE_STARTED,
				evt -> editingStarted(evt));
		graphics.addEventHandler(ActivityEvent.END_TIME_CHANGE_STARTED,
				evt -> editingStarted(evt));
		graphics.addEventHandler(ActivityEvent.HORIZONTAL_DRAG_STARTED,
				evt -> editingStarted(evt));

		/*
		 * Cleanups after editing has finished.
		 */
		graphics.addEventHandler(ActivityEvent.START_TIME_CHANGE_FINISHED,
				evt -> cleanup());
		graphics.addEventHandler(ActivityEvent.END_TIME_CHANGE_FINISHED,
				evt -> cleanup());
		graphics.addEventHandler(ActivityEvent.HORIZONTAL_DRAG_FINISHED,
				evt -> cleanup());

		/*
		 * If the user selects an entry that is a member of a group, then select
		 * all entries in that group.
		 */
		graphics.getSelectedActivities().addListener(
				new ListChangeListener<ActivityRef<?>>() {
					@Override
					public void onChanged(
							javafx.collections.ListChangeListener.Change<? extends ActivityRef<?>> change) {
						while (change.next()) {
							if (!autoSelection) {
								autoSelection = true;
								performGroupSelection();
								autoSelection = false;
							}
						}
					}
				});

		/*
		 * Determines the editing operations that are possible on an agenda
		 * entry.
		 */
		graphics.setActivityEditingCallback(AgendaEntryBase.class,
				new Callback<EditingCallbackParameter, Boolean>() {
					@Override
					public Boolean call(EditingCallbackParameter param) {
						switch (param.getEditMode()) {
						case CHART_VALUE_CHANGE:
						case CHART_VALUE_HIGH_CHANGE:
						case CHART_VALUE_LOW_CHANGE:
						case DRAGGING_VERTICAL:
						case AGENDA_ASSIGNING:
						case PERCENTAGE_COMPLETE_CHANGE:
						case START_TIME_CHANGE:
						case END_TIME_CHANGE:
						case DRAGGING:
							return false;
						case DRAGGING_HORIZONTAL:
						case AGENDA_DRAGGING:
						case AGENDA_END_TIME_CHANGE:
						case AGENDA_START_TIME_CHANGE:
						case NONE:
							return true;
						default:
							return false;
						}
					}
				});
	}

	/*
	 * Used to store the time where an editing operation has initially started.
	 */
	private Instant initialTime;

	/*
	 * Used to store the date where an editing operation has initially started.
	 */
	private LocalDate initialDate;

	/*
	 * Resets the initial time and date. Gets called when an editing operation
	 * has finished.
	 */
	private void cleanup() {
		initialTime = null;
		initialDate = null;
		graphics.redraw();
	}

	private void editingStarted(ActivityEvent evt) {

		Row row = evt.getActivityRef().getRow();
		saveSnapshot(row);

		if (evt.getEventType().equals(ActivityEvent.HORIZONTAL_DRAG_STARTED)) {
			initialTime = evt.getOldTimeInterval().getStartTime();
		} else {
			initialTime = evt.getOldTime();
		}

		initialDate = ZonedDateTime.ofInstant(initialTime, row.getZoneId())
				.toLocalDate();
	}

	/**
	 * Part of the undo / redo functionality. Saves the current state of the
	 * agenda into a snapshot instance.
	 *
	 * @param row
	 *            the row where the agenda is located
	 */
	private void saveSnapshot(Row row) {

		MutableActivityRepository<AgendaEntry> repository = (MutableActivityRepository<AgendaEntry>) row
				.getRepository();

		Iterator<AgendaEntry> iter = repository.getActivities(layer,
				repository.getEarliestTimeUsed(),
				repository.getLatestTimeUsed(), ChronoUnit.DAYS,
				row.getZoneId());

		Snapshot snapshot = new Snapshot(row);

		while (iter.hasNext()) {
			AgendaEntry entry = iter.next();
			snapshot.getStateMap().put(entry, new SavedState(entry));
		}

		undoStack.push(snapshot);

		if (undoStack.size() > 10) {
			undoStack.remove(1);
		}
	}

	private void undo() {

		if (undoStack.isEmpty()) {
			return;
		}

		Snapshot snapshot = undoStack.pop();

		restoreSnapshot(snapshot);

		redoStack.push(snapshot);
	}

	private void redo() {

		if (redoStack.isEmpty()) {
			return;
		}

		Snapshot snapshot = redoStack.pop();

		restoreSnapshot(snapshot);

		undoStack.push(snapshot);
	}

	private void restoreSnapshot(Snapshot snapshot) {
		if (snapshot != null) {

			Row row = snapshot.getRow();

			MutableActivityRepository<AgendaEntry> repository = (MutableActivityRepository<AgendaEntry>) row
					.getRepository();

			Iterator<AgendaEntry> iter = repository.getActivities(layer,
					repository.getEarliestTimeUsed(),
					repository.getLatestTimeUsed(), ChronoUnit.DAYS,
					row.getZoneId());

			while (iter.hasNext()) {
				AgendaEntry entry = iter.next();
				ActivityRef<AgendaEntry> ref = new ActivityRef<AgendaEntry>(
						row, layer, entry);

				SavedState state = snapshot.getStateMap().get(entry);

				if (state != null) {
					ref.detachFromRow();
					entry.setStartTime(state.getStart());
					entry.setEndTime(state.getEnd());
					ref.attachToRow();
				}
			}

			graphics.redraw();
		}
	}

	/**
	 * Gets called multiple times while the user is dragging an entry with the
	 * mouse.
	 *
	 * @param evt
	 *            the activity event issued by the framework to inform the
	 *            application that the user has dragged an entry
	 */
	private void activityDragging(ActivityEvent evt) {

		Row row = evt.getActivityRef().getRow();
		Activity activity = evt.getActivityRef().getActivity();

		Instant startTime = activity.getStartTime();

		ZonedDateTime zonedNewTime = ZonedDateTime.ofInstant(startTime,
				row.getZoneId());

		LocalTime localStartTime = zonedNewTime.toLocalTime();

		ZonedDateTime zonedInitialTime = ZonedDateTime.ofInstant(initialTime,
				row.getZoneId());

		LocalTime oldLocalTime = zonedInitialTime.toLocalTime();
		long deltaMillis = oldLocalTime
				.until(localStartTime, ChronoUnit.MILLIS);
		initialTime = initialTime.plus(deltaMillis, ChronoUnit.MILLIS);

		long deltaDays = initialDate.until(zonedNewTime, ChronoUnit.DAYS);

		initialDate = initialDate.plusDays(deltaDays);

		/*
		 * Perform the same drag on all selected activities, except for the one
		 * already specified by the event.
		 */
		for (ActivityRef<?> ref : graphics.getSelectedActivities()) {

			AgendaEntryBase act = (AgendaEntryBase) ref.getActivity();

			if (act instanceof MutableActivity
					&& !(ref.equals(evt.getActivityRef()))) {

				ref.detachFromRow();

				MutableActivity mutableActivity = act;

				Instant mutableStartTime = mutableActivity.getStartTime();
				Instant mutableEndTime = mutableActivity.getEndTime();

				zonedNewTime = ZonedDateTime
                        .ofInstant(mutableStartTime, row.getZoneId())
                        .plus(deltaMillis, ChronoUnit.MILLIS).plusDays(deltaDays);

				Instant newStartTime = Instant.from(zonedNewTime);
				mutableActivity.setStartTime(newStartTime);

				zonedNewTime = ZonedDateTime
                        .ofInstant(mutableEndTime, row.getZoneId())
                        .plus(deltaMillis, ChronoUnit.MILLIS).plusDays(deltaDays);

				Instant newEndTime = Instant.from(zonedNewTime);
				mutableActivity.setEndTime(newEndTime);

				ref.attachToRow();
			}
		}

		/*
		 * The drag was done between different days, we have to reset the
		 * conflict resolver.
		 */
		if (deltaDays != 0) {
			conflictResolver.reset(true);
		}

		boolean horizontalMove = deltaDays != 0;

		if (context.changeDelayProperty().get() <= 0) {
			conflictResolver.fixScheduleAfterDrag(horizontalMove);
			graphics.redraw();
		} else {
			if (fixThread != null && fixThread.isAlive()) {
				fixThread.cancel();
				horizontalMove = horizontalMove | fixThread.isHorizontalMove();
			}

			fixThread = new FixThread();
			fixThread.setHorizontalMove(horizontalMove);
			fixThread.start();
		}
	}

	private FixThread fixThread;

	/**
	 * The fix thread is used to invoke the conflict resolution delayed. This
	 * way the user can quickly move entries around without triggering the
	 * resolution.
	 */
	class FixThread extends Thread {

		private boolean horizontalMove;
		private boolean cancelled = false;

		public void setHorizontalMove(boolean horizontalMove) {
			this.horizontalMove = horizontalMove;
		}

		public boolean isHorizontalMove() {
			return horizontalMove;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(context.changeDelayProperty().get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (!cancelled) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						conflictResolver.fixScheduleAfterDrag(horizontalMove);
						graphics.redraw();
						horizontalMove = false;
					}
				});
			}
		}

		public void cancel() {
			cancelled = true;
		}
	}

	/**
	 * Gets called multiple times while the user is changing the end time of an
	 * entry with the mouse.
	 *
	 * @param evt
	 *            the activity event issued by the framework to inform the
	 *            application that the user has changed the end time of an entry
	 */
	private void activityEndTimeChanging(ActivityEvent evt) {
		Activity activity = evt.getActivityRef().getActivity();
		Row row = evt.getActivityRef().getRow();

		Instant endTime = activity.getEndTime();
		ZonedDateTime zonedEndTime = ZonedDateTime.ofInstant(endTime,
				row.getZoneId());

		ZonedDateTime zonedOldDateTime = ZonedDateTime.ofInstant(initialTime,
				row.getZoneId());
		long difference = zonedOldDateTime.until(zonedEndTime,
				ChronoUnit.MILLIS);

		initialTime = initialTime.plus(difference, ChronoUnit.MILLIS);

		long deltaDays = initialTime.until(zonedOldDateTime, ChronoUnit.DAYS);

		/*
		 * Perform the same end time change on all selected activities, except
		 * for the one already specified by the event.
		 */
		for (ActivityRef<?> ref : graphics.getSelectedActivities()) {
			Activity act = ref.getActivity();
			if (act instanceof MutableActivity
					&& !(ref.equals(evt.getActivityRef()))) {

				ref.detachFromRow();

				MutableActivity mutableActivity = (MutableActivity) act;
				Instant mutableEndTime = mutableActivity.getEndTime();
				zonedEndTime = ZonedDateTime.ofInstant(mutableEndTime,
						row.getZoneId());
				Instant newEndTime = Instant.from(zonedEndTime.plus(difference,
						ChronoUnit.MILLIS));
				mutableActivity.setEndTime(newEndTime);

				ref.attachToRow();
			}
		}

		conflictResolver.fixScheduleAfterEndTimeChange(deltaDays != 0);
	}

	/**
	 * Gets called multiple times while the user is changing the start time of
	 * an entry with the mouse.
	 *
	 * @param evt
	 *            the activity event issued by the framework to inform the
	 *            application that the user has changed the start time of an
	 *            entry
	 */
	private void activityStartTimeChanging(ActivityEvent evt) {
		Activity activity = evt.getActivityRef().getActivity();
		Row row = evt.getActivityRef().getRow();

		Instant startTime = activity.getStartTime();
		ZonedDateTime zonedStartTime = ZonedDateTime.ofInstant(startTime,
				row.getZoneId());

		ZonedDateTime zonedOldDateTime = ZonedDateTime.ofInstant(initialTime,
				row.getZoneId());
		long difference = zonedOldDateTime.until(zonedStartTime,
				ChronoUnit.MILLIS);

		initialTime = initialTime.plus(difference, ChronoUnit.MILLIS);

		long deltaDays = initialTime.until(zonedOldDateTime, ChronoUnit.DAYS);

		/*
		 * Perform the same start time change on all selected activities, except
		 * for the one already specified by the event.
		 */
		for (ActivityRef<?> ref : graphics.getSelectedActivities()) {
			Activity act = ref.getActivity();
			if (act instanceof MutableActivity
					&& !(ref.equals(evt.getActivityRef()))) {

				ref.detachFromRow();

				MutableActivity mutableActivity = (MutableActivity) act;
				Instant mutableStartTime = mutableActivity.getStartTime();
				zonedStartTime = ZonedDateTime.ofInstant(mutableStartTime,
						row.getZoneId());
				Instant newStartTime = Instant.from(zonedStartTime.plus(
						difference, ChronoUnit.MILLIS));
				mutableActivity.setStartTime(newStartTime);

				ref.attachToRow();
			}
		}

		conflictResolver.fixScheduleAfterStartTimeChange(deltaDays != 0);
	}

	/**
	 * Adds an activity after the user has performed a double click.
	 *
	 * @param evt
	 *            the mouse event
	 */
	private void addActivity(MouseEvent evt) {
		if (evt.getClickCount() == 2) {
			Row agendaRow = graphics.getRowAt(evt.getY());

			if (agendaRow != null) {
				Instant st = graphics.getTimeAt(evt.getX());

				st = ZonedDateTime.ofInstant(st, ZoneId.systemDefault())
						.truncatedTo(ChronoUnit.DAYS)
						.with(graphics.getLocalTimeAt(evt.getY())).toInstant();

				Dateline dateline = graphics.getTimeline().getDateline();
				DayOfWeek firstDayOfWeek = dateline.getFirstDayOfWeek();

				VirtualGrid<?> grid = graphics.getVirtualGrid();
				if (grid != null) {
					st = grid.adjustTime(st, agendaRow.getZoneId(), false,
							firstDayOfWeek);
				}

				Instant et = st.plus(context.initialEntryDurationProperty()
						.get());

				AgendaEntryBase entry = new AgendaEntryBase(Type.SPORT);
				entry.setName(Integer.toString(activityCounter++));
				entry.setStartTime(st);
				entry.setEndTime(et);
				agendaRow.addActivity(layer, entry);
			}
		}
	}

	/*
	 * Used for naming new entries.
	 */
	private int activityCounter = 1;

	/**
	 * Adds an activity based on the lasso event information. The lasso can be
	 * used to create entries on multiple days.
	 *
	 * @param evt
	 *            the lasso event
	 */
	private void addActivity(LassoEvent evt) {

		LassoInfo info = evt.getInfo();

		ZonedDateTime startTime = ZonedDateTime
				.ofInstant(info.getStartTime(), ZoneId.systemDefault())
				.truncatedTo(ChronoUnit.DAYS).with(info.getLocalStartTime());

		ZonedDateTime endTime = ZonedDateTime
				.ofInstant(info.getEndTime(), ZoneId.systemDefault())
				.truncatedTo(ChronoUnit.DAYS).with(info.getLocalEndTime());

		if (!info.getActivities().isEmpty()) {
			
			if (!graphics.isLassoSnapsToGrid()) {
				return;
			}
			
			/*
			 * We can not just check the bounds as they are not precise enough.
			 * We need to work with the time interval of the activity and the
			 * time interval of the lasso.
			 */
			for (ActivityRef<?> ref : info.getActivities()) {
				Activity activity = ref.getActivity();
				if (ActivityHelper.intersect(activity.getStartTime(),
						activity.getEndTime(), info.getStartTime(),
						info.getEndTime())) {
					return;
				}
			}
		}

		List<Row<?, ?, ?>> rows = info.getRows();
		if (rows.size() != 1) {
			return;
		}

		Row agendaRow = rows.get(0);

		if (info.getStartTime().equals(info.getEndTime())) {
			// no drag happened, accidential lasso
			return;
		}

		long columns = startTime.until(endTime, ChronoUnit.DAYS);

		graphics.getSelectedActivities().clear();

		if (columns == 0) {
			AgendaEntryBase entry = new AgendaEntryBase(Type.SPORT);
			entry.setName(Integer.toString(activityCounter++));
			entry.setStartTime(Instant.from(startTime));
			entry.setEndTime(Instant.from(endTime));

			agendaRow.addActivity(layer, entry);

			graphics.getSelectedActivities().add(
					new ActivityRef<AgendaEntry>(agendaRow, layer, entry));
		} else {
			if (graphics.isAutoGridEnabled() && graphics.isLassoSnapsToGrid()) {
				columns--;
			}
			for (int col = 0; col <= columns; col++) {

				endTime = startTime.with(info.getLocalEndTime());

				AgendaEntryBase entry = new AgendaEntryBase(Type.SPORT);
				entry.setName(Integer.toString(activityCounter++));
				entry.setStartTime(grid(Instant.from(startTime), false,
						agendaRow.getZoneId()));
				entry.setEndTime(grid(Instant.from(endTime), true,
						agendaRow.getZoneId()));

				agendaRow.addActivity(layer, entry);

				graphics.getSelectedActivities().add(
						new ActivityRef<AgendaEntry>(agendaRow, layer, entry));

				startTime = startTime.plusDays(1);
			}
		}

	}

	/*
	 * Convenience method for calculating grid locations.
	 */
	private Instant grid(Instant instant, boolean roundUp, ZoneId zoneId) {
		VirtualGrid<?> grid = graphics.getVirtualGrid();
		if (grid != null) {
			Dateline dateline = graphics.getTimeline().getDateline();
			DayOfWeek firstDayOfWeek = dateline.getFirstDayOfWeek();
			return grid.adjustTime(instant, zoneId, roundUp, firstDayOfWeek);
		}

		return instant;
	}

	// Stores copied agenda entries.
	private List<ActivityRef<?>> copiedActivities;

	private void handleKeyEvent(KeyEvent evt) {
		if (evt.isShortcutDown()) {
			switch (evt.getCode()) {
			case C:
				copiedActivities = new ArrayList<>(
						graphics.getSelectedActivities());
				break;
			case V:
				if (copiedActivities != null && !copiedActivities.isEmpty()) {
					graphics.getSelectedActivities().clear();
					pasteCopiedEntries(lastMouseEvent);
					copiedActivities.clear();
					graphics.redraw();
				}
				break;
			case U:
				undo();
				break;
			case R:
				redo();
				break;
			default:
				break;
			}
		} else {
			switch (evt.getCode()) {
			case ESCAPE:
				copiedActivities.clear();
				graphics.redraw();
				break;
			default:
				break;
			}
		}
	}

	private void pasteCopiedEntries(MouseEvent evt) {
		Row row = graphics.getRowAt(evt.getY());
		if (row == null) {
			return;
		}

		Instant time = graphics.getTimeAt(evt.getX());
		LocalTime localTime = graphics.getLocalTimeAt(evt.getY());
		ZonedDateTime zonedDateTime = ZonedDateTime
				.ofInstant(time, ZoneId.systemDefault())
				.truncatedTo(ChronoUnit.DAYS).with(localTime);

		List<ActivityRef<?>> newSelection = new ArrayList<>();
		int count = copiedActivities.size();
		Map<Object, Object> groupIdMap = new HashMap<Object, Object>();
		for (int i = 0; i < count; i++) {
			ActivityRef<?> currentRef = copiedActivities.get(i);
			AgendaEntryBase currentEntry = (AgendaEntryBase) currentRef
					.getActivity();

			Object groupId = null;
			if (currentEntry.getGroupId() != null) {
				groupId = groupIdMap.computeIfAbsent(currentEntry.getGroupId(),
						it -> UUID.randomUUID());
			}

			if (i > 0) {
				ActivityRef<?> prevRef = copiedActivities.get(i - 1);
				AgendaEntryBase prevEntry = (AgendaEntryBase) prevRef
						.getActivity();
				zonedDateTime = zonedDateTime.plus(Duration.between(
						prevEntry.getStartTime(), currentEntry.getStartTime()));
			}

			AgendaEntryBase copy = new AgendaEntryBase(currentEntry.getType());
			copy.setName(Integer.toString(activityCounter++));
			copy.setStartTime(grid(zonedDateTime.toInstant(), false,
					row.getZoneId()));
			copy.setDuration(currentEntry.getDuration());
			copy.setGroupId(groupId);
			row.addActivity(layer, copy);

			newSelection.add(new ActivityRef<AgendaEntry>(row, layer, copy));
		}

		graphics.getSelectedActivities().setAll(newSelection);
		conflictResolver.fixScheduleAfterDrag(true);
		conflictResolver.reset(false);
	}

	/*
	 * A toggle flag used by the selection gridsListener to avoid recursive
	 * selections going on and on.
	 */
	private boolean autoSelection;

	/*
	 * Ensures that group members automatically become selected if one of them
	 * was selected by the user.
	 */
	private void performGroupSelection() {
		List<ActivityRef<?>> additionalSelection = new ArrayList<>();
		if (!graphics.getSelectedActivities().isEmpty()) {
			for (ActivityRef<?> ref : graphics.getSelectedActivities()) {
				AgendaEntryBase entry = (AgendaEntryBase) ref.getActivity();
				Object id = entry.getGroupId();
				if (id != null) {
					Row agendaRow = ref.getRow();

					ActivityRepository<AgendaEntry> repository = agendaRow
							.getRepository();
					Instant st = repository.getEarliestTimeUsed();
					Instant et = repository.getLatestTimeUsed();
					Iterator<AgendaEntry> activities = repository
							.getActivities(layer, st, et, ChronoUnit.DAYS,
									agendaRow.getZoneId());
					while (activities.hasNext()) {
						AgendaEntry nextEntry = activities.next();
						if (id.equals(nextEntry.getGroupId())) {
							ActivityRef<AgendaEntry> e = new ActivityRef<>(
									agendaRow, layer, nextEntry);
							if (!graphics.getSelectedActivities().contains(e)) {
								additionalSelection.add(e);
							}
						}
					}
				}
			}
		}
		graphics.getSelectedActivities().addAll(additionalSelection);
	}

	public List<ActivityRef<?>> getCopiedActivities() {
		return copiedActivities;
	}

	/**
	 * Data structure used to store the current state of the editor.
	 */
	class Snapshot {
		private final Row<?, ?, ?> row;
		private final Map<AgendaEntry, SavedState> stateMap;

		public Snapshot(Row<?, ?, ?> row) {
			this.row = row;
			this.stateMap = new HashMap<>();
		}

		public Row<?, ?, ?> getRow() {
			return row;
		}

		public Map<AgendaEntry, SavedState> getStateMap() {
			return stateMap;
		}
	}

	/**
	 * Data stucture used to store the state of a single agenda entry.
	 */
	class SavedState {
		private final Instant start;
		private final Instant end;

		public SavedState(AgendaEntry entry) {
			this.start = entry.getStartTime();
			this.end = entry.getEndTime();
		}

		public Instant getStart() {
			return start;
		}

		public Instant getEnd() {
			return end;
		}
	}
}
