
package com.jumpandrun;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

public class Enemy {
	public Body body;
	public Fixture enemyPhysicsFixture;
	
	public Vector2 direction = new Vector2(9,0);
	
	public Vector2 position = new Vector2();
	public float angle = 0;
	public boolean kill;
	public int health;
	
	public int size = 1;

	public Enemy(float x, float y, int size) {
		position.x = x;
		position.y = y;
		this.size = size;
		kill = false;
		health = 100;
	}
	
	public void update() {
		position.x = body.getPosition().x;
		position.y = body.getPosition().y;
		if(health <= 0)
			kill = true;
	}

	public void move() {
		
		if(Math.abs(body.getLinearVelocity().x)<0.05f) {
			direction.x = -direction.x;
		}		
		
		List<Contact> contactList = GameInstance.getInstance().world.getContactList();
		for(int i = 0; i < contactList.size(); i++) {
			Contact contact = contactList.get(i);
			if(contact.isTouching()) {
				if(contact.getFixtureA().getBody().getUserData().equals(this)) {
					if(contact.getFixtureA().getBody().getPosition().y > contact.getFixtureB().getBody().getPosition().y-1) {
//						body.applyLinearImpulse(direction.x, direction.y, position.x, position.y);
						body.setLinearVelocity(direction.x, body.getLinearVelocity().y);
					}
				}
				if(contact.getFixtureB().getBody().getUserData().equals(this)) {
					if(contact.getFixtureA().getBody().getPosition().y-1 < contact.getFixtureB().getBody().getPosition().y) {
						body.setLinearVelocity(direction.x, body.getLinearVelocity().y);
//						body.applyLinearImpulse(direction.x, direction.y, position.x, position.y);
					} 
				}
			} else {
				body.setLinearVelocity(direction.x*1f, body.getLinearVelocity().y);
			}
		}
		
		angle = (MathUtils.PI * -position.x) / 2.f;		
	}
	
}
