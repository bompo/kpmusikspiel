package com.jumpandrun;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Block {

	public Body body;
	
	public Vector2 position = new Vector2();
	
	public int id = 0;
	public static int idCnt = 0;
	
	public float angle = 0;
	
	public float highlightAnimate = 0;

	public Block(float x, float y) {
		position.x = x;
		position.y = y;
		idCnt++;
		id = idCnt;
	}
	
	public void update() {
		position.x = body.getPosition().x;
		position.y = body.getPosition().y;
	}

}
