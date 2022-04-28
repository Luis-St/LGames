package net.vgc.network.packet.client;

import java.util.List;

import com.google.common.collect.Lists;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.client.screen.Screen;
import net.vgc.network.FriendlyByteBuffer;
import net.vgc.player.GameProfile;

public class PlayerRemovePacket implements ClientScreenPacket {
	
	protected final GameProfile gameProfile;
	
	public PlayerRemovePacket(GameProfile gameProfile) {
		this.gameProfile = gameProfile;
	}
	
	public PlayerRemovePacket(FriendlyByteBuffer buffer) {
		this.gameProfile = buffer.readGameProfile();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeGameProfile(this.gameProfile);
	}
	
	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleClientPlayerRemove(this.gameProfile);
	}
	
	@Override
	public List<Class<? extends Screen>> getScreens() {
		return Lists.newArrayList();
	}

}