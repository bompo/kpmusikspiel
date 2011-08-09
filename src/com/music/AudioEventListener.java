package com.music;

import com.badlogic.gdx.utils.Array;

public abstract class AudioEventListener {
	
	public AudioEventListener() {
		
	}
	
	abstract public void onEvent(TickEvent te);
	
	abstract public void onMidiEvent(Array<MidiEvent> events, long tick);
	
}
