package com.jumpandrun;

public class RocketLauncher extends Weapon {
	
	public RocketLauncher() {
		lastshot = 1.0f;
		shotlimit = 0.5f;
		ammo = new Rocket(0, 0, 0);
	}
}
