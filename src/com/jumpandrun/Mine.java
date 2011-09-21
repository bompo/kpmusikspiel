package com.jumpandrun;

public class Mine extends Ammo {
	int hitcount = 0;
	
	private float timer = 2;
	
	public Mine(float x, float y, int xdir) {
		super(x, y, xdir);
		damage = 100;
		speed = 50f;
		splashDamage = 10;
		size = 0.1f;
		timer = 2;
	}	
	
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		timer = Math.max(0,timer-delta);
		if(timer<=0) {
			hit = true;
		}
		
		if(hit) {
			hitcount++;
			if(hitcount == 1)
				Resources.getInstance().hit.play();
			body.getFixtureList().get(0).setFilterData(GameInstance.getInstance().bulletSplashCollideFilter);
			body.getFixtureList().get(0).getShape().setRadius(body.getFixtureList().get(0).getShape().getRadius()+delta*10);
			if(body.getFixtureList().get(0).getShape().getRadius()>=splashDamage) {
				kill = true;
			}
			body.setLinearVelocity(1, 1);
		} else {
			size = 0.1f;
		}
	}
	
}
