package com.jumpandrun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;

public class Background {
	static int EMPTY = 0;
	static int BACKGROUNDSTATICTILE = 0xb83232;
	static int BACKGROUNDMOVETILE = 0x470a0a;
		
	int[][] tiles;	
	
	public Array<Block> backgroundBlocks = new Array<Block>();
	public Array<Block> backgroundMoveBlocks = new Array<Block>();
	
	public Background() {
		Pixmap pixmap = new Pixmap(Gdx.files.internal("data/background.png"));
		tiles = new int[pixmap.getWidth()][pixmap.getHeight()];		
		for(int y = 0; y < pixmap.getHeight(); y++) {
			for(int x = 0; x < pixmap.getWidth(); x++) {
				int pix = pixmap.getPixel(x, y) >>> 8;
				if(pix == BACKGROUNDSTATICTILE) {
					backgroundBlocks.add(new Block(x*2,y*-1*2));					
				} else if(pix == BACKGROUNDMOVETILE) {
					backgroundMoveBlocks.add(new Block(x*2,y*-1*2));					
				}
			}
		}	
	}

}
