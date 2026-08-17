# Pre-Publish Checklist

Work through this before the video goes on the website or YouTube.

## Factual claims

Every claim in `script.md`, `voiceover.md` and `captions.srt` must match the published copy
on `docs/index.html`. Re-verify if the website changes.

- [ ] "leading Gantt chart framework for JavaFX" — matches hero copy
- [ ] "domain-independent framework" — matches Core Capabilities intro
- [ ] "canvas-based rendering with a buffer architecture" — matches High Performance card
- [ ] "tens of thousands of activities at 60 fps" — matches High Performance card
- [ ] "pluggable renderers, CSS styling, layer system" — matches Fully Customizable card
- [ ] "milliseconds to decades, multi-row datelines" — matches Flexible Timeline card
- [ ] "Dual, Quad and Multi containers, synchronized scrolling and zooming" — matches card
- [ ] "activity links, curved or straight arrows" — matches Activity Links card
- [ ] "desktop via JavaFX, browser via JPro, iOS/Android via GraalVM + Gluon" — matches card
- [ ] "110+ enterprise customers" — matches stats bar
- [ ] "11 major versions" — matches stats bar
- [ ] "10+ years in production" — matches stats bar
- [ ] "Java 8–26" — matches stats bar; **confirm still current before publishing**
- [ ] "Emirates, NASA, Boeing" — **requires explicit sign-off from DLSC**; these names are
      already public on the website, but a video is a louder medium. Confirm the customer
      references are still permitted, or cut the sentence.

> **Open item for sign-off:** the customer-name mention in scene 7. If in doubt, replace with
> "including some of the largest names in aviation, aerospace and manufacturing."

## Brand and legal

- [ ] Colours match the palette in `assets.md` (`#0F172A`, `#3B82F6`, `#06B6D4`)
- [ ] "FlexGanttFX" spelled correctly everywhere on screen — capital F, G and FX
- [ ] Logo used from the SVG source, not upscaled from a raster
- [ ] No third-party logos or trademarks appear in generated footage
- [ ] No demo footage containing customer branding (e.g. `EmiratesApp`)
- [ ] Music is licensed for commercial use, licence receipt archived
- [ ] AI tool terms permit commercial use of the generated output
- [ ] Dual licensing (AGPLv3 / commercial) is not misrepresented; the video does not imply
      the library is free for proprietary use
- [ ] DLSC Software & Consulting GmbH credited on the end card if required

## Footage quality

- [ ] No AI shot contains hallucinated text, letters or numbers
- [ ] No AI shot contains a fake Gantt chart or fake UI
- [ ] All product footage is real capture, never generated
- [ ] Device screens (shots 12–14) are corner-pinned real screenshots, correct aspect ratio
- [ ] No placeholder or nonsense data visible in any capture
- [ ] No cursor wandering, no stray tooltips, dialogs or notifications
- [ ] Capture footage is genuinely smooth — this shot *is* the performance claim
- [ ] Consistent grade across all shots; no teal or purple drift

## Typography and layout

- [ ] All text inside the centre 90 % title-safe area
- [ ] Lower thirds legible over the busiest frame they cover (add a scrim if not)
- [ ] Minimum on-screen text size ≥ 28 px at 1080p
- [ ] Every text card is on screen long enough to read twice at a normal pace

## Audio

- [ ] Pronunciation correct: FlexGanttFX, JavaFX, JPro, GraalVM, Gluon (see `voiceover.md`)
- [ ] Narration at −16 LUFS integrated, true peak ≤ −1 dBTP
- [ ] Music ducked ~6 dB under narration
- [ ] No clipping, no audible TTS artefacts or breath glitches
- [ ] Video is comprehensible with sound off (most hero and social views are muted)

## Captions

- [ ] `captions.srt` re-timed against the final rendered voiceover, not the target timings
- [ ] Maximum 2 lines per cue, ≤ 42 characters per line
- [ ] Minimum cue duration 1.2 s; reading speed ≤ 20 characters per second
- [ ] No cue overlaps or negative durations
- [ ] Validated in a player (VLC, or YouTube's caption preview)

## Delivery

- [ ] `flexganttfx-overview-1080p.mp4` — 1920×1080, H.264, ~10 Mbps, AAC 192 kbps
- [ ] `flexganttfx-overview-hero-muted.mp4` — no audio track, ~4 Mbps, loops cleanly
- [ ] `flexganttfx-overview-poster.jpg` — 1920×1080 still from the final logo frame
- [ ] Final frame holds ~2 s static so the hero loop and poster both look intentional
- [ ] Verified on a phone screen — text still legible at that size
- [ ] YouTube: title, description with link to flexganttfx.com, captions uploaded, thumbnail
- [ ] Embedded in the hero section of `docs/index.html` with `poster` and `playsinline`
- [ ] Source project, generated clips and prompts archived for future re-cuts
