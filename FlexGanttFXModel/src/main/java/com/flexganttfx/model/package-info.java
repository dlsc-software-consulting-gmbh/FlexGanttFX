/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
/**
 * Top-level model classes required for creating a Gantt chart.
 * <ul>
 * <li><b>Activity:</b> an interface that needs to be implemented by objects that want to be displayed graphically.</li>
 * <li><b>Activity Link:</b> models a dependency between two activities (e.g. successor, predecessor relationships).</li>
 * <li><b>Activity Ref:</b> an exact reference to an activity including the layer and the row where the activity is shown.</li>
 * <li><b>Activity Repository:</b> the object used by rows to store activities.</li>
 * <li><b>Calendar:</b> a specialization of activity repository for calendar information that will be rendered in the background of a row.</li>
 * <li><b>Layer:</b> used for placing activities on different levels (z-ordering).</li>
 * <li><b>Layout:</b> controls the way activities are laid out inside their row or inner line.</li>
 * <li><b>Lines Manager:</b> used to manage the location, height, and individual line layouts.</li> 
 * <li><b>Row:</b> represents a row within the Gantt chart and stores activities inside a repository.</li>
 * </ul>
 * More information for each model type can be found inside the individual class documentation.
 *
 * @since 1.0
 */
package com.flexganttfx.model;

