package com.jumpandrun;

public class RocketLauncher extends Weapon {
	
	public RocketLauncher() {
		lastshot = 0;
		shotlimit = 1.0f;
		ammo = new Rocket(0, 0, 0);
	}
}
