package com.jumpandrun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;

public class Map {
	static int EMPTY = 0;
	static int TILE = 0xffffff;
	static int START = 0xff0000;
	static int END = 0xff00ff;
	static int DISPENSER = 0xff0100;
	
	Array<Block> blocks = new Array<Block>();
	int[][] tiles;	
	public Player bob;
	Array<Dispenser> dispensers = new Array<Dispenser>();
	Dispenser activeDispenser = null;
	
	public Map() {
		Pixmap pixmap = new Pixmap(Gdx.files.internal("data/levels.png"));
		tiles = new int[pixmap.getWidth()][pixmap.getHeight()];		
		for(int y = 0; y < pixmap.getHeight(); y++) {
			for(int x = 0; x < pixmap.getWidth(); x++) {
				int pix = pixmap.getPixel(x, y) >>> 8;
				if(pix == START) {
					Dispenser dispenser = new Dispenser(x, pixmap.getHeight() - 1 - y);
					dispensers.add(dispenser);
					activeDispenser = dispenser;
					bob = new Player(this, activeDispenser.bounds.x, activeDispenser.bounds.y);
					bob.state = Player.SPAWN;
				} else
				if(pix == DISPENSER) {
					Dispenser dispenser = new Dispenser(x, pixmap.getHeight() - 1 - y);
					dispensers.add(dispenser);					
				} else if(pix == TILE) {
					blocks.add(new Block(x*2,y*-1*2));
				}
			}
		}	
	}
	
	public void update(float deltaTime) {
		bob.update(deltaTime);
		if(bob.state == Player.DEAD) bob = new Player(this, activeDispenser.bounds.x, activeDispenser.bounds.y);
		for(int i = 0; i < dispensers.size; i++) {
			if(bob.bounds.overlaps(dispensers.get(i).bounds)) {
				activeDispenser = dispensers.get(i);
			}
		}

	}	
	
	public boolean isDeadly(int tileId) {
		return false;		
	}
}
