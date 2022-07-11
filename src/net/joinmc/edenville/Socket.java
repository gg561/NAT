package net.joinmc.edenville;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import net.joinmc.edenville.Actionable.Action;
import net.joinmc.edenville.db.Instantiable;
import net.joinmc.edenville.db.Instantiable.Instance;
import net.joinmc.edenville.db.Instantiable.InstanceList;

public class Socket<T extends Actionable> {
	
	private static final String CLASS_PATH = "net.joinmc.edenville";
	
	private static HashMap<Integer, HashMap<Class<?>, Method[]>> connectedClasses = new HashMap<Integer, HashMap<Class<?>, Method[]>>();
	private static HashMap<Class<?>, List<Instantiable>> instanceLists = new HashMap<Class<?>, List<Instantiable>>();
	
	private Class<T> actor;
	private int socketID;
	private boolean debug;
	
	public Socket(Class<T> clazz){
		this.actor = clazz;
	}
	
	public Socket(int socketID, Class<T> clazz) {
		this.socketID = socketID;
		this.actor = clazz;
	}
	
	public Socket(Class<T> clazz, boolean debug) {
		this.actor = clazz;
		this.debug = debug;
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public void action(Object... args) {
		//TODO entirely written by machine... totally
		ClassPath cp;
		List<Class<?>> parameters = new ArrayList<Class<?>>();
		for(Object arg : args) {
			parameters.add(arg.getClass());
		}
		try {
			cp = ClassPath.from(this.getClass().getClassLoader());
			System.out.println("[CLPATH]" + cp);
			ImmutableSet<ClassInfo> classInfos = cp.getTopLevelClassesRecursive(CLASS_PATH);
			for(ClassInfo classInfo : classInfos) {
				System.out.println(classInfo.getName());
				Class<?> clazz = classInfo.load();
				//System.out.println("[CLNAME]" + clazz.getName());
				if((clazz.getSuperclass() != null && clazz.getSuperclass().equals(actor)) || (clazz.getInterfaces() != null && Arrays.asList(clazz.getInterfaces()).contains(actor)) || clazz.equals(actor)) {
					System.out.println("[CLSUPER]" + actor.getName());
					if(clazz.isAnnotationPresent(Instance.class)) {
						System.out.println("[CLINST]");
						Object instance = clazz.getDeclaredField(clazz.getAnnotation(Instance.class).instanceFieldName()).get(null);
						Method[] meths = findAllExecutableMethods(findAllMethodsWithAnnotation(clazz, Action.class));
						cacheClass(clazz, meths);
						for(Method meth : meths) {
							List<Class<?>> classParams = new ArrayList<Class<?>>();
							for(Parameter param : meth.getParameters()) {
								classParams.add(param.getType());
							}
							if(classParams.equals(parameters)) {
								System.out.println("[SOCKETEXEC]");
								meth.invoke(instance, args);
							}
						}
					}
				}else 
					continue;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void actionStatic(Object args) {
		actionWithInstance(null, args);
	}
	
	public void actionWithInstance(Object instance, Object...args) {
		//TODO entirely written by machine... totally
				ClassPath cp;
				List<Class<?>> parameters = new ArrayList<Class<?>>();
				for(Object arg : args) {
					parameters.add(arg.getClass());
				}
				try {
					cp = ClassPath.from(this.getClass().getClassLoader());
					System.out.println("[CLPATH]" + cp);
					ImmutableSet<ClassInfo> classInfos = cp.getTopLevelClassesRecursive("net.joinmc.edenville");
					for(ClassInfo classInfo : classInfos) {
						System.out.println(classInfo.getName());
						Class<?> clazz = classInfo.load();
						//System.out.println("[CLNAME]" + clazz.getName());
						if((clazz.getSuperclass() != null && clazz.getSuperclass().equals(actor)) || (clazz.getInterfaces() != null && Arrays.asList(clazz.getInterfaces()).contains(actor)) || clazz.equals(actor)) {
							System.out.println("[CLSUPER]" + actor.getName());
							Method[] meths = findAllExecutableMethods(findAllMethodsWithAnnotation(clazz, Action.class));
							cacheClass(clazz, meths);
							for(Method meth : meths) {
								System.out.println("[METH]" + meth.getName());
								System.out.println("[METHAC]");
								List<Class<?>> classParams = new ArrayList<Class<?>>();
								for(Parameter param : meth.getParameters()) {
									classParams.add(param.getType());
								}
								if(classParams.equals(parameters)) {
									System.out.println("[SOCKETEXEC]");
									meth.invoke(instance, args);
								}
							}
						}else 
							continue;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	
	private void actionIterate(Class<?>[] classes, Object... args) {
		for(Class<?> clazz : classes) {
			//System.out.println("CLNAME " + clazz.getName());
			Field instanceListField = findFieldWithAnnotation(clazz, InstanceList.class);
			Method[] meths = findAllExecutableMethods(findAllMethodsWithParameters(findAllMethodsWithAnnotation(clazz, Action.class), args));
			//System.out.println(Arrays.toString(meths));
			if(meths.length > 0)
				cacheClass(clazz, meths);
			if(instanceListField != null)
				try {
					@SuppressWarnings("unchecked")
					List<Instantiable> instanceList = (List<Instantiable>) instanceListField.get(null);
					cacheInstanceList(clazz, instanceList);
					for(Instantiable instantiable : instanceList) {
						if(!(instantiable.getClass().isInterface() || instantiable.getClass().isAnnotation())) {
							for(Method meth : meths) {
								meth.invoke(instantiable, args);
							}
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	private void actionIterateCached(Object... args) {
		try {
			for(Class<?> clazz : connectedClasses.get(socketID).keySet()) {
			//	System.out.println(clazz.getName());
				List<Instantiable> instanceList = instanceLists.get(clazz);
				if(instanceList != null && !instanceList.isEmpty()) {
					for(Instantiable instantiable : instanceList) {
						if(!(instantiable.getClass().isInterface() || instantiable.getClass().isAnnotation()))
							for(Method meth : connectedClasses.get(socketID).get(clazz)) {
								meth.invoke(instantiable, args);
							}
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void actionIterate(Object... args) {
		if(connectedClasses.containsKey(socketID) && connectedClasses.get(socketID).size() > 0) {
			//System.out.println(connectedClasses);
			actionIterateCached(args);
		}else {
			Class<?>[] classes = findAllClassesImplementing(findAllClassesUnderCurrent(), actor);
			actionIterate(classes, args);
		}
	}
	
	private Field findFieldWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
		for(Field field : clazz.getDeclaredFields()) {
			if(field.isAnnotationPresent(annotation)) {
				return field;
			}
		}
		return null;
	}
	
	private Field[] findAllFieldWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
		List<Field> fields = new ArrayList<Field>();
		for(Field field : clazz.getDeclaredFields()) {
			if(field.isAnnotationPresent(annotation)) {
				fields.add(field);
			}
		}
		return fields.toArray(new Field[fields.size()]);
	}
	
	public void actionAll() {
		
	}
	
	public void refresh() {
		connectedClasses.remove(socketID);
		instanceLists.clear();
	}
	
	private void cacheClass(Class<?> clazz, Method[] methods) {
		if(connectedClasses.containsKey(socketID)) {
			Method[] meths = new Method[0];
			if(connectedClasses.get(socketID).get(clazz) != null) {
				meths = connectedClasses.get(socketID).get(clazz);
			}
			meths = (Method[]) ArrayUtils.addAll(meths, methods);
			//System.out.println(Arrays.toString(meths));
			connectedClasses.get(socketID).put(clazz, meths);
		}else {
			connectedClasses.put(socketID, new HashMap<Class<?>, Method[]>() {{put(clazz, methods);}});
		}
	}
	
	private void cacheInstanceList(Class<?> clazz, List<Instantiable> instanceList) {
		instanceLists.put(clazz, instanceList);
	}
	
	private void removeCachedClass(Class<?> clazz) {
		if(connectedClasses.containsKey(socketID)) {
			connectedClasses.get(socketID).remove(clazz);
		}
	}
	
	private ImmutableSet<ClassInfo> findAllClassesUnderCurrent() {
		ClassPath cp;
		try {
			cp = ClassPath.from(this.getClass().getClassLoader());
			ImmutableSet<ClassInfo> classInfos = cp.getTopLevelClassesRecursive(CLASS_PATH);
			return classInfos;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Class<?>[] findAllClassesImplementing(Collection<ClassInfo> classInfos, Class<?> implemented){
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for(ClassInfo info : classInfos) {
			Class<?> clazz = info.load();
			if(implemented.isAssignableFrom(clazz)) {
				classes.add(clazz);
			}
		}
		return classes.toArray(new Class[classes.size()]);
	}
	
	private Method[] findAllMethodsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
		List<Method> methods = new ArrayList<Method>();
		for(Method method : clazz.getDeclaredMethods()) {
			if(method.isAnnotationPresent(annotation)) {
				methods.add(method);
			}
		}
		for(Method method : clazz.getSuperclass().getDeclaredMethods()) {
			if(method.isAnnotationPresent(annotation)) {
				methods.add(method);
			}
		}
		return methods.toArray(new Method[methods.size()]);
	}
	
	private Method[] findAllMethodsWithParameters(Method[] methods, Object... parameters) {
		List<Method> methodsList = new ArrayList<Method>();
		for(Method method : methods) {
			int index =  0;
			for(Class<?> clazz : method.getParameterTypes()) {
				if(clazz.isAssignableFrom(parameters[index].getClass())) {
					
				}else {
					continue;
				}
				index++;
				methodsList.add(method);
			}
		}
		return methodsList.toArray(new Method[methodsList.size()]);
	}
	
	private Method[] findAllExecutableMethods(Method[] methods) {
		List<Method> methodList = new ArrayList<Method>();
		for(Method method : methods) {
			if(method.isAnnotationPresent(Action.class)) {
				Action action = method.getAnnotation(Action.class);
				if(action.socketID() == socketID) {
					if(action.massExecutable()) {
						methodList.add(method);
					}
				}
			}
		}
		return methodList.toArray(new Method[methodList.size()]);
	}
	
	private String debugMsg(String name, String value) {
		return name + " " + value;
	}
	
	private enum MessageFormat {
		DEBUG(null, null),
		DEBUG_BACKUP(null, null);
		
		private String name;
		private String value;
		private String ensembled;
		private String separatorLeft;
		private String separatorRight;
		
		private MessageFormat(String name, String value) {
			
		}
		
		public void put(String name, String value) {
			this.name = name;
			this.value = value;
			ensembled = separatorLeft + name + separatorRight + value;
		}
		
		public void setEnsembled(String ensembled) {
			this.ensembled = ensembled;
		}
		
		public void nullify() {
			this.separatorLeft = null;
			this.separatorRight = null;
			this.name = null;
			this.value = null;
			setEnsembled(null);
		}
		
		public String getEnsembled() {
			return ensembled;
		}
		
	}

}
