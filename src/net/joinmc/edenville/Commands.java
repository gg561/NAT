package net.joinmc.edenville;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import net.joinmc.edenville.collision.CylinderBox;
import net.joinmc.edenville.landbased.GasChamber;
import net.joinmc.edenville.landbased.Portal;
import net.joinmc.edenville.landbased.Whirlpool;
import net.joinmc.edenville.utils.ConfigFile;
import net.joinmc.edenville.utils.EnumConfigFiles;

public class Commands implements CommandExecutor, TabCompleter{
	
	private Naturality nat;
	
	public Commands(Naturality nat) {
		this.nat= nat;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player)sender;
			if(label.equalsIgnoreCase("nat")) {
				//if(args.length < 6) return false;
				if(args[0].equalsIgnoreCase("cyl")) {
					CylinderBox cyl = new CylinderBox(args[1], new Location(p.getWorld(), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4])), Integer.parseInt(args[5]), Integer.parseInt(args[6]), false);
					cyl.saveToDatabase(Databases.bbs);
				}else if(args[0].equalsIgnoreCase("gaschamber")) {
					CylinderBox cyl = new CylinderBox(args[1], p.getLocation(), Integer.parseInt(args[2]), Integer.parseInt(args[3]), false);
					cyl.setReverse(true);
					cyl.saveToDatabase(Databases.bbs);
					GasChamber chamb = new GasChamber(cyl, 10);
					chamb.saveToDatabase(Databases.gasChambers);
				}else if(args[0].equalsIgnoreCase("portal")) {
					Portal portal = new Portal(p.getLocation());
					Portal link = new Portal(new Location(p.getWorld(), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3])), portal);
					portal.link(link);
					link.saveToDatabase(Databases.ports);
					portal.saveToDatabase(Databases.ports);
				}else if(args[0].equalsIgnoreCase("whirlpool")) {
					CylinderBox box = new CylinderBox(args[1], p.getLocation(), Float.parseFloat(args[2]), Float.parseFloat(args[3]), false);
					box.saveToDatabase(Databases.bbs);
					Whirlpool pool = new Whirlpool(box, p.getLocation());
					pool.saveToDatabase(Databases.whirlpools);
				}
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> completion = new ArrayList<String>();
		return completion;
	}

}
