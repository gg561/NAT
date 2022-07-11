package net.joinmc.edenville.landbased;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.Entity;

import net.edenville.newversion.Column;
import net.edenville.newversion.Table;
import net.edenville.newversion.SQLObjects.SQLiteObject;

import static net.joinmc.edenville.utils.FunctionalityUtils.*;

public class Portal {
	
	private static List<Portal> portals = new ArrayList<Portal>();
	
	private Portal linked;
	private Location loc;
	private boolean exit;
	private UUID uuid;
	
	public Portal(Location loc, Portal linked) {
		this.loc = loc;
		this.loc.getBlock().setType(Material.LIGHT);
		((Light)this.loc.getBlock().getBlockData()).setLevel(5);
		this.linked = linked;
		this.uuid = UUID.randomUUID();
		portals.add(this);
	}
	
	public Portal(Location loc, UUID uuid) {
		this.loc = loc;
		this.loc.getBlock().setType(Material.LIGHT);
		((Light)this.loc.getBlock().getBlockData()).setLevel(5);
		this.uuid = uuid;
		portals.add(this);
	}
	
	public Portal(Location loc) {
		this.loc = loc;
		this.loc.getBlock().setType(Material.LIGHT);
		((Light)this.loc.getBlock().getBlockData()).setLevel(5);
		this.uuid = UUID.randomUUID();
		portals.add(this);
	}
	
	public void teleport(Entity ent) {
		if(!exit) {
			linked.exit = true;
			ent.teleport(linked.loc);
			delay(create(c -> {
				linked.exit = false;
			}), 20 * 10);
		}
	}
	
	public void link(Portal link) {
		this.linked = link;
		link.linked = this;
	}
	
	public void emit() {
		Bukkit.getServer().getWorlds().get(0).spawnParticle(Particle.DRAGON_BREATH, loc, 20);
		Bukkit.getServer().getWorlds().get(0).spawnParticle(Particle.CLOUD, loc, 20);
		Bukkit.getServer().getWorlds().get(0).spawnParticle(Particle.ELECTRIC_SPARK, loc, 20);
	}
	
	@SuppressWarnings("unchecked")
	public void saveToDatabase(Table table) {
		HashMap<Column<? extends Object>, SQLiteObject> values = new HashMap<Column<? extends Object>, SQLiteObject>();
		values.put(table.getColumn("uuid"), new SQLiteObject(uuid.toString()));
		values.put(table.getColumn("world"), new SQLiteObject(loc.getWorld().getName()));
		values.put(table.getColumn("x"), new SQLiteObject((int) loc.getX()));
		values.put(table.getColumn("y"), new SQLiteObject((int) loc.getY()));
		values.put(table.getColumn("z"), new SQLiteObject((int) loc.getZ()));
		values.put(table.getColumn("linked"), new SQLiteObject(linked.uuid.toString()));
		table.insert(values.keySet().toArray(new Column[values.keySet().size()]), values.values().toArray(new SQLiteObject[values.values().size()]));
	}
	
	public static void loadAll(Table table) {
		if(!table.isEmpty()) {
			int size = table.getColumns().get(0).getValues().size();
			String[] links = new String[size];
			for(int i = 0; i < size; i ++) {
				new Portal(new Location(Bukkit.getWorld((String) table.getColumn("world").getValues().get(i)), (int) table.getColumn("x").getValues().get(i), (int) table.getColumn("y").getValues().get(i), (int) table.getColumn("z").getValues().get(i)), UUID.fromString((String) table.getColumn("uuid").getValues().get(i)));
				links[i] = (String) table.getColumn("linked").getValues().get(i);
			}
			for(Portal p : portals) {
				for(String link : links) {
					if(portals.stream().anyMatch(port -> port.uuid.toString().equals(link))) {
						Portal linked = portals.stream().filter(port -> port.uuid.toString().equals(link)).findFirst().get();
						linked.link(p);
						p.link(linked);
					}
				}
			}
		}
	}
	
	public static List<Portal> getPortals(){
		return portals;
	}
	
	public Location getLocation() {
		return loc;
	}

}
