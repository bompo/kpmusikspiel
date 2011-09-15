package com.jumpandrun;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Ammo {
	public Body body;
	public int age;
	public boolean kill = false;
	public boolean hit = false;
	public int damage;
	public int splashDamage;
	public float speed = 80;
	public int xdir = 0;
	public float size = 0.1f;
	
	public Vector2 position = new Vector2();

	public Ammo(float x, float y, int xdir) {
		position.x = x;
		position.y = y;
		age = 0;
		damage = 50;
		splashDamage = 0;
		this.xdir = xdir;
	}
	
	public void update(float delta) {
		position.x = body.getPosition().x;
		position.y = body.getPosition().y;
		size = body.getFixtureList().get(0).getShape().getRadius();
		age+= delta;
//		if(hit) {
//			body.getFixtureList().get(0).setFilterData(GameInstance.getInstance().deadCollideFilter);
//		}
	}
	
}
