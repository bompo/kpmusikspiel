package com.jumpandrun;

import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.events.KillTrigger;
import net.beadsproject.beads.ugens.Envelope;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.WavePlayer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.music.RhythmAudio;
import com.music.RhythmValue;

public class GameScreen extends DefaultScreen {
	Map map;
	MapRenderer renderer;

	public static RhythmAudio ra = new RhythmAudio();
	public RhythmValue rv1;
	public RhythmValue rv2;
	
	double startTime = 0;
	boolean oldValueRv2 = false;

	private boolean jumping = false;

	public GameScreen(Game game) {
		super(game);

	}

	@Override
	public void show() {
		map = new Map();
		renderer = new MapRenderer(map);
		ra.loadMidi("./data/test.mid");
		ra.play();
		rv1 = new RhythmValue(RhythmValue.type.SINE, 20, ra);
		rv2 = new RhythmValue(RhythmValue.type.BIT, 800, ra);
	}

	@Override
	public void render(float delta) {
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
		
		Gdx.app.log("","---");
		
			Gdx.app.log("", Player.JUMP_VELOCITY + "");
		
		
//		if(ra.getPlayedChannels().length>1) {
//		Gdx.app.log("", ra.getPlayedChannels() + "");
		if (ra.getPlayedChannels()[6]==true) {
			Player.JUMP_VELOCITY = 15;
		}
		if (ra.getPlayedChannels()[5]==true) {
			renderer.renderOffsetX = 0.2f;
		}
//		}
		
		renderer.deltaMusic = rv1.getValue();
		map.update(delta);
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		renderer.render(delta);
		// Gdx.app.log("", renderer.map.bob.accel.x + " " +
		// renderer.map.bob.vel.x);
		if (map.bob.state == Player.JUMP) {
			if (!jumping) {
				jumping = true;
			}
		} else {
			jumping = false;
		}

		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			game.setScreen(new MainMenu(game));
		}
	}

	@Override
	public void hide() {
		System.out.println("dispose game screen");
		renderer.dispose();
		ra.stop();
	}

	public float MidiToFrequenc(float note) {
		return (float) (Math.pow(2, (note - 9) / 12.0) * 440);
	}
	
//	private void processMusic() {
//		if (MusicParameters.isBeat(System.nanoTime()-startTime)) {
//			if(onThreeCounter%2 == 0) {
//				freqEnv.addSegment(100, 600);
//			} else {
//				freqEnv.addSegment(800, 600);
//			}
////			
////			renderer.delta =1.03f;
////			renderer.deltaRandom = MathUtils.random(-1, 1);
//			
//			WavePlayer wp = new WavePlayer(ac, MidiToFrequenc(24),
//					Buffer.SAW);
//			WavePlayer wp2 = new WavePlayer(ac, 0, Buffer.SAW);
//			Envelope note = new Envelope(ac, MidiToFrequenc(24));
//
//			
//			if (Player.JUMP_VELOCITY == 5) {
//				Player.JUMP_VELOCITY = 15;
//				if (Math.random() > 0.5) {
//					note = new Envelope(ac, MidiToFrequenc(24));
//					note.addInput(freqEnv);
//					wp = new WavePlayer(ac, note, Buffer.SINE);
//
//				} else {
//					note = new Envelope(ac, MidiToFrequenc(28));
//					note.addInput(freqEnv);
//					wp = new WavePlayer(ac, note, Buffer.SINE);
//				}
//
//			} else {
//				Player.JUMP_VELOCITY = 5;
//				if (Math.random() > 0.5) {
//					note = new Envelope(ac, MidiToFrequenc(12));
//					note.addInput(freqEnv);
//					wp = new WavePlayer(ac, note, Buffer.SINE);
//				} else {
//					note = new Envelope(ac, MidiToFrequenc(14));
//					note.addInput(freqEnv);
//					wp = new WavePlayer(ac, note, Buffer.SINE);
//				}
//			}
//
//			++onThreeCounter;
//			if (onThreeCounter == 0) {
//				wp2 = new WavePlayer(ac, MidiToFrequenc(7), Buffer.SAW);
//			} else if (onThreeCounter == 2) {
//				wp2 = new WavePlayer(ac, MidiToFrequenc(6), Buffer.SAW);
//			} else if (onThreeCounter == 4) {
//				wp2 = new WavePlayer(ac, MidiToFrequenc(5), Buffer.SAW);
//			} else if (onThreeCounter == 6) {
//				wp2 = new WavePlayer(ac, MidiToFrequenc(4), Buffer.SAW);
//				onThreeCounter = -2;
//			}
//
//			Gain g2 = new Gain(ac, 1, new Envelope(ac, 0.1f));
//			((Envelope) g2.getGainEnvelope()).addSegment(0, 1000,
//					new KillTrigger(g2));
//			g2.addInput(wp2);
//			ac.out.addInput(g2);
//
//			Gain g = new Gain(ac, 1, new Envelope(ac, 0.07f));
//			((Envelope) g.getGainEnvelope()).addSegment(0, 1000,
//					new KillTrigger(g));
//			g.addInput(wp);
//			ac.out.addInput(g);
//
//		}
//	}

}

