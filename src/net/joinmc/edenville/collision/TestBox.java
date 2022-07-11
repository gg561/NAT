package net.joinmc.edenville.collision;

import org.bukkit.Location;

public class TestBox extends BoundingBox {

	public TestBox(String name, Location position) {
		super(position, name);
		super.testMode = true;
	}

	@Override
	public void collide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCollide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean contains(Location location) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public TestBox clone() {
		return null;
	}

}
