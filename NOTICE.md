# Third-party notices

This file summarizes third-party dependencies declared in the Maven POM files at the time of review. It is provided for attribution and compatibility tracking; always consult each dependency's own license text for authoritative terms.

FlexGanttFX itself is dual-licensed under the GNU Affero General Public License v3.0 or a commercial license from DLSC Software & Consulting GmbH.

## Published library modules

These are dependencies declared by the four published library modules: `FlexGanttFXCore`, `FlexGanttFXModel`, `FlexGanttFXView`, and `FlexGanttFXExtras`.

| Module | Dependency | Version declared in POM | License | Notes |
| --- | --- | --- | --- | --- |
| FlexGanttFXCore | None | - | - | No direct third-party runtime dependencies. |
| FlexGanttFXModel | `org.openjfx:javafx-base` | `17.0.14` | GPLv2 with Classpath Exception | JavaFX base APIs. |
| FlexGanttFXView | `org.openjfx:javafx-controls` | `17.0.14` | GPLv2 with Classpath Exception | JavaFX controls APIs. |
| FlexGanttFXView | `org.controlsfx:controlsfx` | `11.1.1` | BSD 3-Clause | Excludes OpenJFX transitive artifacts in the POM. |
| FlexGanttFXExtras | `com.dlsc.gemsfx:gemsfx` | `4.4.1` | Apache License 2.0 | Version managed by the parent POM. |
| FlexGanttFXExtras | `org.kordamp.ikonli:ikonli-core` | `12.4.0` | Apache License 2.0 | Icon library. |
| FlexGanttFXExtras | `org.kordamp.ikonli:ikonli-javafx` | `12.4.0` | Apache License 2.0 | JavaFX integration for Ikonli. |
| FlexGanttFXExtras | `org.kordamp.ikonli:ikonli-materialdesign-pack` | `12.4.0` | Apache License 2.0 | Material Design icon pack. |

No AGPLv3-incompatible direct dependency was found in the published library modules.

## Test dependencies

The parent POM declares these test-scope dependencies. They are used for building and testing, not as published runtime dependencies.

| Dependency | Version declared in POM | Scope | License |
| --- | --- | --- | --- |
| `org.junit.jupiter:junit-jupiter-api` | `5.8.1` | test | Eclipse Public License 2.0 |
| `org.junit.jupiter:junit-jupiter-params` | `5.8.1` | test | Eclipse Public License 2.0 |
| `org.junit.jupiter:junit-jupiter-engine` | `5.8.1` | test | Eclipse Public License 2.0 |
| `org.testfx:testfx-junit5` | `4.0.16-alpha` | test | Apache License 2.0 |
| `org.testfx:testfx-core` | `4.0.18` | test | Apache License 2.0 |
| `org.hamcrest:hamcrest-all` | `1.3` | test | BSD 3-Clause |
| `org.mockito:mockito-all` | `1.10.19` | test | MIT License |
| `de.sandec:JMemoryBuddy` | `0.5.5` | test | Apache License 2.0 |

## Demo, showcase, tutorial, and assembly-only dependencies

These dependencies are declared outside the four published library modules. They are used by demos, the showcase, tutorials, tools, or assembly packaging and are not part of the published FlexGanttFX library artifacts.

| Area | Dependency | Version declared in POM | License | Notes |
| --- | --- | --- | --- | --- |
| Tutorials | FlexGanttFX `view` | `12.4.0` | FlexGanttFX dual license | Internal module dependency. |
| Assembly | FlexGanttFX demo/tool modules | `12.4.0` | FlexGanttFX dual license | Packaging dependencies only. |
| Demos / Showcase | `io.github.mkpaz:atlantafx-base` | `2.1.0` | MIT License | Demo styling. |
| Showcase | `com.dlsc.atlantafx:themes` | `1.9.0` | MIT License | Showcase styling. |
| Showcase | `fr.brouillard.oss:cssfx` | `11.5.1` | MIT License | CSS hot-reload tooling. |
| Showcase | `io.github.mkpaz:devtoolsfx-connector` | `1.0.1` | MIT License | Showcase developer tooling. |
| Showcase | `io.github.mkpaz:devtoolsfx-gui` | `1.0.1` | MIT License | Showcase developer tooling. |
| Demos / Showcase | `com.google.code.gson:gson` | `2.10.1` | Apache License 2.0 | Used by data-driven demos. |
| Hospital demo | `com.calendarfx:view` | `12.0.1` | Apache License 2.0 | Demo-only CalendarFX integration. |
| Demos / Showcase | `org.openjfx:javafx-web` | `17.0.14` / `25` | GPLv2 with Classpath Exception | Demo/showcase JavaFX module. |
| Demos | `org.openjfx:javafx-swing` | `17.0.14` / `25` | GPLv2 with Classpath Exception | Demo JavaFX module. |
| Demos | `org.openjfx:javafx-fxml` | `17.0.14` / `25` | GPLv2 with Classpath Exception | Demo JavaFX module. |
| MS Project demo | `net.sf.mpxj:mpxj` | `7.9.3` | LGPL | Demo-only MS Project file support. |
| MS Project demo | `backport-util-concurrent:backport-util-concurrent` | `3.1` | Public Domain / BSD-style | Demo-only transitive support library declared directly. |
| Emirates / MS Project demos | `net.raumzeitfalle.fx:scenic-view` | `11.0.2` | BSD-style License | Demo/tooling-only UI inspection. |
| Emirates / MS Project demos | `one.jpro:jpro-webapi` | `2026.2.0` | Proprietary / commercial | Demo-only JPro integration from the Sandec repository. |
| Showcase | `one.jpro.platform:jpro-mdfx` | `0.6.1` | Apache License 2.0 | Showcase markdown rendering. |
| Space Mission / Showcase | `one.jpro.platform:jpro-utils` | `0.6.1` | Apache License 2.0 | Demo/showcase utilities. |
| Showcase | `com.opencsv:opencsv` | `4.6` | Apache License 2.0 | Showcase data parsing. |
| Showcase | `commons-codec:commons-codec` | `1.17.0` | Apache License 2.0 | Showcase utility dependency. |
| Demos / Showcase / Extras | `org.kordamp.ikonli:*` | `12.4.0` | Apache License 2.0 | Also used by the published Extras module. |

The root POM also declares the Sandec/JPro Artifactory repository for resolving demo and JPro-related artifacts.
