package com.music;

public class TickEvent {
	
	private long tick;
	private long tpb;
	
	public TickEvent(long t, long ticksperbeat) {
		tick = t;
		tpb = ticksperbeat;
	}
	
	public long getTick() {
		return tick;
	}
	public float getEighthNote(){
		double val = tick/(double)(tpb/2);
		return (float) (val-Math.floor(val));
	}
	public float getQuarterNote(){
		double val = tick/(double)(tpb);
		return (float) (val-Math.floor(val));
	}
	public float getHalfNote(){
		double val = tick/(double)(tpb*2);
		return (float) (val-Math.floor(val));
	}
	public float getFullNote(){
		double val = tick/(double)(tpb*4);
		return (float) (val-Math.floor(val));
	}
	
	public boolean isEighthNote(){
		if(tick%(tpb/2) == 0)
			return true;
		return false;
	}
	public boolean isQuarterNote(){
		if(tick%(tpb) == 0)
			return true;
		return false;
	}
	public boolean isHalfNote(){
		if(tick%(tpb*2) == 0)
			return true;
		return false;
	}
	public boolean isFullNote(){
		if(tick%(tpb*4) == 0)
			return true;
		return false;
	}
}
