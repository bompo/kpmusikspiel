package com.jumpandrun;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class PowerUp {

	public Body body;
	
	public Vector2 position = new Vector2();
	public float depth = 0;
	
	public boolean show = true;
	
	public boolean kill = false;
	
	public float angle = 0;
	
	public int id = 0;
	public static int idCnt = 0;
	
	public float highlightAnimate = 0;

	public PowerUp(float x, float y) {
		position.x = x;
		position.y = y;
		idCnt++;
		id = idCnt;
	}
	
	public void update() {
		position.x = body.getPosition().x;
		position.y = body.getPosition().y;
		
		angle = (MathUtils.PI * -position.x) / 2.f;
	}

}
