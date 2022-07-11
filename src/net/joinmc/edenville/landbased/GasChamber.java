package net.joinmc.edenville.landbased;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.edenville.newversion.Column;
import net.edenville.newversion.Table;
import net.edenville.newversion.SQLObjects.SQLiteObject;
import net.joinmc.edenville.Databases;
import net.joinmc.edenville.ExecutableContainer;
import net.joinmc.edenville.collision.BoundingBox;
import net.joinmc.edenville.collision.CylinderBox;
import net.joinmc.edenville.db.Instantiable;
import net.joinmc.edenville.db.Instantiable.Instance;
import net.joinmc.edenville.db.Loadable;
import net.joinmc.edenville.db.Saveable;
import net.joinmc.edenville.utils.ConfigFile;
import net.md_5.bungee.api.ChatColor;

@Instance(instanceFieldName = "INSTANCE")
public class GasChamber extends ExecutableContainer implements Loadable, Saveable, Instantiable{
	
	public static final GasChamber INSTANCE = new GasChamber();
	
	private static final int TPS = 40;
	@InstanceList
	public static List<GasChamber> chambers = new ArrayList<GasChamber>();
	
	private int level;
	
	public GasChamber(CylinderBox region, int level) {
		super(region, region.position);
		chambers.add(this);
	}
	
	public GasChamber() {
		super(null, null);
	}
	
	public GasChamber instantiate(HashMap<String, Object> parameters) {
		return new GasChamber((CylinderBox) parameters.get("box"),(int) parameters.get("level"));
	}
	
	public void execute() {
		
	}
	
	public void execute(Block block) {
		
	}
	
	public void execute(Entity entity) {
		((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.POISON, TPS, level));
		((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, TPS, level));
		((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, TPS, level));
		((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, TPS, level));
		if(entity instanceof Player) {
			((Player)entity).sendTitle(ChatColor.YELLOW + "<<< >>>", ChatColor.RED + "Get back to safety!", 1, 20, 1);
		}
	}
	
	
	public boolean contains(Entity entity) {
		return box.contains(entity);
	}
	
	public boolean contains(Location location) {
		return box.contains(location);
	}
	
	public boolean contains(Block block) {
		return box.contains(block);
	}
	
	public void executeContains(LivingEntity entity) {
		if(contains(entity)) {
			execute(entity);
		}
	}
	/*
	public static void loadAll(ConfigFile config) {
		List<String> gasses = config.get().getStringList("chambers");
		if(!BoundingBox.boxes.isEmpty()) {
			for(String gas : gasses) {
				if(BoundingBox.boxes.stream().filter(b -> b.getName().equals(gas)) != null)
					new GasChamber((CylinderBox) BoundingBox.boxes.stream().filter(b -> b.getName().equals(gas)).findFirst().get(), 1);
				else
					System.err.println("No such Element! Config file is in desync with Database! GasChamber not connected to Box");
			}
		}
	}*/
	
	public HashMap<String, Object> loadFromDatabase(Table table, int rowInd) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("level", table.getColumn("level").getValues().get(rowInd));
		parameters.put("box", BoundingBox.boxes.stream().filter(b -> b.getName().equals(table.getColumn("box").getValues().get(rowInd))).findFirst().get());
		return parameters;
	}
	
	@SuppressWarnings("unchecked")
	public void saveToDatabase(Table table) {
		HashMap<Column<? extends Object>, SQLiteObject> values = new HashMap<Column<? extends Object>, SQLiteObject>();
		values.put(table.getColumn("box"), new SQLiteObject(box.getName()));
		values.put(table.getColumn("level"), new SQLiteObject(level));
		table.insert(values.keySet().toArray(new Column[values.keySet().size()]), values.values().toArray(new SQLiteObject[values.values().size()]));
	}
	
	@Action(socketID = 202, massExecutable = true)
	public static void saveAllToDatabase() {
		Saveable.saveAllToDatabase(Databases.gasChambers, chambers);
	}
	
	@Action(socketID = 201, massExecutable = true)
	public static void loadAllFromDatabase() {
		Loadable.loadAllFromDatabase(Databases.gasChambers, INSTANCE, (i, j) -> {});
	}
	/*
	public void saveToConfig(ConfigFile config) {
		config.addList("chambers", this.box.getName());
	}*/

}
