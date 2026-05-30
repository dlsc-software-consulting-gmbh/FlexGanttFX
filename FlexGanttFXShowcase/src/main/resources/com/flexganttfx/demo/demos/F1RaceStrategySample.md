# F1 Race Strategy

This demo visualizes **live Formula 1 tire strategy data** fetched from the [OpenF1 API](https://openf1.org). Each driver occupies a row and their stint history is rendered as a series of color-coded bars — soft (red), medium (yellow), hard (white), intermediate (green), and wet (blue) — aligned on a shared race-time axis.

Select a season and race weekend from the toolbar to load real-time data. The view loads asynchronously so the UI remains responsive while fetching stints, laps, and driver information. This demo shows how FlexGanttFX handles heterogeneous bar colors within a single row, short time intervals, and live data ingestion via Java's built-in `HttpClient`.
