package net.joinmc.edenville.utils;

import org.bukkit.Bukkit;

public class OutputUtils {
	
	public enum Colors {
		
	}
	
	public static void out(String msg) {
		Bukkit.getLogger().info(msg);
	}
	
	public static void out(Colors color, String msg) {
		out(color + msg);
	}

}
