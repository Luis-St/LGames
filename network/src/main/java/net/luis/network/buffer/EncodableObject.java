package net.luis.network.buffer;

import net.luis.utils.util.ReflectionHelper;

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
	private EncodableObject(FriendlyByteBuffer buffer) {
		Class<? extends Encodable> clazz = (Class<? extends Encodable>) ReflectionHelper.getClassForName(buffer.readString());
		this.object = buffer.read(clazz);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeString(this.object.getClass().getName());
		buffer.write(this.object);
	}
	
	public Encodable getObject() {
		return this.object;
	}
}
