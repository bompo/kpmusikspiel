package com.music;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;


public class MidiChannel {
	private int instrument;
	public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
	private Track track;
	private int[][] keys;
	private long tick; //aktueller tick
	private int trackIndex;
	
	public MidiChannel( Track t) {
		keys = new int[128][];
		for(int i = 0; i < 128; i++) {
			keys[i] = new int[2];
			keys[i][0] = 0; //gedr�ckt
			keys[i][1] = 0; //velocity
		}
		track = t;
		
		//instrument ermitteln
		for (int i=0; i < track.size(); i++) { 
        	MidiEvent event = track.get(i);
        	long tick = event.getTick();
        	MidiMessage message = event.getMessage();
        	if (message instanceof ShortMessage) {
                ShortMessage sm = (ShortMessage) message;
                int command = sm.getCommand();
                if(command == 192){
                	instrument = sm.getData1();
                	break;
                }
        	}
		}
		
		tick = 0;
		trackIndex = 0;
	}
	public long getTick() {
		return tick;
	}
	
	//events zur�ckgeben
	public int[][] iterateTick() {
		tick++;
		int[][] result = new int[200][];
		int eventCounter = 0;
		
		for(int i = trackIndex+1; i < track.size() && track.get(i).getTick() < tick ; i++) {
			trackIndex = i;
			MidiMessage message = track.get(i).getMessage();
			if (message instanceof ShortMessage) {
				ShortMessage sm = (ShortMessage) message;
				if (sm.getCommand() == NOTE_ON) {
					int key = sm.getData1();
                    keys[key][0] = 1;
                    keys[key][1] = sm.getData2(); //velocity;
                    result[eventCounter] = new int[3];
                    result[eventCounter][0] = key;
                    result[eventCounter][1] = 1;
                    result[eventCounter][2] = sm.getData2();
                    
                    
                    eventCounter++;
				}
				else if (sm.getCommand() == NOTE_OFF) {
					int key = sm.getData1();
                    keys[key][0] = 0;
                    keys[key][1] = 0; //velocity;
                    
                    result[eventCounter] = new int[3];
                    result[eventCounter][0] = key;
                    result[eventCounter][1] = 0;
                    result[eventCounter][2] = 0;
                    
                    eventCounter++;
				}
			}
		}
		int[][] finalresult = new int[eventCounter][];
		for(int i = 0; i < eventCounter; i++) {
			finalresult[i] = result[i];
		}
		return finalresult;
	} 
	public void jumpToTick(long t) {
		tick = t;
	}
	
	public int getInstrument() {
		return instrument;
	}
	
}
