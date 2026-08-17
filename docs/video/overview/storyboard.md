# Storyboard — FlexGanttFX Product Overview

16 shots, 77 seconds. `AI-GEN` shots are fully generated; `CAPTURE` shots must be recorded
from a running FlexGanttFX demo (see `assets.md`). `TITLE` shots are motion graphics built
in the editor.

| # | Scene | In–Out | Dur | Type | Visual | Motion | On-screen text | Audio |
|---|-------|--------|-----|------|--------|--------|----------------|-------|
| 1 | Hook | 0:00–0:04 | 4.0 s | AI-GEN | Dark navy void, dense drifting streams of glowing horizontal bars, overlapping and unaligned, shallow depth of field | Slow push in, bars drift right to left | — | Music enters, low |
| 2 | Hook | 0:04–0:08 | 4.0 s | AI-GEN | Same field, bars densify and begin to tremble toward order | Slow rise, slight parallax | — | VO line 1 ends; tension builds |
| 3 | Reveal | 0:08–0:12 | 4.0 s | AI-GEN | Bars snap into clean parallel rows, light sweeps left to right across them | Snap, then settle; camera locks off | — | Soft snap transient |
| 4 | Reveal | 0:12–0:15 | 3.0 s | TITLE | FlexGanttFX wordmark forms from the aligned bars on navy | Letters assemble, subtle glow | `FlexGanttFX` | Music opens up |
| 5 | Reveal | 0:15–0:18 | 3.0 s | CAPTURE | Cross-dissolve from title into a real FlexGanttFX chart, full frame | Very slow 1.03× push in | `Professional Gantt Charts for JavaFX` | VO line 2 |
| 6 | Performance | 0:18–0:24 | 6.0 s | CAPTURE | Large chart with many rows, smooth vertical scroll then horizontal pan | Real UI interaction, no camera move | `Canvas-based rendering` (lower third) | VO |
| 7 | Performance | 0:24–0:30 | 6.0 s | CAPTURE + TITLE | Same chart, continuous zoom out revealing scale; animated activity counter overlay climbing | Zoom driven by the app, counter animated in edit | `Tens of thousands of activities · 60 fps` | VO |
| 8 | Customization | 0:30–0:37 | 7.0 s | CAPTURE | Renderer swap: plain bars → progress bars → chart renderer; then layers toggling visibility | Quick, deliberate UI actions; hold 1 s on each state | `Pluggable renderers · Layers · CSS` | VO |
| 9 | Customization | 0:37–0:45 | 8.0 s | CAPTURE | Continuous timeline zoom from minutes → hours → days → months → years, datelines relabelling | Smooth zoom, single unbroken take | `Milliseconds to decades` | VO |
| 10 | Containers | 0:45–0:51 | 6.0 s | CAPTURE | Dual container, then quad container; scroll one pane and watch the others follow | Real UI, hold on the synchronized scroll | `Dual · Quad · Multi containers` | VO |
| 11 | Containers | 0:51–0:57 | 6.0 s | CAPTURE | Close view of dependency arrows drawing between activities across rows | Slow 1.05× push toward a link cluster | `Activity links` | VO |
| 12 | Deploy | 0:57–1:01 | 4.0 s | AI-GEN + CAPTURE | Desktop monitor on a dark studio surface, chart screenshot composited onto the screen | Slow orbit around the monitor | `Desktop` | Soft riser |
| 13 | Deploy | 1:01–1:04 | 3.0 s | AI-GEN + CAPTURE | Browser window floating in dark space, same chart composited in | Push in, light trail arrives from shot 12 | `Browser · JPro` | VO |
| 14 | Deploy | 1:04–1:07 | 3.0 s | AI-GEN + CAPTURE | Phone held in frame, same chart composited on the display | Slow tilt up | `Mobile · GraalVM + Gluon` | VO |
| 15 | Trust | 1:07–1:11 | 4.0 s | TITLE | Stats resolve as large typography on navy, one line at a time | Staggered fade-up, 0.3 s apart | `110+ enterprise customers` / `11 major versions · 10+ years in production` / `Java 8–26` | VO |
| 16 | CTA | 1:11–1:17 | 6.0 s | TITLE | Logo centre-frame with URL beneath, faint aligned bars in the background | Gentle settle, hold 2 s static at the end | `FlexGanttFX` / `flexganttfx.com` | Music resolves |

## Editing notes

- **Cut on motion, not on beats.** The capture shots have their own rhythm; let the UI action
  finish before cutting.
- **Hold the last frame** of shot 16 for a full 2 seconds. Website hero loops need a clean
  resting frame, and it doubles as the poster image.
- **No text on top of chart detail.** Lower thirds sit in the bottom 15 % of frame, over the
  darkest available area. If a capture is too busy, add a 40 % navy scrim behind the text.
- **Text safe area:** keep all typography inside the centre 90 % — YouTube overlays and
  hero-section cropping both eat the edges.
- **Shots 12–14 are hybrids.** Generate the device and environment with AI, then composite a
  real screenshot onto the screen with corner-pin tracking. Never let the model invent the UI.
- **Consistency across AI shots** comes from reusing the identical style preamble in
  `prompts.md` and grading everything to the same navy/blue palette in the edit.

## Alternative structure — presenter-led cut

If a HeyGen/Synthesia avatar is preferred, restructure as:

| Segment | Duration | Content |
|---------|----------|---------|
| A | 0:00–0:10 | Presenter, medium shot, delivers scenes 1–2 narration |
| B | 0:10–0:52 | Presenter shrinks to a corner inset; capture shots 6–11 fill the frame |
| C | 0:52–1:05 | Presenter returns full frame for the deploy-anywhere message |
| D | 1:05–1:17 | Stats and logo card (shots 15–16), presenter voice only |

Same script, same captures. Only shots 1–4 and 12–14 are dropped.
