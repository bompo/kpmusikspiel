package com.jumpandrun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class MapRenderer {
	Map map;
	OrthographicCamera cam;
	SpriteCache cache;
	SpriteBatch batch = new SpriteBatch(10000);
	ImmediateModeRenderer renderer = new ImmediateModeRenderer(2000 * 2);
	int[][] blocks;
	TextureRegion tile;
	Animation bobLeft;
	Animation bobRight;
	Animation bobJumpLeft;
	Animation bobJumpRight;
	Animation bobIdleLeft;
	Animation bobIdleRight;
	Animation bobDead;
	Animation zap;
	TextureRegion dispenser;
	Animation spawn;
	Animation dying;
	
	public float delta = 0;
	public float deltaRandom = MathUtils.random(-1,1);
	float renderOffsetX =0;
	
	float stateTime = 0;
	Vector3 lerpTarget = new Vector3();
	public double deltaMusic;
	

	public MapRenderer(Map map) {
		this.map = map;
		this.cam = new OrthographicCamera(24, 16);
		this.cam.position.set(map.bob.pos.x, map.bob.pos.y, 0);
		this.cache = new SpriteCache(this.map.tiles.length * this.map.tiles[0].length, false);
		this.blocks = new int[(int) Math.ceil(this.map.tiles.length / 24.0f)][(int) Math.ceil(this.map.tiles[0].length / 16.0f)];

		createAnimations();
		createBlocks();
	}

	public void createBlocks() {
		int width = map.tiles.length;
		int height = map.tiles[0].length;
		for (int blockY = 0; blockY < blocks[0].length; blockY++) {
			for (int blockX = 0; blockX < blocks.length; blockX++) {

				cache.beginCache();
				for (int y = blockY * 16; y < blockY * 16 + 16; y++) {
					for (int x = blockX * 24; x < blockX * 24 + 24; x++) {
						if (x > width)
							continue;
						if (y > height)
							continue;
						int posX = (int) (x);
						int posY = (int) (height - y - 1);
						if (map.tiles[x][y] == Map.TILE)
							cache.add(tile, posX, posY, 1, 1);
					}
				}
				blocks[blockX][blockY] = cache.endCache();
			}
		}
		System.out.println("blocks created");
	}
	
	public void renderBlocks() {
		int width = map.tiles.length;
		int height = map.tiles[0].length;
		for (int blockY = 0; blockY < blocks[0].length; blockY++) {
			for (int blockX = 0; blockX < blocks.length; blockX++) {

				for (int y = blockY * 16; y < blockY * 16 + 16; y++) {
					for (int x = blockX * 24; x < blockX * 24 + 24; x++) {
						if (x > width)
							continue;
						if (y > height)
							continue;
											
						float posX = (x);
						float posY = (height - y - 1 + (renderOffsetX * MathUtils.sin(x)));
						if (map.tiles[x][y] == Map.TILE)
							batch.draw(tile, posX, posY, 1, 1);
					}
				}
			}
		}
	}

	private void createAnimations() {
		this.tile = new TextureRegion(new Texture(Gdx.files.internal("data/tile.png")), 0, 0, 20, 20);
		Texture bobTexture = new Texture(Gdx.files.internal("data/bob.png"));
		TextureRegion[] split = new TextureRegion(bobTexture).split(20, 20)[0];
		TextureRegion[] mirror = new TextureRegion(bobTexture).split(20, 20)[0];
		for (TextureRegion region : mirror)
			region.flip(true, false);
		bobRight = new Animation(0.1f, split[0], split[1]);
		bobLeft = new Animation(0.1f, mirror[0], mirror[1]);
		bobJumpRight = new Animation(0.1f, split[2], split[3]);
		bobJumpLeft = new Animation(0.1f, mirror[2], mirror[3]);
		bobIdleRight = new Animation(0.5f, split[0], split[4]);
		bobIdleLeft = new Animation(0.5f, mirror[0], mirror[4]);
		bobDead = new Animation(0.2f, split[0]);
		split = new TextureRegion(bobTexture).split(20, 20)[2];
		spawn = new Animation(0.1f, split[4], split[3], split[2], split[1]);
		dying = new Animation(0.1f, split[1], split[2], split[3], split[4]);
		dispenser = split[5];
	}


	public void render(float deltaTime) {
		cam.position.lerp(lerpTarget.set(map.bob.pos.x, map.bob.pos.y, 0), 2f * deltaTime);
		cam.update();

		renderOffsetX -= deltaTime;
		if(renderOffsetX<-0.2f) renderOffsetX=-0.2f;
		
		Player.JUMP_VELOCITY -= deltaTime*150f;
		if(Player.JUMP_VELOCITY<5) Player.JUMP_VELOCITY=5;
		
		delta += deltaTime;
//		if(delta<=0) delta =0;
		cache.setTransformMatrix(new Matrix4().setToScaling(1,delta,1));
		
		cache.setProjectionMatrix(cam.combined);
		Gdx.gl.glEnable(GL10.GL_BLEND);
		
//		cache.begin();
//		for (int blockY = 0; blockY < blocks[0].length; blockY++) {
//			for (int blockX = 0; blockX < blocks.length; blockX++) {
//				cache.draw(blocks[blockX][blockY]);
//			}
//		}
//		cache.end();

		stateTime += deltaTime;
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		renderBlocks();
		renderPlayer();
		renderDispensers();
		batch.end();
	}

	private void renderPlayer() {
		Animation anim = null;
		boolean loop = true;
		if (map.bob.state == Player.RUN) {
			if (map.bob.dir == Player.LEFT)
				anim = bobLeft;
			else
				anim = bobRight;
		}
		if (map.bob.state == Player.IDLE) {
			if (map.bob.dir == Player.LEFT)
				anim = bobIdleLeft;
			else
				anim = bobIdleRight;
		}
		if (map.bob.state == Player.JUMP) {
			if (map.bob.dir == Player.LEFT)
				anim = bobJumpLeft;
			else
				anim = bobJumpRight;
		}
		if (map.bob.state == Player.SPAWN) {
			anim = spawn;
			loop = false;
		}
		if (map.bob.state == Player.DYING) {
			anim = dying;
			loop = false;
		}
		batch.draw(anim.getKeyFrame(map.bob.stateTime, loop), map.bob.pos.x, map.bob.pos.y, (float) Math.log10(Player.JUMP_VELOCITY*5),(float)  Math.log10(Player.JUMP_VELOCITY*5));
	
	}

	private void renderDispensers() {
		for (int i = 0; i < map.dispensers.size; i++) {
			Dispenser dispenser = map.dispensers.get(i);
			batch.draw(this.dispenser, dispenser.bounds.x, dispenser.bounds.y, 1, 1);
		}
	}

	public void dispose() {
		cache.dispose();
		batch.dispose();
		tile.getTexture().dispose();
	}
}