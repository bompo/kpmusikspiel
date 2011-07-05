package com.jumpandrun;

import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.utils.Array;
import com.music.RhythmAudio;
import com.music.RhythmValue;

public class GameScreen extends DefaultScreen implements InputProcessor {
	Map map;
	
	final static float MAX_VELOCITY = 7f;		
	boolean jump = false;	
	static World world;
	Body player;
	Fixture playerPhysicsFixture;
	Fixture playerSensorFixture;
	OrthographicCamera cam;
	Box2DDebugRenderer renderer;
	Array<MovingPlatform> platforms = new Array<MovingPlatform>();
	MovingPlatform groundedPlatform = null;
	float stillTime = 0;
	long lastGroundTime = 0;
	SpriteBatch batch;
	BitmapFont font;

	public static RhythmAudio ra = new RhythmAudio();
	public RhythmValue rv1;
	public RhythmValue rv2;
	
	double startTime = 0;
	boolean oldValueRv2 = false;

	private boolean jumping = false;

	public GameScreen(Game game) {
		super(game);
		
		renderer = new Box2DDebugRenderer();
		world = new World(new Vector2(0, -20), true);
		cam = new OrthographicCamera(28, 20);
		createWorld();
		Gdx.input.setInputProcessor(this);
		batch = new SpriteBatch();
		font = new BitmapFont();
	}

	@Override
	public void show() {
		map = new Map();
		
		ra.loadMidi("./data/test.mid");
		ra.play();
		rv1 = new RhythmValue(RhythmValue.type.SINE, 20, ra);
		rv2 = new RhythmValue(RhythmValue.type.BIT, 800, ra);
	}
	
	public static Body createBox(BodyType type, float width, float height, float density) {
		BodyDef def = new BodyDef();
		def.type = type;
		Body box = world.createBody(def);
 
		PolygonShape poly = new PolygonShape();
		poly.setAsBox(width, height);
		box.createFixture(poly, density);
		poly.dispose();
 
		return box;
	}	
 
	public static Body createEdge(BodyType type, float x1, float y1, float x2, float y2, float density) {
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
 
	public static Body createCircle(BodyType type, float radius, float density) {
		BodyDef def = new BodyDef();
		def.type = type;
		Body box = world.createBody(def);
 
		CircleShape poly = new CircleShape();
		poly.setRadius(radius);
		box.createFixture(poly, density);
		poly.dispose();
 
		return box;
	}	
 
	private Body createPlayer() {
		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
		Body box = world.createBody(def);
 
		PolygonShape poly = new PolygonShape();		
		poly.setAsBox(0.45f, 1.4f);
		playerPhysicsFixture = box.createFixture(poly, 1);
		poly.dispose();			
 
		CircleShape circle = new CircleShape();		
		circle.setRadius(0.45f);
		circle.setPosition(new Vector2(0, -1.4f));
		playerSensorFixture = box.createFixture(circle, 0);		
		circle.dispose();		
 
		box.setBullet(true);
 
		return box;
	}
	
	private void createWorld() {
		float y1 = 1; //(float)Math.random() * 0.1f + 1;
		float y2 = y1;
		for(int i = 0; i < 50; i++) {
			Body ground = createEdge(BodyType.StaticBody, -50 + i * 2, y1, -50 + i * 2 + 2, y2, 0);			
			y1 = y2;
			y2 = 1; //(float)Math.random() + 1;
		}			
 
		Body box = createBox(BodyType.StaticBody, 1, 1, 0);
		box.setTransform(30, 3, 0);
		box = createBox(BodyType.StaticBody, 1.2f, 1.2f, 0);
		box.setTransform(5, 2.4f, 0);
		player = createPlayer();
		player.setTransform(10.0f, 4.0f, 0);
		player.setFixedRotation(true);						
 
		for(int i = 0; i < 20; i++) {
			box = createBox(BodyType.DynamicBody, (float)Math.random(), (float)Math.random(), 3);
			box.setTransform((float)Math.random() * 10f - (float)Math.random() * 10f, (float)Math.random() * 10 + 6, (float)(Math.random() * 2 * Math.PI));
		}
 
		for(int i = 0; i < 20; i++) {
			Body circle = createCircle(BodyType.DynamicBody, (float)Math.random() * 0.5f, 3);
			circle.setTransform((float)Math.random() * 10f - (float)Math.random() * 10f, (float)Math.random() * 10 + 6, (float)(Math.random() * 2 * Math.PI));
		}
 
		platforms.add(new MovingPlatform(-2, 3, 2, 0.5f, 2, 0, 4));
		platforms.add(new MovingPlatform(17, 3, 5, 0.5f, 0, 2, 5));		
		platforms.add(new MovingPlatform(-7, 5, 2, 0.5f, -2, 2, 8));	
	}
	
	private boolean isPlayerGrounded(float deltaTime) {				
		groundedPlatform = null;
		List<Contact> contactList = world.getContactList();
		for(int i = 0; i < contactList.size(); i++) {
			Contact contact = contactList.get(i);
			if(contact.isTouching() && (contact.getFixtureA() == playerSensorFixture ||
			   contact.getFixtureB() == playerSensorFixture)) {				
 
				Vector2 pos = player.getPosition();
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

	@Override
	public void render(float delta) {
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
		
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		cam.position.set(player.getPosition().x, player.getPosition().y, 0);
		cam.update();
		cam.apply(Gdx.gl10);
		renderer.render(world);
 
		Vector2 vel = player.getLinearVelocity();
		Vector2 pos = player.getPosition();		
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
			player.setLinearVelocity(vel.x, vel.y);
		}
 
		// calculate stilltime & damp
		if(!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D)) {			
			stillTime += Gdx.graphics.getDeltaTime();
			player.setLinearVelocity(vel.x * 0.9f, vel.y);
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
				player.applyLinearImpulse(0, -24, pos.x, pos.y);				
			}
		}		
 
		// apply left impulse, but only if max velocity is not reached yet
		if(Gdx.input.isKeyPressed(Keys.A) && vel.x > -MAX_VELOCITY) {
			player.applyLinearImpulse(-2f, 0, pos.x, pos.y);
		}
 
		// apply right impulse, but only if max velocity is not reached yet
		if(Gdx.input.isKeyPressed(Keys.D) && vel.x < MAX_VELOCITY) {
			player.applyLinearImpulse(2f, 0, pos.x, pos.y);
		}
 
		// jump, but only when grounded
		if(jump) {			
			jump = false;
			if(grounded) {
				player.setLinearVelocity(vel.x, 0);			
				System.out.println("jump before: " + player.getLinearVelocity());
				player.setTransform(pos.x, pos.y + 0.01f, 0);
				player.applyLinearImpulse(0, 30, pos.x, pos.y);			
				System.out.println("jump, " + player.getLinearVelocity());				
			}
		}					
 
		// update platforms
		for(int i = 0; i < platforms.size; i++) {
			MovingPlatform platform = platforms.get(i);
			platform.update(Math.max(1/30.0f, Gdx.graphics.getDeltaTime()));
		}
 
		// le step...			
		world.step(Gdx.graphics.getDeltaTime(), 4, 4);
		player.setAwake(true);	

//		if (ra.getPlayedChannels()[6]==true) {
//			Player.JUMP_VELOCITY = 15;
//		}
//		if (ra.getPlayedChannels()[5]==true) {
//			renderer.renderOffsetX = 0.2f;
//		}
//		
//		renderer.deltaMusic = rv1.getValue();
//		map.update(delta);
//		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
//		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
//		renderer.render(delta);
//		
//		if (map.bob.state == Player.JUMP) {
//			if (!jumping) {
//				jumping = true;
//			}
//		} else {
//			jumping = false;
//		}


	}
	
	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.W) jump = true;
		
		if (keycode == Keys.ESCAPE) {
			game.setScreen(new MainMenu(game));
		}
		return false;
	}
 
	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Keys.W) jump = false;
		return false;
	}
 
	Vector2 last = null;
	Vector3 point = new Vector3();
 
	@Override
	public boolean touchDown(int x, int y, int pointerId, int button) {
		return false;
	}

	@Override
	public void hide() {
		System.out.println("dispose game screen");
		renderer.dispose();
		ra.stop();
	}

	public float MidiToFrequenc(float note) {
		return (float) (Math.pow(2, (note - 9) / 12.0) * 440);
	}

	@Override
	public boolean keyTyped(char arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchMoved(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

}

