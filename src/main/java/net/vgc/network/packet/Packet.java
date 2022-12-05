package net.vgc.network.packet;

import javafx.application.Platform;
import net.vgc.client.screen.update.ScreenUpdateFactory;
import net.vgc.network.Network;
import net.vgc.network.NetworkSide;
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
		} else {
			Platform.runLater(() -> {
				this.handle(listener);
				Network.INSTANCE.executeOn(NetworkSide.CLIENT, () -> {
					ScreenUpdateFactory.onUpdate();
				});
			});
		}
	}
	
	void handle(T listener);
	
	default boolean skippable() {
		return false;
	}
	
}
