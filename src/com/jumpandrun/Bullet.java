package com.jumpandrun;

public class Bullet extends Ammo {
	
	public Bullet(float x, float y, int xdir) {
		super(x, y, xdir);
		damage = 50;
		speed = 80;
		splashDamage = 0;
	}	
	
	@Override
	public void update(float delta) {
		super.update(delta);
		body.setLinearVelocity(body.getLinearVelocity().x, 0);
	}
}
