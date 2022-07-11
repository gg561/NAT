package net.joinmc.edenville.landbased;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import net.edenville.newversion.Column;
import net.edenville.newversion.Table;
import net.edenville.newversion.SQLObjects.SQLiteObject;
import net.joinmc.edenville.Databases;
import net.joinmc.edenville.ExecutableContainer;
import net.joinmc.edenville.collision.CylinderBox;
import net.joinmc.edenville.db.Instantiable;
import net.joinmc.edenville.db.Instantiable.Instance;
import net.joinmc.edenville.db.Loadable;
import net.joinmc.edenville.db.Saveable;

@Instance(instanceFieldName = "INSTANCE")
public class Whirlpool extends ExecutableContainer implements Loadable, Saveable, Instantiable {

	public static final Whirlpool INSTANCE = new Whirlpool(null);
	
	@InstanceList
	public static List<Whirlpool> whirlpools = new ArrayList<Whirlpool>();
	private UUID id = UUID.randomUUID();
	
	public Whirlpool() {
		super(null, null);
		whirlpools.add(this);
	}

	public Whirlpool(CylinderBox box, Location location) {
		super(box, location);
		whirlpools.add(this);
	}
	
	private Whirlpool(String flag) {
		super(null, null);
	}

	@Override
	public Instantiable instantiate(HashMap<String, Object> parameters) {
		return new Whirlpool((CylinderBox) parameters.get("box"), (Location) parameters.get("location"));
	}

	@Override
	public HashMap<String, Object> loadFromDatabase(Table table, int rowInd) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		this.id = UUID.fromString((String) table.getColumn("uuid").getValues().get(rowInd));
		this.box = CylinderBox.findByName((String) table.getColumn("box").getValues().get(rowInd));
		this.location = new Location(Bukkit.getWorld((String) table.getColumn("world").getValues().get(rowInd)), (double) table.getColumn("x").getValues().get(rowInd), (double) table.getColumn("y").getValues().get(rowInd), (double) table.getColumn("z").getValues().get(rowInd));
		parameters.put("box", this.box);
		parameters.put("location", this.location);
		return parameters;
	}
	
	public void execute() {
		
	}
	
	public void execute(Block block) {
		block.breakNaturally();
		block.getLocation().getWorld().spawnFallingBlock(block.getLocation(), block.getBlockData());
	}
	
	@Override
	public void execute(Entity entity) {
		if(entity instanceof LivingEntity) {
			entity.setVelocity(entity.getVelocity().add(new Vector(0, -10, 0)));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void saveToDatabase(Table table) {
		HashMap<Column<? extends Object>, SQLiteObject> values = new HashMap<Column<? extends Object>, SQLiteObject>();
		values.put(table.getColumn("uuid"), new SQLiteObject(id.toString()));
		values.put(table.getColumn("box"), new SQLiteObject(box.getName()));
		values.put(table.getColumn("world"), new SQLiteObject(location.getWorld().getName()));
		values.put(table.getColumn("x"), new SQLiteObject(location.getX()));
		values.put(table.getColumn("y"), new SQLiteObject(location.getY()));
		values.put(table.getColumn("z"), new SQLiteObject(location.getZ()));
		table.insert(values.keySet().toArray(new Column[values.keySet().size()]), values.values().toArray(new SQLiteObject[values.values().size()]));
	}
	
	@Action(socketID = 202, massExecutable = true)
	public static void saveAllToDatabase() {
		Saveable.saveAllToDatabase(Databases.whirlpools, whirlpools);
	}
	
	@Action(socketID = 201, massExecutable = true)
	public static void loadAllFromDatabase() {
		Loadable.loadAllFromDatabase(Databases.whirlpools, INSTANCE, (i, j) ->{});
	}

}
