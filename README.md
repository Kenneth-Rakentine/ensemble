# ensemble
Monome Norns Script: A living, breathing string ensemble that transforms your playing into cascading, weaving voices 

ENSEMBLE / POLYMORPHIA - PROJECT STATUS DOCUMENT
================================================
Version: 2.2.0
Date: February 6, 2026
Status: WORKING - Phantom Limb dispersion + smoothing

================================================================================
TECHNICAL  
================================================================================

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

================================================================================
CURRENT ARCHITECTURE (v2.1.0)
================================================================================

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

================================================================================
PARAMETER MAPPING
================================================================================

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

================================================================================
SONIC GOALS (from original blueprint)
================================================================================

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

================================================================================
WHAT'S WORKING WELL
================================================================================

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

================================================================================
FILE STRUCTURE
================================================================================

dust/code/ensemble/
├── ensemble.lua
└── lib/
    └── Engine_Polymorphia.sc

================================================================================
SUPERCOLLIDER RULES FOR THIS SYSTEM
================================================================================

1. All `var` declarations MUST be at TOP of function, before any code
2. Use BufRd.ar, NOT GrainBuf.ar (GrainBuf broken on this system)
3. Phasor.ar for position scanning within grain
4. EnvGen.ar with Env.perc or custom Env for amplitude shaping
5. RecordBuf.ar with run parameter for conditional recording
6. Use Dust.kr for irregular timing (more organic than Impulse.kr)

================================================================================
UI STYLE
================================================================================

- Minimal: node visualization as main display
- 8 nodes with connection lines when close
- Status top left ("ACTIVE"/"STANDBY")
- Blend indicator top right (DUST/MIX/MASS)
- Input meter bottom left
- Help text very dim at bottom
- Nodes stay within bounds, gentle pull toward center

================================================================================
INFLUENCES / REFERENCES
================================================================================

- Penderecki "Polymorphia" - microtonal clusters, sul ponticello, col legno
- Jonny Greenwood "Proven Lands" - percussive strings, aleatoric lines
- Jan Jelinek "Loop Finding Jazz Records" - micro-loops, stuttering textures
- KHC (San) - organic fragmentation, distinct fragments
- Discomfort Designs "Phantom Limb" - random delay dispersion, lo-fi textures

================================================================================

================================================================================

--

##OLD VERSION
--

================================================================================
1. THE VISION
================================================================================

A living, breathing string ensemble that transforms your playing into cascading, 
weaving voices using extended techniques - like watching a murmuration of 
starlings form DNA helixes, or an audio-reactive visualizer rendered as sound.

================================================================================
1A. THE PARTICLES → SWARM → MASS CONTINUUM
================================================================================

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

================================================================================
2. SONIC CHARACTER & INFLUENCES
================================================================================

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

================================================================================
3. TECHNICAL BREAKDOWN
================================================================================

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

================================================================================
4. AUDIO-REACTIVE COORDINATE MAPPING (NEW CONCEPT)
================================================================================

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
