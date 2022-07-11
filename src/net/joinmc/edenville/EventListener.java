package net.joinmc.edenville;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.craftbukkit.v1_18_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_18_R2.CraftChunkSnapshot;

import net.joinmc.edenville.collision.BoundingBox;
import net.joinmc.edenville.landbased.GasChamber;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.level.chunk.Chunk;

import static net.joinmc.edenville.utils.OutputUtils.*;
import static net.joinmc.edenville.utils.FunctionalityUtils.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.block.CraftJukebox;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.block.Jukebox;

public class EventListener implements Listener {
	
	private static ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
	
	public EventListener() {
		BookMeta bm = (BookMeta) book.getItemMeta();
		bm.setTitle("Last moment of peace");
		List<String> pages = new ArrayList<String>();
		pages.add("This is the last moment of peace...\nAfter this... only war remains\n\nSo... for one last time, one final moment,\nallow me to come back to this moment,\nto the moment it all started...\nBack to where we were\nWhere we were supposed to be\nBack to being who we are\n who we were supposed to be...");
		bm.setPages(pages);
		book.setItemMeta(bm);
	}
	
	public enum MusicDiscs{
		
		MUSIC_DISC_11(Sound.MUSIC_DISC_11), 
		MUSIC_DISC_13(Sound.MUSIC_DISC_13), 
		MUSIC_DISC_BLOCKS(Sound.MUSIC_DISC_BLOCKS), 
		MUSIC_DISC_CAT(Sound.MUSIC_DISC_CAT),
		MUSIC_DISC_CHIRP(Sound.MUSIC_DISC_CHIRP),
		MUSIC_DISC_MALL(Sound.MUSIC_DISC_MALL),
		MUSIC_DISC_MELLOHI(Sound.MUSIC_DISC_MELLOHI),
		MUSIC_DISC_FAR(Sound.MUSIC_DISC_FAR),
		MUSIC_DISC_STAL(Sound.MUSIC_DISC_STAL),
		MUSIC_DISC_STRAD(Sound.MUSIC_DISC_STRAD),
		MUSIC_DISC_WAIT(Sound.MUSIC_DISC_WAIT),
		MUSIC_DISC_WARD(Sound.MUSIC_DISC_WARD),
		MUSIC_CREATIVE(Sound.MUSIC_CREATIVE);
		
		private Sound disc;
		
		private MusicDiscs(Sound disc) {
			this.disc = disc;
		}
		
		public Sound getDisc() {
			return disc;
		}
		
		public static MusicDiscs getRandomDisc() {
			ThreadLocalRandom tc = ThreadLocalRandom.current();
			int i = tc.nextInt(values().length);
			return values()[i];
		}
	}
	
	@EventHandler
	public final void onRaid(RaidTriggerEvent event) {
		Player p = event.getPlayer();
		p.sendTitle(ChatColor.GOLD + "CONDEMNED", "", 1, 20, 1);
		event.getWorld().strikeLightning(p.getLocation());
		event.getWorld().spawnEntity(event.getRaid().getLocation(), EntityType.IRON_GOLEM);
		p.playSound(p.getLocation(), MusicDiscs.getRandomDisc().getDisc(), 100, 1);
		ItemStack book = EventListener.book.clone();
		BookMeta bm = (BookMeta) book.getItemMeta();
		bm.setAuthor(p.getName());
		book.setItemMeta(bm);
		p.getInventory().addItem();
		CraftPlayer cp = (CraftPlayer) p;
		p.remove();
		delay(create(c -> {cp.setHandle(new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) event.getWorld()).getHandle(), cp.getProfile()));}), 400);
	}
	
	@EventHandler
	public final void onGrow(BlockGrowEvent event) {
		Block b = event.getBlock();
		for(GasChamber gaschamber : GasChamber.chambers) {
			if(gaschamber.contains(b)) {
				event.setCancelled(true);
			}
		}
	}
	
	public final void onLoad(ChunkLoadEvent event) {
		Chunk chunk = ((CraftChunk)event.getChunk()).getHandle();
		boolean hasBlock = chunk.a().i().a(b -> net.minecraft.world.level.block.Block.i(b.b().n()) == 898);
		if(hasBlock) {
			System.out.println("HAS BLOCK");
		}
	}
	
	@EventHandler
	public final void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
	}
	
	private boolean random(int range, Predicate<Integer> predicate) {
		if(predicate.test(ThreadLocalRandom.current().nextInt(range))) {
			return true;
		}
		return false;
	}

}
