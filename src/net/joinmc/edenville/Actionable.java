package net.joinmc.edenville;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface Actionable {
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Action{
		int socketID();
		boolean massExecutable();
	}

}
