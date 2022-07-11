package net.joinmc.edenville.utils;

public class Key<T> {
	
	private T value;
	private String name;
	
	public Key(String name, T value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public T getValue() {
		return value;
	}
	
	public void setValue(T t) {
		this.value = t;
	}

}
