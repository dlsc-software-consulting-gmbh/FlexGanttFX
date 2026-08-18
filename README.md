# FlexGanttFX

[![Build](https://github.com/dlemmermann/FlexGanttFX/actions/workflows/build.yml/badge.svg)](https://github.com/dlemmermann/FlexGanttFX/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.flexganttfx/view?label=Maven%20Central)](https://central.sonatype.com/search?q=g%3Acom.flexganttfx)
[![License: AGPL v3](https://img.shields.io/badge/License-AGPLv3-blue.svg)](LICENSE)
[![JFXCentral](https://img.shields.io/badge/Find_me_on-JFXCentral-blue?logo=googlechrome&logoColor=white)](https://www.jfx-central.com/libraries/flexganttfx)

FlexGanttFX is a professional JavaFX Gantt chart and scheduling framework by [DLSC Software & Consulting GmbH](https://www.dlsc.com). It provides a model, timeline, graphics engine, renderers, layers, and optional controls for building planning, scheduling, and resource visualization applications.

Documentation, screenshots, and product information are available at [flexganttfx.com](https://www.flexganttfx.com).

## Highlights

- JavaFX Gantt chart controls with tree-table row headers and canvas-based rendering
- Time line, date line, event line, grid, calendar, now-line, and overlay layers
- Activity repositories, links, layouts, renderers, and row models
- Extra controls such as status bar, toolbar, radar, layers panel, and property views
- Dual licensing: AGPLv3 or commercial license
- No runtime license key is required. The old license-key enforcement has been removed, so anyone can build and run the library without licensing setup.

## Coordinates

FlexGanttFX publishing to Maven Central is being set up. These are the coordinates for the upcoming `12.3.0` release.

### Maven

```xml
<dependency>
    <groupId>com.flexganttfx</groupId>
    <artifactId>view</artifactId>
    <version>12.3.0</version>
</dependency>
```

Add `extras` if you need the optional helper controls:

```xml
<dependency>
    <groupId>com.flexganttfx</groupId>
    <artifactId>extras</artifactId>
    <version>12.3.0</version>
</dependency>
```

### Gradle

```groovy
implementation "com.flexganttfx:view:12.3.0"
implementation "com.flexganttfx:extras:12.3.0" // optional
```

The lower-level artifacts are `core`, `model`, `view`, and `extras`.

## Quick start

The sketch below shows the essential steps. See
[`FlexGanttFXTutorials`](FlexGanttFXTutorials) for complete, runnable examples.

```java
// 1. A row type: Aircraft rows have Aircraft children and carry Flight activities
public class Aircraft extends Row<Aircraft, Aircraft, Flight> {
    public Aircraft(String name) {
        super(name);
    }
}

// 2. An activity type
public class Flight extends MutableActivityBase<FlightData> {
    public Flight(FlightData data) {
        setUserObject(data);
        setName(data.getFlightNo());
        setStartTime(data.getDepartureTime()); // java.time.Instant
        setEndTime(data.getArrivalTime());
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
graphics.setActivityRenderer(
        Flight.class,
        GanttLayout.class,
        new ActivityBarRenderer<>(graphics, "Flight Renderer"));
graphics.showEarliestActivities();
```

For complete examples, see the tutorial and demo modules in this repository.

## Modules

| Module | Maven artifact | Purpose |
| --- | --- | --- |
| `FlexGanttFXCore` | `core` | Logging and utilities; no JavaFX UI dependencies. |
| `FlexGanttFXModel` | `model` | Domain model: rows, activities, layers, links, repositories, layouts, calendars. |
| `FlexGanttFXView` | `view` | Main controls, renderers, skins, graphics, timeline, date line, event line. |
| `FlexGanttFXExtras` | `extras` | Optional status bar, toolbar, radar, layers panel, and property views. |

Other modules contain demos, tutorials, tooling, integrations, and assembly packaging.

## Documentation and demos

- Website and documentation: <https://www.flexganttfx.com>
- API Javadoc: [`docs/api/index.html`](docs/api/index.html)
- Showcase module: [`FlexGanttFXShowcase`](FlexGanttFXShowcase)
- Demo modules: [`FlexGanttFXDemos`](FlexGanttFXDemos)
- Tutorial module: [`FlexGanttFXTutorials`](FlexGanttFXTutorials)

## Which license do I need?

FlexGanttFX is dual-licensed.

### AGPLv3

You may use FlexGanttFX under the GNU Affero General Public License v3.0. The AGPLv3 is a strong copyleft open source license with network-copyleft obligations. If you distribute an application, or offer a network/SaaS product, that includes FlexGanttFX, you must comply with the AGPLv3 source-code obligations for the combined work.

### Commercial license

If you want to use FlexGanttFX in a proprietary product, closed-source application, or commercial SaaS offering without the AGPLv3 obligations, purchase a commercial license from DLSC.

- Licensing page: <https://www.flexganttfx.com/pages/licensing.html>
- Commercial license document: [`commercial-enterprise-license.pdf`](commercial-enterprise-license.pdf)
- Contact: <dlemmermann@gmail.com>

This README is a practical summary, not legal advice. Please review the license texts and consult your own legal counsel if needed.

## Building from source

Requirements:

- **JDK 25** to build the whole repository. The demo and showcase modules target Java 25.
- The published libraries themselves are compiled with `--release 11`, so **applications only need JDK 11+**.
- JavaFX 17.0.14 for the libraries (managed by Maven), Maven 3.9+.

Build everything from the repository root:

```bash
mvn install
```

Build only the libraries (these target Java 11):

```bash
mvn install -pl FlexGanttFXCore,FlexGanttFXModel,FlexGanttFXView,FlexGanttFXExtras -am
```

Run all tests:

```bash
mvn test
```

Run one module's tests:

```bash
mvn test -pl FlexGanttFXModel
```

Run one test class:

```bash
mvn test -pl FlexGanttFXModel -Dtest=SomeTest
```

The test stack includes JUnit 5, TestFX, Mockito, Hamcrest, and JMemoryBuddy.

## Contributing and community

- [Contributing guidelines](CONTRIBUTING.md)
- [Security policy](SECURITY.md)
- [Code of conduct](CODE_OF_CONDUCT.md)
- [Third-party notices](NOTICE.md)

Before contributing Java code, run `mvn license:format` so new files receive the standard license header.
