package net.joinmc.edenville;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.edenville.newversion.Column;
import net.joinmc.edenville.collision.CylinderBox;
import net.joinmc.edenville.db.Loadable;
import net.joinmc.edenville.db.Saveable;
import net.joinmc.edenville.landbased.GasChamber;
import net.joinmc.edenville.landbased.Portal;
import net.joinmc.edenville.landbased.Whirlpool;
import net.joinmc.edenville.utils.ConfigFile;
import net.joinmc.edenville.utils.EnumConfigFiles;
import net.joinmc.edenville.utils.Injector;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.RegistryMaterials;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockTrapdoor;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import sun.reflect.misc.ReflectUtil;

import static net.joinmc.edenville.utils.OutputUtils.*;
import static net.joinmc.edenville.utils.FunctionalityUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Naturality extends JavaPlugin {
	
	private List<ConfigFile> configs = new ArrayList<ConfigFile>();
	/*
	public void onLoad() {
		Class<RegistryMaterials> reg = RegistryMaterials.class;
		try {
			Field ihc = reg.getDeclaredField("bN");
			ihc.setAccessible(true);
			ihc.set(IRegistry.U, new IdentityHashMap<Block, Holder.c<Block>>());
			ihc.set(IRegistry.aa, new IdentityHashMap<TileEntityTypes<?>, Holder.c<TileEntityTypes<?>>>());
			Field f = reg.getDeclaredField("bL");
			f.setAccessible(true);
			f.set(IRegistry.U, false);
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println(net.minecraft.world.level.material.Material.class.getProtectionDomain().getCodeSource().getLocation());
		UraniumOre.register();
		UraniumOre u = new UraniumOre();
		int ind = 0;
		for(int i = 0; i < IRegistry.U.b(); i ++) {
			System.out.println(IRegistry.U.a(i));
		}
		try {
			Material uo = (Material) Injector.addEnumValue(Material.class, "FOREIGN", Material.values().length, new HashMap<String, Object>(){{
				put("id", IRegistry.U.a(u));
				put("data", MaterialData.class);
				put("durability", (short) 0);
				put("maxStack", 64);
				put("legacy", false);
				put("key", NamespacedKey.minecraft("FOREIGN".toLowerCase(Locale.ROOT)));
				put("ctor", MaterialData.class.isAssignableFrom(MaterialData.class)
					? MaterialData.class.getConstructor(Material.class, Byte.TYPE)
					: null);}});
			
			Field f = Material.class.getDeclaredField("ENUM$VALUES");
			//ReflectUtil.newInstance(arg0)
			f.setAccessible(true);
			/*Field modifiersField = Field.class.getDeclaredField("modifiers");
		    modifiersField.setAccessible(true);
		    modifiersField.setInt(f, f.getModifiers() & ~ Modifier.FINAL);//
			Material[] mats = (Material[]) f.get(null);
			ArrayUtils.add(mats, uo);
			f.set(null, mats);
			Field enumConstantDirectoryField = Class.class.getDeclaredField("enumConstantDirectory");
			enumConstantDirectoryField.setAccessible(true);
			enumConstantDirectoryField.set(Material.class, null);
			Field enumConstantsField = Class.class.getDeclaredField("enumConstants");
			enumConstantsField.setAccessible(true);
			enumConstantsField.set(Material.class, null);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	Socket<Loadable> loadableSocket = new Socket<Loadable>(201, Loadable.class);
	Socket<ExecutableContainer> executableSocket = new Socket<ExecutableContainer>(401, ExecutableContainer.class);
	
	public void onEnable() {
		out("Enabling Naturality...");
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		this.getServer().getPluginCommand("nat").setExecutor(new Commands(this));
		Naturality.getPlugin().saveDefaultConfig();
		initConfigs();
		System.out.println(Databases.bbs.getName());
		Databases.init();
		try {
			CylinderBox.loadAllFromDatabase(Databases.bbs);
			Portal.loadAll(Databases.ports);
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loadableSocket.action();
		repeat(create(c -> {
			/*for(GasChamber chamb : GasChamber.chambers) {
				for(Player p : Bukkit.getOnlinePlayers()) {
					chamb.executeContains(p);
				}
			}*/
			for(Player p : Bukkit.getOnlinePlayers()) {
				executableSocket.actionIterate(p);
			}
		}), 0, 5);
		repeat(create(c -> {
			for(Portal portal : Portal.getPortals()) {
				int rnd = new Random().nextInt(5);
				for(int i = 0; i < rnd; i ++) {
					portal.emit();
				}
				for(Entity entity : portal.getLocation().getWorld().getNearbyEntities(portal.getLocation(), 10, 10, 10, ent -> ent instanceof LivingEntity || ent instanceof Item || ent instanceof Projectile || ent instanceof Vehicle)) {
					int minRange = 2;
					if(entity instanceof Projectile) {
						minRange = 4;
					}
					if(entity.getLocation().distance(portal.getLocation()) < minRange) {
						if(entity instanceof Boat) {
							((Boat)entity).remove(); 
							portal.getLocation().getWorld().dropItem(portal.getLocation(), new ItemStack(Material.STICK) {{setAmount(2);}});
							portal.getLocation().getWorld().dropItem(portal.getLocation(), new ItemStack(Material.OAK_PLANKS) {{setAmount(new Random().nextInt(3) + 3);}});
						}else {
							portal.teleport(entity);
						}
					}
				}
			}
		}), 0, 8);
		/*
		 * id : name : x : y : z : h/height : r/radius : su/shouldUpdate
		 * 1	cyl	   0   0   0   10  		  5			 false
		 */
	}
	
	public void onDisable() {
		out("Disabling Naturality...");/*
		Socket<Saveable> socketSave = new Socket<Saveable>(202, Saveable.class);
		socketSave.action();
		socketSave.refresh();*/
		loadableSocket.refresh();
		executableSocket.refresh();
	}
	
	public static Plugin getPlugin() {
		return JavaPlugin.getPlugin(Naturality.class);
	}
	
	public List<ConfigFile> getConfigs(){
		return configs;
	}
	
	private void initConfigs() {
		ConfigFile defaultfig = new ConfigFile(this, EnumConfigFiles.DEFAULT);
		ConfigFile gaschambers = new ConfigFile(this, EnumConfigFiles.GASCHAMBS);
		defaultfig.save();
		gaschambers.save();
		configs.add(defaultfig);
		configs.add(gaschambers);
	}
}
