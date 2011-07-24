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
	
	public BeadsInstrument(AudioContext ac, int t) {
		gains = new Gain[128]; //f�r jede Taste
		for(int i = 0; i < gains.length; i++)
			gains[i] = null;
		audioContext = ac;
		type = t%5;
		decay = 40;
		release = 100;
		sustain = 0.5f;
		peak = 1.0f;
		volume = 0.01f;
		
	}
	
	public void onMidiEvent(int[][] me) {
		for(int i = 0; i < me.length; i++) {
			Gain g = gains[me[i][0]];
			if(me[i][1] == 0) {
				if(g != null) {
					((Envelope)g.getGainEnvelope()).clear();
					((Envelope)g.getGainEnvelope()).addSegment(0, release, new KillTrigger(g));
					gains[me[i][0]] = null;
				}
			} else if(me[i][1] == 1) {
				
				g = new Gain(audioContext, 1, new Envelope(audioContext, volume*peak));
				/*WavePlayer wp = new WavePlayer(audioContext, (float) (Math.pow(2, (me[i][0] - 48 - 9)/12.0)*440), Buffer.SINE);
				if(type == 1) {
					wp = new WavePlayer(audioContext, (float) (Math.pow(2, (me[i][0] - 48 - 9)/12.0)*440), Buffer.SAW);
				} else if(type == 2) {
					wp = new WavePlayer(audioContext, (float) (Math.pow(2, (me[i][0] - 48 - 9)/12.0)*440), Buffer.SQUARE);
				} else if(type == 3) {
					wp = new WavePlayer(audioContext, (float) (Math.pow(2, (me[i][0] - 48 - 9)/12.0)*440), Buffer.TRIANGLE);
				} else if(type == 4) {
					wp = new WavePlayer(audioContext, (float) (Math.pow(2, (me[i][0] - 48 - 9)/12.0)*440), Buffer.NOISE);
				}
				((Envelope)g.getGainEnvelope()).addSegment(sustain*volume, decay);
				g.addInput(wp);*/
				gains[me[i][0]] = g;
				//audioContext.out.addInput(g);
				
			}
		}
		
	}
	
	public boolean isPlayed() {
		for(int i = 0; i< gains.length; i++) {
			if(gains[i] != null)
				return true;
		}
		return false;
	}
}
