-- ensemble.lua
-- ENSEMBLE v3.0.0 - Penderecki String Texture Generator
-- Fresh build from blueprint vision + spectral analysis of reference
--
-- Architecture: SuperCollider (MASS+DUST grains) + Softcut (continuous texture weave)
-- The SC engine creates the orchestral organism.
-- Softcut adds a living second layer: continuous, overlapping loops that
-- slowly scan through captured material, creating ghost harmonics,
-- phantom chords, and spectral interference patterns.
--
-- K2: Toggle active, K3: Clear buffer
-- E1: Blend (DUST<>MASS), E2: Tension, E3: Texture (softcut weave)

engine.name = "Polymorphia"

sc = softcut

local PHI = 1.618033988749895
local TAU = 2 * math.pi
local BUF_LEN = 60  -- softcut buffer length

------------------------------------------------------------
-- LFSR (deterministic pseudo-random, no math.random in audio)
------------------------------------------------------------
local lfsr = 0xACE1
local function lfsr_next()
  local bit = ((lfsr >> 0) ~ (lfsr >> 2) ~ (lfsr >> 3) ~ (lfsr >> 5)) & 1
  lfsr = (lfsr >> 1) | (bit << 15)
  return lfsr / 65536
end
local function lfsr_range(lo, hi) return lo + lfsr_next() * (hi - lo) end

------------------------------------------------------------
-- STATE
------------------------------------------------------------
local active = false
local time_elapsed = 0
local current_amp, smoothed_amp, peak_amp = 0, 0, 0
local positions = {0, 0}  -- softcut write head positions

-- Params
local blend = 0.5
local tension = 0.5
local texture_amount = 0.15  -- softcut weave amount (default subtle)

------------------------------------------------------------
-- LFO SYSTEM
-- Multiple slow, incommensurate LFOs create organic drift.
-- None are tempo-locked. Rates chosen to avoid alignment.
------------------------------------------------------------
local lfo = {
  glacier = 0,   -- ~180s cycle - macro breathing
  tide    = 0,   -- ~73s cycle  - density swell
  drift   = 0,   -- ~47s cycle  - position scanning
  swirl   = 0,   -- ~29s cycle  - pan movement
  shimmer = 0,   -- ~19s cycle  - brightness/filter
  ripple  = 0,   -- ~11s cycle  - micro grain timing
}

local function update_lfos(dt)
  lfo.glacier = (lfo.glacier + dt * 0.035) % TAU   -- 180s
  lfo.tide    = (lfo.tide    + dt * 0.086) % TAU   -- 73s
  lfo.drift   = (lfo.drift   + dt * 0.134) % TAU   -- 47s
  lfo.swirl   = (lfo.swirl   + dt * 0.217) % TAU   -- 29s
  lfo.shimmer = (lfo.shimmer + dt * 0.331) % TAU   -- 19s
  lfo.ripple  = (lfo.ripple  + dt * 0.571) % TAU   -- 11s
end

------------------------------------------------------------
-- SOFTCUT TEXTURE WEAVE
--
-- Instead of discrete grain triggers, the softcut voices
-- run CONTINUOUSLY as slow-scanning loopers. Each voice
-- reads from a different region of the buffer at slightly
-- different rates, creating spectral interference and
-- phantom harmonics when they overlap.
--
-- Think: 4 string players each bowing the same passage
-- but slightly offset in time, creating a cluster.
--
-- Voice allocation:
--   1-2: Recording (stereo input capture)
--   3-6: Texture weave (continuous overlapping readers)
------------------------------------------------------------
local WEAVE_VOICES = {3, 4, 5, 6}

-- Each weave voice has its own state for continuous modulation
local weave = {
  {offset = 2.0,  scan_rate = 0.003, pan_phase = 0},      -- deep reader
  {offset = 4.5,  scan_rate = 0.005, pan_phase = PHI},     -- mid reader
  {offset = 8.0,  scan_rate = 0.002, pan_phase = PHI*2},   -- far reader
  {offset = 1.2,  scan_rate = 0.007, pan_phase = PHI*3},   -- close reader
}

-- Continuous modulation of the weave voices (called every frame)
local function update_weave()
  if not active then return end
  if texture_amount < 0.01 then return end
  
  local write_pos = positions[1]
  if write_pos < 2 then return end
  
  for i, w in ipairs(weave) do
    local v = WEAVE_VOICES[i]
    
    -- Each voice reads at a slowly drifting offset behind the write head
    -- The offset itself modulates, creating the "scanning" effect
    local base_offset = w.offset
    local drift_mod = math.sin(lfo.drift + i * PHI) * 0.4 + 1.0  -- 0.6x to 1.4x
    local actual_offset = base_offset * drift_mod
    
    -- Target position: behind write head by offset
    local target_pos = write_pos - actual_offset
    if target_pos < 1 then target_pos = target_pos + BUF_LEN end
    
    -- Loop window size breathes with glacier LFO
    -- Longer windows = smeared, shorter = more grain-like
    local window_base = 0.8 + blend * 2.0  -- DUST-biased = shorter windows, MASS = longer
    local window_mod = math.sin(lfo.glacier + i * 1.1) * 0.3 + 1.0
    local window = window_base * window_mod
    
    sc.loop_start(v, target_pos)
    sc.loop_end(v, target_pos + window)
    
    -- Level: modulated by multiple LFOs for breathing quality
    -- The glacier LFO creates long-form swells (the "breathing")
    -- The tide LFO creates medium-term density changes
    -- Input amplitude adds reactivity
    local breath = math.sin(lfo.glacier + i * PHI * 0.7) * 0.5 + 0.5  -- 0 to 1
    local swell = math.sin(lfo.tide + i * 1.7) * 0.3 + 0.7             -- 0.4 to 1.0
    local amp_react = 0.5 + smoothed_amp * 2.0                          -- reactive boost
    amp_react = math.min(amp_react, 1.5)
    
    local level = texture_amount * breath * swell * amp_react * 0.45
    sc.level(v, level)
    
    -- Pan: golden ratio offsets + slow swirl modulation
    local base_pan = ((i * PHI) % 1) * 2 - 1  -- golden ratio distribution
    local pan_mod = math.sin(lfo.swirl + w.pan_phase) * 0.35
    sc.pan(v, util.clamp(base_pan + pan_mod, -1, 1))
    
    -- Filter: shimmer LFO modulates cutoff for spectral movement
    -- Lower blend (DUST) = brighter, higher blend (MASS) = darker
    local base_fc = 2000 + (1 - blend) * 3000  -- 2000-5000 Hz
    local fc_mod = math.sin(lfo.shimmer + i * 2.3) * 800
    local fc = math.max(500, base_fc + fc_mod)
    sc.post_filter_fc(v, fc)
    
    -- Rate: stays at 1.0 (preserve pitch) with VERY subtle drift
    -- ±0.3% = ±5 cents, creates the microtonal cluster beating
    local micro_drift = math.sin(lfo.ripple + i * PHI * 2) * 0.003
    sc.rate(v, 1.0 + micro_drift)
  end
end

------------------------------------------------------------
-- FLOURISH SYSTEM
-- Rare, self-contained rhythmic gestures that emerge from
-- the texture and dissolve back into it. These are the
-- "stress fractures" in the mass - sudden density spikes
-- that sweep from sparse to rapid and back.
------------------------------------------------------------
local flourish = {
  active = false,
  cooldown_until = 0,
}

local function run_flourish()
  if flourish.active then return end
  if texture_amount < 0.05 then return end
  flourish.active = true
  
  clock.run(function()
    -- A flourish temporarily makes voices scan faster and louder
    -- then subsides. Like a gust of wind through the strings.
    local duration = lfsr_range(2.0, 4.0)
    local peak_time = duration * lfsr_range(0.3, 0.6)  -- when the peak hits
    local steps = math.floor(duration / 0.06)
    
    for step = 1, steps do
      if not active then break end
      
      local progress = step / steps  -- 0 to 1
      local elapsed = progress * duration
      
      -- Intensity curve: rises to peak, then fades
      local intensity
      if elapsed < peak_time then
        intensity = (elapsed / peak_time) ^ 0.7  -- concave rise
      else
        intensity = ((duration - elapsed) / (duration - peak_time)) ^ 1.5  -- convex fall
      end
      
      -- During flourish, boost texture level and scan speed
      for i, w in ipairs(weave) do
        local v = WEAVE_VOICES[i]
        -- Temporarily narrow the loop window (more grain-like)
        local narrow = 0.1 + (1 - intensity) * 0.7  -- tighter at peak
        local pos = positions[1] - w.offset * 0.5
        if pos < 1 then pos = pos + BUF_LEN end
        sc.loop_start(v, pos)
        sc.loop_end(v, pos + narrow)
        
        -- Boost level during flourish
        local boost = 1.0 + intensity * 1.5
        local base_level = texture_amount * 0.45
        sc.level(v, base_level * boost)
      end
      
      clock.sleep(0.06)
    end
    
    -- Restore normal state (update_weave will take over next frame)
    flourish.active = false
    flourish.cooldown_until = time_elapsed + lfsr_range(25, 60)
  end)
end

local function maybe_trigger_flourish()
  if not active then return end
  if flourish.active then return end
  if time_elapsed < flourish.cooldown_until then return end
  if texture_amount < 0.05 then return end
  
  -- Probability: low base, boosted by input dynamics
  local prob = 0.002 + smoothed_amp * 0.01
  if lfsr_next() < prob then
    run_flourish()
  end
end

------------------------------------------------------------
-- SOFTCUT SETUP
------------------------------------------------------------
local function setup_softcut()
  sc.buffer_clear()
  audio.level_adc_cut(1)
  audio.level_cut(1)
  
  -- Recording voices (1-2): continuous stereo capture
  for i = 1, 2 do
    sc.enable(i, 1)
    sc.buffer(i, i)
    sc.level(i, 0)           -- silent output (SC handles monitor)
    sc.rec_level(i, 1.0)
    sc.pre_level(i, 0.85)    -- high feedback: old material persists
    sc.loop(i, 1)
    sc.loop_start(i, 1)
    sc.loop_end(i, BUF_LEN)
    sc.position(i, 1)
    sc.play(i, 1)
    sc.rec(i, 0)             -- starts off, enabled on K2
    sc.rate(i, 1)
    sc.fade_time(i, 0.02)
    sc.pre_filter_dry(i, 1)  -- CRITICAL: required for output
  end
  -- Stereo routing
  sc.level_input_cut(1, 1, 1); sc.level_input_cut(2, 1, 0)
  sc.level_input_cut(1, 2, 0); sc.level_input_cut(2, 2, 1)
  
  -- Weave voices (3-6): continuous overlapping readers
  for i, v in ipairs(WEAVE_VOICES) do
    sc.enable(v, 1)
    sc.buffer(v, 1)          -- all read from buffer 1
    sc.level(v, 0)           -- starts silent, update_weave controls level
    sc.loop(v, 1)
    sc.loop_start(v, 1)
    sc.loop_end(v, 3)
    sc.position(v, 1 + i)
    sc.play(v, 1)            -- always playing (continuous)
    sc.rec(v, 0)
    sc.rate(v, 1)
    sc.fade_time(v, 0.1)     -- smooth loop transitions
    sc.level_slew_time(v, 0.15)  -- smooth level changes
    sc.pan_slew_time(v, 0.2)    -- smooth pan drift
    sc.rate_slew_time(v, 0.5)   -- smooth rate changes
    
    -- Filter: LP + dry blend for warmth
    -- Voices alternate between warmer and glassier
    sc.pre_filter_dry(v, 1)
    if i <= 2 then
      -- Warmer voices
      sc.post_filter_dry(v, 0.5)
      sc.post_filter_lp(v, 0.5)
      sc.post_filter_fc(v, 3500)
      sc.post_filter_rq(v, 1.5)
    else
      -- Glassier voices (slight HP for sul ponticello brightness)
      sc.post_filter_dry(v, 0.45)
      sc.post_filter_lp(v, 0.35)
      sc.post_filter_hp(v, 0.2)
      sc.post_filter_fc(v, 4500)
      sc.post_filter_rq(v, 2.0)
    end
  end
  
  -- Phase polling for write head tracking
  sc.event_phase(function(i, x) positions[i] = x end)
  sc.poll_start_phase()
end

------------------------------------------------------------
-- CLEAR
------------------------------------------------------------
local function clear_all()
  sc.buffer_clear()
  engine.clearBuffer(1)
  flourish.active = false
  for _, v in ipairs(WEAVE_VOICES) do
    sc.level(v, 0)
  end
  print(">>> ALL CLEARED")
end

------------------------------------------------------------
-- NODE VISUALIZATION
-- Nodes represent the organism: connected, moving, breathing
------------------------------------------------------------
local nodes = {}
local NUM_NODES = 8

local function init_nodes()
  for i = 1, NUM_NODES do
    nodes[i] = {
      x = 30 + math.cos(i * PHI) * 25 + math.random() * 40,
      y = 28 + math.sin(i * PHI * 1.3) * 14,
      vx = (math.random() - 0.5) * 0.2,
      vy = (math.random() - 0.5) * 0.15,
      size = 2 + math.random() * 1.5,
      active = false,
    }
  end
end

local function update_nodes()
  for i, node in ipairs(nodes) do
    node.x = node.x + node.vx
    node.y = node.y + node.vy
    
    -- LFO drift
    node.x = node.x + math.sin(lfo.swirl + i * 0.5) * 0.1
    node.y = node.y + math.cos(lfo.drift + i * 0.7) * 0.06
    
    -- Bounds
    if node.x < 10 then node.x = 10; node.vx = math.abs(node.vx) * 0.7 end
    if node.x > 118 then node.x = 118; node.vx = -math.abs(node.vx) * 0.7 end
    if node.y < 10 then node.y = 10; node.vy = math.abs(node.vy) * 0.7 end
    if node.y > 48 then node.y = 48; node.vy = -math.abs(node.vy) * 0.7 end
    
    -- Center pull
    node.vx = node.vx + (64 - node.x) * 0.0003
    node.vy = node.vy + (28 - node.y) * 0.0003
    
    -- Decay
    if node.active and math.random() < 0.025 then node.active = false end
    node.size = node.size * 0.993
    if node.size < 1.5 then node.size = 1.5 end
    
    -- Random impulse
    if math.random() < 0.015 then
      node.vx = node.vx + (math.random() - 0.5) * 0.08
      node.vy = node.vy + (math.random() - 0.5) * 0.06
    end
    
    node.vx = node.vx * 0.995
    node.vy = node.vy * 0.995
  end
  
  -- Activate nodes on input
  if active and smoothed_amp > 0.04 then
    local n = math.random(1, NUM_NODES)
    nodes[n].active = true
    nodes[n].size = 2 + smoothed_amp * 3
  end
  
  -- Flourish agitates nodes
  if flourish.active then
    local n = math.random(1, NUM_NODES)
    nodes[n].active = true
    nodes[n].size = math.min(nodes[n].size + 0.4, 4.5)
    nodes[n].vx = nodes[n].vx + (math.random() - 0.5) * 0.2
    nodes[n].vy = nodes[n].vy + (math.random() - 0.5) * 0.15
  end
end

------------------------------------------------------------
-- INPUT
------------------------------------------------------------
local function process_input(amp)
  current_amp = amp
  smoothed_amp = smoothed_amp * 0.92 + amp * 0.08
  peak_amp = amp > peak_amp and amp or peak_amp * 0.994
end

------------------------------------------------------------
-- PARAMS
------------------------------------------------------------
local function setup_params()
  params:add_separator("ENSEMBLE")
  
  params:add_control("blend", "Blend (DUST<->MASS)",
    controlspec.new(0, 1, "lin", 0.01, 0.5))
  params:set_action("blend", function(x)
    blend = x
    engine.blend(x)
  end)
  
  params:add_control("tension", "Tension",
    controlspec.new(0, 1, "lin", 0.01, 0.5))
  params:set_action("tension", function(x)
    tension = x
    engine.tension(x)
  end)
  
  params:add_control("texture", "Texture (weave)",
    controlspec.new(0, 1, "lin", 0.01, 0.15))
  params:set_action("texture", function(x) texture_amount = x end)
  
  params:add_control("evolve", "Evolve",
    controlspec.new(0, 1, "lin", 0.01, 0.6))
  params:set_action("evolve", function(x) engine.evolve(x) end)
  
  params:add_control("movement", "Movement",
    controlspec.new(0, 1, "lin", 0.01, 0.7))
  params:set_action("movement", function(x) engine.movement(x) end)
  
  params:add_separator("SC ENGINE")
  
  params:add_control("mass_gain", "MASS Gain",
    controlspec.new(0, 1, "lin", 0.01, 1.0))
  params:set_action("mass_gain", function(x) engine.massGain(x) end)
  
  params:add_control("dust_gain", "DUST Gain",
    controlspec.new(0, 1, "lin", 0.01, 1.0))
  params:set_action("dust_gain", function(x) engine.dustGain(x) end)
  
  params:add_control("disperse", "Disperse (Phantom)",
    controlspec.new(0, 1, "lin", 0.01, 0.7))
  params:set_action("disperse", function(x) engine.disperse(x) end)
  
  params:add_separator("INPUT")
  
  params:add_control("input_gain", "Input Gain",
    controlspec.new(0.5, 3, "lin", 0.1, 2.0))
  params:set_action("input_gain", function(x) engine.inGain(x) end)
  
  params:add_control("monitor", "Monitor Level",
    controlspec.new(0, 1, "lin", 0.01, 0.8))
  params:set_action("monitor", function(x) engine.monitorLevel(x) end)
  
  params:add_control("persistence", "Persistence",
    controlspec.new(0.8, 0.99, "lin", 0.01, 0.96))
  params:set_action("persistence", function(x) engine.preLevel(x) end)
end

------------------------------------------------------------
-- UI
------------------------------------------------------------
function redraw()
  screen.clear()
  
  -- Node connections
  for i = 1, NUM_NODES do
    for j = i + 1, NUM_NODES do
      local n1, n2 = nodes[i], nodes[j]
      local dx = n2.x - n1.x
      local dy = n2.y - n1.y
      local dist = math.sqrt(dx * dx + dy * dy)
      if dist < 40 then
        local brightness = 1
        if n1.active or n2.active then brightness = 4 end
        if n1.active and n2.active then brightness = 9 end
        screen.level(brightness)
        screen.move(n1.x, n1.y)
        screen.line(n2.x, n2.y)
        screen.stroke()
      end
    end
  end
  
  -- Nodes
  for _, node in ipairs(nodes) do
    if node.active then
      screen.level(15)
      screen.circle(node.x, node.y, node.size)
      screen.fill()
    else
      screen.level(4)
      screen.circle(node.x, node.y, node.size * 0.7)
      screen.stroke()
    end
  end
  
  -- Status top left
  screen.level(12)
  screen.move(2, 7)
  if flourish.active then
    screen.text("FLRSH")
  else
    screen.text(active and "ACTIVE" or "STANDBY")
  end
  
  -- Blend indicator top right
  screen.level(7)
  screen.move(100, 7)
  screen.text(blend < 0.35 and "DUST" or (blend > 0.65 and "MASS" or "MIX"))
  
  -- Texture amount center top
  screen.level(5)
  screen.move(64, 7)
  screen.text_center(string.format("T:%.0f", texture_amount * 100))
  
  -- Weave voice activity (bottom left area)
  for i = 1, 4 do
    local v = WEAVE_VOICES[i]
    local x = 4 + (i - 1) * 8
    -- Brightness proportional to voice level
    local brightness = active and (3 + math.floor(texture_amount * 12)) or 2
    screen.level(brightness)
    screen.circle(x, 55, 2)
    if active and texture_amount > 0.05 then
      screen.fill()
    else
      screen.stroke()
    end
  end
  
  -- Input meter
  screen.level(3)
  screen.rect(40, 54, 78, 4)
  screen.stroke()
  screen.level(10)
  screen.rect(41, 55, smoothed_amp * 76, 2)
  screen.fill()
  
  -- Help
  screen.level(2)
  screen.move(2, 64)
  screen.text("K2:go K3:clr E1:bld E2:tns E3:tex")
  
  screen.update()
end

------------------------------------------------------------
-- CONTROLS
------------------------------------------------------------
function key(n, z)
  if z == 0 then return end
  
  if n == 2 then
    active = not active
    if active then
      engine.record(1)
      engine.grainGate(1)
      for i = 1, 2 do sc.rec(i, 1) end
      print(">>> ACTIVE")
    else
      engine.grainGate(0)
      engine.record(0)
      for i = 1, 2 do sc.rec(i, 0) end
      -- Fade weave voices to silence
      for _, v in ipairs(WEAVE_VOICES) do
        sc.level_slew_time(v, 1.0)
        sc.level(v, 0)
      end
      print(">>> STANDBY")
    end
  elseif n == 3 then
    clear_all()
  end
end

function enc(n, d)
  if n == 1 then
    blend = util.clamp(blend + d * 0.02, 0, 1)
    params:set("blend", blend)
  elseif n == 2 then
    tension = util.clamp(tension + d * 0.02, 0, 1)
    params:set("tension", tension)
  elseif n == 3 then
    texture_amount = util.clamp(texture_amount + d * 0.02, 0, 1)
    params:set("texture", texture_amount)
  end
end

------------------------------------------------------------
-- MAIN UPDATE (25 fps)
------------------------------------------------------------
local function main_update()
  local dt = 1/25
  time_elapsed = time_elapsed + dt
  update_lfos(dt)
  update_weave()
  maybe_trigger_flourish()
  update_nodes()
  redraw()
end

------------------------------------------------------------
-- INIT
------------------------------------------------------------
function init()
  print("")
  print("========================================")
  print("ENSEMBLE v3.0.0 - Polymorphia")
  print("Continuous Texture Weave")
  print("========================================")
  
  -- Flourish cooldown: first one after 30-60s
  flourish.cooldown_until = lfsr_range(30, 60)
  
  init_nodes()
  setup_params()
  setup_softcut()
  
  -- Amplitude polling
  local amp_poll = poll.set("amp_in_l")
  amp_poll.time = 0.04
  amp_poll.callback = process_input
  amp_poll:start()
  
  -- Initialize SC engine with max gains
  clock.run(function()
    clock.sleep(0.5)
    engine.inGain(2.0)
    engine.monitorLevel(0.8)
    engine.blend(0.5)
    engine.tension(0.5)
    engine.evolve(0.6)
    engine.disperse(0.7)
    engine.movement(0.7)
    engine.preLevel(0.96)
    engine.massGain(1.0)
    engine.dustGain(1.0)
    print(">>> SC Engine initialized (all gains max)")
  end)
  
  -- Main loop
  metro.init(main_update, 1/25):start()
  
  params:bang()
  print(">>> Ready")
end

function cleanup()
  engine.grainGate(0)
  engine.record(0)
  engine.massGain(1.0)
  engine.dustGain(1.0)
  sc.poll_stop_phase()
end
