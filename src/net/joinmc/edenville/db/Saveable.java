package net.joinmc.edenville.db;

import java.util.Collection;

import net.edenville.newversion.Table;
import net.joinmc.edenville.Actionable;

public interface Saveable extends Actionable, Instantiable {
	
	public void saveToDatabase(Table table);
	@Action(socketID = 201, massExecutable = true)
	public static void saveAllToDatabase(Table table, Collection<? extends Saveable> saveables) {
		for(Saveable saveable : saveables) {
			saveable.saveToDatabase(table);
		}
	}

}
