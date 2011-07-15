package com.jumpandrun;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class EnemySpawner extends Block {

	public Body body;
	
	public Vector2 position = new Vector2();

	public EnemySpawner(float x, float y) {
		super(x,y);
		position.x = x;
		position.y = y;
	}
	
	public void update() {
		position.x = body.getPosition().x;
		position.y = body.getPosition().y;
	}

}
