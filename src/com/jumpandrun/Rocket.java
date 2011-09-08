package com.jumpandrun;

public class Rocket extends Ammo {
	int hitcount = 0;
	public Rocket(float x, float y, int xdir) {
		super(x, y, xdir);
		damage = 100;
		speed = 1f;
		splashDamage = 6;
	}	
	
	
	@Override
	public void update(float delta) {
		super.update(delta);
		if(hit) {
			hitcount++;
			if(hitcount == 1)
				Resources.getInstance().hit.play();
			body.getFixtureList().get(0).setFilterData(GameInstance.getInstance().bulletSplashCollideFilter);
			body.getFixtureList().get(0).getShape().setRadius(body.getFixtureList().get(0).getShape().getRadius()+delta*10);
			if(body.getFixtureList().get(0).getShape().getRadius()>=splashDamage) {
				kill = true;
			}
		}
		speed +=delta*50;
		if(!hit) {
			body.setLinearVelocity(speed*xdir, 0);
		} else {
			body.setLinearVelocity(1, 1);
		}
	}
	
}
