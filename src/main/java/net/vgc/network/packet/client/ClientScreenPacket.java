package net.vgc.network.packet.client;

import java.util.List;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.client.screen.Screen;

public interface ClientScreenPacket extends ClientPacket {
	
	@Override
	default void handle(ClientPacketListener listener) {
		
	}
	
	List<Class<? extends Screen>> getScreens();
	
}
