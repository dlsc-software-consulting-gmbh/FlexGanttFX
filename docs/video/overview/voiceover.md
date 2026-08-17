# Voiceover — FlexGanttFX Product Overview

## Voice direction

- **Persona:** senior engineer explaining a tool they respect. Assured, never salesy.
- **Gender/accent:** neutral; a warm mid-range voice with a neutral English or light
  European accent suits the DLSC brand. Avoid US "announcer" energy.
- **Pace:** ~150 words per minute. The script is deliberately short so the visuals can
  breathe — do not speed up to fill silence.
- **Emphasis:** land on the concrete numbers (*sixty frames per second*, *milliseconds to
  decades*, *a hundred and ten*). Everything else stays level.
- **Silence:** leave a full beat of silence at 0:00–0:02 and after "flexganttfx dot com".

## Voiceover text

Deliver as one continuous read; the pauses below are where the picture cuts.

```
Every serious scheduling application hits the same wall: thousands of activities,
hundreds of resources, and a timeline that has to stay readable.

[pause 0.6s]

FlexGanttFX is the leading Gantt chart framework for JavaFX — a domain-independent
foundation for anything from project planning to industrial scheduling.

[pause 0.4s]

Rendering runs on canvas with a buffer architecture, so tens of thousands of activities
scroll and zoom at sixty frames per second. No virtualization compromises, no stutter.

[pause 0.4s]

Every visual detail is yours. Pluggable renderers, a layer system, and CSS styling let you
match your domain and your brand. The timeline spans milliseconds to decades, with
multi-row datelines and configurable zoom.

[pause 0.4s]

Dual, quad, and multi-chart containers keep complex resource views synchronized. Activity
links model dependencies between tasks and render them as curved or straight arrows.

[pause 0.5s]

And it deploys everywhere. The same codebase runs on the desktop with JavaFX, in the
browser via JPro, and natively on iOS and Android with GraalVM and Gluon.

[pause 0.5s]

Over a decade in production, eleven major versions, and more than a hundred and ten
enterprise customers — including Emirates, NASA, and Boeing.

[pause 0.4s]

FlexGanttFX. Start building at flexganttfx dot com.
```

**Word count:** ~185 words → ~74 seconds of speech at 150 wpm; with the scripted pauses the
finished track lands at ~77 seconds, matching `captions.srt`.

## Pronunciation guide

| Term | Say it as | Notes |
|------|-----------|-------|
| FlexGanttFX | "flex-GANTT-eff-ex" | Stress on GANTT. Never "flex-gant-fix". |
| Gantt | "gant" (rhymes with *chant*) | Named after Henry Gantt. Hard G. |
| JavaFX | "JAH-va-eff-ex" | Spell out FX. |
| JPro | "jay-pro" | Two syllables. |
| GraalVM | "GRAWL-vee-em" | Like *crawl* with a G. |
| Gluon | "GLOO-on" | Two syllables. |
| DLSC | "dee-ell-ess-see" | Spell out, if mentioned. |
| flexganttfx.com | "flexganttfx dot com" | Write it as "dot com" in the TTS input. |
| 60 fps | "sixty frames per second" | Never "eff-pee-ess". |
| 110+ | "a hundred and ten" | Drop the plus in speech. |
| Java 8–26 | "Java eight through twenty-six" | Only if spoken; it is on-screen text in scene 7. |

Feed the TTS engine the *spoken* forms above rather than the numerals — most engines
mispronounce "60 fps" and "110+".

## TTS settings

### ElevenLabs (recommended)
| Setting | Value |
|---------|-------|
| Model | Eleven Multilingual v2 or Turbo v2.5 |
| Stability | 0.45 |
| Similarity boost | 0.75 |
| Style exaggeration | 0.15 |
| Speaker boost | on |
| Format | 48 kHz, 192 kbps MP3 or WAV |

### OpenAI TTS
| Setting | Value |
|---------|-------|
| Model | `gpt-4o-mini-tts` or `tts-1-hd` |
| Voice | a calm, mid-range voice (e.g. *onyx* or *sage*) |
| Instructions | "Calm, confident, technical. Measured pace, ~150 wpm. Emphasize numbers. No hype." |
| Format | WAV |

## Production notes

- **Render each paragraph separately.** Individual takes are far easier to re-time against
  picture than one long file, and one bad word doesn't cost you the whole read.
- **Re-time the captions afterwards.** `captions.srt` is built from the target timings; once
  the real VO exists, conform the SRT to it. This is the single most-skipped step.
- **Loudness:** narration at −16 LUFS integrated, true peak ≤ −1 dBTP. Music bed −18 LUFS,
  ducked ~6 dB under the voice.
- **Cleanup:** light de-esser, gentle 2:1 compression, high-pass at 80 Hz. Do not over-process
  — synthetic voices get brittle fast.
- **Localisation:** the site ships EN/DE/FR/ZH. If localised cuts are needed, translate this
  file first, then regenerate both the VO and `captions.srt` from the translation — never
  dub over English timings, as German in particular runs ~20 % longer.
