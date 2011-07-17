
package com.jumpandrun;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Player {
	public Body body;
	
	public boolean jump = false;	
	public boolean jumping = false;
	public float xdir = 0;
	
	public Vector2 position = new Vector2();
	
	public float lastshot = 0;
	public final float shotlimit = 0.5f;
	
	public Player(float x, float y) {
		position.x = x;
		position.y = y;
		xdir = 0;
	}
	
	public void update() {
		position.x = body.getPosition().x;
		position.y = body.getPosition().y;
	}

}
