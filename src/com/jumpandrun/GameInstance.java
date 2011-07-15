package com.jumpandrun;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;

public class GameInstance {

	final static float MAX_VELOCITY = 10f;	

	public static GameInstance instance;
	
	public Array<MovingPlatform> platforms = new Array<MovingPlatform>();
	public Array<Block> blocks = new Array<Block>();
	public Array<Enemy> enemies = new Array<Enemy>();
	public World world  = new World(new Vector2(0, -20), true);
	public Player player;	

	float stillTime = 0;
	long lastGroundTime = 0;

	MovingPlatform groundedPlatform = null;
	
	Body box;
	Block block;

	Fixture playerPhysicsFixture;
	Fixture playerSensorFixture;
	
	public static GameInstance getInstance() {
		if (instance == null) {
			instance = new GameInstance();
		}
		return instance;
	}

	public void resetGame() {
	}
	
	public Body createBox(BodyType type, float width, float height, float density) {
		BodyDef def = new BodyDef();
		def.type = type;
		Body box = GameInstance.getInstance().world.createBody(def);
 
		PolygonShape poly = new PolygonShape();
		poly.setAsBox(width, height);
		box.createFixture(poly, density);
		poly.dispose();
 
		return box;
	}	
 
	public Body createEdge(BodyType type, float x1, float y1, float x2, float y2, float density) {
		BodyDef def = new BodyDef();
		def.type = type;
		Body box = world.createBody(def);
		PolygonShape poly = new PolygonShape();		
		poly.setAsEdge(new Vector2(0, 0), new Vector2(x2 - x1, y2 - y1));
		box.createFixture(poly, density);
		box.setTransform(x1, y1, 0);
		poly.dispose();
 
		return box;
	}
 
	public Body createCircle(BodyType type, float radius, float density) {
		BodyDef def = new BodyDef();
		def.type = type;
		Body box = world.createBody(def);
 
		CircleShape poly = new CircleShape();
		poly.setRadius(radius);
		box.createFixture(poly, density);
		poly.dispose();
 
		return box;
	}	
	
	public void addEnemySpawner(float x, float y) {
		EnemySpawner enemySpawner = new EnemySpawner(x, y);
		box = createBox(BodyType.StaticBody, 1, 1, 3);
		box.setTransform(enemySpawner.position.x , enemySpawner.position.y, 0);
		
		enemySpawner.body = box;
		blocks.add(enemySpawner);
		enemySpawner.body.setUserData(enemySpawner);		
	}
	
	public void addJumpBlock(float x, float y) {
		JumpBlock block = new JumpBlock(x,y);
		box = createBox(BodyType.StaticBody, 1, 1, 3);
		box.setTransform(block.position.x , block.position.y, 0);
		
		block.body = box;
		blocks.add(block);
		block.body.setUserData(block);
	}
	
	public void addBlock(float x, float y) {
		Block block = new Block(x,y);
		box = createBox(BodyType.StaticBody, 1, 1, 3);
		box.setTransform(block.position.x , block.position.y, 0);
		
		block.body = box;
		block.body.setUserData(block);
		blocks.add(block);
	}
	
	public void addEnemy() {
		for(Block block:blocks) {
			if(block instanceof EnemySpawner) {
				Enemy enemy = new Enemy(block.position.x, block.position.y-1.5f);
//				BodyDef def = new BodyDef();
//				def.type = BodyType.DynamicBody;
//				Body box = world.createBody(def);
				
				Body box = createBox(BodyType.DynamicBody, 1, 1, 5);
				
//		
//				PolygonShape poly = new PolygonShape();		
//				poly.setAsBox(1f, 1f);
//				Fixture enemyPhysicsFixture = box.createFixture(poly, 0);
//				poly.dispose();			
		
				box.setBullet(true);		 
				box.setTransform(block.position.x, block.position.y-1.5f, 0);
				
//				enemy.enemyPhysicsFixture = enemyPhysicsFixture;
				enemy.body = box;
				enemy.body.setUserData(enemy);
				enemies.add(enemy);
				Gdx.app.log("", "add enemy " + enemy.body.getPosition().x + " " + enemy.body.getPosition().y );
			}
		
		}
	}
 
	public void createPlayer(float x, float y) {
		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
		Body box = world.createBody(def);
		 
		PolygonShape poly = new PolygonShape();		
		poly.setAsBox(0.1f, 0.1f);
		playerPhysicsFixture = box.createFixture(poly, 0);
		poly.dispose();			
 
		CircleShape circle = new CircleShape();		
		circle.setRadius(1f);
		circle.setPosition(new Vector2(0, -0.6f));
		playerSensorFixture = box.createFixture(circle, 1);		
		circle.dispose();		
 
		box.setBullet(true);
 
		box.setFixedRotation(true);
		box.setTransform(x, y, 0);
		
		player = new Player(x, y);
		player.body = box;
		player.body.setUserData(player);
	}
	
	public boolean isPlayerGrounded(float deltaTime) {				
		groundedPlatform = null;
		List<Contact> contactList = world.getContactList();
		for(int i = 0; i < contactList.size(); i++) {
			Contact contact = contactList.get(i);
			if(contact.isTouching() && (contact.getFixtureA() == playerSensorFixture ||
			   contact.getFixtureB() == playerSensorFixture)) {				
 
				Vector2 pos = player.position.tmp();
				WorldManifold manifold = contact.getWorldManifold();
				boolean below = true;
				for(int j = 0; j < manifold.getNumberOfContactPoints(); j++) {
					below &= (manifold.getPoints()[j].y < pos.y - 1.5f);
				}
 
				if(below) {
					if(contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().equals("p")) {
						groundedPlatform = (MovingPlatform)contact.getFixtureA().getBody().getUserData();							
					}
 
					if(contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().equals("p")) {
						groundedPlatform = (MovingPlatform)contact.getFixtureB().getBody().getUserData();
					}											
					return true;			
				}
 
				return false;
			}
		}
		return false;
	}
	
	public void physicStuff(float delta) {
		Vector2 vel = player.body.getLinearVelocity();
		Vector2 pos = player.position.tmp();		
		boolean grounded = isPlayerGrounded(Gdx.graphics.getDeltaTime());
		if(grounded) {
			lastGroundTime = System.nanoTime();
		} else {
			if(System.nanoTime() - lastGroundTime < 100000000) {
				grounded = true;
			}
		}
 
		// cap max velocity on x		
		if(Math.abs(vel.x) > MAX_VELOCITY) {			
			vel.x = Math.signum(vel.x) * MAX_VELOCITY;
			player.body.setLinearVelocity(vel.x, vel.y);
		}
 
		// calculate stilltime & damp
		if(!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D)) {			
			stillTime += Gdx.graphics.getDeltaTime();
			player.body.setLinearVelocity(vel.x * 0.9f, vel.y);
		}
		else { 
			stillTime = 0;
		}			
 
		// disable friction while jumping
		if(!grounded) {			
			playerPhysicsFixture.setFriction(0f);
			playerSensorFixture.setFriction(0f);			
		} else {
			if(!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D) && stillTime > 0.2) {
				playerPhysicsFixture.setFriction(100f);
				playerSensorFixture.setFriction(100f);
			}
			else {
				playerPhysicsFixture.setFriction(0.2f);
				playerSensorFixture.setFriction(0.2f);
			}
 
			if(groundedPlatform != null && groundedPlatform.dist == 0) {
				player.body.applyLinearImpulse(0, -24, pos.x, pos.y);				
			}
		}		
 
		// apply left impulse, but only if max velocity is not reached yet
		if(Gdx.input.isKeyPressed(Keys.A) && vel.x > -MAX_VELOCITY) {
			player.body.applyLinearImpulse(-2f, 0, pos.x, pos.y);
		}
 
		// apply right impulse, but only if max velocity is not reached yet
		if(Gdx.input.isKeyPressed(Keys.D) && vel.x < MAX_VELOCITY) {
			player.body.applyLinearImpulse(2f, 0, pos.x, pos.y);
		}
 
		// jump, but only when grounded
		if(player.jump) {			
			player.jump = false;
			if(grounded) {
				player.body.setLinearVelocity(vel.x, 0);			
				System.out.println("jump before: " + player.body.getLinearVelocity());
				player.body.setTransform(pos.x, pos.y + 0.01f, 0);
				player.body.applyLinearImpulse(0, 80, pos.x, pos.y);			
				System.out.println("jump, " + player.body.getLinearVelocity());				
			}
		}					
 
		// update platforms
		for(int i = 0; i < platforms.size; i++) {
			MovingPlatform platform = platforms.get(i);
			platform.update(Math.max(1/30.0f, Gdx.graphics.getDeltaTime()));
		}
		
		//update jump plattforms
		for(int i = 0; i < blocks.size; i++) {
			Block block = blocks.get(i);
			if(block instanceof JumpBlock) {
				if(((JumpBlock) block).jumpAnim > 0) {
					((JumpBlock) block).jumpAnim = Math.max(0, ((JumpBlock) block).jumpAnim - Gdx.graphics.getDeltaTime());
				} 
			}			
		}
		
		//update enemies
		for(int i = 0; i < enemies.size; i++) {
			Enemy enemy = enemies.get(i);
			enemy.update();	
			enemy.body.setAwake(true);
		}
 
		// le step...			
		world.step(delta, 50, 50);
		player.update();
		player.body.setAwake(true);
		
	}
	
	public void activateJumpBlocks() {
		List<Contact> contactList = world.getContactList();
		for(int i = 0; i < contactList.size(); i++) {
			Contact contact = contactList.get(i);
			if(contact.isTouching()) {
				if(contact.getFixtureA().getBody().getUserData() instanceof JumpBlock) {
					if(contact.getFixtureA().getBody().getPosition().y < contact.getFixtureB().getBody().getPosition().y-1) {
						contact.getFixtureB().getBody().applyLinearImpulse(0, 60, player.position.x, player.position.y);
	//					((JumpBlock)contact.getFixtureA().getBody().getUserData()).jump();
					}
				}
				if(contact.getFixtureB().getBody().getUserData() instanceof JumpBlock ) {
					if(contact.getFixtureA().getBody().getPosition().y-1 > contact.getFixtureB().getBody().getPosition().y) {
					contact.getFixtureA().getBody().applyLinearImpulse(0, 60, player.position.x, player.position.y);
//					((JumpBlock)contact.getFixtureB().getBody().getUserData()).jump();
					}
				}
			}
		}
		
		for(int i = 0; i < blocks.size; i++) {
			Block block = blocks.get(i);
			if(block instanceof JumpBlock) {
				((JumpBlock) block).jump();
			}			
		}
	}
}