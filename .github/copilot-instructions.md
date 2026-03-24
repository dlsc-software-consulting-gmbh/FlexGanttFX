# FlexGanttFX Copilot Instructions

FlexGanttFX is a commercial JavaFX Gantt chart framework (dual-licensed AGPLv3 + commercial) developed by DLSC Software & Consulting GmbH. Online docs: https://www.flexganttfx.com

## Build & Test

```bash
mvn install                                          # Full build (all modules)
mvn test                                             # All tests
mvn test -pl FlexGanttFXModel                        # Tests in one module
mvn test -pl FlexGanttFXModel -Dtest=IntervalTreeActivityRepositoryTest          # Single test class
mvn test -pl FlexGanttFXModel -Dtest=IntervalTreeActivityRepositoryTest#myMethod # Single method
mvn license:format                                   # Apply/fix license headers on all .java files
```

- Java 11, JavaFX 19.0.2.1
- CI uses `mvn -B install` on JDK 11 (see `.github/workflows/build.yml`)
- Test stack: JUnit 5, TestFX 4.0.16-alpha, Mockito, Hamcrest, JMemoryBuddy

## Module Dependency Order

```
FlexGanttFXCore      (licensing, logging — no FX deps)
  └── FlexGanttFXModel  (domain model — no UI)
        └── FlexGanttFXView   (controls, renderers, skins)
              └── FlexGanttFXExtras  (statusbar, toolbar, radar, layers panel)
```

Modules in this repo beyond the four above are either demos/tutorials (`FlexGanttFXSampler`, `FlexGanttFXTutorials`, `FlexGanttFXEmirates`, `FlexGanttFXCovid`), integrations (`FlexGanttFXMSProject`, `FlexGanttFXiCal`, `FlexGanttFXProject`), tooling (`FlexGanttFXEditor`, `FlexGanttFXExperimental`, `FlexGanttFXLicensing`), or distribution (`FlexGanttFXAssembly`).

## Architecture

### Core Concepts

The chart is built around four model primitives:

| Type | Role |
|------|------|
| `Row<P, C, A>` | Tree node; P = parent row type, C = child row type, A = activity type |
| `Activity` | A time-bounded item rendered on a row (has id, name, startTime, endTime as `Instant`) |
| `Layer` | Groups activities for rendering; controls z-order, visibility, opacity |
| `ActivityLink` | Expresses a dependency between two `ActivityRef` instances |

`ActivityRef` = (Row + Layer + Activity) — uniquely identifies an activity's position.

### Activity Type Hierarchy

```
Activity (interface)
  └── ActivityBase<T>               immutable, with optional user object
        └── MutableActivityBase<T>  mutable (setStartTime/setEndTime/etc.)

ChartActivity / ChartActivityBase         adds chartValue (double)
CompletableActivity / CompletableActivityBase  adds percentageComplete (double)
HighLowChartActivity / HighLowChartActivityBase  adds high/low (double)
MutableCompletableActivityBase            mutable + completable
```

All `*Base` classes hold a generic user-object (`T`) via `setUserObject`/`getUserObject`.

### View Architecture

`GanttChart<R extends Row>` is the main JavaFX control. Its structure:

- **Left:** `TreeTableView` (row hierarchy)
- **Right:** `GraphicsBase<R>` — Canvas-based rendering area
- **Top:** `Timeline` → `Dateline` (time scale) + `Eventline` (event markers)

Skins live in the `impl.com.flexganttfx.skin` package — treat as internal.

Public rendering is done by **Renderers** (`com.flexganttfx.view.graphics.renderer`):
- `ActivityRenderer<A>` — base for bar renderers (most common to extend)
- `ActivityBarRenderer<A>` — standard filled bar
- `CompletableActivityRenderer` — progress bar
- `ChartActivityRenderer`, `HighLowChartActivityRenderer`
- `LinkRenderer`, `StraightLinkRenderer`, `CurvedLinkRenderer`
- `RowRenderer` — row background

Built-in **System Layers** (canvas overlays, `com.flexganttfx.view.graphics.layer`): `CalendarLayer`, `GridLinesLayer`, `NowLineLayer`, `ChartLinesLayer`, `LayoutLayer`, `RowLayer`, `InnerLinesLayer`, `AgendaLinesLayer`, `DSTLineLayer`, etc.

`GanttChartLite<R>` is a lighter variant; multi-chart containers: `DualGanttChartContainer`, `MultiGanttChartContainer`, `QuadGanttChartContainer`.

### Activity Repository

Rows use `ActivityRepository` for storage. Default: `IntervalTreeActivityRepository` (fast range queries). Alternative: `ListActivityRepository`. Swap via `row.setRepository(...)`. Custom repositories extend `ActivityRepositoryBase` or `MutableActivityRepositoryBase`.

### Layout

Each row/line has a `Layout` that influences rendering and editing:
- `GanttLayout` — standard bars (default)
- `ChartLayout` — chart-style rendering
- `PoolLayout` — pooled/packed bars

Set per row: `row.setLayout(new GanttLayout())`.

## Key Conventions

### Package Structure

```
com.flexganttfx.core.*              Licensing (FlexGanttFX), logging (LoggingDomain)
com.flexganttfx.model.*             Row, Activity, Layer, ActivityLink, ActivityRef
  .activity.*                       Activity implementations
  .repository.*                     Repository implementations
  .calendar.*                       Calendar (weekend, working hours)
  .layout.*                         Layout implementations
  .dateline.*, .timeline.*          Time/scale models
  .util.*                           ChronoUnitUtils, IntervalTree, TimeInterval
com.flexganttfx.view.*              GanttChart, GanttChartLite, containers
  .graphics.*                       GraphicsBase, ActivityBounds, events
    .renderer.*                     Renderer classes
    .layer.*                        System layer classes
  .timeline.*                       Timeline, Dateline, Eventline, DatelineCell
  .util.*                           FlexGanttFXControl, RowHeaderColumn, Messages
impl.com.flexganttfx.skin.*         Internal skins — do not use directly
```

### Naming Patterns

- `*Base` — abstract/base implementation (e.g., `ActivityBase`, `MutableActivityBase`, `RendererBase`)
- `Mutable*` — adds setters to an otherwise immutable type
- `*Repository` — activity storage abstraction
- `*Renderer` — draws to `Canvas` via `GraphicsContext`
- `*Skin` — JavaFX skin (in `impl` package, internal)

### JavaFX Property Convention

All model/view state is exposed as JavaFX properties following the standard pattern:

```java
private final StringProperty name = new SimpleStringProperty(this, "name", "default");
public final StringProperty nameProperty() { return name; }
public final String getName()              { return nameProperty().get(); }
public final void setName(String v)        { nameProperty().set(v); }
```

### Renderer Color Properties

Renderers have five state variants — set them programmatically (not via CSS):

```java
renderer.setFill(Color.STEELBLUE);
renderer.setFillSelected(Color.valueOf("#F21B1BBB"));
renderer.setFillHover(Color.GREEN);
renderer.setFillHighlight(Color.YELLOW.deriveColor(1,1,1,.5));
renderer.setFillPressed(Color.STEELBLUE.darker());
// Same pattern for stroke*
```

### License Header

Every `.java` file must start with the standard header. Run `mvn license:format` to apply it automatically. The header template is in `license-header.txt`.

### Licensing at Runtime

`FlexGanttFX.setLicenseKey(String)` must be called **once** before the chart is shown. It can also be passed as a system property: `-Dflexganttfx.license=<key>`. In tests, call it in the `@Start` method.

### null Safety

`requireNonNull()` is used throughout. Passing null where not expected is a programming error, not a handled case.

## Typical Usage Pattern

```java
// 1. Define a Row subclass (Fleet = parent, Aircraft = child, Flight = activity)
class Aircraft extends Row<Fleet, Aircraft, Flight> {
    public Aircraft(String name) { super(name); }
}

// 2. Define an Activity subclass
class Flight extends MutableActivityBase<FlightData> {
    public Flight(FlightData data) {
        setUserObject(data);
        setName(data.flightNo);
        setStartTime(data.departureTime);
        setEndTime(data.arrivalTime);
    }
}

// 3. Wire up the chart
GanttChart<Aircraft> gantt = new GanttChart<>(new Aircraft("ROOT"));

Layer layer = new Layer("Flights");
gantt.getLayers().add(layer);

Aircraft b747 = new Aircraft("B747");
b747.addActivity(layer, new Flight(flightData));
gantt.getRoot().getChildren().add(b747);

gantt.getTimeline().showTemporalUnit(ChronoUnit.HOURS, 10);

GraphicsBase<Aircraft> graphics = gantt.getGraphics();
graphics.setActivityRenderer(Flight.class, GanttLayout.class,
    new ActivityBarRenderer<>(graphics, "Flight Renderer"));
graphics.showEarliestActivities();
```

## Logging

`LoggingDomain` provides named `java.util.logging.Logger` instances. Use these — do not create ad-hoc loggers:

```java
LoggingDomain.RENDERING.fine("drawing activity");
LoggingDomain.REPOSITORY.info("loading data");
// Others: PERFORMANCE, CONFIG, MODEL, EDITING, NAVIGATION, EVENTS, DND
```

## CSS

Each control loads its own stylesheet from resources:

| Control | Stylesheet |
|---------|-----------|
| `GanttChart` | `com/flexganttfx/view/gantt.css` |
| `GraphicsBase` | `com/flexganttfx/view/graphics/graphics.css` |
| `Timeline` | `com/flexganttfx/view/timeline/timeline.css` |
| `Dateline` | `com/flexganttfx/view/timeline/dateline.css` |
| `Eventline` | `com/flexganttfx/view/timeline/eventline.css` |

CSS uses standard JavaFX `-fx-` properties. Notable style classes: `.gantt-chart`, `.gantt-tree-table-view`, `.row-header-cell`. Renderer colors are not CSS-driven — use renderer properties instead.

## In-Repo Documentation

Detailed guides are in `docs/_docs/`:
- `2025-08-18-model.md` — Activity types, repositories, layouts
- `2024-06-06-graphics.md` — Rendering and renderers
- `2024-06-05-timeline.md` / `dateline.md` / `eventline.md` — Timeline controls
- `2025-12-17-styling.md` — CSS reference
- `2025-12-16-logging.md` — Logging configuration
- `2024-06-04-tutorial.md` — Getting started tutorial
