# FlexGanttFX Product Overview Video — Production Kit

Everything needed to produce a **~77 second, 16:9 product overview video** for FlexGanttFX,
intended for the website hero section and YouTube.

The video is produced primarily with AI generation tools (text-to-video, image-to-video and
AI voice), combined with real screen captures of FlexGanttFX demo applications.

## Files

| File | Purpose |
|------|---------|
| `script.md` | Scene-by-scene narration, timings, and on-screen text |
| `storyboard.md` | Shot-by-shot table: visual, camera motion, text, audio |
| `prompts.md` | Copy-paste AI generation prompts per shot (+ avatar-led alternative) |
| `voiceover.md` | Clean voiceover text, pronunciation guide, TTS settings |
| `captions.srt` | Timed subtitles matching the voiceover |
| `assets.md` | Brand palette, fonts, logos, and screen-capture b-roll instructions |
| `capture.sh` | Automated macOS recorder for the real-UI shots (clip + still) |
| `drive.py` | Posts real mouse/scroll events so recorded shots contain repeatable motion |
| `checklist.md` | QA checklist before publishing |

## Important: AI generation cannot draw a real Gantt chart

Text-to-video models will happily produce something that *looks like* a scheduling UI, but
it will not be FlexGanttFX. Any shot claiming to show the product must be a **real screen
capture** or an **image-to-video animation of a real screenshot**.

The kit therefore splits shots into two categories:

- **`AI-GEN`** — abstract, atmospheric, or typographic shots. Fully AI generated.
- **`CAPTURE`** — the product itself. Recorded from a running demo app (see `assets.md`),
  optionally animated further with image-to-video or motion graphics.

This split is marked on every shot in `storyboard.md`.

## Recommended tools

| Job | Options |
|-----|---------|
| Text-to-video (AI-GEN shots) | OpenAI Sora, Google Veo, Runway Gen-4, Luma Dream Machine |
| Image-to-video (screenshot motion) | Runway Gen-4, Luma, Kling |
| AI voiceover | ElevenLabs, OpenAI TTS, PlayHT |
| Presenter avatar (alt. track) | HeyGen, Synthesia |
| Screen capture (CAPTURE shots) | `capture.sh` + `drive.py` in this directory (ffmpeg-based), or OBS Studio / ScreenFlow |
| Assembly / edit | DaVinci Resolve (free), Final Cut Pro, Premiere Pro |
| Motion titles | After Effects, Motion, or Resolve Fusion |

## Production order

1. **Read `assets.md`** and record the CAPTURE b-roll first, using `capture.sh` and
   `drive.py`. Everything else is timed around real footage, and recording usually reveals
   what the script should emphasise.
2. **Generate the voiceover** from `voiceover.md`. The VO defines the true runtime — the
   timings in `script.md` are targets, not gospel.
3. **Re-time `captions.srt`** against the rendered VO. Do this before editing picture.
4. **Generate the AI-GEN shots** using `prompts.md`. Expect 3–6 attempts per shot; generative
   video is a slot machine. Keep the style preamble identical across all shots for coherence.
5. **Assemble** to the storyboard, layer titles, add music, mix.
6. **Run `checklist.md`** before publishing.

## Licensing and legal notes

- FlexGanttFX is dual-licensed (AGPLv3 / commercial) by DLSC Software & Consulting GmbH.
  If the video mentions licensing, mention *both* options — never imply it is free for
  proprietary use.
- Customer names (Emirates, NASA, Boeing) already appear publicly on `docs/index.html` and
  may be mentioned **as text only**. Do not generate, imitate, or animate their logos,
  aircraft liveries, or trademarks in AI footage.
- Music must be licensed for commercial use. Do not use unlicensed tracks on a marketing
  asset that will sit on a commercial product page.
- Check your AI video tool's terms permit commercial use of generated output.

## Publishing

Place the final render alongside this kit or on a CDN, then embed it in the hero section of
`docs/index.html`. Recommended exports:

- `flexganttfx-overview-1080p.mp4` — 1920×1080, H.264, ~10 Mbps (YouTube, website)
- `flexganttfx-overview-hero-muted.mp4` — same picture, no audio, ~4 Mbps (autoplay loop)
- `flexganttfx-overview-poster.jpg` — 1920×1080 still for the `poster` attribute
