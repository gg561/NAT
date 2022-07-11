package net.joinmc.edenville.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;

import sun.misc.Unsafe;
import sun.reflect.Reflection;


public class Injector{
	
	public static void extend() {
		/*Instrument.
		Class.class.getDeclaredMethod("asSubclass", Class.class).setAccessible(true);;
		Injector.class.asSubclass(Class.class);*/
	}
	
	public static Method setAccessible(Class<?> clazz, String methodName, boolean accessibility, Class<?>... parameterTypes) {
		try {
			Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
			method.setAccessible(accessibility);
			return method;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Unsafe getUnsafe() {
		try {
			Field f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			Unsafe u = (Unsafe) f.get(null);
			return u;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Constructor<?> setAccessible(Class<?> clazz, boolean accessibility, Class<?>...parameterTypes) {
		try {
			Constructor<?> c = clazz.getDeclaredConstructor(parameterTypes);
			c.setAccessible(accessibility);
			return c;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object construct(Class<?> clazz, Object[] varargs, Class<?>...parameterTypes) {
		Constructor<?> constructor = setAccessible(clazz, true, parameterTypes);
		try {
			return constructor.newInstance(varargs);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static Class<?> getInnerClass(Class<?> superclass, String innerClassName){
		for(Class<?> clazz : superclass.getDeclaredClasses()) {
			if(clazz.getName().equals(innerClassName)) {
				return clazz;
			}
		}
		return null;
	}
	
	public static Object constructInnerClass(Class<?> superclass, String innerClassName, Object[] varargs, Class<?>...parameterTypes) {
		Object rv = construct(getInnerClass(superclass, innerClassName), varargs, parameterTypes);
		return rv;
	}
	
	public static void injectInto() {
		
	}
	
	public static Object setAccessible(Class<?> clazz, String fieldName, boolean accessibility) {
		try {
			Field f = clazz.getDeclaredField(fieldName);
			f.setAccessible(accessibility);
			return f.get(new Object());
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void makeAccessible(Field field) throws Exception {
	    field.setAccessible(true);
	    Field modifiersField = Field.class.getDeclaredField("modifiers");
	    modifiersField.setAccessible(true);
	    modifiersField.setInt(field, field.getModifiers() & ~ Modifier.FINAL);
	}
	
	public static void backwards(Class<?> c1, Class<?> c2) {
		/*Reflection.filterMethods(c2, )
		Class.class.getDeclaredFields()*/
	}
	
	public static Enum<?> addEnumValue(Class<?> clazz, String name, int ordinal, Map<String, Object> values) {
		try {
			Enum<?> enun = (Enum<?>) ((Unsafe) getUnsafe()).allocateInstance(clazz);
			/*Field name1 = Enum.class.getDeclaredField("name");
			makeAccessible(name1);
			name1.set(enun, name);
			Field ordinal1 = Enum.class.getDeclaredField("ordinal");
			makeAccessible(ordinal1);
			ordinal1.set(enun, ordinal);*/
			for(String n : values.keySet()) {
				Field f = enun.getClass().getDeclaredField(n);
				f.setAccessible(true);
				f.set(enun, values.get(n));
			}
			return enun;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
