package net.joinmc.edenville.db;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface Instantiable {
	
	public static List<Loadable> stored = new ArrayList<Loadable>();
	
	public Instantiable instantiate(HashMap<String, Object> parameters);
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Instance{
		String instanceFieldName();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface InstanceList{
		
	}

}
