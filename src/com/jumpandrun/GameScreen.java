package com.jumpandrun;

import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
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
	World world;
	Body player;
	Fixture playerPhysicsFixture;
	Fixture playerSensorFixture;
	PerspectiveCamera cam;
	Array<MovingPlatform> platforms = new Array<MovingPlatform>();
	Array<Body> boxes = new Array<Body>();
	MovingPlatform groundedPlatform = null;
	float stillTime = 0;
	long lastGroundTime = 0;
	SpriteBatch batch;
	BitmapFont font;

	public static RhythmAudio ra = new RhythmAudio();
	public RhythmValue rv1;
	public RhythmValue rv2;
	
	float startTime = 0;
	float delta = 0;
	boolean oldValueRv2 = false;

	private boolean jumping = false;
	
	// GLES20
	Matrix4 model = new Matrix4().idt();
	Matrix4 tmp = new Matrix4().idt();
	private ShaderProgram transShader;
	private ShaderProgram bloomShader;
	Mesh blockModel;
	Mesh playerModel;
	Mesh targetModel;
	Mesh quadModel;
	Mesh wireCubeModel;
	Mesh sphereModel;
	Mesh sphereSliceModel;
	FrameBuffer frameBuffer;
	FrameBuffer frameBufferVert;
	
	float angleXBack = 0;
	float angleYBack = 0;
	float angleXFront = 0;
	float angleYFront = 0;
	Vector3 xAxis = new Vector3(1, 0, 0);
	Vector3 yAxis = new Vector3(0, 1, 0);
	Vector3 zAxis = new Vector3(0, 0, 1);

	public GameScreen(Game game) {
		super(game);
		
		map = new Map();
		
		world = new World(new Vector2(0, -20), true);
		
		cam = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0, 0,-5f);
		cam.direction.set(0, 0, -1);
		cam.up.set(0, 1, 0);
		cam.near = 1f;
		cam.far = 1000;
		
		createWorld();
		Gdx.input.setInputProcessor(this);
		batch = new SpriteBatch();
		font = new BitmapFont();
		
		blockModel = Resources.getInstance().blockModel;
		playerModel = Resources.getInstance().playerModel;
		targetModel = Resources.getInstance().targetModel;
		quadModel = Resources.getInstance().quadModel;
		wireCubeModel = Resources.getInstance().wireCubeModel;
		sphereModel = Resources.getInstance().sphereModel;
		sphereSliceModel = Resources.getInstance().sphereSliceModel;
		
		transShader = Resources.getInstance().transShader;
		bloomShader = Resources.getInstance().bloomShader;
		
		initRender();
	}
	
	public void initRender() {
		Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		frameBuffer = new FrameBuffer(Format.RGB565, Resources.getInstance().m_i32TexSize, Resources.getInstance().m_i32TexSize, false);		
		frameBufferVert = new FrameBuffer(Format.RGB565, Resources.getInstance().m_i32TexSize, Resources.getInstance().m_i32TexSize, false);
		
		Gdx.gl20.glDepthMask(true);
	}

	@Override
	public void show() {
		
		ra.loadMidi("./data/test.mid");
		ra.play();
		rv1 = new RhythmValue(RhythmValue.type.SINE, 20, ra);
		rv2 = new RhythmValue(RhythmValue.type.BIT, 800, ra);
	}
	
	public Body createBox(BodyType type, float width, float height, float density) {
		BodyDef def = new BodyDef();
		def.type = type;
		Body box = world.createBody(def);
 
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
		player = createPlayer();
		player.setTransform(10.0f, 4.0f, 0);
		player.setFixedRotation(true);
		
		
		Gdx.app.log("", map.blocks.size+"");
		for(Block block:map.blocks) {	
			Body box = createBox(BodyType.StaticBody, 1, 1, 3);
			box.setTransform(block.x*2 , block.y*-1*2, 0);
			boxes.add(box);
			
		}	
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
		startTime+=delta;
		
		angleXBack += MathUtils.sin(startTime) * delta * 10f;
		angleYBack += MathUtils.cos(startTime) * delta * 5f;

		angleXFront += MathUtils.sin(startTime) * delta * 10f;
		angleYFront += MathUtils.cos(startTime) * delta * 5f;
		
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		cam.position.set(player.getPosition().x, player.getPosition().y, 15);
		cam.update();
		
		if (Resources.getInstance().bloomOnOff) {
			frameBuffer.begin();
			renderScene();
			frameBuffer.end();

			// PostProcessing
			Gdx.gl.glDisable(GL20.GL_CULL_FACE);
			Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
			Gdx.gl.glDisable(GL20.GL_BLEND);

			frameBuffer.getColorBufferTexture().bind(0);

			bloomShader.begin();
			bloomShader.setUniformi("sTexture", 0);
			bloomShader.setUniformf("bloomFactor", Helper.map((MathUtils.sin(startTime * 3f) * delta * 50f) + 0.5f, 0, 1, 0.50f, 0.55f));

			frameBufferVert.begin();
			bloomShader.setUniformf("TexelOffsetX", Resources.getInstance().m_fTexelOffset);
			bloomShader.setUniformf("TexelOffsetY", 0.0f);
			quadModel.render(bloomShader, GL20.GL_TRIANGLE_STRIP);
			frameBufferVert.end();

			frameBufferVert.getColorBufferTexture().bind(0);

			frameBuffer.begin();
			bloomShader.setUniformf("TexelOffsetX", 0.0f);
			bloomShader.setUniformf("TexelOffsetY", Resources.getInstance().m_fTexelOffset);
			quadModel.render(bloomShader, GL20.GL_TRIANGLE_STRIP);
			frameBuffer.end();

			bloomShader.end();
		}

		// render scene again
		renderScene();
		
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDisable(GL20.GL_BLEND);
				
		if(Resources.getInstance().bloomOnOff) {
			batch.enableBlending();
			batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);
			batch.begin();
			batch.draw(frameBuffer.getColorBufferTexture(), 0, 0,800,480,0,0,frameBuffer.getWidth(),frameBuffer.getHeight(),false,true);
			batch.end();
		}
		 
		physicStuff();	
		


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

	private void physicStuff() {
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
	}

	private void renderScene() {
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		
		Gdx.gl20.glEnable(GL20.GL_BLEND);
		Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
				
		transShader.begin();
		transShader.setUniformMatrix("VPMatrix", cam.combined);
		
		//render boxes
		for (Body box : boxes) {
			if(cam.frustum.pointInFrustum(new Vector3(box.getTransform().getPosition().x, box.getTransform().getPosition().y, 0))) {
				tmp.idt();
				model.idt();
				
				tmp.setToScaling(1f, 1f, 1f);
				model.mul(tmp);
	
				tmp.setToTranslation(box.getTransform().getPosition().x, box.getTransform().getPosition().y, 0);
				model.mul(tmp);
	
				tmp.setToScaling(0.95f, 0.95f, 0.95f);
				model.mul(tmp);
						
				transShader.setUniformMatrix("MMatrix", model);
				
				transShader.setUniformf("a_color", Resources.getInstance().blockColor[0], Resources.getInstance().blockColor[1], Resources.getInstance().blockColor[2], Resources.getInstance().blockColor[3]);
				blockModel.render(transShader, GL20.GL_TRIANGLES);
	
				transShader.setUniformf("a_color",Resources.getInstance().blockEdgeColor[0], Resources.getInstance().blockEdgeColor[1],Resources.getInstance().blockEdgeColor[2], Resources.getInstance().blockEdgeColor[3]);
				wireCubeModel.render(transShader, GL20.GL_LINE_STRIP);
			}
		}
		
		//render player
		{
			tmp.idt();
			model.idt();
			
			tmp.setToScaling(1f, 1f, 1f);
			model.mul(tmp);
			
			tmp.setToTranslation(player.getTransform().getPosition().x, player.getTransform().getPosition().y-0.8f, 0);
			model.mul(tmp);
			
			tmp.setToRotation(xAxis, angleXBack);
			model.mul(tmp);
			tmp.setToRotation(yAxis, angleYBack);
			model.mul(tmp);

			tmp.setToScaling(0.5f, 0.5f, 0.5f);
			model.mul(tmp);
	
			transShader.setUniformMatrix("MMatrix", model);
			transShader.setUniformf("a_color",Resources.getInstance().playerColor[0], Resources.getInstance().playerColor[1], Resources.getInstance().playerColor[2], Resources.getInstance().playerColor[3]);
			playerModel.render(transShader, GL20.GL_TRIANGLES);
			
			tmp.setToScaling(2.0f, 2.0f, 2.0f);
			model.mul(tmp);

			//render hull			
			transShader.setUniformMatrix("MMatrix", model);
			transShader.setUniformf("a_color",Resources.getInstance().playerEdgeColor[0], Resources.getInstance().playerEdgeColor[1], Resources.getInstance().playerEdgeColor[2], Resources.getInstance().playerEdgeColor[3]);
			playerModel.render(transShader, GL20.GL_LINE_STRIP);
		}
		transShader.end();
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.W) jump = true;
		
		if (keycode == Keys.ESCAPE) {
			game.setScreen(new MainMenu(game));
		}
		
		if (keycode == Keys.R) {
			for(Body box:boxes) {
				box.setTransform(box.getTransform().getPosition().x+delta,box.getTransform().getPosition().y+delta*(MathUtils.sin(startTime)*delta*500f),0);
			}
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


