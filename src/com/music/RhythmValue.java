package com.music;


public class RhythmValue {
	public enum type{
		SINE, BIT
	}
	
	private double value;
	private type transition;
	private int period;
	private RhythmAudio rhythmAudio;
	private int counter;
	
	
	public RhythmValue(type t, int p, RhythmAudio ra) {
		transition = t;
		period = p;
		rhythmAudio = ra;
		value = 0.0f;
		counter = 0;
		
		rhythmAudio.registerRhythmValue(this);
	}
	public void iterate() {
		if(transition == type.SINE) {
			value = Math.sin(counter*(2*Math.PI/period));
		} else if(transition == type.BIT) {
			if(counter%period < period/2.0){
				value = 1;
			} else {
				value = -1;
			}
		}
		counter++;
	}
	public double getValue() {
		return value;
	}
	
}
