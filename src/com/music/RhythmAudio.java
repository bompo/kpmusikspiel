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
	
	public RhythmAudio() {
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
							for(int i = 0; i < 4; i++) {
								iterate();
							}
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
		midi.iterate();
		for(RhythmValue rv: rValues) {
			rv.iterate();
		}
	}
	public void	registerRhythmValue(RhythmValue rv) {
		rValues.add(rv);
	}
	public void	removeRhythmValue(RhythmValue rv) {
		//todo
	}
	public void play()  {
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
