package net.luis.network.packet.listener;

import com.google.common.collect.Lists;
import net.luis.network.Connection;
import net.luis.network.packet.Packet;
import net.luis.utils.util.ClassPathInspector;
import net.luis.utils.util.ReflectionHelper;
import net.luis.utils.util.SimpleEntry;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-st
 *
 */

class PacketInvokHelper {
	
	private static final Class<?> APPLICATION_TYPE_CLASS = Objects.requireNonNull(ReflectionHelper.getClassForName("net.luis.game.application.ApplicationType"));
	
	static List<Class<?>> getSubscribers(Object type) {
		assert APPLICATION_TYPE_CLASS.isInstance(type);
		return ClassPathInspector.getClasses("net.luis").stream().filter((clazz) -> {
			return clazz.isAnnotationPresent(PacketSubscriber.class);
		}).filter((clazz) -> {
			Object object = ReflectionHelper.invoke(ReflectionHelper.getMethod(APPLICATION_TYPE_CLASS, "getShortName"), type);
			assert object instanceof String;
			return clazz.getPackageName().replace("net.luis.", "").toLowerCase().startsWith(((String) object).toLowerCase());
		}).collect(Collectors.toList());
	}
	
	static List<Method> getListeners(Class<?> clazz, Packet packet) {
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
	
	static Object getInstanceObject(Object application, Class<?> clazz, PacketSubscriber subscriber) {
		if (subscriber.value().trim().isEmpty()) {
			return null;
		}
		Object instanceObject = application;
		for (String getter : splitInstanceGetters(subscriber.value())) {
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
	
	private static Field getConnectionField(Class<?> clazz) {
		if (ReflectionHelper.hasField(clazz, "connection")) {
			Field field = ReflectionHelper.getField(clazz, "connection");
			assert field != null;
			if (field.getType() == Connection.class) {
				return field;
			}
			return null;
		}
		return null;
	}
	
	static void setConnectionInstance(Connection connection, Class<?> clazz, Object instanceObject) {
		Field field = getConnectionField(clazz);
		if (field != null) {
			ReflectionHelper.set(field, instanceObject, connection);
		}
	}
	
	private static GetterInfo[] getObjectGetters(Packet packet) {
		return Lists.newArrayList(packet.getClass().getDeclaredMethods()).stream().filter((method) -> {
			return method.isAnnotationPresent(PacketGetter.class);
		}).map((method) -> {
			String packetName = packet.getClass().getName();
			boolean nullable = method.isAnnotationPresent(Nullable.class);
			PacketGetter getter = method.getAnnotation(PacketGetter.class);
			if (getter.parameterName().isEmpty()) {
				if (!getter.getterPrefix().isEmpty()) {
					String prefix = getter.getterPrefix();
					if (method.getName().startsWith(prefix)) {
						String name = method.getName().substring(prefix.length());
						return new GetterInfo(packetName, name.substring(0, 1).toLowerCase() + name.substring(1), method.getName(), nullable);
					} else {
						throw new RuntimeException("Getters in packet " + packet.getClass().getName() + " specify a prefix, but do not start with the prefix '" + prefix + "'");
					}
				} else if (method.getName().startsWith("get")) {
					String name = method.getName().substring(3);
					return new GetterInfo(packetName, name.substring(0, 1).toLowerCase() + name.substring(1), method.getName(), nullable);
				} else if (method.getName().startsWith("is")) {
					String name = method.getName().substring(2);
					return new GetterInfo(packetName, name.substring(0, 1).toLowerCase() + name.substring(1), method.getName(), nullable);
				} else {
					throw new RuntimeException("Method " + method.getName() + " in packet " + packetName + " was declared as a getter, but does not specify a valid prefix");
				}
			}
			return new GetterInfo(packetName, getter.parameterName(), method.getName(), nullable);
		}).toArray(GetterInfo[]::new);
	}
	
	private static void checkNullValues(Method method, List<Entry<GetterInfo, Object>> objects, List<Object> values) {
		Parameter[] parameters = method.getParameters();
		if (parameters.length != values.size()) {
			throw createException(method, values.toArray());
		}
		for (int i = 0; i < parameters.length; i++) {
			if (values.get(i) == null) {
				GetterInfo info = objects.get(i).getKey();
				if (!info.nullable()) {
					throw new RuntimeException("Getter " + info.getterName() + " in packet " + info.packetName() + " returned null but the getter is not marked as Nullable");
				} else if (!parameters[0].isAnnotationPresent(Nullable.class)) {
					throw new RuntimeException("Parameter " + parameters[0].getName() + " of type " + parameters[0].getType().getName() + " is not marked as Nullable but the value returned from the packet getter is null");
				}
			}
		}
	}
	
	private static Object[] sortBySignature(Object application, Method method, List<Entry<GetterInfo, Object>> objects, List<Class<?>> objectInfo) {
		List<Object> values = Lists.newArrayList();
		if (application != null) {
			values.add(application);
		}
		for (int i = 0; i < method.getParameters().length; i++) {
			Class<?> parameter = method.getParameterTypes()[i];
			String parameterName = method.getParameters()[i].getName();
			for (Entry<GetterInfo, Object> entry : objects) {
				boolean multipleObjects = Collections.frequency(objectInfo, entry.getValue().getClass()) > 1;
				if (ClassUtils.primitiveToWrapper(parameter).isInstance(entry.getValue())) {
					if (entry.getKey().parameterName().equals(parameterName)) {
						values.add(entry.getValue());
						break;
					} else if (!multipleObjects) {
						values.add(entry.getValue());
						break;
					}
				}
			}
		}
		if (values.size() != objects.size()) {
			throw new RuntimeException("Sorting parameters of method " + method.getDeclaringClass().getName() + "#" + method.getName() + " by signature failed");
		}
		checkNullValues(method, objects, values);
		return values.toArray();
	}
	
	private static List<Class<?>> generateObjectInfo(List<Entry<GetterInfo, Object>> objects) {
		List<Class<?>> objectClasses = Lists.newArrayList();
		for (Entry<GetterInfo, Object> entry : objects) {
			objectClasses.add(entry.getValue().getClass());
		}
		return objectClasses;
	}
	
	static Object[] getPacketObjects(Object application, Packet packet, Method method) {
		List<Entry<GetterInfo, Object>> objects = Lists.newArrayList();
		for (GetterInfo getterInfo : getObjectGetters(packet)) {
			objects.add(new SimpleEntry<>(getterInfo, ReflectionHelper.invoke(packet.getClass(), getterInfo.getterName(), packet)));
		}
		return sortBySignature(application, method, objects, generateObjectInfo(objects));
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
		String name = method.getDeclaringClass().getSimpleName() + "#" + method.getName();
		String expectedParameters = Arrays.stream(method.getParameterTypes()).map(Class::getName).toList().toString();
		String obtainedParameters = Arrays.stream(objects).map(Object::getClass).map(Class::getName).toList().toString(); // TODO: add null check
		return new RuntimeException("Invalid method signature of method " + name + ", expected parameter " + expectedParameters + " but " + obtainedParameters + " was passed");
	}
	
	private record GetterInfo(String packetName, String parameterName, String getterName, boolean nullable) {
		
	}
	
}
