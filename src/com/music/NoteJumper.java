package com.music;

import com.badlogic.gdx.math.Vector2;

public class NoteJumper {
	public float velocity;
	public int note;
	public long start;
	public BofNote bofNote;
	public Vector2 posA, posB;
	public boolean alive;
	public float random;
	public int channel;

	public NoteJumper(BofEvent me, long tick) {
		velocity = me.velocity;
		note = me.note;
		start = tick;
		bofNote = null;
		float range = 10;
		posA = new Vector2 ((float) (Math.random() - 0.5) * range, (float) (Math.random() - 0.5) * range);
		posA.add(new Vector2(23,-15));
		posB = new Vector2(posA.x+range*(float)(Math.random()-0.5), posA.y+range*(float)(Math.random()-0.5));//(float) (Math.random() - 0.5) * range, (float) (Math.random() - 0.5) * range);
		
		alive = true;
		random = (float) (Math.random() - 0.5) * 2;
		channel = me.channel;
		bofNote = me.bofNote;
	}
}
