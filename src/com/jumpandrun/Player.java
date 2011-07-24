
package com.jumpandrun;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Player {
	public Body body;
	
	public boolean alive = true;
	
	public boolean jump = false;	
	public boolean jumping = false;
	public float xdir;
	
	public Vector2 position = new Vector2();
	
	public float lastshot = 0;
	public final float shotlimit = 0.1f;
	
	public float angle = 0;

	public Player(float x, float y) {
		position.x = x;
		position.y = y;
		xdir = 1;
	}
	
	public void update() {
		position.x = body.getPosition().x;
		position.y = body.getPosition().y;
		angle = (MathUtils.PI * -position.x) / 2.f;
	}

}
