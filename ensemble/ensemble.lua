-- ensemble.lua
-- ENSEMBLE v2.2.0 - Penderecki String Texture Generator
-- K2: Toggle, K3: Clear buffer
-- E1: Blend, E2: Tension, E3: Persistence

engine.name = "Polymorphia"

local active = false
local blend = 0.5
local tension = 0.4
local in_amp = 0

local nodes = {}
local NUM_NODES = 8
local phi = 1.618033988749

local lfo_phase = 0
local drift_phase = 0

function init()
  print("")
  print("========================================")
  print("ENSEMBLE v2.2.0 - Polymorphia")
  print("Phantom Limb Dispersion + Smoothing")
  print("========================================")

  for i = 1, NUM_NODES do
    nodes[i] = {
      x = 30 + math.cos(i * phi) * 25 + math.random() * 40,
      y = 28 + math.sin(i * phi * 1.3) * 14,
      vx = (math.random() - 0.5) * 0.2,
      vy = (math.random() - 0.5) * 0.15,
      size = 2 + math.random() * 1.5,
      active = false
    }
  end

  params:add_separator("ENSEMBLE")

  params:add_control("blend", "Blend (DUST<->MASS)", controlspec.new(0, 1, "lin", 0.01, 0.5))
  params:set_action("blend", function(x)
    blend = x
    engine.blend(x)
  end)

  params:add_control("tension", "Tension", controlspec.new(0, 1, "lin", 0.01, 0.4))
  params:set_action("tension", function(x)
    tension = x
    engine.tension(x)
  end)

  params:add_control("evolve", "Evolve", controlspec.new(0, 1, "lin", 0.01, 0.5))
  params:set_action("evolve", function(x) engine.evolve(x) end)

  params:add_control("movement", "Movement", controlspec.new(0, 1, "lin", 0.01, 0.6))
  params:set_action("movement", function(x) engine.movement(x) end)

  params:add_separator("MASS LAYER")

  params:add_control("mass_gain", "MASS Gain", controlspec.new(0, 1, "lin", 0.01, 0.58))
  params:set_action("mass_gain", function(x) engine.massGain(x) end)

  params:add_control("mass_density", "MASS Density", controlspec.new(0.5, 4, "lin", 0.1, 1.5))
  params:set_action("mass_density", function(x) engine.massDensity(x) end)

  params:add_control("mass_size", "MASS Grain Size", controlspec.new(2, 8, "lin", 0.1, 4))
  params:set_action("mass_size", function(x) engine.massSize(x) end)

  params:add_separator("DUST LAYER")

  params:add_control("dust_gain", "DUST Gain", controlspec.new(0, 1, "lin", 0.01, 0.50))
  params:set_action("dust_gain", function(x) engine.dustGain(x) end)

  params:add_control("dust_density", "DUST Density", controlspec.new(3, 25, "lin", 1, 10))
  params:set_action("dust_density", function(x) engine.dustDensity(x) end)

  params:add_control("dust_size", "DUST Grain Size", controlspec.new(0.02, 0.12, "lin", 0.005, 0.065))
  params:set_action("dust_size", function(x) engine.dustSize(x) end)

  params:add_control("disperse", "Disperse (Phantom)", controlspec.new(0, 1, "lin", 0.01, 0.6))
  params:set_action("disperse", function(x) engine.disperse(x) end)

  params:add_separator("INPUT")

  params:add_control("input_gain", "Input Gain", controlspec.new(0.5, 3, "lin", 0.1, 1.5))
  params:set_action("input_gain", function(x) engine.inGain(x) end)

  params:add_control("monitor", "Monitor Level", controlspec.new(0, 0.8, "lin", 0.01, 0.2))
  params:set_action("monitor", function(x) engine.monitorLevel(x) end)

  params:add_control("persistence", "Persistence", controlspec.new(0.8, 0.98, "lin", 0.01, 0.93))
  params:set_action("persistence", function(x) engine.preLevel(x) end)

  params:bang()

  local amp_poll = poll.set("amp_in_l")
  amp_poll.time = 0.05
  amp_poll.callback = function(val)
    in_amp = val
    if active and val > 0.04 then
      local n = math.random(1, NUM_NODES)
      nodes[n].active = true
      nodes[n].size = 2 + val * 3
    end
  end
  amp_poll:start()

  clock.run(function()
    while true do
      clock.sleep(1/25)
      update_visualization()
      redraw()
    end
  end)

  clock.run(function()
    clock.sleep(0.5)
    engine.inGain(1.5)
    engine.monitorLevel(0.2)
    engine.blend(0.5)
    engine.tension(0.4)
    engine.evolve(0.5)
    engine.disperse(0.6)
    engine.movement(0.6)
    engine.preLevel(0.93)
    print(">>> Startup complete")
  end)
end

function update_visualization()
  lfo_phase = lfo_phase + 0.006
  drift_phase = drift_phase + 0.004

  for i, node in ipairs(nodes) do
    node.x = node.x + node.vx
    node.y = node.y + node.vy

    node.x = node.x + math.sin(lfo_phase + i * 0.5) * 0.12
    node.y = node.y + math.cos(drift_phase + i * 0.7) * 0.08

    local margin = 10
    if node.x < margin then
      node.x = margin
      node.vx = math.abs(node.vx) * 0.7
    end
    if node.x > 118 then
      node.x = 118
      node.vx = -math.abs(node.vx) * 0.7
    end
    if node.y < margin then
      node.y = margin
      node.vy = math.abs(node.vy) * 0.7
    end
    if node.y > 48 then
      node.y = 48
      node.vy = -math.abs(node.vy) * 0.7
    end

    node.vx = node.vx + (64 - node.x) * 0.0003
    node.vy = node.vy + (28 - node.y) * 0.0003

    if node.active and math.random() < 0.025 then
      node.active = false
    end

    node.size = node.size * 0.993
    if node.size < 1.5 then node.size = 1.5 end

    if math.random() < 0.015 then
      node.vx = node.vx + (math.random() - 0.5) * 0.08
      node.vy = node.vy + (math.random() - 0.5) * 0.06
    end

    node.vx = node.vx * 0.995
    node.vy = node.vy * 0.995
  end
end

function key(n, z)
  if z == 0 then return end

  if n == 2 then
    active = not active
    if active then
      engine.record(1)
      engine.grainGate(1)
      print(">>> ACTIVE")
    else
      engine.grainGate(0)
      engine.record(0)
      print(">>> STANDBY")
    end
  elseif n == 3 then
    engine.clearBuffer(1)
    for i, node in ipairs(nodes) do
      node.active = false
      node.size = 2
    end
    print(">>> Buffer cleared")
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
    local p = params:get("persistence") + d * 0.005
    params:set("persistence", util.clamp(p, 0.8, 0.98))
  end
end

function redraw()
  screen.clear()

  screen.level(2)
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

  for i, node in ipairs(nodes) do
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

  screen.level(12)
  screen.move(2, 7)
  screen.text(active and "ACTIVE" or "STANDBY")

  screen.level(7)
  screen.move(100, 7)
  local blend_text = blend < 0.35 and "DUST" or (blend > 0.65 and "MASS" or "MIX")
  screen.text(blend_text)

  screen.level(3)
  screen.rect(2, 52, 18, 3)
  screen.stroke()
  screen.level(10)
  screen.rect(2, 52, in_amp * 18, 3)
  screen.fill()

  screen.level(2)
  screen.move(2, 62)
  screen.text("K2:go K3:clr E1:bld E2:tns E3:per")

  screen.update()
end

function cleanup()
  engine.grainGate(0)
  engine.record(0)
end
