package net.vgc.data.serialization;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.data.tag.Tag;
import net.vgc.data.tag.tags.CompoundTag;
import net.vgc.util.ReflectionHelper;

public class SerializationUtil {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	@Nullable
	public static <T> T deserialize(Class<T> clazz, Path path) {
		try {
			Tag tag = Tag.load(path);
			if (tag instanceof CompoundTag CcompoundTag) {
				return deserialize(clazz, CcompoundTag);
			}
			LOGGER.warn("Tag {} is not an instance of CompoundTag, but it is a type of {}", tag, tag.getClass().getSimpleName());
		} catch (Exception e) {
			LOGGER.error("Fail to load Tag from file {}", path);
			throw new RuntimeException(e);
		}
		return null;
	}
	
	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(Class<T> clazz, CompoundTag tag) {
		try {
			if (ReflectionHelper.hasInterface(clazz, Serializable.class)) {
				if (ReflectionHelper.hasConstructor(clazz, CompoundTag.class)) {
					return ReflectionHelper.newInstance(clazz, tag);
				}
				Method method = getMethod(clazz);
				if (method != null)  {
					if (Modifier.isStatic(method.getModifiers())) {
						return (T) ReflectionHelper.invoke(method, null, tag);
					} else {
						LOGGER.warn("Fail to deserialize a serializable object of type {}, since method {} must be static", clazz.getSimpleName(), method.getName());
					}
				}
				if (ReflectionHelper.hasConstructor(clazz)) {
					Constructor<T> loadConstructor = ReflectionHelper.getConstructor(clazz);
					if (ReflectionHelper.hasMethod(clazz, "deserialize", CompoundTag.class)) {
						Method loadMethod = ReflectionHelper.getMethod(clazz, "deserialize", CompoundTag.class);
						T instance = ReflectionHelper.newInstance(loadConstructor);
						ReflectionHelper.invoke(loadMethod, instance, tag);
						return instance;
					} else {
						LOGGER.warn("Fail to deserialize object of type {}, since the deserialize method is missing or the parameters does not match", clazz.getSimpleName());
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Fail to deserialize a serializable object of type {}, since {}", clazz.getSimpleName(), e.getMessage());
			throw new RuntimeException(e);
		}
		LOGGER.warn("Fail deserialize a serializable object of type {}", clazz.getSimpleName());
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
