package com.music;

public class MidiEvent {
	public static final int NOTE_OFF = 0,
							NOTE_ON = 1;
							
	public int type, channel, velocity, note;
	public float age = 0;
	public int kill = -1;
	
	public MidiEvent(int t, int n, int ch, int vel) {
		type = t;
		channel = ch;
		velocity = vel;
		note = n;
	}
	
}
