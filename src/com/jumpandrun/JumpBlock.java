package com.jumpandrun;

import com.badlogic.gdx.math.Vector2;

public class JumpBlock extends Block {
	private static boolean switcher = false;
	public float jumpAnim = 0;
	public int type = 0;
	
	public Vector2 positionAnim = new Vector2();

	public JumpBlock(float x, float y) {
		super(x,y);
		switcher = !switcher;
		if(switcher)
			type = 0;
		else
			type = 1;
	}
	
	public void jump() {
		jumpAnim = 1;
	}

}
