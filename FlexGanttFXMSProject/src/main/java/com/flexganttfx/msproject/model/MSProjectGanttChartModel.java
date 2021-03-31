/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.msproject.model;

import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Task;

import com.flexganttfx.model.ActivityLink;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;

public class MSProjectGanttChartModel {

	public Map<Task, ActivityRef<MSProjectTaskActivity>> taskMap;

	private ProjectFile projectFile;

	private List<Layer> layers = new ArrayList<>();

	private List<ActivityLink<?>> links = new ArrayList<>();

	private MSProjectTaskRow root = new MSProjectTaskRow();

	public MSProjectGanttChartModel(ProjectFile projectFile) {
		requireNonNull(projectFile);

		this.projectFile = projectFile;
		this.taskMap = new HashMap<Task, ActivityRef<MSProjectTaskActivity>>();

		Layer layer = new Layer("Default");
		layers.add(layer);

		List<MSProjectTaskRow> children = new ArrayList<>();
		for (net.sf.mpxj.Task task : projectFile.getChildTasks()) {
			children.add(new MSProjectTaskRow(layer, task, taskMap));
		}

		root.getChildren().setAll(children);

		for (Task task : projectFile.getChildTasks()) {
			createLinks(task);
		}
	}

	public final MSProjectTaskRow getRoot() {
		return root;
	}

	public final List<ActivityLink<?>> getLinks() {
		return links;
	}

	public final List<Layer> getLayers() {
		return layers;
	}

	private void createLinks(Task task) {
		List<Relation> successors = task.getSuccessors();
		if (successors != null) {
			for (Relation relation : successors) {
				createRelation(task, relation, true);
			}
		}

		List<Relation> predecessors = task.getPredecessors();
		if (predecessors != null) {
			for (Relation relation : predecessors) {
				createRelation(task, relation, false);
			}
		}

		for (Task childTask : task.getChildTasks()) {
			createLinks(childTask);
		}
	}

	private void createRelation(Task task, Relation relation, boolean successor) {
		Task sourceTask = relation.getSourceTask();
		Task targetTask = relation.getTargetTask();

		ActivityRef<MSProjectTaskActivity> sourceRef = taskMap.get(sourceTask);
		ActivityRef<MSProjectTaskActivity> targetRef = taskMap.get(targetTask);

		if (successor) {
			links.add(new ActivityLink<>(sourceRef, targetRef));
		} else {
			links.add(new ActivityLink<>(targetRef, sourceRef));
		}
	}

	public final ProjectFile getProjectFile() {
		return projectFile;
	}

	public final Instant getHorizonStart() {
		return projectFile.getStartDate().toInstant();
	}

	public final Instant getHorizonEnd() {
		return projectFile.getFinishDate().toInstant();
	}
}
