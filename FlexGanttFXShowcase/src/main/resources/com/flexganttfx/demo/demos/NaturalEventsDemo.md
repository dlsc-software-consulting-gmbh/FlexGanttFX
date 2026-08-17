# NASA Natural Events

This demo visualizes **live environmental event data** from the [NASA Earth Observatory Natural Event Tracker (EONET)](https://eonet.gsfc.nasa.gov). Events — including wildfires, severe storms, volcanic eruptions, floods, and sea/lake ice changes — are fetched from the EONET v3 API and grouped into category rows on a shared timeline.

Adjust the lookback window from the toolbar to broaden or narrow the displayed events. Each event is rendered as a bar spanning its reported start and end dates, color-coded by event category. The demo shows how FlexGanttFX handles open-ended events (those still ongoing), category-level row grouping, and asynchronous data loading from a REST API with no authentication required.
