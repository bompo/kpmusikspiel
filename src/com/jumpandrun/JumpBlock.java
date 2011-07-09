package com.jumpandrun;

import com.badlogic.gdx.math.Vector2;

public class JumpBlock extends Block {
	
	public float jumpAnim = 0;
	
	public Vector2 positionAnim = new Vector2();

	public JumpBlock(float x, float y) {
		super(x,y);
	}
	
	public void jump() {
		jumpAnim = 1;
	}

}
