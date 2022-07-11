package net.joinmc.edenville.utils;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import net.joinmc.edenville.Naturality;

public class FunctionalityUtils {
	
	public static BukkitRunnable create(Consumer<Void> func) {
		return new BukkitRunnable() {

			@Override
			public void run() {
				func.accept(null);
			}
			
		};
	}
	
	public static void repeat(BukkitRunnable runnable) {
		runnable.runTaskTimer(Naturality.getPlugin(), 0, 0);
	}
	
	public static void repeat(BukkitRunnable runnable, int delay, int period) {
		runnable.runTaskTimer(Naturality.getPlugin(), delay, period);
	}
	
	public static void run(BukkitRunnable runnable) {
		runnable.runTask(Naturality.getPlugin());
	}
	
	public static void delay(BukkitRunnable runnable, int delay) {
		runnable.runTaskLater(Naturality.getPlugin(), delay);
	}

}
