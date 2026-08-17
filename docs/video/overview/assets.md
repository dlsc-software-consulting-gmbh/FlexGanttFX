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

They are **automated** — `capture.sh` and `drive.py` in this directory position the demo
window, post real input events, and record a cropped 60 fps clip plus a matching still. No
hand-held mouse work is required, and every shot is reproducible.

### One-time setup

```bash
brew install ffmpeg

# drive.py needs pyobjc for real CoreGraphics events
cd docs/video/overview
python3 -m venv .venv
.venv/bin/pip install pyobjc-framework-Quartz
```

Grant the terminal **Screen Recording** and **Accessibility** permission in
System Settings ▸ Privacy & Security. Without them the script cannot record or move windows.

### Recording a shot

```bash
# 1. start a demo
mvn -pl FlexGanttFXDemos/FlexGanttFXAirport javafx:run &

# 2. list window titles if unsure
docs/video/overview/capture.sh --list

# 3. record a static hero shot
docs/video/overview/capture.sh shot05-hero-airport 4 "Frankfurt Airport"

# 4. record a shot with real UI motion (horizontal timeline pan)
DRIVE_PYTHON=docs/video/overview/.venv/bin/python \
DRIVE="hscroll 800 400 -60 220 0.04" \
docs/video/overview/capture.sh shot06-timeline-pan 12 "Frankfurt Airport"
```

Output lands in `docs/video/overview/captures/` (git-ignored) as a 2560×1440 60 fps H.264
clip and a 2560×1440 PNG still.

`drive.py` supports `click`, `scroll`, `hscroll`, `zoom` (ctrl+scroll) and `drag`, all with
step and delay parameters so the motion is slow and even on camera. Run it with no
arguments for the full reference.

### Gotchas learned the hard way

- **Always pass the window-title substring.** Several JavaFX windows are usually open
  (showcase, IDE previews, other demos) and the script will otherwise grab the wrong one.
- **AppleScript clicks do not work.** `System Events ... click` is an accessibility action
  that JavaFX ignores; `drive.py` posts real CGEvents at the HID tap instead, which works.
- **Restart the demo before a take.** Exploratory clicking leaves the chart zoomed or
  scrolled somewhere unhelpful, and default state is the best-looking state.
- **Vertical scrolling needs enough rows.** In a demo with only a handful of rows nothing
  moves; pan the timeline horizontally instead — it also reads better on video.
- **`zoom` (ctrl+scroll) is not bound in every demo.** Where it does nothing, drive the
  toolbar zoom buttons instead — e.g. `drive.py click 337 187` hits zoom-in on the Airport
  demo at the default window placement. Repeat the click for a stepped zoom.

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

### Shot-to-demo mapping (verified by recording each one)

| Storyboard shot | Source | Class / demo |
|-----------------|--------|--------------|
| 5 — hero chart | Domain demo | `AirportApp` — dark theme, dual container, immediately legible |
| 6 — smooth panning | Domain demo | `AirportApp`, driven with `hscroll` |
| 7 — scale / 60 fps | Domain demo | `FactoryApp` — ~50 resources, drives well with a long `scroll` |
| 8 — renderers & status colours | Domain demo | `FactoryApp` — Scheduled / In Progress / Done / Delayed legend |
| 9 — timeline zoom | Domain demo | `FactoryApp`, stepped with `click <zoom-in> 10 0.7` (days → minutes) |
| 10 — synchronized containers | Domain demo | `HospitalApp` — dual pane plus agenda day view |
| 11 — activity links | Domain demo | `HospitalApp` or `SpaceMissionApp` — zoom in ~2–3 steps from the default view |
| 12–14 — device screens | Domain demo | Same chart as shot 5, recaptured at each device aspect ratio |

> **Use the domain demos, not the showcase demos.** `FlexGanttFXShowcase` demos such as
> `QuadGanttChartContainerDemo` and `LinksStressTestDemo` are development fixtures: they
> render empty charts or placeholder rows named `row 0 / sub Row0 : 0`, which look amateurish
> on video. `FlexGanttFXDemos` (Airport, Factory, Hospital, F1, Sprint, SpaceMission) ship
> realistic data and custom renderers.
>
> `ShowcaseApp` additionally fails to start on JavaFX 25 — `ShowcaseView` uses
> `StageStyle.EXTENDED`, a preview feature — so launch individual demos directly:
>
> ```bash
> mvn -pl <module> dependency:build-classpath -Dmdep.outputFile=/tmp/cp.txt -DincludeScope=runtime
> java -cp "<module>/target/classes:$(cat /tmp/cp.txt)" com.flexganttfx.factory.FactoryApp
> ```
>
> Standalone demo windows are titled `FlexGanttFX — <demo name>`; domain demos use their own
> titles, e.g. `FlexGanttFX – Factory Scheduling Demo`.

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
