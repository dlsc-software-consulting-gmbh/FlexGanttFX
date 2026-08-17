# USGS Earthquakes

This demo visualizes **live seismic event data** from the [USGS Earthquake Hazards Program](https://earthquake.usgs.gov). Earthquake events are fetched in real time via the USGS GeoJSON feed and organized into magnitude bands (Minor, Light, Moderate, Strong, Major, Great). Each event appears as a bar anchored to its UTC origin time with a duration proportional to its magnitude.

Use the toolbar to set a date range and minimum magnitude filter. The view loads asynchronously and shows a progress overlay while the request is in flight. This demo illustrates how FlexGanttFX can turn point-in-time events into visual intervals, apply severity-based color coding, and handle variable-density datasets that span months or years.
