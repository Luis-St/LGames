package net.luis.network.buffer;

import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public interface Encodable {
	
	default <T extends Encodable> @NotNull T cast(@NotNull Class<T> clazz) {
		return clazz.cast(this);
	}
	
	void encode(@NotNull FriendlyByteBuffer buffer);
	
}
