package net.project.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

public class ReflectionUtil {
	
	protected static final Logger LOGGER = LogManager.getLogger(ReflectionUtil.class);
	
	public static boolean hasInterface(Class<?> clazz, Class<?> iface) {
		if (iface.isInterface()) {
			return Lists.newArrayList(clazz.getInterfaces()).contains(iface);
		}
		return false;
	}
	
	@Nullable
	public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameters) {
		Constructor<T> constructor = null;
		try {
			constructor = clazz.getDeclaredConstructor(parameters);
		} catch (NoSuchMethodException e) {
			LOGGER.warn("Fail to get Constructor for parameters " + Lists.newArrayList(parameters).stream().map(Class::getSimpleName).collect(Collectors.toList()) + " in Class " + clazz.getSimpleName(), e);
		} catch (SecurityException e) {
			LOGGER.warn("No permisson to get the Constructor of Class " + clazz.getSimpleName(), e);
		}
		return constructor;
	}
	
	public static boolean hasConstructor(Class<?> clazz, Class<?>... parameters) {
		Constructor<?> constructor = null;
		try {
			constructor = clazz.getDeclaredConstructor(parameters);
		} catch (Exception e) {
			
		}
		return constructor != null;
	}
	
	@Nullable
	public static <T> T newInstance(Constructor<T> constructor, Object... parameters) {
		List<String> parameterNames = Lists.newArrayList(parameters).stream().map(Object::getClass).map(Class::getSimpleName).collect(Collectors.toList());
		T instance = null;
		try {
			constructor.setAccessible(true);
			instance = constructor.newInstance(parameters);
		} catch (InstantiationException e) {
			LOGGER.warn("Can not create a new instance of Class " + constructor.getDeclaringClass().getSimpleName() + " with arguments " + parameterNames, e);
		} catch (IllegalAccessException e) {
			LOGGER.warn("Can not access the Constructor of Class " + constructor.getDeclaringClass().getSimpleName(), e);
		} catch (IllegalArgumentException e) {
			LOGGER.warn("The arguments " + parameterNames + " does not match with those of the Constructor");
		} catch (InvocationTargetException e) {
			LOGGER.warn("Something went wrong while invoke the Constructor of Class " + constructor.getDeclaringClass().getSimpleName(), e);
		}
		return instance;
	}
	
	@Nullable
	public static <T> T newInstance(Class<T> clazz, Object... parameters) {
		return newInstance(getConstructor(clazz, Lists.newArrayList(parameters).stream().map(Object::getClass).toArray(Class<?>[]::new)), parameters);
	}
	
	@Nullable
	public static Method getMethod(Class<?> clazz, String name, Class<?>... parameters) {
		List<String> parameterNames = Lists.newArrayList(parameters).stream().map(Object::getClass).map(Class::getSimpleName).collect(Collectors.toList());
		Method method = null;
		try {
			method = clazz.getDeclaredMethod(name, parameters);
		} catch (NoSuchMethodException e) {
			LOGGER.warn("Fail to get Method for name " + name + " and parameters " + parameterNames + " in Class " + clazz.getSimpleName(), e);
		} catch (SecurityException e) {
			LOGGER.warn("No permisson to get the Method with name " + name + " and parameters " + parameterNames + " in Class " + clazz.getSimpleName(), e);
		}
		return method;
	}
	
	public static boolean hasMethod(Class<?> clazz, String name, Class<?>... parameters) {
		Method method = null;
		try {
			method = clazz.getDeclaredMethod(name, parameters);
		} catch (Exception e) {
			
		}
		return method != null;
	}
	
	@Nullable
	public static Object invoke(Method method, Object instance, Object... parameters) {
		List<String> parameterNames = Lists.newArrayList(parameters).stream().map(Object::getClass).map(Class::getSimpleName).collect(Collectors.toList());
		Object returnValue = null;
		try {
			method.setAccessible(true);
			returnValue = method.invoke(instance, parameters);
		} catch (IllegalAccessException e) {
			LOGGER.warn("Can not access the Method of Class " + method.getDeclaringClass().getSimpleName() + " with name " + method.getName() + " and parameters " + parameterNames, e);
		} catch (IllegalArgumentException e) {
			LOGGER.warn("The parameters " + parameterNames + " does not match with those of the Method");
		} catch (InvocationTargetException e) {
			LOGGER.warn("Something went wrong while invoke the Method of Class " + method.getDeclaringClass().getSimpleName() + " with name " + method.getName() + " and parameters " + parameterNames, e);
		}
		return returnValue;
	}
	
	@Nullable
	public static Object invoke(Class<?> clazz, String name, Object... parameters) {
		return invoke(getMethod(clazz, name, Lists.newArrayList(parameters).stream().map(Object::getClass).toArray(Class<?>[]::new)), name, parameters);
	}
	
}
