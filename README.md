# ensemble_v3.0.0<br>
fragmenteded string texture generator for norns<br>
<p align="center">
  <img src="ensemble-logo.svg" width="360">
</p>


# ENSEMBLE — String Texture Generator

## Comprehensive Blueprint v3.0

**Script Version:** v3.0.0 (Continuous Texture Weave Architecture)  
**Platform:** Monome Norns / Fates  
**Date:** February 10, 2026  
**Status:** Working 

---

## Table of Contents

1. [The Vision](#1-the-vision)
2. [Sonic Character & Influences](#2-sonic-character--influences)
3. [The Target Sound](#3-the-target-sound)
4. [Spectral Analysis of Reference Audio](#4-spectral-analysis-of-reference-audio)
5. [The Breakthrough: Continuous Pressure Fields](#5-the-breakthrough-continuous-pressure-fields)
6. [Audio-Reactive Coordinate Mapping](#6-audio-reactive-coordinate-mapping)
7. [Technical Architecture](#7-technical-architecture)
8. [SuperCollider Engine: Engine_Polymorphia](#8-supercollider-engine-engine_polymorphia)
9. [Softcut Texture Weave](#9-softcut-texture-weave)
10. [LFO System](#10-lfo-system)
11. [Flourish System](#11-flourish-system)
12. [Parameters & Controls](#12-parameters--controls)
13. [UI Behavior](#13-ui-behavior)
14. [Key Technical Learnings](#14-key-technical-learnings)
15. [Development History](#15-development-history)
16. [Success Criteria](#16-success-criteria)
17. [Future Directions](#17-future-directions)

---

## 1. The Vision

A living, breathing string ensemble that transforms your playing into cascading, weaving voices using extended techniques — like watching a murmuration of starlings form DNA helixes, or an audio-reactive visualizer rendered as sound.

**In one sentence:**

> "A slowly breathing, hyper-dense string swarm made of microtonal friction, bow noise, and spectral pressure — an evolving orchestral organism rather than a composition."

### The Particles → Swarm → Mass Continuum

The Blend parameter controls the particle clustering, moving along a spectrum:

- **DUST end (sparse):** Morse code dots, col legno strikes, pointillist stippling. Individual events are clearly audible, separated in time. Brittle, insect-like, skittering.
- **Middle (textured):** Interplay of articulations — short grains and long grains coexisting, weaving around each other. The "murmuration" quality.
- **MASS end (dense):** Smeared bowing, sustained clusters, fog. Individual voices fuse into a granular mass. Continuous pressure field.

### The Dual Nature

The script always maintains BOTH textures simultaneously, in different proportions:

- **MASS:** Smeared notes that rise, undulate, and bow — the "bed" or "fog." Long grains (3–6 seconds), slow envelopes, formant filtering, comb tension, staged evolution. Creates the orchestral foundation that never fully clears.
- **DUST:** Stippling clusters with percussive friction — the surface texture. Short grains (30–120ms), quick envelopes, Phantom Limb dispersion. Creates the skittering, insect-leg quality on top.

These two textures coexist and interweave, controlled by the Blend parameter.

### The Call & Response

The system decides when and where to place particles based on input dynamics. This is not a looper — it is a living organism that listens to what you play and responds with its own interpretation:

- Louder transients = denser particle clouds
- Sustained input = hovering swarm quality
- Silence = texture gradually thins but never fully disappears (persistence)
- Playing dynamics shape the character of the response over time

---

## 2. Sonic Character & Influences

### 2.1 Primary References

**Penderecki's "Polymorphia" — Middle Section:**

The primary sonic target. Characteristics to emulate:

- Microtonal clusters: multiple pitches within 50–200 cents of each other
- Sul ponticello: glassy, near-bridge brightness where bow noise mixes with pitch
- Percussive col legno: struck strings with the wood of the bow
- Tapping strings between bridge and tailpiece
- Discontinuous, punctual sounds creating tension and chaos
- Atonal, noise-like pitches contrasted with sustained cluster sections
- Slow bowing attacks vs sharp staccato
- Dissonant but beautiful

**Jonny Greenwood — "Proven Lands":**

- Percussive strings with rapid bows bouncing
- Stabbing, bulbous, cacophonous figures
- Aleatoric (random) melodic lines
- Skittish, anarchic energy

**Jan Jelinek — "Loop Finding Jazz Records" (The Micro-Loop Aesthetic):**

- Extreme micro-looping: trademark stuttering, particulate textures
- Heavy filtering and formant shifting on tiny fragments
- Loop-finding: engine detects and extracts micro-loops from input
- Vinyl crackle/noise integration as organic texture
- CRITICAL: Micro-loops maintain coherence — not formless blips
- Particulation has musical meaning
- Sparse, minimal, lowercase aesthetics with lots of space between sounds

**KHC (San) — Tape/Electroacoustic:**

- Organic fragmentation
- Granular but NOT a blur
- Each fragment audible and distinct
- Stuttering, cascading, weaving

**Discomfort Designs "Phantom Limb":**

- Random delay dispersion
- Lo-fi textures with progressive darkening
- Points of sound scattered across time and space

### 2.2 Visual & Conceptual Metaphors

- DNA helix rotating and undulating
- Murmuration of starlings — flocks forming and fragmenting
- Braiding, weaving, cascading ribbons
- Throbbing, breathing movement
- Stippled pointillism — Morse code dots, not blurs
- Audio-reactive visualizer patterns rendered as sound (MilkDrop)
- Fog thickening and thinning
- Pressure accumulating, then slightly releasing
- A self-organizing system / swarm intelligence
- Skittering insect legs clustering from all angles
- Oiled pumpkin ripping apart — hearing disintegration
- Particulate clusters that rise, dissolve, float through the stereo field

### 2.3 Key Sonic Goals

- Fragments that SWIM around binaural space
- Clusters that TWIST around other clusters
- Percussive stippling BUILDING tension and speed
- Formed granules that SWOOP down and around
- Slow bowing RISERS that build like orchestral crescendos
- Interplay of disparate articulations
- Asynchronous threads of sound interacting
- Smeared attacks rather than clear note onsets
- Ghost harmonics / phantom chords that vanish immediately
- The texture never fully clears — always a bed of activity

### 2.4 Fragment Size Philosophy

**The Jelinek Principle:** Micro doesn't mean microscopic. A 30ms fragment of a guitar note still sounds like "guitar" — it's the attack, the pick scrape, the string resonance. That's the coherence we're preserving. The magic is in the PATTERN of how these recognizable micro-events are arranged, not in reducing them to pure abstraction.

**Size ranges and their character:**

| Range | Duration | Character | Use |
|-------|----------|-----------|-----|
| Too small | <5ms | Formless clicks, no tonal content | Avoid |
| Micro | 20–50ms | Percussive attacks, col legno. Retains spectral identity | DUST grains |
| Short | 50–150ms | Staccato bow strokes. Clear pitch/timbre relationship | DUST grains |
| Medium | 150–400ms | Sustained phrases. Melodic/harmonic content | Transition zone |
| Long | 500ms–6s | Bowing gestures, risers. Smeared, sustained mass | MASS grains |

**Grain envelope philosophy:** NOT harsh expodec (causes unpleasant choppy clicks). Instead: quasi-gaussian / Tukey-style envelopes with soft rounded attack, brief sustain, gentle decay. The "stipple" quality comes from grain DENSITY and PLACEMENT PATTERNS, not from harsh envelope shapes.

---

## 3. The Target Sound

### 3.1 Detailed Analysis

The following analysis was derived from AI-generated reference audio (Sora) that closely matches the Penderecki aesthetic we are targeting.

**Overall impression:** A dense, evolving orchestral mass. Not melodic in the traditional sense. It behaves like a living texture — a shifting cloud of string energy that breathes, swells, fractures, and recombines. It feels suspended in time: motion everywhere, but no forward-driving rhythm. The sound exists as an environment, not a piece.

**Emotional qualities:**

- Suspended anxiety
- Grandeur without triumph
- Beauty that feels unstable or endangered
- A sense of inevitability or looming presence
- It doesn't tell a story — it creates a state
- You don't listen to it; you exist within it

**Texture & density:** At any given moment, dozens of micro-events overlap. Individual voices are not easily separable — they fuse into a granular mass. Density ebbs and flows slowly, like fog thickening and thinning. Even at its thinnest, there's always a bed of activity underneath.

**Pitch & harmony:** No functional harmony. Clusters dominate — tight pitch groupings within semitone or quarter-tone range. Slow microtonal drift inside clusters. When partials align briefly, they create ghost harmonics or fleeting "phantom chords" that vanish immediately. The ear perceives pressure and tension, not traditional consonance/dissonance.

**Articulation:** Slow bow pressure changes. Crescendi that don't resolve. Smeared attacks rather than clear onsets. Occasional sharper accents that feel like stress fractures in the mass, not rhythmic hits. Sul ponticello brightness. Bow noise mixed with pitch. Air and friction as part of the sound. Nothing clean or polished — intentionally raw, fibrous, tactile.

**Motion & evolution:** Motion is internal, not directional. No clear beginning or ending gestures. The mass seems to rotate, billow, ripple internally. Changes happen slowly, over seconds rather than beats.

**Spectral character:** Rich in upper partials. Slightly brittle or glassy. Never warm or lush in a romantic sense. High-frequency shimmer from bow noise. Midrange congestion (intentional). Very little true low-end foundation — weight comes from density, not bass.

**Space & depth:** Wide but shallow. Feels like a large ensemble in a resonant space, but without clear localization. Reverb is subtle and smeared — blends into the texture. Rather than placing players in space, the sound places the listener inside the sound field.

---

## 4. Spectral Analysis of Reference Audio

The Sora-generated reference clip (10 seconds of Penderecki-style orchestral texture) was subjected to rigorous computational spectral analysis. These measurements informed the v3.0.0 architecture and explain why the "continuous texture weave" approach works.

### 4.1 Measured Values

| Parameter | Value | Significance |
|-----------|-------|--------------|
| Spectral Flatness | 0.0001 | Intensely tonal (0 = pure tone, 1 = white noise). This is NOT noise — it is densely layered pitched content |
| Spectral Centroid | 1583 Hz mean | Mid-frequency emphasis. Not bright, not dark |
| Spectral Rolloff (85%) | 2721 Hz | Energy drops steeply above ~2.7 kHz. Dark compared to typical music |
| L/R Correlation | 0.110 | Extremely wide stereo. Side signal (0.165 RMS) is louder than mid signal (0.092 RMS) |
| RMS Energy | 0.025–0.218 | ~9:1 dynamic range. Clear breathing/swelling quality |
| Zero Crossing Rate | 0.030 | Very low — confirms tonal, not noisy content |
| Onsets Detected | 67 in 10s | ~6.7 per second — constant micro-event density |

### 4.2 Energy Distribution by Frequency Band

| Band | Energy (dB from peak) |
|------|----------------------|
| Sub-bass | -13.6 |
| Bass | -18.3 |
| Low-mid | -29.3 |
| Mid | -36.9 |
| Upper-mid | -44.8 |
| Presence | -52.8 |
| Brilliance | -59.0 |
| Air | -67.7 |

**Key finding:** Energy is overwhelmingly concentrated in sub-bass and bass. Each successive band drops by ~7 dB. The perception of "string texture" comes from the upper frequency content, but the physical energy and weight of the sound lives low. This is why dense filtering creates the right character — you're sculpting in the upper frequencies while the foundation holds everything together.

### 4.3 Temporal Evolution

The RMS energy profile reveals the "breathing" pattern — not random variation, but slow cyclical swelling:

| Time Window | Mean RMS | Peak RMS | Character |
|------------|----------|----------|-----------|
| 0–1s | 0.039 | 0.060 | Quiet onset |
| 1–2s | 0.099 | 0.201 | First swell |
| 2–3s | 0.097 | 0.218 | Peak pressure |
| 3–4s | 0.043 | 0.058 | Breathing down |
| 4–5s | 0.105 | 0.199 | Second swell |
| 5–6s | 0.107 | 0.196 | Sustained |
| 6–7s | 0.060 | 0.104 | Thinning |
| 7–8s | 0.129 | 0.183 | Third swell |
| 8–9s | 0.068 | 0.100 | Breathing |
| 9–10s | 0.067 | 0.149 | Thinning |

This ~3–4 second breathing cycle is what the glacier/tide LFOs in v3.0.0 recreate.

### 4.4 Pitch Analysis

| Time Window | Median Pitch | Spread | Range |
|------------|-------------|--------|-------|
| All windows | ~400 Hz (G4) | ±200 Hz | 235–1182 Hz |

**Key finding:** Despite the perception of "atonal clusters," there is a consistent pitch center around G4 (400 Hz). The "cluster" quality comes from simultaneous voices spread ±200 Hz around this center — approximately a major 6th of spread. The voices drift independently but orbit a gravitational center. This validates the approach of reading from the same buffer material at near-unity rate with tiny (±5 cent) microtonal drift.

### 4.5 What The Numbers Tell Us

The spectral analysis revealed a fundamental truth about the target sound that all previous implementations missed:

**The reference is a continuous pressure field, not a collection of discrete events.**

The L/R correlation of 0.11 means the left and right channels are nearly uncorrelated — each ear receives almost independent information. The side signal is 1.8x louder than the mid signal. This is not "stereo panning" of discrete grains — it is a diffuse field where multiple independent sources create an immersive wash.

The spectral flatness of 0.0001 means this is overwhelmingly tonal. Not noise, not clicks, not granular artifacts — dense, overlapping pitched material that fuses into a mass. You can't create this quality by triggering individual grains with silences between them. You create it by running multiple voices continuously, reading overlapping regions of the same material, and letting spectral interference do the work.

This is why v3.0.0 replaced discrete grain triggers with continuously-running softcut voices.

---

## 5. The Breakthrough: Continuous Pressure Fields

### 5.1 Why Discrete Triggers Failed

Previous versions (v0.11 through v2.5.2) all used some variation of:

1. Capture fragments into a pool
2. Trigger individual grains on a schedule (metro, LFO, probability)
3. Control texture by varying trigger rate, grain size, and envelope

This approach has a fundamental problem: no matter how fast you trigger grains, there are gaps between them. Each grain has a distinct onset and offset. The listener perceives individual events, not a continuous pressure field. Even with overlapping grains, the stochastic timing creates a "sparkle" quality that sounds like a granular synthesizer, not like an orchestra.

The Polymorphia texture analysis identified this clearly:

> "Individual events not perceptually isolated — continuous pressure field. No clear grain attack — everything smeared. Think: continuous friction field."

### 5.2 The Architectural Insight

The v3.0.0 architecture inverts the approach. Instead of triggering discrete events, the softcut voices run **continuously** as slow-scanning overlapping loopers:

- 4 voices always playing, always looping
- Each reads from a different region of the buffer
- Each drifts slowly through buffer time
- The loop windows breathe open and closed
- Levels swell and recede with multiple LFOs
- Rate drifts ±5 cents for microtonal cluster beating

Think of it as 4 string players, each bowing the same passage but slightly offset in time. They don't start and stop — they sustain continuously. The texture emerges from the interference between their overlapping readings:

- When two voices read similar regions: reinforcement, ghost harmonics
- When voices drift apart: wider texture, phantom chords
- When a voice crosses a transient in the buffer: momentary accent
- When loop windows narrow: more grain-like, repetitive (stress fracture quality)
- When loop windows widen: smeared, sustained mass

### 5.3 How It Sounds vs. How It's Built

**What you hear:** A slowly breathing orchestral mass that responds to your playing. Clusters that swell and thin. Ghost harmonics that appear and vanish. Occasional stress fractures that feel like a string section digging in. A stereo field that places you inside the sound.

**What's actually happening:** SuperCollider's MASS synth creates the primary texture — 4 BufRd voices with formant filtering, comb tension, staged evolution (shimmer, quiver, glissando). SuperCollider's DUST synth creates the percussive surface — 4 BufRd voices with quick envelopes, plus 6 Phantom Limb delay taps for dispersion. Softcut's 4 weave voices add a second layer of continuous scanning, creating spectral interference patterns that neither SC layer produces alone. Six incommensurate LFOs modulate everything slowly, ensuring the texture never repeats.

---

## 6. Audio-Reactive Coordinate Mapping

Inspired by MilkDrop audio visualizers — treating audio processing like vertex shaders and texture mapping.

### 6.1 Core Concept

Instead of thinking in terms of grain triggering, think of voices as "vertices" in a coordinate space warped by mathematical functions:

- **X axis:** Stereo pan position (−1 to +1)
- **Y axis:** Pitch (cents from root)
- **Z axis:** Time delay (offset behind write head)
- **W axis:** Amplitude (voice level)

### 6.2 Implemented Motion Equations

**Golden ratio panning:** `pan(voice_i) = ((i × φ) mod 1) × 2 − 1`

This distributes voices across the stereo field using the golden ratio (φ ≈ 1.618), creating a natural, non-uniform spread that avoids the symmetry of equal spacing. Each voice occupies a unique position that feels organic.

**Trigonometric drift:** `pan_final = pan_base + sin(lfo_swirl + voice_phase) × 0.35`

Slow sinusoidal modulation creates the sensation of voices drifting through the stereo field — the DNA helix rotation.

**Amplitude-reactive clustering:** When input amplitude is high, voices are pulled toward center (clustering). When quiet, they spread (dispersing). This creates the murmuration quality — tightening under pressure, loosening in silence.

**Fibonacci cascade timing** (in SC engine): Voices trigger at Fibonacci-related intervals, modulated by LFSR for organic variation. MASS voices use slower cascades (0.4–1.0× density), DUST voices use faster, irregular timing (Dust.kr).

### 6.3 The Visualization-to-Sound Metaphor

Just as MilkDrop visualizers use per-pixel equations to warp screen geometry, ENSEMBLE uses per-voice equations to warp audio coordinate space. The result is sound that feels visually geometric — you can almost "see" the patterns as you hear them.

---

## 7. Technical Architecture

### 7.1 System Overview

```
AUDIO INPUT (guitar, synth, etc.)
       │
       ├──→ SuperCollider Engine (Engine_Polymorphia.sc)
       │    ├── \polyRec: Circular buffer recorder (45s stereo)
       │    ├── \polyMass: 4 BufRd voices, long grains, formant/comb processing
       │    └── \polyDust: 4 BufRd voices + 6 Phantom Limb delay taps
       │
       └──→ Softcut (managed by ensemble.lua)
            ├── Voices 1-2: Stereo recording (60s buffer)
            └── Voices 3-6: Continuous texture weave readers
```

The SC engine and softcut operate on SEPARATE buffer systems. This is a fundamental architectural constraint of norns: SuperCollider grains cannot read from softcut buffers, and vice versa. Each system captures and processes audio independently.

### 7.2 File Structure

```
dust/code/ensemble/
├── ensemble.lua                  (Lua script — UI, softcut, control logic)
└── lib/
    └── Engine_Polymorphia.sc     (SuperCollider engine — all DSP)
```

### 7.3 Signal Flow

**SuperCollider path:** ADC → `\polyRec` (records to SC buffer, monitors input) → `\polyMass` (reads buffer, outputs processed long grains) + `\polyDust` (reads buffer, outputs processed short grains + dispersed taps) → DAC

**Softcut path:** ADC → voices 1–2 (record to softcut buffer) → voices 3–6 (read continuously from buffer, LP/HP filtered, level/pan modulated by LFOs) → DAC

Both paths mix at the DAC output simultaneously.

---

## 8. SuperCollider Engine: Engine_Polymorphia

### 8.1 Overview

Version: v2.4.2. Three persistent synths — no synth spawning. All voices pre-allocated.

**Critical discovery:** GrainBuf.ar produces NO OUTPUT on this norns/fates system despite correct parameters. All grains use the manual BufRd + Phasor + EnvGen approach instead.

### 8.2 Recorder (\polyRec)

- Records `SoundIn.ar([0,1])` to 45-second stereo circular buffer
- Publishes amplitude to `ampBus` (control rate) for reactivity
- Publishes write position to `phaseBus` (control rate) for grain following
- `preLevel` parameter (default 0.95) controls buffer feedback — high values mean old material persists, building density over time
- Monitors input to DAC when active

### 8.3 MASS Layer (\polyMass) — 4 Voices

Each voice is a BufRd reader with its own behavior:

**Voice 0 — Sparse Bass + Rising Glissando:**
- Grain size: `baseSize × LFNoise2(0.006)` (variable, ~3–6s)
- Density: 0.4× base (slowest triggerer)
- Reads 6–22% behind write head
- 20% chance of octave-down (bass voice, rate 0.5)
- Subtle rising glissando: +0.5–1.5% pitch slide over grain duration
- ~12% chance of reverse playback
- Pan: slow swooping sinusoid

**Voice 1 — Rising Glissando (Penderecki ascending gesture):**
- Density: 0.7× base
- Reads 8–25% behind write head
- Rising gliss: +0.5–2%
- ~8% reverse chance
- Golden ratio pan offset with swooping modulation

**Voice 2 — Descending Glissando (Sul Ponticello):**
- Density: 0.85× base
- Reads 10–30% behind write head
- Falling gliss: −0.5 to −1.5%
- ~10% reverse chance
- Wide pan sweep

**Voice 3 — Random Direction Glissando:**
- Density: 1.0× base (fastest)
- Reads 12–35% behind write head
- Random ±1.5% gliss direction
- ~25% reverse chance (most reversals)
- LFNoise2 random pan (most chaotic)

**Per-voice processing chain:**
1. BufRd reading with 4-point interpolation
2. Envelope: slow attack (400–600ms), sustain, gentle release
3. Tremolo (3–6 Hz, depth modulated by evolution stage)
4. Shimmer (4.5–6.5 Hz amplitude wobble, increases with evolution)
5. Formant filtering: 3 resonant BPFs at vowel-like frequencies (~400, ~1200, ~2400 Hz) that drift slowly via LFOs
6. Comb filtering: `CombC` at 12–34ms delay, feedback modulated by Tension parameter. Creates metallic "string tension" resonance
7. LP filter: 4500–8000 Hz range, modulated by LFO
8. Stereo placement via `Balance2`

**Staged Evolution:**
- Stage 0: Dry grains with mild filtering
- Stage 1: Comb stress increases, longer grain durations
- Stage 2: Shimmer — amplitude wobble at 4–6 Hz
- Stage 3: Quiver — micro pitch vibrato at 5–7 Hz + stronger comb stress

Evolution progresses automatically via the `evolve` parameter × a slow LFO.

**Global MASS processing:**
- Mix: voices summed at 0.38 level
- Smoothing: LP at 8 kHz + dual allpass diffusion (12ms + 19ms)
- Light reverb: FreeVerb2 at 10% mix, 0.4 room
- Soft clip: `(sig × 1.6).clip(-1.3, 1.3) × 0.85`
- DC removal: `LeakDC`
- Final scaling: `× gain × blend × env`

### 8.4 DUST Layer (\polyDust) — 4 Voices + 6 Phantom Limb Taps

**Grain voices (4):**
- Rate: always 1.0 (NO pitch change — preserves original pitch)
- Grain size: ~30–120ms (baseSize × random 0.45–1.7)
- Timing: `Dust.kr` for irregular, organic triggering (not mechanical Impulse)
- Reads very close behind write head: 2–20% offset (near-immediate echo)
- Quick perc envelopes: 2–5ms attack
- Density modulated by: cluster LFO (0.055 Hz), swarm LFO (0.08 Hz), burst LFO (LFNoise1 0.18 Hz), build envelopes (Env.perc triggered by Dust.kr 0.12)
- Input amplitude scales density (louder playing = more grains)

**Phantom Limb Dispersion (6 delay taps):**

Inspired by the Discomfort Designs Phantom Limb pedal. Each tap:
- Reads from the raw DUST signal via `DelayC`
- Random delay times drifting slowly: 60ms → 220ms → 380ms → 550ms → 850ms → 1150ms → 1700ms
- Each tap randomly gated by a probability LFO (chance decreases for longer taps)
- Progressive LP filtering: 7000 Hz → 6200 → 5400 → 4600 → 3800 → 3000 Hz (longer taps = darker)
- Independent pan positions drifting via slow sinusoids
- Creates evolving pointillistic scatter: dust grains echo and disperse across time and stereo space

**Global DUST processing:**
- HP filter: 200 Hz (removes muddy low end, keeps sul ponticello brightness)
- LP filter: 9000 Hz
- Allpass diffusion: 7ms
- Soft clip
- Final scaling: `× gain × (1 − blend) × env × 1.5`

### 8.5 Engine Commands

| Command | Type | Description |
|---------|------|-------------|
| `record` | int | Start/stop recording (0 or 1) |
| `grainGate` | int | Enable/disable both grain layers |
| `inGain` | float | Input gain (default 2.0) |
| `monitorLevel` | float | Direct monitor level (default 0.8) |
| `preLevel` | float | Buffer persistence/feedback (default 0.95) |
| `blend` | float | DUST↔MASS crossfade (0–1) |
| `tension` | float | Comb filter stress (0–1) |
| `evolve` | float | Evolution speed/depth (0–1) |
| `disperse` | float | Phantom Limb tap amount (0–1) |
| `massGain` | float | MASS layer level |
| `dustGain` | float | DUST layer level |
| `massDensity` | float | MASS grain density |
| `dustDensity` | float | DUST grain density |
| `massSize` | float | MASS grain duration |
| `dustSize` | float | DUST grain duration |
| `movement` | float | Stereo spread (sets both massSpread and dustSpread) |
| `clearBuffer` | int | Zero the SC buffer |
| `testTone` | int | Play a diagnostic 440/550 Hz beep |

---

## 9. Softcut Texture Weave

### 9.1 The Paradigm Shift

Previous versions used softcut as a discrete grain trigger system: capture fragments, store positions, trigger playback on a schedule. v3.0.0 completely replaces this with a **continuous texture weave** — 4 voices that run permanently, creating spectral interference patterns through overlapping reading of the same buffer material.

### 9.2 Recording Voices (1–2)

- 60-second stereo buffer
- Continuous capture when active
- `pre_level` 0.85: old material persists beneath new, building density
- `pre_filter_dry` = 1 (CRITICAL — required for audio output)
- Stereo routing: voice 1 = left input, voice 2 = right input
- Silent output (level = 0) — SC engine handles monitoring

### 9.3 Weave Voices (3–6)

Four continuously-playing voices, each with unique character:

| Voice | Buffer Offset | Scan Rate | Pan Phase | Character |
|-------|--------------|-----------|-----------|-----------|
| 3 | 2.0s behind | 0.003 | 0 | Deep reader — oldest material |
| 4 | 4.5s behind | 0.005 | φ | Mid reader — medium history |
| 5 | 8.0s behind | 0.002 | 2φ | Far reader — distant memory |
| 6 | 1.2s behind | 0.007 | 3φ | Close reader — near-immediate |

**Continuous modulation (every frame at 25 fps):**

- **Loop position:** Each voice reads at a slowly drifting offset behind the write head. The offset itself modulates via the drift LFO (×0.6 to ×1.4), creating a "scanning" effect through buffer time.
- **Loop window size:** Breathes with the glacier LFO. Longer windows = smeared mass. Shorter = more grain-like. Blend parameter scales the base: DUST-biased = shorter windows, MASS-biased = longer.
- **Level:** Modulated by glacier LFO (long breathing), tide LFO (medium swell), and input amplitude (reactivity). Creates the slow density ebb and flow matching the reference audio's 3–4 second breathing cycle.
- **Pan:** Golden ratio base positions + slow swirl LFO modulation. Creates the wide, decorrelated stereo field measured in the reference (0.11 L/R correlation).
- **Filter:** Shimmer LFO modulates cutoff. Voices 3–4: warmer (LP blend, fc ~3500 Hz). Voices 5–6: glassier (LP + slight HP, fc ~4500 Hz). Creates the layered spectral character.
- **Rate:** 1.0 ± 0.003 (±5 cents). Four voices at slightly different microtonal offsets create the cluster beating that is the signature of the Penderecki sound.

### 9.4 Why This Works

The continuous approach naturally produces the qualities the spectral analysis identified:

- **Wide stereo:** 4 voices with golden ratio pan offsets + independent drift → nearly uncorrelated L/R
- **Tonal density:** Overlapping readings of pitched buffer material → fused mass, not noise
- **Breathing:** Glacier LFO modulating levels over ~180s cycles, nested with faster swells
- **Ghost harmonics:** When two voices read similar regions simultaneously, partials reinforce briefly then diverge → phantom chords
- **Stress fractures:** When a voice's loop window narrows over a transient in the buffer → momentary accent emerges from the texture

---

## 10. LFO System

### 10.1 Design Philosophy

Six LFOs at incommensurate rates, none tempo-locked. The rates are chosen so they never align, creating truly non-repeating organic motion. This directly addresses the Polymorphia analysis principle: "AVOID tempo-locked LFOs."

### 10.2 LFO Table

| Name | Cycle Length | Rate (rad/s) | Controls | Character |
|------|-------------|---------------|----------|-----------|
| Glacier | ~180s | 0.035 | Voice levels, loop window size | Macro breathing — the slowest tide |
| Tide | ~73s | 0.086 | Density swell, level modulation | Medium-term density changes |
| Drift | ~47s | 0.134 | Buffer offset modulation | Position scanning — how far back voices read |
| Swirl | ~29s | 0.217 | Pan position modulation | Stereo field movement |
| Shimmer | ~19s | 0.331 | Filter cutoff modulation | Spectral brightness drift |
| Ripple | ~11s | 0.571 | Micro pitch drift (rate modulation) | Microtonal cluster beating |

### 10.3 Incommensurability

The ratio between any two adjacent LFO rates is irrational (none are integer multiples of each other). The combined period before exact repetition is effectively infinite. This ensures the texture evolves continuously without ever cycling back to an identical state — matching the reference audio's quality of "motion everywhere, but no pattern."

---

## 11. Flourish System

### 11.1 Concept

Rare, self-contained events that emerge from the texture and dissolve back into it. These are the "stress fractures" in the mass — sudden density spikes that come and go like gusts of wind through strings.

### 11.2 Behavior

- Cooldown: 25–60 seconds between flourishes (rare events)
- Trigger probability: very low base (0.2%) + input amplitude boost
- Duration: 2–4 seconds
- Shape: rises to a peak, then fades — concave rise, convex fall
- At peak: loop windows narrow (more grain-like), levels boost 1–2.5×
- After: normal continuous weave resumes seamlessly

### 11.3 What It Sounds Like

A moment where the texture suddenly tightens and intensifies — like the string section briefly digging into a passage with more pressure. Then it relaxes back into the ambient mass. These moments provide the "stress fracture" accents described in the target sound analysis: "sharper accents that feel like stress fractures in the mass, not rhythmic hits."

---

## 12. Parameters & Controls

### 12.1 Main Controls (Encoders)

| Control | Parameter | Range | Default | Effect |
|---------|-----------|-------|---------|--------|
| E1 | Blend | 0–1 | 0.5 | DUST↔MASS crossfade. Also affects softcut window size |
| E2 | Tension | 0–1 | 0.5 | Comb filter stress in MASS. String wire resonance |
| E3 | Texture | 0–1 | 0.15 | Softcut weave layer amount. Start subtle |

### 12.2 Key Controls

| Control | Action |
|---------|--------|
| K2 | Toggle ACTIVE/STANDBY (recording + grains on/off) |
| K3 | Clear all buffers (SC + softcut) |

### 12.3 Params Menu

**ENSEMBLE:** Blend, Tension, Texture, Evolve, Movement

**SC ENGINE:** MASS Gain, DUST Gain, Disperse (Phantom Limb)

**INPUT:** Input Gain, Monitor Level, Persistence (buffer feedback)

---

## 13. UI Behavior

### 13.1 Node Visualization

The main display shows 8 interconnected nodes — an abstract representation of the organism:

- Nodes drift slowly with LFO-influenced motion
- Connections appear between nearby nodes (< 40px)
- Active nodes (responding to input) are filled, bright (level 15)
- Connected active nodes glow brighter
- Inactive nodes are outlined, dim (level 4)
- Flourish events agitate nodes (increased velocity, brightening)
- Gentle pull toward center prevents drift off-screen

### 13.2 Status Indicators

- **Top left:** ACTIVE / STANDBY / FLRSH (during flourish events)
- **Top center:** Texture amount (T:##)
- **Top right:** Blend mode (DUST / MIX / MASS)
- **Bottom left:** 4 weave voice indicators (filled = active)
- **Bottom center:** Input amplitude meter (horizontal bar)
- **Bottom:** Dim help text (K2:go K3:clr E1:bld E2:tns E3:tex)

---

## 14. Key Technical Learnings

### 14.1 SuperCollider on Norns/Fates

**GrainBuf.ar does not work.** Despite correct buffer data and parameters, it produces no output on this system. Solution: manual grains using `BufRd.ar` + `Phasor.ar` + `EnvGen.ar`.

**Memory constraints:** 60-second stereo buffer in SC = memory allocation error. 45 seconds works reliably. No per-grain synth spawning — use fixed voice pools inside persistent synths.

**All `var` declarations must be at TOP of function,** before any executable code. SC on norns enforces this strictly.

**Use `Dust.kr` for irregular timing,** not `Impulse.kr`. `Dust.kr` produces statistically random triggers that sound organic. `Impulse.kr` sounds mechanical.

### 14.2 Softcut Critical Settings

- `pre_filter_dry(voice, 1)` — REQUIRED or signal is filtered out silently
- Positions should start at 1, not 0
- `audio.level_adc_cut(1)` — route ADC to softcut
- `audio.level_cut(1)` — enable softcut output
- System PARAMS > SOFTCUT levels must be at 0 dB
- Stereo routing via `level_input_cut` matrix (not automatic)

### 14.3 SC + Softcut Separation

SuperCollider grains CANNOT read from softcut buffers. They are entirely separate buffer systems. Each must capture and process audio independently. This is a fundamental norns architecture constraint, not a bug.

### 14.4 Lua Ordering

Lua resolves local variables top-to-bottom. Any state table (like `flourish`) that is referenced inside a function must be declared BEFORE that function's definition in the source file. Functions inside `clock.run` are resolved at runtime, so forward references work there — but not in direct function bodies.

### 14.5 Parameter Naming

Avoid reserved param IDs: "reverb" conflicts with the norns system reverb. Use "rev_amt" or similar alternatives.

### 14.6 LFSR vs math.random

Use a Linear Feedback Shift Register for pseudo-random values in audio-rate code. `math.random()` can cause timing inconsistencies. The LFSR is deterministic and lightweight:

```lua
local lfsr = 0xACE1
local function lfsr_next()
  local bit = ((lfsr >> 0) ~ (lfsr >> 2) ~ (lfsr >> 3) ~ (lfsr >> 5)) & 1
  lfsr = (lfsr >> 1) | (bit << 15)
  return lfsr / 65536
end
```

---

## 15. Development History

### Phase 1: Pure Softcut (v0.11, January 2026)

First working version. 2 recording voices + 4 playback voices, all softcut. MASS + DUST concept established. No SuperCollider processing. Proved the dual-layer concept worked but lacked spectral richness.

### Phase 2: SuperCollider Engine (v0.50–v2.1.0, January 2026)

Introduced Engine_Polymorphia with persistent synths. Discovered GrainBuf failure, developed BufRd workaround. Added formant filtering, comb tension, staged evolution. Established the 3-synth architecture (recorder + MASS + DUST).

### Phase 3: Hybrid Architecture (v2.2.0–v2.4.2, January–February 2026)

Added Phantom Limb dispersion to DUST layer. Subtle glissando on MASS voices (Penderecki ascending/descending gestures). Sparse octave-down bass voice. LP smoothing + allpass diffusion to reduce harshness. Reached stable, musically satisfying SC engine. Added softcut texture layer alongside SC engine.

### Phase 4: Texture Layer Experiments (v2.5.0–v2.5.2, February 2026)

Added attention windows (Jelinek-style lock-on to recent phrases). Swirl events with SC ducking. Volume LFO for evolving presence. Flourish-based stutters (accelerating/decelerating rhythmic gestures). Good ideas, but discrete trigger approach was fundamentally wrong for the target sound.

### Phase 5: Continuous Texture Weave (v3.0.0, February 2026)

Complete rethinking based on spectral analysis of reference audio. Replaced all discrete softcut triggers with continuously-running overlapping readers. Six incommensurate LFOs. Simplified flourish system. Best result achieved — first attempt matched the vision.

**The key insight:** The reference audio is a continuous pressure field (spectral flatness 0.0001, L/R correlation 0.11) with breathing density (3–4 second RMS cycles). You cannot create this by triggering individual grains. You create it by running multiple voices continuously and letting spectral interference emerge naturally.

---

## 16. Success Criteria

### 16.1 The Jelinek Test

Play a single guitar chord. Do the resulting particles sound like "refined fragments of that chord" or "random digital noise"? If the former, you've succeeded. Each micro-event should whisper its source material.

### 16.2 The Polymorphia Test

Does the texture evoke:

- Percussive col legno strikes building tension?
- Sustained clusters that swell and release?
- Sul ponticello brightness mixed with bow noise?
- Discontinuous sounds creating beautiful chaos?

### 16.3 The Murmuration Test

Do the voices:

- Move through binaural space?
- Form clusters that twist and diverge?
- Create the sensation of flocking behavior?
- Build, peak, dissipate, rebuild?

### 16.4 The Smeared Mass Test

Does the texture:

- Never fully clear — always a bed of activity?
- Change over seconds, not milliseconds?
- Place you inside the sound field (not outside listening in)?

### 16.5 The Spectral Verification

Does the output match these measured properties of the reference:

- Overwhelmingly tonal (not noisy)?
- Wide stereo (low L/R correlation)?
- Breathing density with 3–4 second cycles?
- Dark spectral rolloff (energy below 3 kHz)?
- Constant micro-event density (~6–7 per second)?

### 16.6 Complete Checklist

- ✓ Fragments sound articulate, not formless
- ✓ Voices cascade with organic timing
- ✓ Stereo field feels alive (constant movement)
- ✓ Texture breathes with playing intensity
- ✓ MASS layer provides sustained fog/bed
- ✓ DUST layer provides surface friction/stippling
- ✓ Both SC layers coexist and interweave
- ✓ Softcut weave adds spectral interference / ghost harmonics
- ✓ Sound degrades/layers over time (high persistence)
- ✓ Sparse to dense continuum works (Blend parameter)
- ✓ No clicks or glitches (soft envelopes)
- ✓ Fully automatic/generative (no manual triggering required)
- ✓ Parameters evolve organically via incommensurate LFOs
- ✓ Flourishes create rare stress-fracture accents
- ✓ Original pitch preserved (rate ≈ 1.0, only ±5 cent drift)

---

## 17. Future Directions

### 17.1 Sonic Enhancements

- **Micro-loop detection (Jelinek technique):** Analyze fragments for repeating zero-crossing patterns, mark as "loop-capable," apply tight looping + heavy formant shifting
- **Bucket brigade decay:** Progressive filter darkening per generation of buffer feedback
- **Vinyl crackle integration:** Subtle noise layer for organic texture
- **Freeze mode:** Stop recording but keep all processing active — material evolves endlessly
- **Presets:** Polymorphia (dense, harsh), Chamber Whispers (sparse, reverberant), Struck Strings (percussive, dry), Glassy Shimmer (bright, sul ponticello), Degraded Cascade (heavy feedback, foggy)

### 17.2 Technical Improvements

- **Filter modulation in softcut:** More spectral movement via `post_filter_fc` modulation (partially implemented in v3.0.0)
- **Attention windows:** Jelinek-style "lock-on" to specific buffer regions during interesting input (implemented in v2.5.0, not yet ported to v3.0.0)
- **MIDI control:** Map external controllers to Blend, Tension, Texture for live performance
- **Buffer visualization:** Show softcut buffer waveform on screen

### 17.3 Philosophical Notes

The development history of ENSEMBLE demonstrates a key principle: sometimes the right architecture matters more than the right parameters. Versions 1 through 2.5 all used discrete grain triggering with increasingly sophisticated scheduling (metros, LFOs, flourishes, attention windows). None achieved the target sound. Version 3.0.0 changed the fundamental approach — continuous overlapping readers instead of discrete triggers — and succeeded on the first attempt.

The lesson: when iterating on parameters isn't converging, question the architecture.

---

## Appendix A: Polymorphia Texture Analysis (Synthesis Parameters)

Detailed frame-by-frame analysis of Penderecki's "Polymorphia" for granular synthesis translation.

### 5:20–5:30: Dense Sustained Mass

- Grain size: 15–40ms, very high density, overlapping clouds
- Hann/Gaussian windows, no clear grain attack — everything smeared
- Constant rolling buffer (1–2s), small fast position jitter
- Very slow random walk LFO on pitch (±10–20 cents)
- Slight stereo decorrelation
- HP slightly (remove body), gentle comb filtering (metallic string tension)
- Think: "continuous friction field"

### 5:31–5:40: Brittle Pointillism

- Grain size: 30–80ms, medium density
- Slightly sharper Hann, grain envelopes now audible
- Use onset detection to retrigger and momentarily increase density
- Micro-vibrato (5–7 Hz, very shallow), randomized panning
- Slight amplitude modulation, slow tremolo

### 8:12–8:27: Long Evolving Mass

- Grain size: 80–200ms, medium-high density
- Gaussian (very smooth), slow scan through buffer, no hard retriggers
- Very gentle formant shifting, drifting bandpass filters, slowly changing Q
- Extremely slow LFOs (10–30 seconds), multiple slightly out of sync
- This is macro-motion, not micro-detail

### 9:10–9:15: Violent Chaos

- Grain size: 10–25ms, very high density
- Hann slightly clipped
- Spike density on onset, widen pitch randomization
- Mix filtered noise per grain, modulated by input amplitude
- Short comb filtering (2–8ms), random feedback — metallic tearing
- Chaotic LFOs (sample & hold), no periodic vibrato
- This should feel dangerous, not musical

### 9:56–10:00: Sustained Tension Plateau

- Grain size: 40–100ms, high but constant density
- Gaussian, narrow scan range, small jitter
- Fixed center ±15 cent slow drift, no fast vibrato
- No tremolo, no onset retriggers
- Let mass breathe via slow density modulation
- This is pressure without rupture

### Key Synthesis Principles

**PRIORITIZE:** density modulation over rhythm, slow spectral motion over pitch, overlapping envelopes over discrete events, instability over control

**AVOID:** obvious looping, tempo-locked LFOs, clean pitch tracking, regular grain spacing

---

## Appendix B: SuperCollider Rules for This System

1. All `var` declarations MUST be at the top of the function, before any code
2. Use `BufRd.ar`, NOT `GrainBuf.ar` (broken on this norns/fates system)
3. `Phasor.ar` for position scanning within each grain
4. `EnvGen.ar` with `Env.perc` or custom `Env` for amplitude shaping
5. `RecordBuf.ar` with `run` parameter for conditional recording
6. Use `Dust.kr` for irregular timing (more organic than `Impulse.kr`)
7. Keep buffer ≤ 45 seconds to avoid SC memory allocation errors
8. No per-grain synth spawning — fixed voice pools only
9. Prefer `clip` over `tanh` for saturation (gentler character)
10. Always include `LeakDC.ar` at the end of the signal chain

---

*ENSEMBLE v3.0.0 — February 10, 2026*  
*Platform: Monome Norns / Fates*  
*Engine: Engine_Polymorphia.sc v2.4.2*  
*Script: ensemble.lua v3.0.0*


ENSEMBLE - PROJECT STATUS DOCUMENT<br>
================================================
Version: 2.2.0
Date: February 6, 2026
Status: WORKING - Phantom Limb dispersion + smoothing

================================================================================<br>
TECHNICAL  <br>
================================================================================<br>

**Manual grains using BufRd + Phasor + EnvGen**

This approach works reliably:
```supercollider
trig = Impulse.kr(density);
pos = TRand.kr(0.05, 0.95, trig) * numFrames;
phasor = Phasor.ar(trig, BufRateScale.kr(bufnum), pos, pos + (size * SampleRate.ir), pos);
env = EnvGen.ar(Env.perc(attack, release), trig);
grain = BufRd.ar(2, bufnum, phasor, loop: 1, interpolation: 2) * env;
```

PlayBuf also works - this is how we confirmed buffers contain audio.

================================================================================<br>
CURRENT ARCHITECTURE (v2.1.0)<br>
================================================================================<br>

ENGINE: Engine_Polymorphia.sc (SuperCollider)
SCRIPT: ensemble.lua (Lua)

BUFFER: 45 seconds stereo, circular recording with high persistence (preLevel 0.93)

SYNTHS:
1. \polyRec - Recorder
   - Records input to circular buffer
   - Publishes write position to control bus (for write-head following)
   - Publishes input amplitude to control bus (for reactivity)
   
2. \polyMass - MASS layer (4 BufRd voices)
   - Long grains: 3-6 seconds
   - Slow attack envelopes (500ms attack, 800ms release)
   - Reads BEHIND write head (2-8 seconds offset)
   - Formant-like filtering (3 resonant BPFs at vowel frequencies)
   - Comb filtering for string tension
   - STAGED EVOLUTION: shimmer (amplitude wobble) + quiver (pitch vibrato)
   - LP filter ~3500-5000 Hz
   - Golden ratio panning with slow LFO drift
   - Microtonal pitch drift via rate (±1.5-2 cents)
   - SMOOTHING: LP @ 6kHz + dual allpass diffusion
   - Gentler saturation (clip instead of tanh)
   
3. \polyDust - DUST layer (4 BufRd voices + 6 delay taps)
   - Short grains: 30-120ms
   - Quick perc envelopes (3-6ms attack)
   - Reads close behind write head (20-180ms offset)
   - HP filter ~250 Hz (gentler sul ponticello)
   - PHANTOM LIMB DISPERSION: 6 delay taps (80ms-1.8s)
     - Each tap randomly gated by probability LFO
     - Progressive LP filtering on longer taps (darkening)
     - Pan positions drift independently
     - Creates evolving pointillistic scatter patterns
   - Density modulated by clusterLfo, swarmLfo, burstLfo
   - Uses Dust.kr for irregular timing
   - Smoothing: LP @ 7kHz + allpass diffusion

CONTROL BUSES:
- ampBus: Input amplitude for reactivity
- phaseBus: Write head position for grain following

================================================================================<br>
PARAMETER MAPPING<br>
================================================================================<br>

MAIN CONTROLS (Encoders):
- E1: Blend (0-1) - Crossfades DUST ↔ MASS
- E2: Tension (0-1) - Comb filter strength, string stress
- E3: Persistence (0.8-0.98) - Buffer feedback/layering

KEYS:
- K2: Toggle active (record + grains)
- K3: Clear buffer

PARAMS MENU:
- Blend, Tension, Movement, Evolve
- MASS: Gain, Density, Size
- DUST: Gain, Density, Size, Disperse (Phantom Limb effect)
- Input Gain, Monitor Level, Persistence

================================================================================<br>
SONIC GOALS (from original blueprint)<br>
================================================================================<br>

TARGET SOUND: Penderecki's "Polymorphia" middle section
- Hyper-dense string swarm
- Microtonal friction and bow noise
- Spectral pressure without traditional harmony
- Slowly breathing, evolving mass
- Tension without release
- Murmuration-like swarm intelligence

DUAL NATURE:
- MASS: Smeared notes that rise, undulate, bow - the "bed" or "fog"
- DUST: Stippling clusters with percussive friction - surface texture

STAGED EVOLUTION (implemented in MASS):
- Stage 0: Dry grains
- Stage 1: Comb stress increases
- Stage 2: Shimmer (amplitude wobble at 4-6Hz)
- Stage 3: Quiver (micro pitch vibrato at 5-7Hz) + stronger comb

================================================================================<br>
WHAT'S WORKING WELL<br>
================================================================================<br>

✓ BufRd-based grains produce audio reliably
✓ MASS: Long grains with slow attack, formant filtering, comb tension
✓ DUST: Phantom Limb-style dispersion (6 delay taps, random gating)
✓ Write-head following: Grains read what was just recorded
✓ Input reactivity: DUST density responds to playing dynamics
✓ Persistence: Old sounds layer and weave until overwritten
✓ Node visualization responds to input amplitude
✓ Golden ratio panning creates natural stereo distribution
✓ Staged evolution: shimmer + quiver modulated by evolve parameter
✓ Smoothing stage: LP filter + allpass diffusion reduces harshness
✓ Significant gain boost for better output levels
✓ Gentler saturation curve (clip instead of harsh tanh)

================================================================================<br>
FILE STRUCTURE<br>
================================================================================<br>

dust/code/ensemble/
├── ensemble.lua
└── lib/
    └── Engine_Polymorphia.sc

================================================================================<br>
SUPERCOLLIDER RULES FOR THIS SYSTEM<br>
================================================================================<br>

1. All `var` declarations MUST be at TOP of function, before any code
2. Use BufRd.ar, NOT GrainBuf.ar (GrainBuf broken on this system)
3. Phasor.ar for position scanning within grain
4. EnvGen.ar with Env.perc or custom Env for amplitude shaping
5. RecordBuf.ar with run parameter for conditional recording
6. Use Dust.kr for irregular timing (more organic than Impulse.kr)

================================================================================<br>
UI STYLE<br>
================================================================================<br>

- Minimal: node visualization as main display
- 8 nodes with connection lines when close
- Status top left ("ACTIVE"/"STANDBY")
- Blend indicator top right (DUST/MIX/MASS)
- Input meter bottom left
- Help text very dim at bottom
- Nodes stay within bounds, gentle pull toward center

================================================================================<br>
INFLUENCES / REFERENCES<br>
================================================================================<br>

- Penderecki "Polymorphia" - microtonal clusters, sul ponticello, col legno
- Jonny Greenwood "Proven Lands" - percussive strings, aleatoric lines
- Jan Jelinek "Loop Finding Jazz Records" - micro-loops, stuttering textures
- KHC (San) - organic fragmentation, distinct fragments
- Discomfort Designs "Phantom Limb" - random delay dispersion, lo-fi textures

================================================================================<br>
<br>
================================================================================<br>

--

##OLD VERSION
--

================================================================================<br>
1. THE VISION<br>
================================================================================<br>

A living, breathing string ensemble that transforms your playing into cascading, 
weaving voices using extended techniques - like watching a murmuration of 
starlings form DNA helixes, or an audio-reactive visualizer rendered as sound.

================================================================================<br>
1A. THE PARTICLES → SWARM → MASS CONTINUUM<br>
================================================================================<br>

Density parameter controls the particle clustering, from sparse Morse code 
dots to dense undulating mass. Grain/fragment size stays micro (20-50ms) to 
maintain stippled, pointillist quality. Each particle carries formant-sculpted 
spectral content. The 'call & response' is the system deciding when/where to 
place particles based on input dynamics.

PERCUSSIVE PATTERN GENERATION:
• Input amplitude envelope triggers particle density bursts
• Louder transients = denser particle clouds
• Sustained input = particles maintain 'hovering swarm' quality
• Undulation from particle placement timing (rhythmic stippling, not smooth grains)

================================================================================<br>
2. SONIC CHARACTER & INFLUENCES<br>
================================================================================<br>

2.1 Primary References
-----------------------

PENDERECKI'S 'POLYMORPHIA':
• Microtonal clusters (multiple pitches within 50-200 cents)
• Sul ponticello (glassy, near-bridge brightness)
• Percussive col legno (struck strings)
• Slow bowing attacks vs sharp staccato
• Dissonant but beautiful

JAN JELINEK 'LOOP FINDING JAZZ RECORDS' - THE MICRO-LOOP AESTHETIC:
• EXTREME MICRO-LOOPING - trademark stuttering, particulate textures
• HEAVY FILTERING AND FORMANT SHIFTING on tiny fragments
• LOOP-FINDING ASPECT - engine detects/extracts micro-loops from input
• VINYL CRACKLE/NOISE INTEGRATION - organic texture layer
• Incredibly refined, stuttering textures
• CRITICAL: Micro-loops maintain coherence - not formless blips
• Some dust specks allowed, but particulation has musical meaning
• Sparse, minimal, lowercase aesthetics
• Lots of space between sounds

KHC (SAN) - TAPE/ELECTROACOUSTIC:
• Organic fragmentation
• Granular but NOT a blur
• Each fragment audible and distinct
• Stuttering, cascading, weaving

2.2 Visual & Conceptual Metaphors
----------------------------------
• DNA helix rotating and undulating
• Flocks of birds forming/fragmenting
• Braiding, weaving, cascading
• Throbbing, breathing movement
• Stippled pointillism - Morse code dots, not blurs
• Audio-reactive visualizer patterns rendered as sound

2.3 Fragment Size Philosophy
-----------------------------

BALANCE BETWEEN MICRO AND COHERENT:

TOO SMALL (avoid):
• <5ms: Formless clicks, no tonal content
• Pure specks of noise
• No recognizable relationship to source material

SWEET SPOT (target):
• 20-50ms (micro): Percussive attacks, col legno strikes
  - Still contains attack transient character
  - Recognizable as 'struck' or 'plucked'
  - Retains some spectral identity of source
• 50-150ms (short): Staccato bow strokes
  - Clear pitch/timbre relationship to input
  - 'Musical' fragment, not just noise
• 150-400ms (medium): Sustained phrases
  - Clear melodic/harmonic content
  - Phrase-like quality

JELINEK PRINCIPLE:
Micro doesn't mean microscopic. A 30ms fragment of a guitar note still 
sounds like 'guitar' - it's the attack, the pick scrape, the string 
resonance. That's the coherence we're preserving. The magic is in the 
PATTERN of how these recognizable micro-events are arranged, not in 
reducing them to pure abstraction.

VISUAL METAPHOR:
• DNA helix rotating and undulating
• Flocks of birds forming/fragmenting
• Braiding, weaving, cascading
• Throbbing, breathing movement

================================================================================<br>
3. TECHNICAL BREAKDOWN<br>
================================================================================<br>

3.1 Fragment Capture Strategy
------------------------------

PROBLEM WITH ORIGINAL APPROACH:
Random grain triggering creates chaos with no voice character distinction 
and doesn't follow phrasing.

NEW APPROACH - 'EXCITATION PROFILING':
When onset detected, analyze the attack characteristics:
• Rise Time: <5ms = percussive, >20ms = bowed
• Spectral Centroid: High = bright/ponticello, Low = dark/tasto
• Noisiness: High = scratchy bow, Low = pure tone
• Amplitude: Loud = strike, Quiet = whisper

Capture multiple fragment sizes simultaneously:
• Micro (20-50ms): Percussive clicks, col legno strikes
• Short (50-150ms): Staccato bow strokes
• Medium (150-400ms): Sustained tones, phrases

3.2 Voice Assignment & Behavior
--------------------------------

Instead of 12 generic voices, create voice archetypes based on fragment 
characteristics:

A. PERCUSSIVE VOICES (fast attack fragments)
• Behavior: Sharp, staccato, clustered
• Pitch: Microtonal spread (±25-75 cents)
• Rhythm: Syncopated, irregular bursts
• Decay: Fast (0.5-1.5s)
• Pan: Wide stereo jumps

B. BOWED VOICES (slow attack fragments)
• Behavior: Sustained, legato, overlapping
• Pitch: Wider spread (±50-150 cents)
• Rhythm: Long phrases, smooth
• Decay: Slow (3-6s)
• Pan: Gradual stereo movement

C. SCRATCHY VOICES (noisy fragments)
• Behavior: Sul ponticello, near-bridge
• Pitch: Extreme spread (±100-200 cents)
• Rhythm: Tremolo-like repetition
• Decay: Medium (1.5-3s)
• Pan: Circular motion

3.3 The 'Weaving' Algorithm
----------------------------

GOAL: Voices cascade and braid, not just play randomly.

FIBONACCI-BASED CASCADE TIMING:
Voice 1 triggers at: t = 0ms
Voice 2 triggers at: t = 89ms (Fib: 89)
Voice 3 triggers at: t = 233ms (Fib: 144 + 89)
Voice 4 triggers at: t = 466ms (Fib: 233 + 233)
Voice 5 triggers at: t = 699ms (Fib: 233 + 466)

LINEAR FEEDBACK SHIFT REGISTER (LFSR) EVOLUTION:
• Each cascade cycle generates new LFSR seed
• Shifts Fibonacci timings: t_new = t * (0.9 + LFSR * 0.2)
• Creates organic variation, not robotic repetition

GOLDEN RATIO PANNING:
Pan positions follow φ (phi): Voice N pan = ((N * φ) mod 1) * 2 - 1
Creates natural stereo distribution that feels alive.

3.4 Bucket Brigade Decay
-------------------------

PROGRESSIVE DEGRADATION:
Each voice generation gets more degraded:
• Generation 0 (original): Bright, clear
• Generation 1 (1st echo): -3dB, LPF @ 8kHz
• Generation 2 (2nd echo): -6dB, LPF @ 4kHz, detune -10¢
• Generation 3 (3rd echo): -9dB, LPF @ 2kHz, detune -25¢
• Generation 4+: Continues until inaudible

IMPLEMENTATION FORMULA:
• Cutoff freq: 8000 * (0.5 ^ generation) Hz
• Detune: generation * -15 cents
• Level: 0.8 ^ generation

Creates foggy, murky degradation like analog delays.

3.5 Cluster Behavior
--------------------

Microtonal clusters need to feel like one gestalt voice.

CLUSTERING RULES:
1. Choose root pitch from fragment's detected pitch
2. Generate cluster: [root, root+50¢, root+125¢, root+175¢]
3. Voice coupling: Clustered voices share decay envelope
4. Amplitude modulation: Slow LFO (0.1-0.5 Hz) to cluster as unit
5. Spectral coherence: All cluster voices use similar filter movement

RESULT: Cluster sounds like one shimmering, dissonant chord.

3.6 Cascading Density Control
------------------------------

Track input dynamics over time for breathing, undulating movement:

density_history = {last 5 seconds of amplitude}
current_density = weighted_average(density_history)
target_density = current_density * response_curve
actual_density += (target - actual) * smoothing

CASCADE SIZE BY AMPLITUDE:
• Quiet: 2-4 voices (sparse)
• Medium: 6-8 voices (textured)
• Loud: 10-12 voices (dense cluster)

3.7 Micro-Loop Detection (NEW - Jelinek Technique)
---------------------------------------------------

LOOP-FINDING ENGINE CONCEPT:
• Analyze captured fragments for repeating patterns
• Extract loops within loops (2-10ms repeating cycles)
• These become 'stutter points' - hyper-focused micro-repetitions
• Formant shift + heavy filtering on these micro-loops
• Creates Jelinek's signature 'vinyl stuck in a groove' texture

IMPLEMENTATION APPROACH:
• When fragment captured, look for zero-crossings indicating period
• If period detected < 15ms, mark fragment as 'loop-capable'
• These fragments get special treatment: tight looping + heavy processing
• Mix 10-20% of these micro-loops into the particle stream

COHERENCE PRESERVATION:
• Even 2-10ms loops retain some spectral character of source
• The 'stuck groove' effect is musical because it's a real slice of the input
• Not random noise, but hyper-focused repetition of a moment

================================================================================<br>
4. AUDIO-REACTIVE COORDINATE MAPPING (NEW CONCEPT)<br>
================================================================================<br>

Inspired by MilkDrop audio visualizers - treating audio processing like vertex 
shaders and texture mapping algorithms.

4.1 Core Concept: Audio as Visual Geometry
-------------------------------------------

Instead of thinking purely in terms of grain triggering, think of voices as 
'vertices' in a coordinate space that can be warped, rotated, and textured 
by mathematical functions.

KEY METAPHOR SHIFT:
• Traditional granular: Grains triggered randomly in time
• Texture mapping approach: Voices mapped to coordinates (stereo field, time, pitch)
• Equations drive movement through this space dynamically

4.2 Trigonometric Oscillations as Pattern Drivers
--------------------------------------------------

Similar to MilkDrop presets using sin(time) and cos(time) to create pulsing 
patterns, we can use trigonometric functions to guide voice behavior.

EXAMPLES OF AUDIO-REACTIVE TRIGONOMETRY:
pan(voice, t) = sin(voice_index * φ + bass * 2π)
pitch(voice, t) = root + 100 * sin(t * mid_freq + voice_phase)
density(t) = base_density * (1 + 0.5 * sin(t * 0.2 + treb * π))

WHAT THIS ACHIEVES:
• DNA helix rotation: Pan oscillates through stereo field cyclically
• Breathing texture: Density expands/contracts rhythmically
• Pitch undulation: Voices sweep up/down in coordinated patterns

INTEGRATION WITH LFSR:
The LFSR provides pseudo-random variation on top of the trigonometric base 
pattern, preventing purely mathematical repetition while maintaining organic 
coherence.

4.3 Vertex Equations for Coordinate Mapping
--------------------------------------------

Think of each voice as a 'vertex' with coordinates in multiple dimensions:

COORDINATE SPACE DIMENSIONS:
• X: Stereo pan position (-1 to 1)
• Y: Pitch (cents from root)
• Z: Time delay (cascade offset in ms)
• W: Amplitude (voice level)

VERTEX TRANSFORMATION EXAMPLE:
// Spiral motion through coordinate space
radius = 0.5 + 0.3 * amplitude
angle = voice_index * φ + time * rotation_speed
x = radius * cos(angle)  // Pan position
y = radius * sin(angle) * 200  // Pitch offset in cents
z = (angle / 2π) * 1000  // Time delay in ms

This creates a spiral pattern where voices rotate through the stereo field 
while simultaneously sweeping pitch and cascading in time - the DNA helix 
in action!

4.4 Warping and Distortion via Spectral Processing
---------------------------------------------------

The 'texture mapping' of voices can be distorted using spectral effects to 
create the watery, glassy character we're after.

SPECTRAL WARPING TECHNIQUES:

1. VOCODER AS TEXTURE FILTER (BROWSER GRAINS APPROACH):
• 8-band vocoder with mostly low values (0-30%)
• 1-2 bands with higher values (60-90%) as 'resonance peaks'
• Creates watery, filtered character with emphasized formants
• Example config: [20%, 15%, 10%, 80%, 25%, 15%, 10%, 5%]

2. FORMANT SHIFTING AS COORDINATE TRANSFORM:
• Shift formants up/down based on voice position in coordinate space
• Voices at extremes of stereo field get shifted formants
• Creates spatial coherence: left = darker, right = brighter
• formant_shift(voice) = pan_position * 400 cents

3. RING MODULATION AS TEXTURE DISTORTION:
• Ring mod frequency follows trigonometric pattern
• Adds metallic, glassy overtones
• Modulation depth audio-reactive to input brightness
• ring_freq(t) = 200 + 300 * sin(t * 0.5 + spectral_brightness * π)

4.5 Time Domain Warping: The LFO as Pattern Clock
--------------------------------------------------

Instead of fixed cascade timing, use LFOs as 'clocks' that drive the 
sequence pattern.

PATTERN CLOCK CONCEPT:
• LFO controls the rate at which we advance through the cascade sequence
• Faster LFO = voices trigger more rapidly (compressed cascade)
• Slower LFO = voices spread out in time (stretched cascade)

IMPLEMENTATION:
// LFO-driven timing instead of fixed Fibonacci
lfo_phase = sin(time * lfo_rate) * 0.5 + 0.5  // 0 to 1
timing_mult = 0.5 + lfo_phase * 2  // 0.5x to 2.5x speed
voice_delay[i] = fibonacci[i] * timing_mult

This creates breathing cascade patterns where voices compress together and 
stretch apart cyclically, driven by audio amplitude or an independent 
modulation source.

4.6 Motion Equations: Directional Movement Patterns
----------------------------------------------------

Voices don't just exist in space - they move through it with velocity and 
direction.

VELOCITY VECTORS:
Each voice has: position, velocity, acceleration
voice.pan += voice.pan_velocity * dt
voice.pan_velocity += voice.pan_accel * dt

ATTRACTION/REPULSION FORCES:
• Voices attract to center when amplitude is high (clustering)
• Voices repel from each other when too close (avoid masking)
• Creates organic flocking behavior like starlings

force_to_center = (0 - voice.pan) * cluster_strength
force_from_neighbors = sum(repulsion / distance^2)
voice.pan_accel = force_to_center + force_from_neighbors

4.7 Integration with Existing Architecture
-------------------------------------------

These coordinate mapping concepts can be layered on top of the existing 
cascade system:

HYBRID APPROACH:
1. Fibonacci + LFSR still determines base cascade timing
2. Trigonometric functions modulate those timings
3. Golden ratio panning becomes basis for spiral motion equations
4. Bucket brigade degradation stays as-is (simulates texture LOD)
5. Vocoder/ring mod/formant become coordinate-aware
