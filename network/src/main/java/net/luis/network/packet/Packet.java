package net.luis.network.packet;

import javafx.application.Platform;
import net.luis.network.Connection;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.listener.PacketInvoker;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public interface Packet {
	
	void encode(@NotNull FriendlyByteBuffer buffer);
	
	default void handleLater(@NotNull Connection connection) {
		if (Platform.isFxApplicationThread()) {
			PacketInvoker.invoke(connection, this);
		} else {
			Platform.runLater(() -> {
				PacketInvoker.invoke(connection, this);
			});
		}
	}
	
	default boolean skippable() {
		return false;
	}
	
}
