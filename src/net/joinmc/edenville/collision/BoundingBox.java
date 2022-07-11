package net.joinmc.edenville.collision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import net.edenville.newversion.Column;
import net.edenville.newversion.Table;
import net.edenville.newversion.SQLObjects.SQLiteObject;

import static net.joinmc.edenville.utils.OutputUtils.*;

public abstract class BoundingBox {
	
	public static List<BoundingBox> boxes = new ArrayList<BoundingBox>();
	
	public Vector x;
	public Vector y;
	public Vector z;
	public Vector velocity = new Vector(0, 0, 0);
	public Location position;
	protected String name;
	protected boolean isMovable = true;
	protected boolean shouldUpdate = true;
	protected boolean testMode = false;
	
	public BoundingBox(Location position, String name) {
		this.position = position;
		this.name = name;
		boxes.add(this);
	}
	
	public abstract void collide();
	public abstract void onCollide();
	 /**
	  * 
	  * @param dir - direction to be repelled towards
	  * @param force - force of the repel
	  * @param other
	  * 
	  */
	public void repel(Vector dir, double force, BoundingBox other) {
		if(testMode) {
			out("#" + boxes.indexOf(this) + " is repelling " + boxes.indexOf(other));
		}
		if(isMovable && other.isMovable) {
			other.velocity.add(dir.multiply(force));
			velocity.add(dir.multiply(force));
		}else if(other.isMovable && !isMovable) {
			other.velocity.add(dir.multiply(force));
		}else if(isMovable && !other.isMovable) {
			velocity.add(dir.multiply(force));
		}
	}
	
	public void update() {
		updatePosition();
	}
	
	public abstract boolean contains(Location location);
	public boolean contains(Entity entity) {
		return contains(entity.getLocation());
	}
	public boolean contains(Block block) {
		return contains(block.getLocation());
	}
	
	private void updatePosition() {
		int loops = 0;
		while(velocity.getX() != 0 || velocity.getY() != 0 || velocity.getZ() != 0) {
			position.add(velocity);
			//x
			if(velocity.getX() < 0) velocity.setX(velocity.getX() + 1);
			else velocity.setX(velocity.getX() - 1);
			//y
			if(velocity.getY() < 0) velocity.setY(velocity.getY() + 1);
			else velocity.setY(velocity.getY() - 1);
			//z
			if(velocity.getZ() < 0) velocity.setZ(velocity.getZ() + 1);
			else velocity.setZ(velocity.getZ() - 1);
			loops++;
		}
		if(testMode) {
			out("Updated Position " + loops + " times for Bounding Box #" + boxes.indexOf(this));
			out("^ Position updated : " + position);
			out("^ Velocity updated : " + velocity);
		}
	}
	
	public static void updateAll(boolean showOutput) {
		int output = 0;
		for(BoundingBox box : boxes) {
			if(box.shouldUpdate) {
				box.update();
				output++;
			}
		}
		if(showOutput) {
			out("Updated " + output + " Bounding Boxes");
		}
	}
	
	public String getName() {
		return name;
	}
	
	public HashMap<Column<? extends Object>, SQLiteObject> saveToDatabaseCMD(Table table) {
		HashMap<Column<? extends Object>, SQLiteObject> values = new HashMap<Column<? extends Object>, SQLiteObject>();
		values.put(table.getColumn("name"), new SQLiteObject(this.getName()));
		values.put(table.getColumn("world"), new SQLiteObject(this.position.getWorld().getName()));
		values.put(table.getColumn("x"), new SQLiteObject(this.position.getX()));
		values.put(table.getColumn("y"), new SQLiteObject(this.position.getY()));
		values.put(table.getColumn("z"), new SQLiteObject(this.position.getZ()));
		values.put(table.getColumn("shouldUpdate"), new SQLiteObject(this.shouldUpdate));
		return values;
	}
	
	@SuppressWarnings("unchecked")
	public void saveToDatabase(Table table) {
		HashMap<Column<? extends Object>, SQLiteObject> values = saveToDatabaseCMD(table);
		table.insert(values.keySet().toArray(new Column[values.keySet().size()]), values.values().toArray(new SQLiteObject[values.values().size()]));
	}
	
	public void loadFromDatabse(Table table) {
		int rowInd = table.getColumn("name").getValues().indexOf(this.name);
		this.position = new Location(Bukkit.getServer().getWorld((String) table.getColumn("world").getValues().get(rowInd)), (float) table.getColumn("x").getValues().get(rowInd), (float) table.getColumn("y").getValues().get(rowInd), (float) table.getColumn("z").getValues().get(rowInd));
	}
	
	public static void loadAllFromDatabase(Table table, BoundingBox image, BiConsumer<Integer, BoundingBox> plug) throws CloneNotSupportedException {
		int ind = table.getColumns().get(0).getValues().size();
		for(int i = 0; i < ind; i++) {
			Location loc = new Location(Bukkit.getServer().getWorld((String) table.getColumn("world").getValues().get(i)), new Number((java.lang.Number) table.getColumn("x").getValues().get(i)).getNumber(), new Number((java.lang.Number) table.getColumn("y").getValues().get(i)).getNumber(), new Number((java.lang.Number) table.getColumn("z").getValues().get(i)).getNumber());
			BoundingBox box = (BoundingBox) image.clone();
			box.name = (String) table.getColumn("name").getValues().get(i);
			box.position = loc;
			box.shouldUpdate = ((int)table.getColumn("shouldUpdate").getValues().get(i)) == 1;
			plug.accept(i, box);
		}
	}
	
	public abstract BoundingBox clone();

}
