package net.vgc.network.packet;

import javafx.application.Platform;
import net.vgc.network.Connection;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.listener.PacketInvoker;

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
