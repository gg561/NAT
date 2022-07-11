package net.joinmc.edenville.db;

import java.util.HashMap;
import java.util.function.BiConsumer;

import net.edenville.newversion.Table;
import net.joinmc.edenville.Actionable;

public interface Loadable extends Instantiable, Actionable{
	
	@Action(socketID = 201, massExecutable = false)
	public HashMap<String, Object> loadFromDatabase(Table table, int rowInd);
	@Action(socketID = 201, massExecutable = false)
	public static void loadAllFromDatabase(Table table, Loadable instantiation, BiConsumer<Integer, Table> cons) {
		int size = table.getColumns().get(0).getValues().size();
		for(int i = 0; i < size; i++) {
			instantiation.instantiate(instantiation.loadFromDatabase(table, i));//Creates new object per row : Instantiate for every ROW('i')
			cons.accept(i, table); //Socket for additional actions
		}
	}

}
