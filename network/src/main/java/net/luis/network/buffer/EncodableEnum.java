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

public class EncodableEnum implements Encodable {
	
	private final Enum<?> object;
	
	public EncodableEnum(@NotNull Enum<?> object) {
		this.object = Objects.requireNonNull(object);
	}
	
	@DecodingConstructor
	private EncodableEnum(@NotNull FriendlyByteBuffer buffer) {
		ReflectionHelper.enableExceptionLogging();
		Class<?> clazz = ReflectionHelper.getClassForName(buffer.readString());
		ReflectionHelper.disableExceptionLogging();
		int ordinal = buffer.readInt();
		assert clazz != null;
		this.object = (Enum<?>) clazz.getEnumConstants()[ordinal];
	}
	
	@Override
	public void encode(@NotNull FriendlyByteBuffer buffer) {
		buffer.writeString(this.object.getClass().getName());
		buffer.writeInt(this.object.ordinal());
	}
	
	public @NotNull Enum<?> get() {
		return this.object;
	}
	
	@SuppressWarnings("unchecked")
	public <T> @Nullable T getAs(@NotNull Class<T> clazz) {
		if (clazz.isInstance(this.object)) {
			return (T) this.object;
		}
		return null;
	}
	
	public <T> @NotNull  T getAsOrThrow(@NotNull Class<T> clazz) {
		T object = this.getAs(clazz);
		if (object != null) {
			return object;
		}
		throw new ClassCastException();
	}
	
}
