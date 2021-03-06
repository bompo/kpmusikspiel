package com.music;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.events.KillTrigger;
import net.beadsproject.beads.ugens.Envelope;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.WavePlayer;


public class BeadsInstrument {

	private Gain[] gains;
	private AudioContext audioContext;
	
	private int decay, release;
	private float sustain, volume, peak;
	private int type;
	private int channel;
	
	public BeadsInstrument(AudioContext ac, int ch, int t) {
		gains = new Gain[128]; //f�r jede Taste
		for(int i = 0; i < gains.length; i++)
			gains[i] = null;
		audioContext = ac;
		type = t%5;
		decay = 40;
		release = 800;
		sustain = 0.5f;
		peak = 1.0f;
		volume = 0.2f;
		channel = ch;
		
	}
	
	@SuppressWarnings("deprecation")
	public void onMidiEvent(BofEvent me) {
		/*

		Gain gain = gains[me.note];
		
		if(me.type == BofEvent.NOTE_OFF) {
			if(gain != null) {
				((Envelope)gain.getGainEnvelope()).clear();
				((Envelope)gain.getGainEnvelope()).addSegment(0, release, new KillTrigger(gain));
				gains[me.note] = null;
				
			}
			
		} else if(me.type == BofEvent.NOTE_ON) {
			
			if(gain != null) {
				((Envelope)gain.getGainEnvelope()).clear();
				((Envelope)gain.getGainEnvelope()).addSegment(0, release, new KillTrigger(gain));
				gains[me.note] = null;
				
			}
			//System.out.println("vel: " + me.velocity);
			gain = new Gain(audioContext, 1, new Envelope(audioContext, volume*peak*me.velocity/125f));
			WavePlayer wp = new WavePlayer(audioContext, (float) (Math.pow(2, (me.note - 48 - 9)/12.0)*440), Buffer.SINE);
			if(type == 1) {
				wp = new WavePlayer(audioContext, (float) (Math.pow(2, (me.note - 48 - 9)/12.0)*440), Buffer.SAW);
			} else if(type == 2) {
				wp = new WavePlayer(audioContext, (float) (Math.pow(2, (me.note - 48 - 9)/12.0)*440), Buffer.SQUARE);
			} else if(type == 3) {
				wp = new WavePlayer(audioContext, (float) (Math.pow(2, (me.note - 48 - 9)/12.0)*440), Buffer.TRIANGLE);
			} else if(type == 4) {
				wp = new WavePlayer(audioContext, (float) (Math.pow(2, (me.note - 48 - 9)/12.0)*440), Buffer.NOISE);
			}
			((Envelope)gain.getGainEnvelope()).addSegment(sustain*volume*me.velocity/125f, decay);
			gain.addInput(wp);
			gains[me.note] = gain;
			audioContext.out.addInput(gain);
		}
		*/
	
	}
	
	public boolean isPlayed() {
		for(int i = 0; i< gains.length; i++) {
			if(gains[i] != null)
				return true;
		}
		return false;
	}
}
