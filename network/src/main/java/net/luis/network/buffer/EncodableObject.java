package net.luis.network.buffer;

import net.luis.network.annotation.DecodingConstructor;
import net.luis.utils.util.reflection.ReflectionHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class EncodableObject implements Encodable {
	
	private final Encodable object;
	
	public EncodableObject(@NotNull Encodable object) {
		this.object = object;
	}
	
	@DecodingConstructor
	@SuppressWarnings("unchecked")
	private EncodableObject(@NotNull FriendlyByteBuffer buffer) {
		boolean nullObject = buffer.readBoolean();
		if (nullObject) {
			this.object = null;
		} else {
			ReflectionHelper.enableExceptionLogging();
			Class<? extends Encodable> clazz = (Class<? extends Encodable>) ReflectionHelper.getClassForName(buffer.readString());
			ReflectionHelper.disableExceptionLogging();
			assert clazz != null;
			this.object = Objects.requireNonNull(buffer.read(clazz), "Can not read object of type " + clazz.getName() + " because the type does not exists");
		}
	}
	
	@Override
	public void encode(@NotNull FriendlyByteBuffer buffer) {
		buffer.writeBoolean(this.object == null);
		if (this.object != null) {
			buffer.writeString(this.object.getClass().getName());
			buffer.write(this.object);
		}
	}
	
	public @Nullable Encodable get() {
		return this.object;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Encodable> @Nullable T getAs(@NotNull Class<T> clazz) {
		if (clazz.isInstance(this.object)) {
			return (T) this.object;
		}
		return null;
	}
	
	public <T extends Encodable> @NotNull T getAsOrThrow(@NotNull Class<T> clazz) {
		T object = this.getAs(clazz);
		if (object != null) {
			return object;
		}
		throw new ClassCastException();
	}
	
}
