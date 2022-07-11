package net.joinmc.edenville.collision;

import java.util.HashMap;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import net.edenville.newversion.Column;
import net.edenville.newversion.Table;
import net.edenville.newversion.SQLObjects.SQLiteObject;

public class CylinderBox extends BoundingBox {
	
	private float radius;
	private boolean reverse;
	
	public CylinderBox(String name, Location position, double height, float radius, boolean shouldUpdate) {
		super(position, name);
		super.y = new Vector(0, height, 0);
		super.shouldUpdate = shouldUpdate;
		this.radius = radius;
	}

	@Override
	public void collide() {
		onCollide();
	}

	@Override
	public void onCollide() {
		
	}

	@Override
	public boolean contains(Location location) {
		Vector pos = new Vector(location.getX(), 0, location.getZ());
		if(!reverse) {
			if(pos.distance(new Vector(this.position.getX(), 0, this.position.getZ())) < radius) {
				if(location.getY() > position.getY() && location.getY() < position.getY() + this.y.getY()) {
					return true;
				}
			} 
		}else {
			if(pos.distance(new Vector(this.position.getX(), 0, this.position.getZ())) > radius) {
				return true;
			}else if(location.getY() < position.getY() || location.getY() > position.getY() + this.y.getY()) {
				return true;
			}
		}
		return false;
	}
	
	public HashMap<Column<? extends Object>, SQLiteObject> saveToDatabaseCMD(Table table) {
		HashMap<Column<? extends Object>, SQLiteObject> values = super.saveToDatabaseCMD(table);
		values.put(table.getColumn("height"), new SQLiteObject(this.y.getY()));
		values.put(table.getColumn("radius"), new SQLiteObject(this.radius));
		values.put(table.getColumn("reverse"), new SQLiteObject(this.reverse));
		return values;
	}

	public boolean isReverse() {
		return reverse;
	}

	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}
	
	public static void loadAllFromDatabase(Table table) throws CloneNotSupportedException {
		if(!table.isEmpty())
			BoundingBox.loadAllFromDatabase(table, new CylinderBox("", new Location(null, 0, 0, 0), 0, 0, false), (i, box) -> {
				((CylinderBox) box).radius = (int) table.getColumn("radius").getValues().get(i);
				box.y.setY((int) table.getColumn("height").getValues().get(i));
				((CylinderBox) box).reverse = ((int) table.getColumn("reverse").getValues().get(i)) == 1;
			});
	}
	
	public CylinderBox clone() {
		CylinderBox clone = new CylinderBox(name, position, super.y.getY(), radius, shouldUpdate);
		return clone;
	}
	
	public static CylinderBox findByName(String name) {
		for(BoundingBox box : BoundingBox.boxes.stream().filter(b -> b instanceof CylinderBox).collect(Collectors.toList())) {
			if(box.getName().equals(name)) return (CylinderBox) box;
		}
		return null;
	}

}
