
package com.jumpandrun;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public class Enemy {
	public Body body;
	public Fixture enemyPhysicsFixture;
	
	public Vector2 position = new Vector2();
	public float angle = 0;

	public Enemy(float x, float y) {
		position.x = x;
		position.y = y;
	}
	
	public void update() {
		position.x = body.getPosition().x;
		position.y = body.getPosition().y;
		angle = body.getAngle();
	}

}
