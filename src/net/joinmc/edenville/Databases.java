package net.joinmc.edenville;

import net.edenville.newversion.Column;
import net.edenville.newversion.Database;
import net.edenville.newversion.Table;

public class Databases {
	
	public static Database database = new Database(Naturality.getPlugin().getDataFolder().getAbsolutePath(), Naturality.getPlugin().getName());
	public static Table bbs = new Table(database, "CylinderBoxes");
	public static Table ports = new Table(database, "Portals");
	public static Table whirlpools = new Table(database, "Whirlpools");
	public static Table gasChambers = new Table(database, "GasChambers");
	
	public static void init() {
		database.createDatabase();
		database.addTable(bbs);
		bbs.createTable();
		bbs.addColumn(new Column<String>("name", "dv"));
		bbs.addColumn(new Column<String>("world", "dv"));
		bbs.addColumn(new Column<Float>("x", 0f));
		bbs.addColumn(new Column<Float>("y", 0f));
		bbs.addColumn(new Column<Float>("z", 0f));
		bbs.addColumn(new Column<Integer>("height", 0));
		bbs.addColumn(new Column<Integer>("radius", 0));
		bbs.addColumn(new Column<Boolean>("shouldUpdate", false));
		bbs.addColumn(new Column<Boolean>("reverse", false));
		database.addTable(gasChambers);
		gasChambers.createTable();
		gasChambers.addColumn(new Column<String>("box", "dv"));
		gasChambers.addColumn(new Column<Integer>("level", 0));
		database.addTable(ports);
		ports.createTable();
		ports.addColumn(new Column<String>("uuid", "dv"));
		ports.addColumn(new Column<String>("world", "dv"));
		ports.addColumn(new Column<Float>("x", 0f));
		ports.addColumn(new Column<Float>("y", 0f));
		ports.addColumn(new Column<Float>("z", 0f));
		ports.addColumn(new Column<String>("linked", "dv"));
		database.addTable(whirlpools);
		whirlpools.createTable();
		whirlpools.addColumn(new Column<String>("uuid", "dv"));
		whirlpools.addColumn(new Column<String>("box", "dv"));
		whirlpools.addColumn(new Column<String>("world", "dv"));
		whirlpools.addColumn(new Column<Float>("x", 0f));
		whirlpools.addColumn(new Column<Float>("y", 0f));
		whirlpools.addColumn(new Column<Float>("z", 0f));
		//bbs.syphon();
	}

}
