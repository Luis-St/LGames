package net.project.data.serialization;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.project.data.tag.tags.CompoundTag;
import net.project.util.ReflectionUtil;

public class SerializationUtil {
	
	protected static final Logger LOGGER = LogManager.getLogger(SerializationUtil.class);
	
	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(Class<T> clazz, CompoundTag tag) {
		try {
			if (ReflectionUtil.hasInterface(clazz, Serializable.class)) {
				if (ReflectionUtil.hasConstructor(clazz, CompoundTag.class)) {
					return ReflectionUtil.newInstance(clazz, tag);
				}
				Method method = getMethod(clazz);
				if (method != null && Modifier.isStatic(method.getModifiers()))  {
					return (T) ReflectionUtil.invoke(method, null, tag);
				}
				Constructor<T> loadConstructor = ReflectionUtil.getConstructor(clazz);
				Method loadMethod = ReflectionUtil.getMethod(clazz, "deserialize", CompoundTag.class);
				if (loadConstructor != null && loadMethod != null) {
					T instance = ReflectionUtil.newInstance(loadConstructor);
					ReflectionUtil.invoke(loadMethod, instance, tag);
					return instance;
				}
			}
		} catch (Exception e) {
			LOGGER.warn("Fail to create a Serializable object", e);
		}
		return null;
	}
	
	protected static Method getMethod(Class<?> clazz) throws Exception {
		Method method = null;
		if (ReflectionUtil.hasMethod(clazz, "load", CompoundTag.class)) {
			method = ReflectionUtil.getMethod(clazz, "load", CompoundTag.class);
		} else if (ReflectionUtil.hasMethod(clazz, "deserialize", CompoundTag.class)) {
			method = ReflectionUtil.getMethod(clazz, "deserialize", CompoundTag.class);
		} else if (ReflectionUtil.hasMethod(clazz, "create", CompoundTag.class)) {
			method = ReflectionUtil.getMethod(clazz, "create", CompoundTag.class);
		}
		return method;
	}
	
}
