package net.luis.network.buffer;

import net.luis.network.annotation.DecodingConstructor;
import net.luis.utils.util.ReflectionHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class EncodableObject implements Encodable {
	
	private final Encodable object;
	
	public EncodableObject(Encodable object) {
		this.object = object;
	}
	
	@DecodingConstructor
	@SuppressWarnings("unchecked")
	private EncodableObject(FriendlyByteBuffer buffer) {
		ReflectionHelper.enableExceptionLogging();
		Class<? extends Encodable> clazz = (Class<? extends Encodable>) ReflectionHelper.getClassForName(buffer.readString());
		ReflectionHelper.disableExceptionLogging();
		assert clazz != null;
		this.object = Objects.requireNonNull(buffer.read(clazz), "Can not read object of type " + clazz.getName() + " because the type does not exists");
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeString(this.object.getClass().getName());
		buffer.write(this.object);
	}
	
	public Encodable get() {
		return this.object;
	}
	
	@Nullable
	@SuppressWarnings("unchecked")
	public <T extends Encodable> T getAs(Class<T> clazz) {
		if (clazz.isInstance(this.object)) {
			return (T) this.object;
		}
		return null;
	}
	
	@Nullable
	public <T extends Encodable> T getAsOrThrow(Class<T> clazz) {
		T object = this.getAs(clazz);
		if (object != null) {
			return object;
		}
		throw new ClassCastException();
	}
	
}
