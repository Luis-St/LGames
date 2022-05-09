package net.vgc.network.packet.client;

import net.vgc.Main;
import net.vgc.client.network.ClientPacketListener;
import net.vgc.network.FriendlyByteBuffer;
import net.vgc.player.GameProfile;

public class PlayerRemovePacket implements ClientPacket {
	
	protected final GameProfile profile;
	
	public PlayerRemovePacket(GameProfile profile) {
		this.profile = profile;
	}
	
	public PlayerRemovePacket(FriendlyByteBuffer buffer) {
		Main.LOGGER.debug("decode before {}b", buffer.readableBytes());
		this.profile = buffer.readProfile();
		Main.LOGGER.debug("profile {}", this.profile);
		Main.LOGGER.debug("decode after {}b", buffer.readableBytes());
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		Main.LOGGER.debug("encode before {}b", buffer.readableBytes());
		buffer.writeProfile(this.profile);
		Main.LOGGER.debug("profile {}", this.profile);
		Main.LOGGER.debug("encode after {}b", buffer.readableBytes());
	}
	
	@Override
	public void handle(ClientPacketListener listener) {
		listener.handlePlayerRemove(this.profile);
	}
	
	public GameProfile getProfile() {
		return this.profile;
	}
	
}
