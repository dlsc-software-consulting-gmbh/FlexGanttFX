# AI Generation Prompts — FlexGanttFX Product Overview

Prompts for the `AI-GEN` and hybrid shots in `storyboard.md`. Shots marked `CAPTURE` are not
listed here — those are recorded from the real application (see `assets.md`).

## Style preamble

Prepend this to **every** text-to-video prompt. Consistency across shots comes almost
entirely from repeating it verbatim.

```
Cinematic 3D motion graphics, premium enterprise software commercial. Deep navy background
(#0F172A), accent colors electric blue (#3B82F6) and cyan (#06B6D4), cool white highlights.
Clean minimal composition, generous negative space, soft volumetric lighting, shallow depth
of field, subtle film grain. Calm confident pacing, slow deliberate camera movement.
No people, no text, no logos, no user interface elements. 16:9, 1920x1080, 30fps.
```

## Negative prompt

Use on every generation:

```
text, letters, words, numbers, watermark, logo, user interface, dashboard, charts with
labels, spreadsheet, gantt chart, screenshot, warm colors, orange, purple, neon, cluttered,
busy background, fast cuts, shaky camera, lens flare, cartoon, low resolution, distorted
geometry, flickering, morphing artifacts
```

> **Why "no gantt chart" in the negative prompt:** the model cannot render a real
> FlexGanttFX chart, and a fake one is worse than none. Abstract bars are fine; anything
> resembling an actual product UI must be a real screen capture.

---

## Shot 1 — Chaos (4.0 s)

```
[STYLE PREAMBLE]

Hundreds of thin glowing horizontal bars floating at different depths in a dark navy void,
drifting slowly from right to left. The bars are misaligned and overlapping, varying lengths
and brightness, some in blue, some in cyan, most dim. A sense of dense unresolved
information. Camera pushes forward very slowly through the layers. Shallow depth of field,
foreground bars soft and out of focus.
```

## Shot 2 — Tension (4.0 s)

```
[STYLE PREAMBLE]

The same field of thin glowing horizontal bars in a dark navy void, now denser and more
numerous. The bars begin to tremble and drift toward horizontal alignment but have not yet
settled. Faint blue light builds from the center of the frame. Camera rises slowly with
gentle parallax between the layers.
```

## Shot 3 — Alignment (4.0 s)

```
[STYLE PREAMBLE]

Hundreds of glowing horizontal bars snap into perfect parallel rows across a dark navy void,
forming a clean ordered grid of light. A bright cyan light sweep travels left to right
across the rows, illuminating each in sequence. Camera settles and locks off. The moment
chaos becomes order. Crisp, calm, resolved.
```

## Shot 4 — Wordmark (3.0 s)

Build this as a **motion title in the editor**, not with AI. Generative models cannot render
"FlexGanttFX" reliably, and a misspelled brand name in a marketing asset is fatal.

- Type: Inter ExtraBold, tracking −0.02 em, white on `#0F172A`
- Animation: letters assemble from the horizontal bars of shot 3, 0.8 s ease-out, then a
  soft blue glow blooms and settles
- Background: hold the aligned-bars plate from shot 3 at 15 % opacity

If you insist on generating a background plate for it:

```
[STYLE PREAMBLE]

A calm field of perfectly aligned thin glowing horizontal bars in a dark navy void, softly
out of focus, with a gentle blue glow blooming from the center of the frame. Almost still,
only the faintest drift. Designed as an empty background plate with clear negative space in
the middle third.
```

---

## Shots 12–14 — Deploy anywhere (hybrid)

**Generate the environment and device only. The screen must be black or a flat solid
color** so a real screenshot can be corner-pinned onto it in post. Never let the model
invent the interface.

### Shot 12 — Desktop (4.0 s)

```
[STYLE PREAMBLE]

A slim modern widescreen desktop monitor on a dark matte studio surface, screen switched off
and completely black, no reflections on the display. Rim lighting in blue and cyan from
behind. Camera orbits slowly to the right around the monitor. Product photography aesthetic,
dark and premium.
```

### Shot 13 — Browser (3.0 s)

```
[STYLE PREAMBLE]

An abstract floating rectangular glass panel with a thin blue edge glow, suspended in dark
navy space, its face completely black and empty. Faint browser-like chrome suggested only by
a subtle bar along the top edge, no buttons, no text. A thin trail of cyan light arrives
from the left and wraps around the panel. Camera pushes in slowly.
```

### Shot 14 — Mobile (3.0 s)

```
[STYLE PREAMBLE]

A modern smartphone standing upright on a dark matte surface, screen switched off and
completely black. Cool blue rim light along one edge, cyan accent light from behind.
Camera tilts up slowly from the base of the phone. Dark premium product photography.
```

**Compositing recipe for 12–14:**
1. Track the four screen corners (Mocha, After Effects corner pin, or Resolve planar tracker).
2. Corner-pin a real FlexGanttFX screenshot from `assets.md` onto the black screen.
3. Add a screen glow matched to the composited image and a 3–5 % screen reflection.
4. Do **not** stretch the screenshot to a different aspect ratio — recapture at the device's
   ratio instead. A squashed chart reads as fake immediately.

---

## Shots 15–16 — Stats and CTA

Build as motion titles in the editor. Typography accuracy matters more than generated
motion, and the numbers must be exact.

Optional AI background plate:

```
[STYLE PREAMBLE]

A very dark navy field with a subtle grid of faint horizontal blue lines, slowly drifting.
Extremely minimal, almost still, heavily out of focus. Pure background plate with large
empty space in the center for typography.
```

---

## Alternative track — presenter avatar

For a HeyGen or Synthesia cut (see the alternative structure in `storyboard.md`).

**Avatar brief:**
```
Professional software-industry presenter, 30s-40s, business casual (dark shirt, no tie),
neutral confident delivery, minimal hand gestures. Medium shot, centered, framed with
headroom. Background: dark navy studio with a soft blue gradient and shallow depth of field.
Consistent lighting across all segments.
```

**Segment A script (0:00–0:10):**
> Every serious scheduling application hits the same wall: thousands of activities, hundreds
> of resources, and a timeline that has to stay readable. FlexGanttFX is the leading Gantt
> chart framework for JavaFX.

**Segment C script (0:52–1:05):**
> Dual, quad, and multi-chart containers keep complex resource views synchronized, and
> activity links model the dependencies between them. And it deploys everywhere — desktop
> with JavaFX, the browser via JPro, and iOS and Android with GraalVM and Gluon.

Voice settings for the avatar match `voiceover.md`.

---

## Practical generation notes

- **Budget 3–6 attempts per shot.** Generative video is non-deterministic; generate a batch,
  pick the best, discard the rest.
- **Fix the seed** once a shot works, then vary only the camera instruction to produce
  matching coverage.
- **Generate long, cut short.** Ask for 5–8 seconds even when you need 3 — the usable window
  is rarely at the start.
- **Grade everything afterwards** to the exact palette in `assets.md`. Models drift toward
  teal and purple; a shared grade in the edit is what makes 16 shots feel like one film.
- **Reject any take containing text.** Models hallucinate garbled lettering constantly, and
  it will read as sloppy on a product page.
