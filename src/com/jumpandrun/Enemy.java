
package com.jumpandrun;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Enemy {
	public Body body;
	
	public Vector2 position = new Vector2();

	public Enemy(float x, float y) {
		position.x = x;
		position.y = y;
	}
	
	public void update() {
		position.x = body.getPosition().x;
		position.y = body.getPosition().y;
	}

}
