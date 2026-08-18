/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.msproject.model;

import com.flexganttfx.model.ActivityLink;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Task;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class MSProjectGanttChartModel {

	public Map<Task, ActivityRef<MSProjectTaskActivity>> taskMap;

	private final ProjectFile projectFile;

	private final List<Layer> layers = new ArrayList<>();

	private final List<ActivityLink<?>> links = new ArrayList<>();

	private final MSProjectTaskRow root = new MSProjectTaskRow();

	public MSProjectGanttChartModel(ProjectFile file) {
		requireNonNull(file);

		projectFile = file;
		taskMap = new HashMap<>();

		Layer layer = new Layer("Default");
		layers.add(layer);

		List<MSProjectTaskRow> children = new ArrayList<>();
		for (net.sf.mpxj.Task task : file.getChildTasks()) {
			children.add(new MSProjectTaskRow(layer, task, taskMap));
		}

		root.getChildren().setAll(children);

		for (Task task : file.getChildTasks()) {
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
				createRelation(relation, true);
			}
		}

		List<Relation> predecessors = task.getPredecessors();
		if (predecessors != null) {
			for (Relation relation : predecessors) {
				createRelation(relation, false);
			}
		}

		for (Task childTask : task.getChildTasks()) {
			createLinks(childTask);
		}
	}

	private void createRelation(Relation relation, boolean successor) {
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
