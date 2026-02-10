// Engine_Polymorphia.sc v2.4.2
// Penderecki String Texture Generator
// Subtle glissando, no constant detuning
// Sparse octave-down bass voice preserved
// Works with hybrid Lua (SC + Softcut texture)

Engine_Polymorphia : CroneEngine {
	var buf;
	var recSynth;
	var massSynth;
	var dustSynth;
	var ampBus;
	var phaseBus;

	*new { arg context, doneCallback;
		^super.new(context, doneCallback);
	}

	alloc {
		buf = Buffer.alloc(context.server, context.server.sampleRate * 45, 2);
		ampBus = Bus.control(context.server, 1);
		phaseBus = Bus.control(context.server, 1);

		SynthDef(\polyRec, { |bufnum, run=0, inGain=2.0, monLevel=0.8, preLevel=0.95|
			var input, amp, phase, writePos;
			input = SoundIn.ar([0,1]) * inGain;
			amp = Amplitude.kr(Mix(input), 0.01, 0.2);
			Out.kr(ampBus, amp);
			phase = Phasor.ar(0, BufRateScale.kr(bufnum), 0, BufFrames.kr(bufnum));
			writePos = phase / BufFrames.kr(bufnum);
			Out.kr(phaseBus, A2K.kr(writePos));
			RecordBuf.ar(input, bufnum, loop: 1, run: run, preLevel: preLevel);
			Out.ar(0, input * monLevel * run);
		}).add;

		// MASS LAYER - subtle glissando, no constant detuning
		SynthDef(\polyMass, { |bufnum, gate=0, gain=1.0, blend=0.5,
			density=1.5, baseSize=4, spread=0.5, tension=0.5, evolve=0.6|

			var env, sig, numFrames, inAmp, writePos;
			var lfo1, lfo2, lfo3, evolveLfo;
			var v0, v1, v2, v3;
			var e0, e1, e2, e3;
			var p0, p1, p2, p3;
			var pos0, pos1, pos2, pos3;
			var rate0, rate1, rate2, rate3;
			var pan0, pan1, pan2, pan3;
			var size0, size1, size2, size3;
			var trig0, trig1, trig2, trig3;
			var offset0, offset1, offset2, offset3;
			var filt0, filt1, filt2, filt3;
			var phi, comb0, comb1, comb2, comb3;
			var formant1, formant2, formant3;
			var stage, shimmer;
			var gliss0, gliss1, gliss2, gliss3;
			var reverse0, reverse1, reverse2, reverse3;
			var tremolo0, tremolo1, tremolo2, tremolo3;
			var panSwoop0, panSwoop1, panSwoop2, panSwoop3;
			var bassGate;

			env = Lag.kr(gate, 0.5);
			numFrames = BufFrames.kr(bufnum);
			inAmp = In.kr(ampBus).lag(0.5);
			writePos = In.kr(phaseBus).lag(0.1);

			lfo1 = SinOsc.kr(0.035).range(0, 1);
			lfo2 = SinOsc.kr(0.025).range(0, 1);
			lfo3 = SinOsc.kr(0.04).range(0, 1);
			evolveLfo = SinOsc.kr(0.02).range(0, 1);

			phi = 1.618033988749;
			stage = (evolve * evolveLfo).clip(0, 1);

			formant1 = 400 + (lfo1 * 200) + (stage * 100);
			formant2 = 1200 + (lfo2 * 400) + (stage * 200);
			formant3 = 2400 + (lfo3 * 600) + (stage * 300);

			shimmer = SinOsc.kr(4.5 + (stage * 2)).range(1 - (stage * 0.15), 1);

			density = density * (1 + (inAmp * 0.5));

			// Voice 0 - SPARSE BASS + subtle rising glissando
			size0 = baseSize * LFNoise2.kr(0.006).range(0.9, 1.4);
			trig0 = Impulse.kr(density * 0.4);
			offset0 = LFNoise1.kr(0.015).range(0.06, 0.22);
			pos0 = ((writePos - offset0).wrap(0.01, 0.99)) * numFrames;
			// Subtle glissando: slides up to +1.5% over grain duration
			gliss0 = EnvGen.kr(Env([0, LFNoise1.kr(0.08).range(0.005, 0.015)], [size0], \lin), trig0);
			// Only drop to bass ~20% of the time
			bassGate = (LFNoise0.kr(0.05) > 0.6);
			rate0 = Select.kr(bassGate, [1.0 + gliss0, 0.5 + (gliss0 * 0.5)]);
			// Occasional reverse
			reverse0 = (LFNoise0.kr(0.06) > 0.88).lag(0.3) * -2 + 1;
			rate0 = rate0 * reverse0;
			panSwoop0 = SinOsc.kr(0.07 + (lfo1 * 0.03)).range(-0.8, 0.8);
			pan0 = panSwoop0 * spread;
			p0 = Phasor.ar(trig0, BufRateScale.kr(bufnum) * rate0, pos0, pos0 + (size0 * SampleRate.ir * rate0.abs), pos0);
			e0 = EnvGen.ar(Env([0, 0.25, 1, 1, 0], [0.6, 0.4, size0 - 1.8, 1.0], [4, 2, 0, -4]), trig0);
			tremolo0 = SinOsc.kr(3 + (stage * 2)).range(0.88, 1);
			v0 = BufRd.ar(2, bufnum, p0, loop: 1, interpolation: 4) * e0 * shimmer * tremolo0;
			filt0 = BPF.ar(v0, formant1 * 0.8, 0.45) * 1.6;
			filt0 = filt0 + (BPF.ar(v0, formant2 * 0.85, 0.35) * 0.85);
			v0 = (v0 * 0.45) + (filt0 * 0.55);
			comb0 = CombC.ar(v0, 0.06, 0.022 + (tension * 0.012), 0.45 + (tension * 0.5) + (stage * 0.3));
			v0 = v0 + (comb0 * (tension + (stage * 0.2)) * 0.25);
			v0 = LPF.ar(v0, 4500 + (lfo1 * 1500));
			v0 = Balance2.ar(v0[0], v0[1], pan0);

			// Voice 1 - subtle rising glissando (Penderecki ascending gesture)
			size1 = baseSize * LFNoise2.kr(0.008).range(0.8, 1.25);
			trig1 = Impulse.kr(density * 0.7);
			offset1 = LFNoise1.kr(0.018).range(0.08, 0.25);
			pos1 = ((writePos - offset1).wrap(0.01, 0.99)) * numFrames;
			// Subtle rising gliss: +0.5% to +2%
			gliss1 = EnvGen.kr(Env([0, LFNoise1.kr(0.1).range(0.005, 0.02)], [size1], \lin), trig1);
			reverse1 = (LFNoise0.kr(0.05) > 0.92).lag(0.25) * -2 + 1;
			rate1 = (1.0 + gliss1) * reverse1;
			panSwoop1 = SinOsc.kr(0.09 + (lfo2 * 0.04)).range(-0.7, 0.7);
			pan1 = (((1 * phi) % 1) * 2 - 1 + panSwoop1 * 0.5).clip(-1, 1) * spread;
			p1 = Phasor.ar(trig1, BufRateScale.kr(bufnum) * rate1, pos1, pos1 + (size1 * SampleRate.ir * rate1.abs), pos1);
			e1 = EnvGen.ar(Env([0, 0.3, 1, 1, 0], [0.5, 0.35, size1 - 1.6, 0.75], [4, 2, 0, -4]), trig1);
			tremolo1 = SinOsc.kr(4.5 + (stage * 2.5)).range(0.9, 1);
			v1 = BufRd.ar(2, bufnum, p1, loop: 1, interpolation: 4) * e1 * shimmer * tremolo1;
			filt1 = BPF.ar(v1, formant1 * 1.1, 0.38) * 1.4;
			filt1 = filt1 + (BPF.ar(v1, formant2, 0.3) * 0.75);
			v1 = (v1 * (0.5 - (stage * 0.1))) + (filt1 * (0.5 + (stage * 0.1)));
			comb1 = CombC.ar(v1, 0.05, 0.018 + (tension * 0.008), 0.35 + (tension * 0.45) + (stage * 0.25));
			v1 = v1 + (comb1 * (tension + (stage * 0.16)) * 0.22);
			v1 = LPF.ar(v1, 5500 + (lfo2 * 1500) - (stage * 300));
			v1 = Balance2.ar(v1[0], v1[1], pan1);

			// Voice 2 - subtle descending glissando (sul ponticello)
			size2 = baseSize * LFNoise2.kr(0.009).range(0.7, 1.15);
			trig2 = Impulse.kr(density * 0.85);
			offset2 = LFNoise1.kr(0.022).range(0.1, 0.3);
			pos2 = ((writePos - offset2).wrap(0.01, 0.99)) * numFrames;
			// Subtle falling gliss: -0.5% to -1.5%
			gliss2 = EnvGen.kr(Env([0, LFNoise1.kr(0.09).range(-0.015, -0.005)], [size2], \lin), trig2);
			reverse2 = (LFNoise0.kr(0.055) > 0.9).lag(0.2) * -2 + 1;
			rate2 = (1.0 + gliss2) * reverse2;
			panSwoop2 = SinOsc.kr(0.11 + (lfo3 * 0.05)).range(-0.9, 0.9);
			pan2 = panSwoop2 * spread;
			p2 = Phasor.ar(trig2, BufRateScale.kr(bufnum) * rate2, pos2, pos2 + (size2 * SampleRate.ir * rate2.abs), pos2);
			e2 = EnvGen.ar(Env([0, 0.2, 1, 1, 0], [0.45, 0.4, size2 - 1.5, 0.65], [4, 2, 0, -4]), trig2);
			tremolo2 = SinOsc.kr(5.5 + (stage * 3)).range(0.85, 1);
			v2 = BufRd.ar(2, bufnum, p2, loop: 1, interpolation: 4) * e2 * shimmer * tremolo2;
			filt2 = BPF.ar(v2, formant2 * 1.15, 0.32) * 1.3;
			filt2 = filt2 + (BPF.ar(v2, formant3, 0.25) * 0.55);
			v2 = (v2 * (0.45 - (stage * 0.08))) + (filt2 * (0.55 + (stage * 0.08)));
			comb2 = CombC.ar(v2, 0.05, 0.012 + (tension * 0.01), 0.38 + (tension * 0.4) + (stage * 0.28));
			v2 = v2 + (comb2 * (tension + (stage * 0.14)) * 0.2);
			v2 = LPF.ar(v2, 6500 + (lfo3 * 1200) - (stage * 400));
			v2 = Balance2.ar(v2[0], v2[1], pan2);

			// Voice 3 - random subtle glissando direction
			size3 = baseSize * LFNoise2.kr(0.011).range(0.65, 1.1);
			trig3 = Impulse.kr(density * 1.0);
			offset3 = LFNoise1.kr(0.025).range(0.12, 0.35);
			pos3 = ((writePos - offset3).wrap(0.01, 0.99)) * numFrames;
			// Random direction gliss: ±1.5%
			gliss3 = EnvGen.kr(Env([0, LFNoise1.kr(0.12).range(-0.015, 0.015)], [size3], \lin), trig3);
			reverse3 = (LFNoise0.kr(0.1) > 0.75).lag(0.15) * -2 + 1;
			rate3 = (1.0 + gliss3) * reverse3;
			panSwoop3 = LFNoise2.kr(0.08).range(-1, 1);
			pan3 = panSwoop3 * spread;
			p3 = Phasor.ar(trig3, BufRateScale.kr(bufnum) * rate3, pos3, pos3 + (size3 * SampleRate.ir * rate3.abs), pos3);
			e3 = EnvGen.ar(Env([0, 0.28, 1, 1, 0], [0.4, 0.3, size3 - 1.4, 0.7], [4, 2, 0, -4]), trig3);
			tremolo3 = SinOsc.kr(6 + (stage * 3.5)).range(0.82, 1);
			v3 = BufRd.ar(2, bufnum, p3, loop: 1, interpolation: 4) * e3 * shimmer * tremolo3;
			filt3 = BPF.ar(v3, formant1 * 1.05, 0.35) * 1.2;
			filt3 = filt3 + (BPF.ar(v3, formant3 * 1.1, 0.22) * 0.48);
			v3 = (v3 * (0.48 - (stage * 0.1))) + (filt3 * (0.52 + (stage * 0.1)));
			comb3 = CombC.ar(v3, 0.05, 0.02 + (tension * 0.007), 0.35 + (tension * 0.5) + (stage * 0.22));
			v3 = v3 + (comb3 * (tension + (stage * 0.17)) * 0.22);
			v3 = LPF.ar(v3, 5800 + (lfo1 * 1500) - (stage * 350));
			v3 = Balance2.ar(v3[0], v3[1], pan3);

			sig = (v0 + v1 + v2 + v3) * 0.38;

			// Lighter smoothing
			sig = LPF.ar(sig, 8000);
			sig = AllpassC.ar(sig, 0.05, 0.012, 0.06);
			sig = AllpassC.ar(sig, 0.05, 0.019, 0.04);

			sig = sig + (FreeVerb2.ar(sig[0], sig[1], 0.1, 0.4, 0.25) * 0.07);
			sig = sig * gain * blend * env;
			sig = (sig * 1.6).clip(-1.3, 1.3) * 0.85;
			sig = LeakDC.ar(sig);

			Out.ar(0, sig);
		}).add;

		// DUST LAYER - no pitch deviation
		SynthDef(\polyDust, { |bufnum, gate=0, gain=1.0, blend=0.5,
			density=6, baseSize=0.065, spread=0.9, disperse=0.7|

			var env, sig, numFrames, inAmp, writePos;
			var clusterLfo, swarmLfo, burstLfo, buildLfo;
			var v0, v1, v2, v3;
			var e0, e1, e2, e3;
			var p0, p1, p2, p3;
			var pos0, pos1, pos2, pos3;
			var offset0, offset1, offset2, offset3;
			var size0, size1, size2, size3;
			var trig0, trig1, trig2, trig3;
			var localDensity, clusterPan, burstDensity;
			var rawDust, dispersed;
			var tap0, tap1, tap2, tap3, tap4, tap5;
			var tapGate0, tapGate1, tapGate2, tapGate3, tapGate4, tapGate5;
			var tapTime0, tapTime1, tapTime2, tapTime3, tapTime4, tapTime5;
			var tapPan0, tapPan1, tapPan2, tapPan3, tapPan4, tapPan5;
			var chanceLfo, disperseLfo;

			env = Lag.kr(gate, 0.12);
			numFrames = BufFrames.kr(bufnum);
			inAmp = In.kr(ampBus).lag(0.08);
			writePos = In.kr(phaseBus).lag(0.05);

			clusterLfo = SinOsc.kr(0.055).range(0.3, 1.5);
			swarmLfo = SinOsc.kr(0.08).range(0, 1);
			burstLfo = LFNoise1.kr(0.18).range(0.3, 2.0);
			buildLfo = EnvGen.kr(Env.perc(2, 6, 1, -2), Dust.kr(0.12)) + 0.3;

			localDensity = density * clusterLfo * (0.4 + (inAmp * 1.5)) * burstLfo * 0.55;
			burstDensity = localDensity * buildLfo;
			clusterPan = SinOsc.kr(0.04).range(-0.5, 0.5);

			// All voices at rate = 1.0 (no pitch change)
			size0 = baseSize * TRand.kr(0.5, 1.7, Impulse.kr(burstDensity * 0.7));
			trig0 = Dust.kr(burstDensity * 0.65);
			offset0 = TRand.kr(0.02, 0.15, trig0);
			pos0 = ((writePos - offset0).wrap(0.01, 0.99)) * numFrames;
			p0 = Phasor.ar(trig0, BufRateScale.kr(bufnum), pos0, pos0 + (size0 * SampleRate.ir), pos0);
			e0 = EnvGen.ar(Env.perc(0.002, size0 * 1.3, 1, -6), trig0);
			v0 = BufRd.ar(2, bufnum, p0, loop: 1, interpolation: 2) * e0;
			v0 = Balance2.ar(v0[0], v0[1], (clusterPan + TRand.kr(-0.4, 0.4, trig0)).clip(-1, 1) * spread);

			size1 = baseSize * TRand.kr(0.45, 1.6, Impulse.kr(burstDensity * 0.9));
			trig1 = Dust.kr(burstDensity * 0.8 * (1 + inAmp));
			offset1 = TRand.kr(0.025, 0.12, trig1);
			pos1 = ((writePos - offset1).wrap(0.01, 0.99)) * numFrames;
			p1 = Phasor.ar(trig1, BufRateScale.kr(bufnum), pos1, pos1 + (size1 * SampleRate.ir), pos1);
			e1 = EnvGen.ar(Env.perc(0.004, size1 * 1.2, 1, -6), trig1);
			v1 = BufRd.ar(2, bufnum, p1, loop: 1, interpolation: 2) * e1;
			v1 = Balance2.ar(v1[0], v1[1], (clusterPan + TRand.kr(-0.5, 0.5, trig1)).clip(-1, 1) * spread);

			size2 = baseSize * TRand.kr(0.55, 1.65, Impulse.kr(burstDensity * 0.6));
			trig2 = Dust.kr(burstDensity * 0.55 * swarmLfo);
			offset2 = TRand.kr(0.03, 0.2, trig2);
			pos2 = ((writePos - offset2).wrap(0.01, 0.99)) * numFrames;
			p2 = Phasor.ar(trig2, BufRateScale.kr(bufnum), pos2, pos2 + (size2 * SampleRate.ir), pos2);
			e2 = EnvGen.ar(Env.perc(0.003, size2 * 1.25, 1, -6), trig2);
			v2 = BufRd.ar(2, bufnum, p2, loop: 1, interpolation: 2) * e2;
			v2 = Balance2.ar(v2[0], v2[1], TRand.kr(-1, 1, trig2) * spread);

			size3 = baseSize * TRand.kr(0.5, 1.7, Impulse.kr(burstDensity * 1.1));
			trig3 = Dust.kr(burstDensity * 0.75 * (1 - swarmLfo + 0.4));
			offset3 = TRand.kr(0.02, 0.14, trig3);
			pos3 = ((writePos - offset3).wrap(0.01, 0.99)) * numFrames;
			p3 = Phasor.ar(trig3, BufRateScale.kr(bufnum), pos3, pos3 + (size3 * SampleRate.ir), pos3);
			e3 = EnvGen.ar(Env.perc(0.005, size3 * 1.1, 1, -6), trig3);
			v3 = BufRd.ar(2, bufnum, p3, loop: 1, interpolation: 2) * e3;
			v3 = Balance2.ar(v3[0], v3[1], TRand.kr(-0.9, 0.9, trig3) * spread);

			rawDust = (v0 + v1 + v2 + v3) * 0.32;

			// === PHANTOM LIMB DISPERSION ===
			chanceLfo = SinOsc.kr(0.065).range(0.25, 0.95);
			disperseLfo = LFNoise1.kr(0.045).range(0.65, 1.35);

			tapTime0 = LFNoise2.kr(0.028).range(0.06, 0.22);
			tapTime1 = LFNoise2.kr(0.022).range(0.12, 0.38);
			tapTime2 = LFNoise2.kr(0.032).range(0.28, 0.55);
			tapTime3 = LFNoise2.kr(0.025).range(0.45, 0.85);
			tapTime4 = LFNoise2.kr(0.03).range(0.65, 1.15);
			tapTime5 = LFNoise2.kr(0.02).range(0.9, 1.7);

			tapGate0 = Lag.kr(LFNoise0.kr(3.5).range(0, 1) < (chanceLfo * disperse), 0.04);
			tapGate1 = Lag.kr(LFNoise0.kr(2.8).range(0, 1) < (chanceLfo * disperse * 0.92), 0.05);
			tapGate2 = Lag.kr(LFNoise0.kr(2.2).range(0, 1) < (chanceLfo * disperse * 0.85), 0.06);
			tapGate3 = Lag.kr(LFNoise0.kr(1.9).range(0, 1) < (chanceLfo * disperse * 0.75), 0.07);
			tapGate4 = Lag.kr(LFNoise0.kr(1.5).range(0, 1) < (chanceLfo * disperse * 0.65), 0.08);
			tapGate5 = Lag.kr(LFNoise0.kr(1.2).range(0, 1) < (chanceLfo * disperse * 0.52), 0.09);

			tapPan0 = SinOsc.kr(0.075).range(-0.65, 0.65);
			tapPan1 = SinOsc.kr(0.1).range(-0.85, 0.85);
			tapPan2 = SinOsc.kr(0.065).range(-0.75, 0.75);
			tapPan3 = SinOsc.kr(0.09).range(-0.95, 0.95);
			tapPan4 = SinOsc.kr(0.055).range(-0.55, 0.55);
			tapPan5 = SinOsc.kr(0.08).range(-0.9, 0.9);

			tap0 = DelayC.ar(rawDust, 2, tapTime0) * tapGate0;
			tap0 = LPF.ar(tap0, 7000);
			tap0 = Balance2.ar(tap0[0], tap0[1], tapPan0);

			tap1 = DelayC.ar(rawDust, 2, tapTime1) * tapGate1;
			tap1 = LPF.ar(tap1, 6200);
			tap1 = Balance2.ar(tap1[0], tap1[1], tapPan1);

			tap2 = DelayC.ar(rawDust, 2, tapTime2) * tapGate2;
			tap2 = LPF.ar(tap2, 5400);
			tap2 = Balance2.ar(tap2[0], tap2[1], tapPan2);

			tap3 = DelayC.ar(rawDust, 2, tapTime3) * tapGate3;
			tap3 = LPF.ar(tap3, 4600);
			tap3 = Balance2.ar(tap3[0], tap3[1], tapPan3);

			tap4 = DelayC.ar(rawDust, 2, tapTime4) * tapGate4;
			tap4 = LPF.ar(tap4, 3800);
			tap4 = Balance2.ar(tap4[0], tap4[1], tapPan4);

			tap5 = DelayC.ar(rawDust, 2, tapTime5) * tapGate5;
			tap5 = LPF.ar(tap5, 3000);
			tap5 = Balance2.ar(tap5[0], tap5[1], tapPan5);

			dispersed = (tap0 + tap1 + tap2 + tap3 + tap4 + tap5) * disperseLfo;

			sig = rawDust + (dispersed * disperse * 0.75);

			sig = HPF.ar(sig, 200);
			sig = LPF.ar(sig, 9000);
			sig = AllpassC.ar(sig, 0.03, 0.007, 0.03);

			sig = sig * gain * (1 - blend) * env * 1.5;
			sig = (sig * 1.4).clip(-1.2, 1.2) * 0.9;
			sig = LeakDC.ar(sig);

			Out.ar(0, sig);
		}).add;

		SynthDef(\polyTest, {
			var sig, env;
			sig = SinOsc.ar([440, 550]) * 0.4;
			env = EnvGen.kr(Env.perc(0.01, 0.4), doneAction: 2);
			Out.ar(0, sig * env);
		}).add;

		context.server.sync;

		recSynth = Synth(\polyRec, [\bufnum, buf.bufnum, \run, 0, \preLevel, 0.95], context.xg);
		massSynth = Synth.after(recSynth, \polyMass, [\bufnum, buf.bufnum, \gate, 0]);
		dustSynth = Synth.after(massSynth, \polyDust, [\bufnum, buf.bufnum, \gate, 0]);

		context.server.sync;

		this.addCommand("record", "i", { |msg| recSynth.set(\run, msg[1]); });
		this.addCommand("grainGate", "i", { |msg|
			massSynth.set(\gate, msg[1]);
			dustSynth.set(\gate, msg[1]);
		});
		this.addCommand("inGain", "f", { |msg| recSynth.set(\inGain, msg[1]); });
		this.addCommand("monitorLevel", "f", { |msg| recSynth.set(\monLevel, msg[1]); });
		this.addCommand("preLevel", "f", { |msg| recSynth.set(\preLevel, msg[1]); });
		this.addCommand("blend", "f", { |msg|
			massSynth.set(\blend, msg[1]);
			dustSynth.set(\blend, msg[1]);
		});
		this.addCommand("tension", "f", { |msg| massSynth.set(\tension, msg[1]); });
		this.addCommand("evolve", "f", { |msg| massSynth.set(\evolve, msg[1]); });
		this.addCommand("disperse", "f", { |msg| dustSynth.set(\disperse, msg[1]); });
		this.addCommand("massGain", "f", { |msg| massSynth.set(\gain, msg[1]); });
		this.addCommand("dustGain", "f", { |msg| dustSynth.set(\gain, msg[1]); });
		this.addCommand("massDensity", "f", { |msg| massSynth.set(\density, msg[1]); });
		this.addCommand("dustDensity", "f", { |msg| dustSynth.set(\density, msg[1]); });
		this.addCommand("massSize", "f", { |msg| massSynth.set(\baseSize, msg[1]); });
		this.addCommand("dustSize", "f", { |msg| dustSynth.set(\baseSize, msg[1]); });
		this.addCommand("massSpread", "f", { |msg| massSynth.set(\spread, msg[1]); });
		this.addCommand("dustSpread", "f", { |msg| dustSynth.set(\spread, msg[1]); });
		this.addCommand("movement", "f", { |msg|
			massSynth.set(\spread, msg[1] * 0.6);
			dustSynth.set(\spread, msg[1]);
		});
		this.addCommand("testTone", "i", { |msg| Synth(\polyTest); });
		this.addCommand("clearBuffer", "i", { |msg| buf.zero; });

		">>> POLYMORPHIA v2.4.2 READY <<<".postln;
	}

	free {
		recSynth.free;
		massSynth.free;
		dustSynth.free;
		buf.free;
		ampBus.free;
		phaseBus.free;
	}
}
