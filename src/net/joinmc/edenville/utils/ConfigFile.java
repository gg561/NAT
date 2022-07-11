package net.joinmc.edenville.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.joinmc.edenville.Naturality;

public class ConfigFile {

	private File f;
	private FileConfiguration c;
	private String n;
	private Naturality gap;
	private int index;
	private static List<String> transferValues = new ArrayList<String>();
	private static List<String> a = new ArrayList<String>();
	
	public ConfigFile(Naturality gap, FileConfiguration defaultConfig) {
		c = defaultConfig;
		n = defaultConfig.getName();
		f = new File(gap.getDataFolder(), n);
	}
	
	public ConfigFile(Naturality gap, String fileName) {
		n = fileName;
		this.gap = gap;
		f = new File(this.gap.getDataFolder(), n);
		c = YamlConfiguration.loadConfiguration(f);
	}
	
	public ConfigFile(Naturality gap, EnumConfigFiles config) {
		n = config.getPath();
		this.gap = gap;
		index = config.getIndex();
		f = new File(this.gap.getDataFolder(), n);
		c = YamlConfiguration.loadConfiguration(f);
	}
	
	public void add(String path, String value) {
		String previous = c.getString(path);
		c.set(path, previous + value);
		save();
	}
	
	public void add(String path, List<String> slist) {
		List<String> list = new ArrayList<String>();
		if(c.getStringList(path) != null) {
			list = c.getStringList(path);
		}
		list.addAll(slist);
		c.set(path, list);
		save();
	}
	
	public void add(String path, int number) {
		int finalNumber = c.getInt(path);
		c.set(path, finalNumber + number);
		save();
	}
	
	public void add_(String path, String value, boolean isFin) {
		if(value != null) {
			if(c.getStringList(path) != null) {
				a = c.getStringList(path);
			}
			a.add(value);
			if(!isFin) {
				save();
				return;
			}else {
				addAll(path);
			}
		}else {
			addAll(path);
		}
		save();
	}
	
	private void addAll(String path) {
		c.set(path, a);
		a.clear();
		save();
	}
	
	public void addList(String path, String value) {
		List<String> list = new ArrayList<String>();
		if(c.getStringList(path) != null) {
			list = c.getStringList(path);
		}
		list.add(value);
		c.set(path, list);
		save();
	}
	
	public void addMap(String path, String v0, Object v1) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		ConfigurationSection section = c.getConfigurationSection(path);
		if(section != null && !section.getKeys(false).isEmpty()) {
			for(String key : section.getKeys(false)) {
				map.put(key, c.get(path + "." + key));
			}
		}
		map.put(v0, v1);
		for(String p : map.keySet()) {
			if(!(v1 instanceof Map)) {
				c.set(path + "." + p, map.get(p));
				Bukkit.getLogger().info(path + "." + p + " " + map.get(p));
			}else {
				c.set(path + ".mapInMap", true);
				Bukkit.getLogger().info(((Map)v1).size() + "");
				for(Object vp : ((Map)v1).keySet()) {
					if(vp instanceof String) {
						c.set(path + "." + p + "." + vp, ((Map)v1).get(vp));
					}
				}
			}
		}
		save();
	}
	
	/*
	public void addList(String path, List<Key> list) {
		List<Key> original = new ArrayList<Key>();
		ConfigurationSection section = c.getConfigurationSection(path);
		if(section.getKeys(false) == null) return;
		for(String key : section.getKeys(false)) {
			Key<Object> k = list.stream().filter(a -> a.getName().equals(key)).findFirst().get();
			if(k.getValue().getClass().getSuperclass().equals(Number.class)) {
				k.setValue((Number)c.get(path + "." + k.getName()) + (Integer)k.getValue());
			}
		}
	}*/
	
	public void add(String path, ItemStack item) {
		if(c.getList(path) != null) {
			List<?> result = c.getList(path);
			try {
				Field field = this.getClass().getDeclaredField("result");
				ParameterizedType type = (ParameterizedType) field.getGenericType();
				Class<?> clazz = (Class<?>)type.getActualTypeArguments()[0];
				if(clazz == ItemStack.class) {
					((List<ItemStack>)result).add(item);
					c.set(path, result);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void clear() {
		this.f.delete();
		save();
	}
	
	public void set(String path, Object value) {
		c.set(path, value);
		save();
	}
	
	public void set(String path, Inventory inv) {
		int index = 0;
		for(ItemStack item : inv.getContents()) {
			if(item != null && item.getType() != Material.AIR) {
				c.set(path + "." + Integer.toString(index), item);
			}
			index++;
		}
		save();
	}
	
	public void set(String path, ItemStack[] items) {
		int index = 0;
		for(ItemStack item : items) {
			if(item != null && item.getType() != Material.AIR) {
				c.set(path + "." + Integer.toString(index), item);
			}
			index++;
		}
		save();
	}
	/*
	public void set(String path, Property property) {
		c.set(path + ".value", property.getValue());
		c.set(path + ".signature", property.getSignature());
		save();
	}*/
	
	public void setMap(String path, Map<String, ?> map) {
		try {
			if(map.values().stream().anyMatch(t -> t instanceof Map)) {
				for(String key : map.keySet()) {
					for(String k : ((Map<String, ?>)map.get(key)).keySet()) {
						c.set(path + "." + key + "." + k, ((Map<String, ?>)map.get(key)).get(k));
					}
				}
			}else {
				for(String key : map.keySet()) {
					c.set(path + "." + key, map.get(key));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		save();
	}
	
	public void remove(String path) {
		c.set(path, null);
		save();
	}
	
	public void remove(String path, String value) {
		List<String> list = new ArrayList<String>();
		if(c.getStringList(path) != null) {
			list = c.getStringList(path);
			list.remove(value);
			c.set(path, list);
		}
		save();
	}
	
	public void save() {
		try {
			if(f.exists()) {
				c.save(f);
			}else {
				Bukkit.getLogger().info("FileInexistantException : ConfigFile.save");
				f = new File(this.gap.getDataFolder(), n);
				f.createNewFile();
			}
		}catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void saveDefault() {
		Naturality.getPlugin().saveDefaultConfig();
	}
	
	public FileConfiguration get() {
		return c;
	}
	
	public HashMap<?, ?> getMap(String path){
		ConfigurationSection section = c.getConfigurationSection(path);
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		if(section != null && !section.getKeys(false).isEmpty()) {
			for(String key : section.getKeys(false)) {
				if(!section.getBoolean("mapInMap")) {
					if(key != "mapInMap") {
						map.put(key, section.get(key));
					}
				}else {
					Map<Object, Object> newMap = new HashMap<Object, Object>();
					ConfigurationSection sec = c.getConfigurationSection(path + "." + key);
					if(sec != null && !sec.getKeys(false).isEmpty()) {
						for(String k : sec.getKeys(false)) {
							newMap.put(k, sec.get(k));
						}
					}
					map.put(key, newMap);
				}
			}
		}
		return map;
	}
	
	public File getFile() {
		return f;
	}
	
	public static List<ConfigFile> loadAllConfigs(Naturality gap) {
		List<ConfigFile> configs = new ArrayList<ConfigFile>();
		ConfigFile cf = new ConfigFile(gap, EnumConfigFiles.DEFAULT);
		cf.save();
		configs.add(cf.index, cf);
		return configs;
	}

}
