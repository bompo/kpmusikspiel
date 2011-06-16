package com.music;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import net.beadsproject.beads.core.AudioContext;


public class MidiPlayer {
	private Sequence sequence;
	private MidiChannel[] channels;
	private BeadsInstrument[] instruments;
	private AudioContext audioContext;
	
	public static final int NOTE_ON = 0x90;
	
	public MidiPlayer(String file, AudioContext ac){
		audioContext = ac;
		loadFile(file);
		
	}
	
	public void loadFile(String file) {
		 try {
			sequence = MidiSystem.getSequence(new File(file));
			
			channels = new MidiChannel[sequence.getTracks().length];
			instruments = new BeadsInstrument[sequence.getTracks().length];
			for(int i = 0; i < channels.length; i++) {
				channels[i] = new MidiChannel(sequence.getTracks()[i]);
				instruments[i] = new BeadsInstrument(audioContext, i);
			}
			
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void iterate() {
		for(int i = 0; i < channels.length; i++) {
			instruments[i].onMidiEvent(channels[i].iterateTick());
		}
	}
	
	public boolean[] getPlayedChannels() {
		boolean[] result = new boolean[channels.length];
		for(int i = 0; i < channels.length; i++) {
			result[i] = instruments[i].isPlayed();
		}
		return result;
	}
}
