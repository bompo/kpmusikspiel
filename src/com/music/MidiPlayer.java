package com.music;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import com.badlogic.gdx.utils.Array;

import net.beadsproject.beads.core.AudioContext;

public class MidiPlayer {
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

	public Array<MidiEvent> iterate() {
		Array<MidiEvent> result = bofSeq.getMidiEventsAt(tick);//new Array<MidiEvent>();
		
		for(MidiEvent me: result) {
			instruments[me.channel].onMidiEvent(me);
		}
		tick++;
		return result;
	}

	public BofSequence getBofSequence() {
		return bofSeq;
	}

	private class ChannelParser {

		public static final int NOTE_ON = 0x90;
		public static final int NOTE_OFF = 0x80;
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
