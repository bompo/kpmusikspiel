package com.jumpandrun;

public class MachineGun extends Weapon {
	
	public MachineGun() {
		lastshot = 0;
		shotlimit = 0.1f;
		ammo = new Bullet(0, 0, 0);
	}
}
