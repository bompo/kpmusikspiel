package com.music;

public class BofNote {
	private int note, channel;
	private float velocity;
	private long start, end; //in ticks
	
	public BofNote(int note, int channel, float velocity, long start, long end) {
		this.note = note;
		this.channel = channel;
		this.velocity = velocity;
		this.start = start;
		this.end = end;
	}
	public int getNote() {
		return note;
	}
	public int getChannel() {
		return channel;
	}
	public float getVelocity() {
		return velocity;
	}
	public long getStart() {
		return start;
	}
	public long getEnd() {
		return end;
	}
	public float getFraction(long tick) {
		if(isPlayedAt(tick)) {
			long pos = tick-start;
			return pos/(float)getDuration();
		}
		return -1;
	}
	public boolean isPlayedAt(long tick) {
		return (tick >= start && tick < end);
	}
	public boolean isPlayedInRegion(long s, long e) {
		return ((start >= s && start < e) || (end >= s && end < e) || (start < s && end > e));
	}
	public long getDuration() {
		return end-start;
	}
	public long getPlayedFor(long tick) {
		if(isPlayedAt(tick)) {
			return tick-start;
		}
		return 0;
	}
	public BofEvent getMidiEvent(long tick) {
		if(start == tick) {
			return new BofEvent(BofEvent.NOTE_ON, note, channel, (int) (velocity*127), this);
		}else if(end == tick) {
			return new BofEvent(BofEvent.NOTE_OFF, note, channel, (int) (velocity*127), this);
		}
		return null;
	}
}
