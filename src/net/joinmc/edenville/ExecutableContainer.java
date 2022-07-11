package net.joinmc.edenville;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import net.joinmc.edenville.collision.BoundingBox;

public abstract class ExecutableContainer implements Actionable {
	
	protected BoundingBox box;
	protected Location location;
	
	public ExecutableContainer(BoundingBox box, Location loc) {
		this.box = box;
		this.location = loc;
	}
	
	@Action(socketID = 400, massExecutable = false)
	public abstract void execute();
	@Action(socketID = 400, massExecutable = false)
	public abstract void execute(Block block);
	@Action(socketID = 400, massExecutable = false)
	public abstract void execute(Entity entity);
	
	@Action(socketID = 401, massExecutable = true)
	public void executeIfContains(Location location) {
		if(box.contains(location)) {
			execute();
		}
	}
	
	@Action(socketID = 401, massExecutable = true)
	public void executeIfContains(Block block) {
		if(box.contains(block.getLocation())) {
			execute(block);
		}
	}
	
	@Action(socketID = 401, massExecutable = true)
	public void executeIfContains(Entity entity) {
		if(box.contains(entity.getLocation())) {
			execute(entity);
		}
	}
	
	public Location getLocation() {
		return location;
	}
	
	public BoundingBox getBox() {
		return box;
	}

}
