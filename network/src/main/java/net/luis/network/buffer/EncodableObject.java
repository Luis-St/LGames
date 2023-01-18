package net.luis.network.buffer;

import net.luis.network.annotation.DecodingConstructor;
import net.luis.utils.util.ReflectionHelper;
import org.jetbrains.annotations.Nullable;

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
	
	@SuppressWarnings("unchecked")
	@DecodingConstructor
	private EncodableObject(FriendlyByteBuffer buffer) {
		Class<? extends Encodable> clazz = (Class<? extends Encodable>) ReflectionHelper.getClassForName(buffer.readString());
		this.object = buffer.read(clazz);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeString(this.object.getClass().getName());
		buffer.write(this.object);
	}
	
	public Encodable get() {
		return this.object;
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
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
