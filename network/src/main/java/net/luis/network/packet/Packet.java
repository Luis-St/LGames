package net.luis.network.packet;

import javafx.application.Platform;
import net.luis.network.Connection;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.listener.PacketInvoker;

/**
 *
 * @author Luis-st
 *
 */

public interface Packet {
	
	void encode(FriendlyByteBuffer buffer);
	
	default void handleLater(Connection connection) {
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
