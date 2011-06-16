package com.jumpandrun;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.jogl.JoglApplication;

public class DesktopStarter extends Game {
	
	public static void main(String[] args) {
		new JoglApplication(new DesktopStarter(),
				"JumpAndRun", 480, 320, false);
	}
	
	@Override 
	public void create () {
		setScreen(new GameScreen(this));
	}

}
