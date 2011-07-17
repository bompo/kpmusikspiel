package com.jumpandrun;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public class Bullet {
	public Body body;
	//public Fixture bulletPhysicsFixture;
	
	public Vector2 position = new Vector2();

	public Bullet(float x, float y) {
		position.x = x;
		position.y = y;
	}
	
	public void update() {
		position.x = body.getPosition().x;
		position.y = body.getPosition().y;
	}
}
