// Engine_Polymorphia.sc v2.2.0
// Penderecki String Texture Generator
// MASS: Long grains, slow attack, formant filtering, staged evolution
// DUST: Sparse grains with Phantom Limb-style delay dispersion
// Smoothed harmonic content, significant gain boost

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

		SynthDef(\polyRec, { |bufnum, run=0, inGain=1.5, monLevel=0.3, preLevel=0.93|
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

		// MASS LAYER - smoothed harmonics
		SynthDef(\polyMass, { |bufnum, gate=0, gain=0.75, blend=0.5,
			density=1.5, baseSize=4, spread=0.4, drift=0.015, tension=0.4, evolve=0.5|

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
			var stage, shimmer, quiver;
			var smoothed;

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

			formant1 = 380 + (lfo1 * 180) + (stage * 100);
			formant2 = 1100 + (lfo2 * 350) + (stage * 200);
			formant3 = 2200 + (lfo3 * 500) + (stage * 300);

			shimmer = SinOsc.kr(4.5 + (stage * 2)).range(1 - (stage * 0.15), 1);
			quiver = SinOsc.kr(5.5 + (stage * 1.5)).range(-1, 1) * stage * 0.008;

			density = density * (1 + (inAmp * 0.4));

			size0 = baseSize * LFNoise2.kr(0.008).range(0.85, 1.3);
			trig0 = Impulse.kr(density * 0.6);
			offset0 = LFNoise1.kr(0.02).range(0.05, 0.2);
			pos0 = ((writePos - offset0).wrap(0.01, 0.99)) * numFrames;
			rate0 = 1.0 + (LFNoise2.kr(0.01).range(-0.8, 0.5) * drift) + quiver;
			pan0 = (((0 * phi) % 1) * 2 - 1 + (lfo1 * 0.3)).clip(-1, 1) * spread;
			p0 = Phasor.ar(trig0, BufRateScale.kr(bufnum) * rate0, pos0, pos0 + (size0 * SampleRate.ir), pos0);
			e0 = EnvGen.ar(Env([0, 0.3, 1, 1, 0], [0.5, 0.3, size0 - 1.6, 0.8], [4, 2, 0, -4]), trig0);
			v0 = BufRd.ar(2, bufnum, p0, loop: 1, interpolation: 4) * e0 * shimmer;
			filt0 = BPF.ar(v0, formant1, 0.4) * 1.5;
			filt0 = filt0 + (BPF.ar(v0, formant2, 0.3) * 0.8);
			filt0 = filt0 + (BPF.ar(v0, formant3, 0.25) * 0.4);
			v0 = (v0 * (0.5 - (stage * 0.15))) + (filt0 * (0.5 + (stage * 0.15)));
			comb0 = CombC.ar(v0, 0.05, 0.015 + (tension * 0.01), 0.4 + (tension * 0.5) + (stage * 0.3));
			v0 = v0 + (comb0 * (tension + (stage * 0.2)) * 0.25);
			v0 = LPF.ar(v0, 3500 + (lfo1 * 1500) - (stage * 500));
			v0 = Balance2.ar(v0[0], v0[1], pan0);

			size1 = baseSize * LFNoise2.kr(0.01).range(0.8, 1.25);
			trig1 = Impulse.kr(density * 0.75);
			offset1 = LFNoise1.kr(0.018).range(0.08, 0.25);
			pos1 = ((writePos - offset1).wrap(0.01, 0.99)) * numFrames;
			rate1 = 1.0 + (LFNoise2.kr(0.012).range(-0.7, 0.6) * drift * 1.1) + (quiver * 0.9);
			pan1 = (((1 * phi) % 1) * 2 - 1 + (lfo2 * 0.25)).clip(-1, 1) * spread;
			p1 = Phasor.ar(trig1, BufRateScale.kr(bufnum) * rate1, pos1, pos1 + (size1 * SampleRate.ir), pos1);
			e1 = EnvGen.ar(Env([0, 0.25, 1, 1, 0], [0.6, 0.35, size1 - 1.7, 0.75], [4, 2, 0, -4]), trig1);
			v1 = BufRd.ar(2, bufnum, p1, loop: 1, interpolation: 4) * e1 * shimmer;
			filt1 = BPF.ar(v1, formant1 * 1.1, 0.35) * 1.4;
			filt1 = filt1 + (BPF.ar(v1, formant2 * 0.95, 0.28) * 0.75);
			v1 = (v1 * (0.5 - (stage * 0.12))) + (filt1 * (0.5 + (stage * 0.12)));
			comb1 = CombC.ar(v1, 0.05, 0.018 + (tension * 0.008), 0.35 + (tension * 0.45) + (stage * 0.25));
			v1 = v1 + (comb1 * (tension + (stage * 0.18)) * 0.22);
			v1 = LPF.ar(v1, 3800 + (lfo2 * 1200) - (stage * 400));
			v1 = Balance2.ar(v1[0], v1[1], pan1);

			size2 = baseSize * LFNoise2.kr(0.009).range(0.75, 1.2);
			trig2 = Impulse.kr(density * 0.9);
			offset2 = LFNoise1.kr(0.022).range(0.1, 0.3);
			pos2 = ((writePos - offset2).wrap(0.01, 0.99)) * numFrames;
			rate2 = 1.0 + (LFNoise2.kr(0.015).range(-0.6, 0.5) * drift * 0.9) + (quiver * 1.1);
			pan2 = (((2 * phi) % 1) * 2 - 1 + (lfo3 * 0.28)).clip(-1, 1) * spread;
			p2 = Phasor.ar(trig2, BufRateScale.kr(bufnum) * rate2, pos2, pos2 + (size2 * SampleRate.ir), pos2);
			e2 = EnvGen.ar(Env([0, 0.2, 1, 1, 0], [0.55, 0.4, size2 - 1.65, 0.7], [4, 2, 0, -4]), trig2);
			v2 = BufRd.ar(2, bufnum, p2, loop: 1, interpolation: 4) * e2 * shimmer;
			filt2 = BPF.ar(v2, formant1 * 0.9, 0.38) * 1.3;
			filt2 = filt2 + (BPF.ar(v2, formant3 * 0.85, 0.22) * 0.5);
			v2 = (v2 * (0.5 - (stage * 0.1))) + (filt2 * (0.5 + (stage * 0.1)));
			comb2 = CombC.ar(v2, 0.05, 0.012 + (tension * 0.012), 0.38 + (tension * 0.4) + (stage * 0.28));
			v2 = v2 + (comb2 * (tension + (stage * 0.15)) * 0.2);
			v2 = LPF.ar(v2, 4000 + (lfo3 * 1000) - (stage * 450));
			v2 = Balance2.ar(v2[0], v2[1], pan2);

			size3 = baseSize * LFNoise2.kr(0.011).range(0.7, 1.15);
			trig3 = Impulse.kr(density * 1.05);
			offset3 = LFNoise1.kr(0.025).range(0.12, 0.35);
			pos3 = ((writePos - offset3).wrap(0.01, 0.99)) * numFrames;
			rate3 = 1.0 + (LFNoise2.kr(0.013).range(-0.65, 0.55) * drift * 1.05) + (quiver * 0.95);
			pan3 = (((3 * phi) % 1) * 2 - 1 + (lfo1 * 0.22)).clip(-1, 1) * spread;
			p3 = Phasor.ar(trig3, BufRateScale.kr(bufnum) * rate3, pos3, pos3 + (size3 * SampleRate.ir), pos3);
			e3 = EnvGen.ar(Env([0, 0.28, 1, 1, 0], [0.45, 0.32, size3 - 1.5, 0.73], [4, 2, 0, -4]), trig3);
			v3 = BufRd.ar(2, bufnum, p3, loop: 1, interpolation: 4) * e3 * shimmer;
			filt3 = BPF.ar(v3, formant2 * 1.05, 0.32) * 1.2;
			filt3 = filt3 + (BPF.ar(v3, formant3 * 1.1, 0.2) * 0.45);
			v3 = (v3 * (0.5 - (stage * 0.13))) + (filt3 * (0.5 + (stage * 0.13)));
			comb3 = CombC.ar(v3, 0.05, 0.02 + (tension * 0.006), 0.32 + (tension * 0.5) + (stage * 0.22));
			v3 = v3 + (comb3 * (tension + (stage * 0.17)) * 0.23);
			v3 = LPF.ar(v3, 3600 + (lfo2 * 1400) - (stage * 350));
			v3 = Balance2.ar(v3[0], v3[1], pan3);

			sig = (v0 + v1 + v2 + v3) * 0.35;  // boosted

			// SMOOTHING STAGE - gentle LP + allpass diffusion
			sig = LPF.ar(sig, 6000);
			sig = AllpassC.ar(sig, 0.05, 0.012, 0.08);
			sig = AllpassC.ar(sig, 0.05, 0.019, 0.06);

			sig = sig + (FreeVerb2.ar(sig[0], sig[1], 0.15, 0.5, 0.3) * 0.1);
			sig = sig * gain * blend * env;
			// Gentler saturation curve
			sig = (sig * 1.5).clip(-1.2, 1.2) * 0.85;
			sig = LeakDC.ar(sig);

			Out.ar(0, sig);
		}).add;

		// DUST LAYER - Phantom Limb style dispersion
		// Sparse grains fed through multiple random delay taps
		SynthDef(\polyDust, { |bufnum, gate=0, gain=0.65, blend=0.5,
			density=6, baseSize=0.065, spread=0.85, drift=0.012, disperse=0.6|

			var env, sig, numFrames, inAmp, writePos;
			var clusterLfo, swarmLfo, burstLfo;
			var v0, v1, v2, v3;
			var e0, e1, e2, e3;
			var p0, p1, p2, p3;
			var pos0, pos1, pos2, pos3;
			var offset0, offset1, offset2, offset3;
			var size0, size1, size2, size3;
			var trig0, trig1, trig2, trig3;
			var localDensity, clusterPan;
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

			// Much sparser base density
			clusterLfo = SinOsc.kr(0.06).range(0.3, 1.4);
			swarmLfo = SinOsc.kr(0.09).range(0, 1);
			burstLfo = LFNoise1.kr(0.2).range(0.4, 1.8);

			// Reduced density for more space
			localDensity = density * clusterLfo * (0.5 + (inAmp * 1.2)) * burstLfo * 0.6;
			clusterPan = SinOsc.kr(0.045).range(-0.4, 0.4);

			// Only 4 source voices (sparser)
			size0 = baseSize * TRand.kr(0.5, 1.6, Impulse.kr(localDensity * 0.7));
			trig0 = Dust.kr(localDensity * 0.65);
			offset0 = TRand.kr(0.02, 0.15, trig0);
			pos0 = ((writePos - offset0).wrap(0.01, 0.99)) * numFrames;
			p0 = Phasor.ar(trig0, BufRateScale.kr(bufnum), pos0, pos0 + (size0 * SampleRate.ir), pos0);
			e0 = EnvGen.ar(Env.perc(0.003, size0 * 1.2, 1, -6), trig0);
			v0 = BufRd.ar(2, bufnum, p0, loop: 1, interpolation: 2) * e0;
			v0 = Balance2.ar(v0[0], v0[1], (clusterPan + TRand.kr(-0.35, 0.35, trig0)).clip(-1, 1) * spread);

			size1 = baseSize * TRand.kr(0.45, 1.5, Impulse.kr(localDensity * 0.9));
			trig1 = Dust.kr(localDensity * 0.8);
			offset1 = TRand.kr(0.03, 0.12, trig1);
			pos1 = ((writePos - offset1).wrap(0.01, 0.99)) * numFrames;
			p1 = Phasor.ar(trig1, BufRateScale.kr(bufnum), pos1, pos1 + (size1 * SampleRate.ir), pos1);
			e1 = EnvGen.ar(Env.perc(0.005, size1 * 1.1, 1, -6), trig1);
			v1 = BufRd.ar(2, bufnum, p1, loop: 1, interpolation: 2) * e1;
			v1 = Balance2.ar(v1[0], v1[1], (clusterPan + TRand.kr(-0.4, 0.4, trig1)).clip(-1, 1) * spread);

			size2 = baseSize * TRand.kr(0.55, 1.55, Impulse.kr(localDensity * 0.75));
			trig2 = Dust.kr(localDensity * 0.7);
			offset2 = TRand.kr(0.025, 0.18, trig2);
			pos2 = ((writePos - offset2).wrap(0.01, 0.99)) * numFrames;
			p2 = Phasor.ar(trig2, BufRateScale.kr(bufnum), pos2, pos2 + (size2 * SampleRate.ir), pos2);
			e2 = EnvGen.ar(Env.perc(0.004, size2 * 1.15, 1, -6), trig2);
			v2 = BufRd.ar(2, bufnum, p2, loop: 1, interpolation: 2) * e2;
			v2 = Balance2.ar(v2[0], v2[1], TRand.kr(-0.8, 0.8, trig2) * spread);

			size3 = baseSize * TRand.kr(0.5, 1.6, Impulse.kr(localDensity * 1.1));
			trig3 = Dust.kr(localDensity * 0.85);
			offset3 = TRand.kr(0.02, 0.14, trig3);
			pos3 = ((writePos - offset3).wrap(0.01, 0.99)) * numFrames;
			p3 = Phasor.ar(trig3, BufRateScale.kr(bufnum), pos3, pos3 + (size3 * SampleRate.ir), pos3);
			e3 = EnvGen.ar(Env.perc(0.006, size3 * 1.0, 1, -6), trig3);
			v3 = BufRd.ar(2, bufnum, p3, loop: 1, interpolation: 2) * e3;
			v3 = Balance2.ar(v3[0], v3[1], TRand.kr(-0.9, 0.9, trig3) * spread);

			// Raw dust grains before dispersion
			rawDust = (v0 + v1 + v2 + v3) * 0.3;

			// === PHANTOM LIMB STYLE DISPERSION ===
			// 6 delay taps with random gating (like 9 micro-loopers)
			chanceLfo = SinOsc.kr(0.07).range(0.3, 0.9);
			disperseLfo = LFNoise1.kr(0.05).range(0.7, 1.3);

			// Delay times drift slowly
			tapTime0 = LFNoise2.kr(0.03).range(0.08, 0.25);
			tapTime1 = LFNoise2.kr(0.025).range(0.15, 0.4);
			tapTime2 = LFNoise2.kr(0.035).range(0.3, 0.6);
			tapTime3 = LFNoise2.kr(0.028).range(0.5, 0.9);
			tapTime4 = LFNoise2.kr(0.032).range(0.7, 1.2);
			tapTime5 = LFNoise2.kr(0.022).range(1.0, 1.8);

			// Random gate triggers (probability-based like Chance knob)
			tapGate0 = Lag.kr(LFNoise0.kr(3).range(0, 1) < (chanceLfo * disperse), 0.05);
			tapGate1 = Lag.kr(LFNoise0.kr(2.5).range(0, 1) < (chanceLfo * disperse * 0.9), 0.06);
			tapGate2 = Lag.kr(LFNoise0.kr(2).range(0, 1) < (chanceLfo * disperse * 0.85), 0.07);
			tapGate3 = Lag.kr(LFNoise0.kr(1.8).range(0, 1) < (chanceLfo * disperse * 0.75), 0.08);
			tapGate4 = Lag.kr(LFNoise0.kr(1.5).range(0, 1) < (chanceLfo * disperse * 0.65), 0.09);
			tapGate5 = Lag.kr(LFNoise0.kr(1.2).range(0, 1) < (chanceLfo * disperse * 0.55), 0.1);

			// Pan positions for taps
			tapPan0 = SinOsc.kr(0.08).range(-0.6, 0.6);
			tapPan1 = SinOsc.kr(0.11).range(-0.8, 0.8);
			tapPan2 = SinOsc.kr(0.07).range(-0.7, 0.7);
			tapPan3 = SinOsc.kr(0.095).range(-0.9, 0.9);
			tapPan4 = SinOsc.kr(0.065).range(-0.5, 0.5);
			tapPan5 = SinOsc.kr(0.085).range(-0.85, 0.85);

			// Create delay taps with LP filtering for progressive darkening
			tap0 = DelayC.ar(rawDust, 2, tapTime0) * tapGate0;
			tap0 = LPF.ar(tap0, 5500);
			tap0 = Balance2.ar(tap0[0], tap0[1], tapPan0);

			tap1 = DelayC.ar(rawDust, 2, tapTime1) * tapGate1;
			tap1 = LPF.ar(tap1, 4800);
			tap1 = Balance2.ar(tap1[0], tap1[1], tapPan1);

			tap2 = DelayC.ar(rawDust, 2, tapTime2) * tapGate2;
			tap2 = LPF.ar(tap2, 4200);
			tap2 = Balance2.ar(tap2[0], tap2[1], tapPan2);

			tap3 = DelayC.ar(rawDust, 2, tapTime3) * tapGate3;
			tap3 = LPF.ar(tap3, 3600);
			tap3 = Balance2.ar(tap3[0], tap3[1], tapPan3);

			tap4 = DelayC.ar(rawDust, 2, tapTime4) * tapGate4;
			tap4 = LPF.ar(tap4, 3000);
			tap4 = Balance2.ar(tap4[0], tap4[1], tapPan4);

			tap5 = DelayC.ar(rawDust, 2, tapTime5) * tapGate5;
			tap5 = LPF.ar(tap5, 2400);
			tap5 = Balance2.ar(tap5[0], tap5[1], tapPan5);

			// Mix raw grains with dispersed taps
			dispersed = (tap0 + tap1 + tap2 + tap3 + tap4 + tap5) * disperseLfo;

			sig = rawDust + (dispersed * disperse * 0.7);

			// HPF for sul ponticello brightness but gentler
			sig = HPF.ar(sig, 250);

			// SMOOTHING: gentle LP + allpass
			sig = LPF.ar(sig, 7000);
			sig = AllpassC.ar(sig, 0.03, 0.008, 0.04);

			sig = sig * gain * (1 - blend) * env * 1.4;  // boosted
			// Gentler saturation
			sig = (sig * 1.3).clip(-1.1, 1.1) * 0.9;
			sig = LeakDC.ar(sig);

			Out.ar(0, sig);
		}).add;

		SynthDef(\polyTest, {
			var sig, env;
			sig = SinOsc.ar([440, 550]) * 0.3;
			env = EnvGen.kr(Env.perc(0.01, 0.4), doneAction: 2);
			Out.ar(0, sig * env);
		}).add;

		context.server.sync;

		recSynth = Synth(\polyRec, [\bufnum, buf.bufnum, \run, 0, \preLevel, 0.93], context.xg);
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
			massSynth.set(\spread, msg[1] * 0.5);
			dustSynth.set(\spread, msg[1]);
		});
		this.addCommand("testTone", "i", { |msg| Synth(\polyTest); });
		this.addCommand("clearBuffer", "i", { |msg| buf.zero; });

		">>> POLYMORPHIA v2.2.0 READY <<<".postln;
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
