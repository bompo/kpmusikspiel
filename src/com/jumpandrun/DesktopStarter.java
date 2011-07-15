package com.jumpandrun;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopStarter extends Game {

	public static void main(String[] args) {
		DisplayMode displayMode = LwjglApplicationConfiguration.getDesktopDisplayMode();

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "KPSpielundMusik";

		// try {
		// // find first display mode that allows us 640*480*16
		// int mode = -1;
		// DisplayMode[] modes =
		// LwjglApplicationConfiguration.getDisplayModes();
		// for (int i = 0; i < modes.length; i++) {
		// if (modes[i].width == 800 && modes[i].height == 480) {
		// mode = i;
		// break;
		// }
		// }
		// if (mode != -1) {
		// // select above found displaymode
		// System.out.println("Setting display mode to " + modes[mode]);
		// displayMode = modes[mode];
		// System.out.println("Created display.");
		// }
		// } catch (Exception e) {
		// System.err.println("Failed to create display due to " + e);
		// }
		//

		config.setFromDisplayMode(displayMode);

		config.width = 800;
		config.height = 480;

		config.fullscreen = false;
		config.samples = 0;
		config.useGL20 = true;
		new LwjglApplication(new DesktopStarter(), config);
	}

	@Override
	public void create() {
		setScreen(new GameScreen(this));
		Gdx.graphics.setVSync(false);
	}

}
