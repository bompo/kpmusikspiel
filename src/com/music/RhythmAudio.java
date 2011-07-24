package com.music;

import java.util.LinkedList;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.ugens.Clock;


public class RhythmAudio {
	
	public static MidiPlayer midi;
	public static final AudioContext ac = new AudioContext();
	public static LinkedList<RhythmValue> rValues;
	public static boolean playing;
	public static long startTime, doneTicks;
	public final static long bpm = 140;
	
	public RhythmAudio() {
		doneTicks = 0;
		midi = null;
		rValues = new LinkedList<RhythmValue>();
		playing = false;
		
		final int tempo = 1;
		Clock clock = new Clock(ac, tempo);
		
		clock.addMessageListener(
				new Bead() {
					public void messageReceived(Bead message) {
						Clock c = (Clock) message;
						if (c.isBeat()) {
								iterate();
						}
					}
				});
		ac.out.addDependent(clock);
		ac.start();
	}
	
	public void loadMidi(String file) {
		midi = new MidiPlayer(file, ac);
	}
	
	public static void iterate() {
		if(!playing)
			return;
		long currentTicks = (long) ((System.nanoTime()-startTime)/(4464285.71));
		long todo = currentTicks-doneTicks;
		for(long i = 0; i < todo; i++) {
			midi.iterate();
			for(RhythmValue rv: rValues) {
				rv.iterate();
			}
		}
		doneTicks = currentTicks;
	}
	public void	registerRhythmValue(RhythmValue rv) {
		rValues.add(rv);
	}
	public void	removeRhythmValue(RhythmValue rv) {
		//todo
	}
	public void play()  {
		startTime = System.nanoTime();
		playing = true;
	}
	public void pause() {
		playing = false;
	}
	
	public void stop() {
		ac.stop();
	}
	
	public boolean[] getPlayedChannels() {
		return midi.getPlayedChannels();
	}
}
