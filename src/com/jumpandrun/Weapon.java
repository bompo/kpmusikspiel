package com.jumpandrun;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Weapon {

	public float lastshot;
	public float shotlimit;
	public Ammo ammo;	
	
	public void update(float delta) {
		lastshot+= delta;
	}
	
	public void shoot(final Vector2 position, final Vector2 velocity, final int xdir) {
		if(lastshot < shotlimit)
			return;
		
		lastshot = 0;
		Ammo b = new Ammo(position.x, position.y-1.5f, xdir);
		if(ammo instanceof Bullet) {
			b = new Bullet(position.x, position.y-1.5f,xdir);
		} else if(ammo instanceof Rocket) {
			b = new Rocket(position.x, position.y-1.5f,xdir);
		}
		
		Body box = GameInstance.getInstance().createBox(BodyType.DynamicBody, 0.2f, 0.2f, 1);
		
		box.setBullet(true);		 
		box.setTransform(position.x+ xdir, position.y, 0);
		Vector2 dir = new Vector2(xdir, 0);
		dir = dir.nor();
		b.speed += (velocity.x*xdir);
		dir.mul(b.speed);
		box.setLinearVelocity(dir);
		
		b.body = box;
		b.body.setUserData(b);
		GameInstance.getInstance().bullets.add(b);
	}
}
