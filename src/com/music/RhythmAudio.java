package com.music;

import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.utils.Array;
import net.beadsproject.beads.core.AudioContext;


public class RhythmAudio {
	
	public MidiPlayer midi;
	public AudioContext ac;
	public boolean playing;
	public long startTime, doneTicks;
	public final long bpm = 120;
	private Array<AudioEventListener> listeners;
	private long offset = 0;
	
	public RhythmAudio() {
		//JavaSoundAudioIO jsa = new JavaSoundAudioIO(4048);
		ac = new AudioContext();//jsa);
		
		
		listeners = new Array<AudioEventListener>();
		doneTicks = 0;
		midi = new MidiPlayer("", ac);
		playing = false;
		
		
		/*final Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				if (false) timer.cancel();
				else update();
			}
		};
		//start des Timers:
		timer.scheduleAtFixedRate(task, 0, 1);
		Integer tin = new Integer(800);*/
		/*
		final int tempo = 1;
		Clock clock = new Clock(ac, tempo);
		
		
		
		clock.addMessageListener(
				new Bead() {
					public void messageReceived(Bead message) {
						Clock c = (Clock) message;
						//if (c.isBeat()) {
								iterate();
						//}
								
					}
				});
		ac.out.addDependent(clock);*/
		ac.start();
	}
	
	public void loadMidi(String file) {
		midi = new MidiPlayer(file, ac);
	}
	
	public void update() {
		if(!playing)
			return;
		
		long currentTicks = (long) ((System.nanoTime()-startTime)/(60000000000.0/(double)bpm/96.0)) + offset;// midi.micros*1000));
		long todo = currentTicks-doneTicks;
		for(long i = 0; i < todo; i++) {
			for(AudioEventListener l : listeners) {
				l.onEvent(new TickEvent(doneTicks+i, 96));
			}
			
			Array<BofEvent> events = midi.iterate();
			for(AudioEventListener l : listeners) {
				l.onMidiEvent(events, doneTicks+i);
			}
		}
		doneTicks = currentTicks;
	}
	public void gotoTick(long tick) {
		offset += tick - (doneTicks);
		doneTicks = tick;
		midi.tick = doneTicks;
	}
	public void play()  {
		startTime = System.nanoTime();
		playing = true;
	}
	public void pause() {
		playing = false;
	}
	
	public void stop() {
		playing = false;
		ac.stop();
	}
	
	public void registerBeatListener(AudioEventListener ael) {
		listeners.add(ael);
	}
	public long getTick() {
		return doneTicks;
	}
	public BofSequence getBofSequence() {
		return midi.getBofSequence();
	}
	public void addNote(BofNote note) {
		midi.getBofSequence().addNote(note);
	}
	public void saveMidi() {
		if(midi.saveFile("saveTest2.mid"))
			System.out.println("SUCCESS!");
		else
			System.out.println("FAIL :) .... :(");
	}
}
