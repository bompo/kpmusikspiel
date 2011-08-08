package com.jumpandrun;

public class Mine extends Ammo {
	
	public Mine(float x, float y, int xdir) {
		super(x, y, xdir);
		damage = 100;
		speed = 0f;
		splashDamage = 5;
	}	
	
	
	@Override
	public void update(float delta) {
		super.update(delta);
		if(hit) {
			body.getFixtureList().get(0).getShape().setRadius(body.getFixtureList().get(0).getShape().getRadius()+delta*10);
			if(body.getFixtureList().get(0).getShape().getRadius()>=splashDamage) {
				kill = true;
			}
			body.setLinearVelocity(1, 1);
		}
	}
	
}
