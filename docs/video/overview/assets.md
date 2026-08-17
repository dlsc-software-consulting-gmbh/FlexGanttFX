# Assets & B-Roll Guide

Brand tokens, existing artwork, and instructions for capturing the real product footage the
video depends on.

## Brand palette

Taken from `:root` in `docs/assets/css/style.css` — use these exact values so the video
matches the website.

| Token | Hex | Use in video |
|-------|-----|--------------|
| `--navy` | `#0F172A` | Primary background for all title cards and AI shots |
| `--navy-light` | `#1E293B` | Secondary background, scrims behind lower thirds |
| `--navy-mid` | `#334155` | Dividers, subtle panel fills |
| `--blue` | `#3B82F6` | Primary accent, key light, active bars |
| `--blue-dark` | `#2563EB` | Shadowed accent, gradient end |
| `--blue-light` | `#60A5FA` | Highlighted numerals in stat cards |
| `--cyan` | `#06B6D4` | Secondary accent, light sweeps and trails |
| `--white` | `#FFFFFF` | Headlines |
| `--gray-400` | `#94A3B8` | Sub-labels and captions |

Grade every AI-generated shot toward this palette. Generative models drift toward teal and
purple; a shared grade is what makes the shots feel like one piece.

## Typography

- **Primary:** Inter (imported by the site from Google Fonts, see `docs/assets/css/style.css`)
- Headlines: Inter ExtraBold (800), tracking −0.02 em
- Sub-labels: Inter SemiBold (600), uppercase, tracking +0.1 em, colour `--blue`
- Body/captions: Inter Regular (400)
- Code, if shown: Fira Code (the site's code font)

## Existing artwork in the repo

| File | Notes |
|------|-------|
| `docs/assets/images/flexganttfx-logo.svg` | Primary wordmark — use for shots 4 and 16 |
| `docs/assets/images/flexganttfx-icon.svg` | Icon only, for corner bug / watermark |
| `dlsc-logo.png` | DLSC company logo (repo root), for the end card if desired |
| `docs/assets/images/macbook-gantt.png` | Existing device mock-up still |
| `docs/assets/images/macbook-demo.gif` | Existing animated demo loop — useful reference for pacing |
| `docs/assets/images/duke-gantt.png` | Duke mascot artwork |

Prefer the SVG logos — they scale to any resolution without softening.

## Screen capture b-roll (the `CAPTURE` shots)

These are the shots that actually sell the product. AI cannot fake them.

### Running the demos

The showcase application aggregates most individual demos:

```bash
mvn install                              # build everything first
mvn -pl FlexGanttFXShowcase javafx:run   # launches com.flexganttfx.demo.showcase.ShowcaseApp
```

Standalone domain demos live under `FlexGanttFXDemos` and each has a `javafx:run` config:

```bash
mvn -pl FlexGanttFXDemos/FlexGanttFXAirport javafx:run
```

Available domain demos: `AirportApp`, `FactoryApp`, `HospitalApp`, `F1App`, `SprintApp`,
`SpaceMissionApp`, `EmiratesApp`, `WeatherApp`, `EarthquakeApp`, `NaturalEventsApp`,
`MSProjectApp`, `AgendaEditorApp`.

### Shot-to-demo mapping

| Storyboard shot | Source | Class / demo |
|-----------------|--------|--------------|
| 5 — hero chart | Domain demo | `AirportApp` or `FactoryApp` — visually rich, immediately legible |
| 6 — smooth scrolling | Showcase | `gantt/GanttChartDemo` or `AirportApp` with many rows |
| 7 — scale / 60 fps | Showcase | `gantt/CanvasBufferDemo`, or `gantt/LinksStressTestDemo` for a heavy load |
| 8 — renderers & layers | Showcase | `gantt/SystemLayersDemo`, `layout/MixedLayoutsDemo`, `layout/ChartLayoutDemo`, `gantt/AtlantaFXStylingDemo` |
| 9 — timeline zoom | Showcase | `timeline/ChronoUnitTimelineDemo` and `timeline/SimpleUnitTimelineDemo` |
| 10 — containers | Showcase | `container/DualGanttChartContainerDemo`, `container/QuadGanttChartContainerDemo`, `container/MultiGanttChartContainerDemo` |
| 11 — activity links | Showcase | `model/LinksDemo`, `gantt/LinksStressTestDemo` |
| 12–14 — device screens | Domain demo | Same chart as shot 5, recaptured at each device aspect ratio |

`AirportApp` is the strongest single source: aircraft, gates, and ground operations read
instantly to a non-domain audience, and it exercises renderers, links and hierarchy at once.

### Capture settings

- **Resolution:** capture at 2560×1440 or higher, deliver at 1920×1080. The headroom lets you
  push in on a shot without softening it.
- **Frame rate:** 60 fps capture, conform to 30 fps in the edit. This is the only honest way
  to show that the rendering is smooth.
- **Window:** run the demo maximised, hide the macOS dock and menu bar, disable notifications.
- **Cursor:** keep it visible only when it explains an interaction; hide it otherwise.
  Never show a cursor hunting for a menu.
- **Scaling:** on a HiDPI display, capture at native resolution — do not upscale a 1× capture.
- **Data:** make sure the visible data looks plausible and professional. Placeholder strings
  like "Test 1 / Test 2" undermine the whole video.
- **Duration:** record 2–3× longer than the storyboard slot and choose the calmest section.

### Interaction choreography

Rehearse each interaction before recording. Rules that make capture footage look intentional:

1. Move deliberately — slow, steady drags read far better than quick flicks.
2. One idea per shot. Do not scroll *and* zoom *and* toggle a layer in the same take.
3. Pause 1 second before and after every action, giving clean cut points.
4. Never let a dialog, tooltip or error appear unless it is the subject of the shot.

## Device compositing plates (shots 12–14)

Recapture the same chart at each target aspect ratio rather than stretching one capture:

| Shot | Target | Capture size |
|------|--------|--------------|
| 12 Desktop | 16:9 | 2560×1440 |
| 13 Browser | 16:10 | 2560×1600 (include a minimal browser chrome or add it in post) |
| 14 Mobile | 9:19.5 | 1170×2532 (portrait; use `GanttChartLite` or a narrow window) |

## Trademark constraints

- Emirates, NASA and Boeing may be mentioned **as text**, matching the existing claim on
  `docs/index.html`. Do not generate or animate their logos, liveries, or trademarks.
- Do not show the `EmiratesApp` demo UI if it contains Emirates branding — use a
  domain-neutral demo such as `AirportApp` or `FactoryApp` instead.
- The JavaFX, GraalVM, Gluon and JPro names are third-party marks; text mentions only.
