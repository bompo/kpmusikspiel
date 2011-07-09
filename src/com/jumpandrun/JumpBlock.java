package com.jumpandrun;

import com.badlogic.gdx.math.Vector2;

public class JumpBlock extends Block {
	
	public float jumpAnim = 0;
	
	public Vector2 positionAnim = new Vector2();

	public JumpBlock(float x, float y) {
		super(x,y);
	}
	
	public void jump() {
		jumpAnim = 2.f;
	}
	
	public void update() {
		body.setTransform(position.x, position.y + jumpAnim, 0);
		positionAnim.x = body.getPosition().x;
		positionAnim.y = body.getPosition().y;
	}

}
