package com.jumpandrun;

public class Rocket extends Ammo {
	
	public Rocket(float x, float y, int xdir) {
		super(x, y, xdir);
		damage = 100;
		speed = 1f;
		splashDamage = 10;
	}	
	
	
	@Override
	public void update(float delta) {
		super.update(delta);
		speed +=delta*50;
		body.setLinearVelocity(speed*xdir, 0);
	}
	
}
