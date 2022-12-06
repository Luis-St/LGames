package net.vgc.network.packet;

import javafx.application.Platform;
import net.vgc.network.Network;
import net.vgc.network.buffer.FriendlyByteBuffer;

/**
 *
 * @author Luis-st
 *
 */

public interface Packet<T extends PacketHandler> {
	
	void encode(FriendlyByteBuffer buffer);
	
	default void handleLater(T listener) {
		if (Platform.isFxApplicationThread()) {
			this.handle(listener);
			Network.INSTANCE.handlePacket(this);
		} else {
			Platform.runLater(() -> {
				this.handle(listener);
				Network.INSTANCE.handlePacket(this);
			});
		}
	}
	
	void handle(T listener);
	
	default boolean skippable() {
		return false;
	}
	
}
