package com.jumpandrun;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.jogl.JoglApplication;
import com.badlogic.gdx.backends.jogl.JoglApplicationConfiguration;

public class DesktopStarter extends Game {
	
	public static void main(String[] args) {
		JoglApplicationConfiguration config = new JoglApplicationConfiguration();
		config.title = "KPSpielundMusik";

		config.fullscreen = false;
		config.width = 800;
		config.height = 480;
		//fullscreen
		//config.setFromDisplayMode(JoglApplicationConfiguration.getDesktopDisplayMode());
		config.samples = 4;
		config.useGL20 = true;
		config.r = 5;
		config.g = 6;
		config.b = 5;
		config.a = 0;
		new JoglApplication(new DesktopStarter(), config);
	}
	
	@Override 
	public void create () {
		setScreen(new GameScreen(this));
	}

}
