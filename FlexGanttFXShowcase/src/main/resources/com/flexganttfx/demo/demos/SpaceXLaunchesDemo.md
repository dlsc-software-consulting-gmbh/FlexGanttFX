# SpaceX Launches

This demo visualizes the **full SpaceX launch history** using data from the [public SpaceX REST API](https://github.com/r-spacex/SpaceX-API). All launches are loaded at startup and organized into a two-level hierarchy: rocket type at the top level, launchpad at the second level. Each launch is rendered as a bar at its UTC launch date, color-coded by outcome — green for success, red for failure, and blue for upcoming missions.

This demo demonstrates how FlexGanttFX handles a multi-decade timeline, a two-level row hierarchy (rocket → launchpad), outcome-driven color coding, and real-time data loading from a public REST API. It also shows the `showEarliestActivities()` / `showLatestActivities()` navigation API for jumping to the start or end of a long timeline.
