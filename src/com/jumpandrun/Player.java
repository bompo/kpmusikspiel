
package com.jumpandrun;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Player {
	public Body body;
	
	public boolean alive = true;
	
	public boolean jump = false;	
	public boolean jumping = false;
	public int xdir;
	
	public Vector2 position = new Vector2();
	
	public Weapon weapon;
	
	public float angle = 0;

	public Player(float x, float y) {
		position.x = x;
		position.y = y;
		xdir = 1;
		weapon = new RocketLauncher();
	}
	
	public void update(float delta) {
		position.x = body.getPosition().x;
		position.y = body.getPosition().y;
		angle = -position.x*360/(2*MathUtils.PI);
		
		weapon.update(delta);
	}
	
	public void reset() {
		weapon.bullets.clear();
	}
	
	public void shoot() {		
		weapon.shoot(position, body.getLinearVelocity(), xdir);
	}

}
