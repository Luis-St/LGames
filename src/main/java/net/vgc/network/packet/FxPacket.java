package net.vgc.network.packet;

import javafx.application.Platform;

public interface FxPacket<T extends PacketListener> extends Packet<T> {
	
	@Override
	default void handle(T listener) {
		if (Platform.isFxApplicationThread()) {
			this.handleFxWrapped(listener);
		} else {
			Platform.runLater(() -> {
				this.handleFxWrapped(listener);
			});
		}
	}
	
	void handleFxWrapped(T listener);
	
}
