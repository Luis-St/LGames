package net.vgc.data.serialization;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Logger;

import net.vgc.data.tag.tags.CompoundTag;
import net.vgc.util.ReflectionHelper;
import net.vgc.util.Util;

public class SerializationUtil {
	
	protected static final Logger LOGGER = Util.getLogger(SerializationUtil.class);
	
	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(Class<T> clazz, CompoundTag tag) {
		try {
			if (ReflectionHelper.hasInterface(clazz, Serializable.class)) {
				if (ReflectionHelper.hasConstructor(clazz, CompoundTag.class)) {
					return ReflectionHelper.newInstance(clazz, tag);
				}
				Method method = getMethod(clazz);
				if (method != null && Modifier.isStatic(method.getModifiers()))  {
					return (T) ReflectionHelper.invoke(method, null, tag);
				}
				Constructor<T> loadConstructor = ReflectionHelper.getConstructor(clazz);
				Method loadMethod = ReflectionHelper.getMethod(clazz, "deserialize", CompoundTag.class);
				if (loadConstructor != null && loadMethod != null) {
					T instance = ReflectionHelper.newInstance(loadConstructor);
					ReflectionHelper.invoke(loadMethod, instance, tag);
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
		if (ReflectionHelper.hasMethod(clazz, "load", CompoundTag.class)) {
			method = ReflectionHelper.getMethod(clazz, "load", CompoundTag.class);
		} else if (ReflectionHelper.hasMethod(clazz, "deserialize", CompoundTag.class)) {
			method = ReflectionHelper.getMethod(clazz, "deserialize", CompoundTag.class);
		} else if (ReflectionHelper.hasMethod(clazz, "create", CompoundTag.class)) {
			method = ReflectionHelper.getMethod(clazz, "create", CompoundTag.class);
		}
		return method;
	}
	
}
