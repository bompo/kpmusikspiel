package com.music;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.sound.midi.spi.MidiFileWriter;

import com.badlogic.gdx.utils.Array;

import net.beadsproject.beads.core.AudioContext;

public class MidiPlayer {
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	private Sequence sequence;
	private BeadsInstrument[] instruments;
	private AudioContext audioContext;
	public double micros = 1337;
	private BofSequence bofSeq;
	private long tick = 0;

	public MidiPlayer(String file, AudioContext ac) {
		audioContext = ac;
		bofSeq = new BofSequence();
		if(file.equals("")) {
			instruments = new BeadsInstrument[2];
			for (int i = 0; i < 2; i++) {
				//channels[i] = new MidiChannel();
				instruments[i] = new BeadsInstrument(audioContext, i, 0);
				micros = 96;
			}
			bofSeq = new BofSequence();
		} else {
			loadFile(file);
		}
		
	}

	public void loadFile(String file) {
		try {
			sequence = MidiSystem.getSequence(new File(file));

			instruments = new BeadsInstrument[sequence.getTracks().length];
			for (int i = 0; i < sequence.getTracks().length; i++) {
				//channels[i] = new MidiChannel(sequence.getTracks()[i]);
				instruments[i] = new BeadsInstrument(audioContext, i, 0);
				micros = 96;
			}

			makeBofSequence();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public boolean saveFile(String file) {
		long lastTick = 0;
		int channels = 0;
		{
			Array<BofNote> bNotes = bofSeq.getNotes();
			for(BofNote note: bNotes) {
				if(note.getEnd() > lastTick)
					lastTick = note.getEnd();
				if(note.getChannel() > channels)
					channels = note.getChannel();
			}
		}
		channels++;
		Sequence seq;
		try {	//CREATE SEQUENCE
			seq = new Sequence(Sequence.PPQ, 96);
		} catch (InvalidMidiDataException e2) {
			return false;
		}
		for(int i = 0; i < channels; i++) {
			seq.createTrack();
		}
		Track[] tracks = seq.getTracks();
		System.out.println("laasttick " + lastTick);
		for(int i = 0 ; i < lastTick; i++) {
			Array<BofEvent> bEvents = bofSeq.getMidiEventsAt(i);
			for(BofEvent e: bEvents) {
				ShortMessage sm = new ShortMessage();
				try {
					// SET MIDI MESSAGE
					if(e.type == BofEvent.NOTE_ON)
						sm.setMessage(NOTE_ON, 1, e.note, e.velocity);
					else if(e.type == BofEvent.NOTE_OFF)
						sm.setMessage(NOTE_OFF, 1, e.note, e.velocity);
					/*sm.setMessage(NOTE_ON);
					if(e.type == BofEvent.NOTE_OFF)
						sm.setMessage(NOTE_OFF);*/
				} catch (InvalidMidiDataException e1) {
					System.out.println("LAWLZ");
					return false;
				}
				
				javax.sound.midi.MidiEvent me = new javax.sound.midi.MidiEvent(sm, i);
				tracks[e.channel].add(me);
			}
		}
		for(int i = 0; i < channels; i++) {
			System.out.println("events: " + tracks[i].ticks());
		}
		
		try {	//SAVE
			//System.out.println("count " + MidiSystem.getMidiFileTypes().length);
			MidiSystem.write(seq, MidiSystem.getMidiFileTypes()[1], new File(file));
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	public Array<BofEvent> iterate() {
		Array<BofEvent> result = bofSeq.getMidiEventsAt(tick);
		
		for(BofEvent me: result) {
			instruments[me.channel].onMidiEvent(me);
		}
		tick++;
		return result;
	}

	public BofSequence getBofSequence() {
		return bofSeq;
	}

	private class ChannelParser {
		public BofNote[] keys;
		
		public ChannelParser() {
			keys = new BofNote[128];
		}
		
		public Array<BofNote> parseChannel(Track track, int channel) {
			keys = new BofNote[128];
			Array<BofNote> result = new Array<BofNote>();
			
			for (int i = 0; i < track.size(); i++) {
				MidiMessage message = track.get(i).getMessage();
				if (message instanceof ShortMessage) {
					ShortMessage sm = (ShortMessage) message;
					if (sm.getCommand() == NOTE_ON) {
						int key = sm.getData1();
						
						BofNote note = new BofNote(key, channel, sm.getData2()/127.0f, track.get(i).getTick(), track.get(i).getTick());
						keys[key] = note;

					} else if (sm.getCommand() == NOTE_OFF) {
						int key = sm.getData1();
						BofNote temp = keys[key];
						result.add(new BofNote(temp.getNote(), temp.getChannel(), temp.getVelocity(), temp.getStart(), track.get(i).getTick()));
					}
				}
			}
			return result;
		}

	}

	private void makeBofSequence() {
		ChannelParser chp = new ChannelParser();
		
		for (int i = 0; i < sequence.getTracks().length; i++) {
			Array<BofNote> notes = chp.parseChannel(sequence.getTracks()[i], i);
			for(BofNote note: notes) {
				bofSeq.addNote(note);
			}
		}
	}
}
