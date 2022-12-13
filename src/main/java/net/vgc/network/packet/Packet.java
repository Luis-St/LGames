package net.vgc.network.packet;

import javafx.application.Platform;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.listener.PacketInvoker;

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
			PacketInvoker.invoke(this);
		} else {
			Platform.runLater(() -> {
				this.handle(listener);
				PacketInvoker.invoke(this);
			});
		}
	}
	
	void handle(T listener);
	
	default boolean skippable() {
		return false;
	}
	
}
