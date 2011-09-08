package com.jumpandrun;

public class Mine extends Ammo {
	
	public Mine(float x, float y, int xdir) {
		super(x, y, xdir);
		damage = 100;
		speed = 50f;
		splashDamage = 10;
	}	
	
	
	@Override
	public void update(float delta) {
		super.update(delta);
		if(hit) {
			body.getFixtureList().get(0).setFilterData(GameInstance.getInstance().bulletSplashCollideFilter);
			body.getFixtureList().get(0).getShape().setRadius(body.getFixtureList().get(0).getShape().getRadius()+delta*10);
			if(body.getFixtureList().get(0).getShape().getRadius()>=splashDamage) {
				kill = true;
			}
			body.setLinearVelocity(1, 1);
		} else {
			if(body.getLinearVelocity().x >0 && body.getLinearVelocity().y >0) {
			body.setLinearVelocity(body.getLinearVelocity().x-(delta*50), body.getLinearVelocity().y-(delta*50));
			}
		}
	}
	
}
