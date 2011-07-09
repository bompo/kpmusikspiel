package com.jumpandrun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
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
	
	public Map() {
		Pixmap pixmap = new Pixmap(Gdx.files.internal("data/levels.png"));
		tiles = new int[pixmap.getWidth()][pixmap.getHeight()];		
		for(int y = 0; y < pixmap.getHeight(); y++) {
			for(int x = 0; x < pixmap.getWidth(); x++) {
				int pix = pixmap.getPixel(x, y) >>> 8;
				if(pix == START) {
					GameInstance.getInstance().createPlayer(x*2,y*-1*2);					
				} else if(pix == TILE) {
					GameInstance.getInstance().addBlock(x*2,y*-1*2);		
				}
			}
		}	
	}
		
	public boolean isDeadly(int tileId) {
		return false;		
	}

}
