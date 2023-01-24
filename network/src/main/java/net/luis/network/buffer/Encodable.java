package net.luis.network.buffer;

/**
 *
 * @author Luis-st
 *
 */

public interface Encodable {
	
	default <T extends Encodable> T cast(Class<T> clazz) {
		return clazz.cast(this);
	}
	
	void encode(FriendlyByteBuffer buffer);
	
}
