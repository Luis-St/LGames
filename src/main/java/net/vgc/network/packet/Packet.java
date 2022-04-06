package net.vgc.network.packet;

import javafx.application.Platform;
import net.vgc.network.FriendlyByteBuffer;

public interface Packet<T extends PacketListener> {
	
	void encode(FriendlyByteBuffer buffer);
	
	default void handleLater(T listener) {
		if (Platform.isFxApplicationThread()) {
			this.handle(listener);
		} else {
			Platform.runLater(() -> {
				this.handle(listener);
			});
		}
	}
	
	void handle(T listener);
	
	default boolean skippable() {
		return false;
	}
	
}
