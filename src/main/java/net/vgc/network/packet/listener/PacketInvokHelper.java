package net.vgc.network.packet.listener;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;

import com.google.common.collect.Lists;

import net.luis.utils.util.ClasspathInspector;
import net.luis.utils.util.ReflectionHelper;
import net.luis.utils.util.SimpleEntry;
import net.vgc.common.application.GameApplication;
import net.vgc.network.NetworkSide;
import net.vgc.network.packet.Packet;

/**
 *
 * @author Luis-st
 *
 */

class PacketInvokHelper {
	
	static List<Class<?>> getSubscribers(NetworkSide side) {
		return ClasspathInspector.getClasses().stream().filter((clazz) -> {
			return clazz.isAnnotationPresent(PacketSubscriber.class);
		}).filter((clazz) -> {
			return Lists.newArrayList(clazz.getAnnotation(PacketSubscriber.class).value()).contains(side);
		}).collect(Collectors.toList());
	}
	
	static List<Method> getListeners(Class<?> clazz, Packet<?> packet) {
		return Lists.newArrayList(clazz.getDeclaredMethods()).stream().filter((method) -> {
			return method.isAnnotationPresent(PacketListener.class);
		}).filter((method) -> {
			return method.getAnnotation(PacketListener.class).value().isAssignableFrom(packet.getClass());
		}).collect(Collectors.toList());
	}
	
	static String[] splitInstanceGetters(String getter) {
		List<String> excludeFirst = Lists.newArrayList("", "Client", "Server", "AccountServer");
		String[] getters = getter.split("#");
		return excludeFirst.contains(getters[0]) ? ArrayUtils.remove(getters, 0) : getters;
	}
	
	static Object getInstanceObject(GameApplication application, Class<?> clazz, PacketSubscriber subscriber) {
		if (subscriber.getter().trim().isEmpty()) {
			return null;
		}
		Object instanceObject = application;
		for (String getter : splitInstanceGetters(subscriber.getter())) {
			Class<?> instanceClass = instanceObject.getClass();
			if (ReflectionHelper.hasMethod(instanceClass, getter)) {
				instanceObject = ReflectionHelper.invoke(instanceClass, getter, instanceObject);
				if (instanceObject == null) {
					return null;
				}
			} else if (instanceClass.getSuperclass() != null) {
				Class<?> superClass = instanceClass;
				Method method = null;
				while ((superClass = superClass.getSuperclass()) != null) {
					if (ReflectionHelper.hasMethod(superClass, getter)) {
						method = ReflectionHelper.getMethod(superClass, getter);
						break;
					}
				}
				if (method != null) {
					instanceObject = ReflectionHelper.invoke(method, instanceObject);
					if (instanceObject == null) {
						return null;
					}
				} else {
					throw new RuntimeException("Instance object cannot be determined because class " + instanceClass.getName() + " and none of its super classes have method " + getter);
				}
			} else {
				throw new RuntimeException("Instance object cannot be determined because class " + instanceClass.getName() + " has no method " + getter);
			}
		}
		return instanceObject.getClass() == clazz ? instanceObject : null;
	}
	
	private static GetterInfo[] getObjectGetters(Packet<?> packet) {
		return Lists.newArrayList(packet.getClass().getDeclaredMethods()).stream().filter((method) -> {
			return method.isAnnotationPresent(PacketGetter.class);
		}).map((method) -> {
			PacketGetter getter = method.getAnnotation(PacketGetter.class);
			if (getter.parameterName().isEmpty()) {
				if (!getter.getterPrefix().isEmpty()) {
					String prefix = getter.getterPrefix();
					if (method.getName().startsWith(prefix)) {
						String name = method.getName().substring(prefix.length());
						return new GetterInfo(name.substring(0, 1).toLowerCase() + name.substring(1), method.getName());
					} else {
						throw new RuntimeException("Getters in packet " + packet.getClass().getName() + " specify a prefix, but do not start with the prefix '" + prefix + "'");
					}
				} else if (method.getName().startsWith("get")) {
					String name = method.getName().substring(3);
					return new GetterInfo(name.substring(0, 1).toLowerCase() + name.substring(1), method.getName());
				} else if (method.getName().startsWith("is")) {
					String name = method.getName().substring(2);
					return new GetterInfo(name.substring(0, 1).toLowerCase() + name.substring(1), method.getName());
				} else {
					throw new RuntimeException("Method " + method.getName() + " in packet " + packet.getClass().getName() + " was declared as a getter, but does not specify a valid prefix");
				}
			}
			return new GetterInfo(getter.parameterName(), method.getName());
		}).toArray(GetterInfo[]::new);
	}
	
	private static Object[] sortBySignature(GameApplication application, Method method, List<Entry<GetterInfo, Object>> objects, boolean multipleObjects) {
		List<Object> parameters = Lists.newArrayList();
		if (application != null) {
			parameters.add(application);
		}
		for (int i = 0; i < method.getParameters().length; i++) {
			Class<?> parameter = method.getParameterTypes()[i];
			String parameterName = method.getParameters()[i].getName();
			for (Entry<GetterInfo, Object> entry : objects) {
				if (entry.getValue().getClass() != ClassUtils.primitiveToWrapper(parameter)) {
					continue;
				} else if (entry.getKey().parameterName().equals(parameterName)) {
					parameters.add(entry.getValue());
				} else if (multipleObjects) {
					continue;
				} else {
					parameters.add(entry.getValue());
				}
			}
		}
		return parameters.toArray();
	}
	
	private static boolean hasMultipleObjects(List<Entry<GetterInfo, Object>> objects) {
		List<Class<?>> objectClasses = Lists.newArrayList();
		for (Entry<GetterInfo, Object> entry : objects) {
			Class<?> clazz = entry.getValue().getClass();
			if (!objectClasses.contains(clazz)) {
				objectClasses.add(clazz);
			}
		}
		if (objectClasses.size() > objects.size()) {
			throw new RuntimeException("More object classes found than objects exist");
		}
		return objects.size() > objectClasses.size();
	}
	
	static Object[] getPacketObjects(GameApplication application, Packet<?> packet, Method method) {
		List<Entry<GetterInfo, Object>> objects = Lists.newArrayList();
		for (GetterInfo getterInfo : getObjectGetters(packet)) {
			Object object = Objects.requireNonNull(ReflectionHelper.invoke(packet.getClass(), getterInfo.getterName(), packet), "Getter " + getterInfo.getterName() + " in packet " + packet.getClass().getName() + " must not return null");
			objects.add(new SimpleEntry<>(getterInfo, object));
		}
		return sortBySignature(application, method, objects, hasMultipleObjects(objects));
	}
	
	static boolean validateSignature(Method method, Object... objects) {
		if (method.getParameterCount() != objects.length) {
			return false;
		} else if (method.getParameterCount() == 0) {
			return true;
		} else if (method.getParameterCount() == 1) {
			return ClassUtils.primitiveToWrapper(method.getParameterTypes()[0]).isAssignableFrom(ClassUtils.primitiveToWrapper(objects[0].getClass()));
		}
		for (int i = 0; i < method.getParameterTypes().length; i++) {
			if (!ClassUtils.primitiveToWrapper(method.getParameterTypes()[i]).isAssignableFrom(ClassUtils.primitiveToWrapper(objects[i].getClass()))) {
				return false;
			}
		}
		return true;
	}
	
	static RuntimeException createException(Method method, Object... objects) {
		String expectedParameters = Arrays.asList(method.getParameterTypes()).stream().map(Class::getName).toList().toString();
		String obtainedParameters = Arrays.asList(objects).stream().map(Object::getClass).map(Class::getName).toList().toString();
		return new RuntimeException("Invalid method signature, expected parameter " + expectedParameters + " but " + obtainedParameters + " was passed");
	}
	
	private static record GetterInfo(String parameterName, String getterName) {
		
	}
	
}
