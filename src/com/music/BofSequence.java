package com.music;

import com.badlogic.gdx.utils.Array;

public class BofSequence {
	private Array<BofNote> notes;
	
	public BofSequence() {
		notes = new Array<BofNote>();
	}
	public void addNote(BofNote note) {
		notes.add(note);
	}
	public Array<BofNote> getNotes() {
		Array<BofNote> result = new Array<BofNote>();
		result.addAll(notes);
		return result;
	}
	public Array<BofNote> getNotesAt(long tick) {
		Array<BofNote> result = new Array<BofNote>();
		for(BofNote note: notes) {
			if(note.isPlayedAt(tick)) {
				result.add(note);
			}
		}
		return result;
	}
	public Array<BofNote> getInRegion(long tickStart, long tickEnd) {
		Array<BofNote> result = new Array<BofNote>();
		for(BofNote note: notes) {
			if(note.isPlayedInRegion(tickStart, tickEnd)) {
				result.add(note);
			}
		}
		return result;
	}
	public Array<MidiEvent> getMidiEventsAt(long tick) {
		Array<MidiEvent> result = new Array<MidiEvent>();
		//Array<BofNote> na = getInRegion(tick-1, tick+1);
		for(BofNote bn: notes) {
			MidiEvent temp = bn.getMidiEvent(tick);
			if(temp != null)
				result.add(temp);
		}
		return result;
	}
	public void clear() {
		notes = new Array<BofNote>();
	}
}
