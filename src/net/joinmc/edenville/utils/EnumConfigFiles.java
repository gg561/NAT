package net.joinmc.edenville.utils;

public enum EnumConfigFiles {
	
	DEFAULT("config.yml", 0), 
	GASCHAMBS("gaschambs.yml", 1),
	GODS("gods.yml", 2),
	EFFECTS("effects.yml", 3);
	
	private String path;
	private int index;
	
	private EnumConfigFiles(String path, int index) {
		this.path = path;
		this.index = index;
	}

	public String getPath() {
		return path;
	}

	public int getIndex() {
		return index;
	}

}
