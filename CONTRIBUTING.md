# Contributing to FlexGanttFX

Thank you for helping improve FlexGanttFX. This repository contains the JavaFX Gantt chart framework published by DLSC Software & Consulting GmbH.

## Building

FlexGanttFX requires **JDK 25** for a full build of the repository, because the demo
and showcase modules target Java 25. The four published library modules
(`FlexGanttFXCore`, `FlexGanttFXModel`, `FlexGanttFXView`, `FlexGanttFXExtras`) are
compiled with `--release 11` and can be built on their own:

```bash
mvn install -pl FlexGanttFXCore,FlexGanttFXModel,FlexGanttFXView,FlexGanttFXExtras -am
```

Full build:

```bash
mvn install
```

The full build runs from the repository root and creates the library modules, demos, tutorials, and assembly artifacts.

## Testing

Run all tests:

```bash
mvn test
```

Run tests for one module:

```bash
mvn test -pl FlexGanttFXModel
```

Run one test class:

```bash
mvn test -pl FlexGanttFXModel -Dtest=SomeTest
```

## Module dependency order

The published library modules are layered in this order:

1. `FlexGanttFXCore` - logging and utilities, no JavaFX UI dependencies
2. `FlexGanttFXModel` - domain model, rows, activities, repositories, layouts
3. `FlexGanttFXView` - controls, renderers, skins, graphics, timeline
4. `FlexGanttFXExtras` - status bar, toolbar, radar, layers panel, property views

Please keep dependencies flowing in that direction only. Demo, showcase, tutorial, and assembly modules must not become dependencies of the library modules.

## Releasing

Releases are created by the **Release** GitHub Actions workflow
(`.github/workflows/release.yml`). It is started manually via *Actions → Release → Run
workflow* and takes the new version number as its only input. The workflow

1. sets the version in all POMs (`versions:set`) and in `README.md`, `NOTICE.md` and the
   bug report issue template, then commits and pushes the change to `master`,
2. builds the whole reactor and deploys to Maven Central with `-Drelease=true`, which
   activates the `release` profile (sources JAR, Javadoc JAR, GPG signatures). Only
   `FlexGanttFXCore`, `FlexGanttFXModel`, `FlexGanttFXView` and `FlexGanttFXExtras` are
   published; the demo, showcase, tutorial and assembly modules set `skipPublishing`,
3. creates the `v<version>` tag, the changelog and the GitHub release with JReleaser
   (`jreleaser.yml`) and attaches the assembly `-bin.zip` and `-src.zip` archives.

Publishing is automatic (`autoPublish=true`), so a release becomes visible on Maven
Central without a manual confirmation step in the Central portal. Do not push release
tags by hand - the tag is created by the workflow.

The workflow requires the repository secrets `PUBLISHER_PORTAL_USERNAME`,
`PUBLISHER_PORTAL_TOKEN`, `GPG_PRIVATE_KEY` and `GPG_PASSPHRASE`.

## License headers

Every `.java` file must carry the standard header from `license-header.txt`. Before submitting a pull request, run:

```bash
mvn license:format
```

## Code style and conventions

- Use the JavaFX property pattern for observable state:

  ```java
  private final StringProperty name = new SimpleStringProperty(this, "name", "default");
  public final StringProperty nameProperty() { return name; }
  public final String getName() { return nameProperty().get(); }
  public final void setName(String name) { nameProperty().set(name); }
  ```

- JavaFX property accessor methods (`xxxProperty()`), getters, and setters should be `final`.
- Use `Objects.requireNonNull(...)` / `requireNonNull(...)` for required parameters. Passing `null` where not documented is a programming error.
- Use `LoggingDomain` for logging. Do not create ad-hoc loggers.
- Packages below `impl.com.flexganttfx.*` are internal implementation details and are not public API.
- Keep public API changes deliberate and documented.

## Pull request expectations

- Keep pull requests small and focused.
- Explain what changed and why.
- Add or update tests for bug fixes and behavior changes.
- Include screenshots or recordings for visible UI changes when helpful.
- Run the relevant Maven test command before opening the pull request.

## Contributor license

FlexGanttFX is dual-licensed: the public edition is available under the GNU Affero General Public License v3.0 (`LICENSE`), and DLSC also sells commercial licenses. To keep both editions viable, DLSC Software & Consulting GmbH must be able to include contributed code, documentation, tests, examples, and other materials in both the AGPLv3 edition and the commercial edition.

By submitting a pull request or other contribution to this repository, you agree that:

1. You license your contribution to DLSC Software & Consulting GmbH under the GNU Affero General Public License v3.0.
2. You also license your contribution to DLSC Software & Consulting GmbH under the terms of the FlexGanttFX commercial license, including a broad, worldwide, perpetual, irrevocable, royalty-free, sublicensable right to use, reproduce, modify, distribute, and include your contribution in FlexGanttFX commercial licenses and commercial products.
3. You certify that you have the right to make this contribution and to grant these rights.
4. Your contribution is your own original work, or you have permission to submit it under these terms.

Please sign your commits using the Developer Certificate of Origin style trailer:

```text
Signed-off-by: Your Name <you@example.com>
```

You can add it with:

```bash
git commit -s
```

Larger or strategically important contributions may require a separate Contributor License Agreement before they can be accepted.

This section is plain-language project policy, not legal advice. DLSC may replace or supplement it with a formal CLA at any time.
