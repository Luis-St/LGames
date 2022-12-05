package net.vgc.game.action.data.gobal;

import net.vgc.game.action.data.GameActionData;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.player.GameProfile;

/**
 *
 * @author Luis-st
 *
 */

public class ProfileData extends GameActionData {
	
	private final GameProfile profile;
	
	public ProfileData(GameProfile profile) {
		super();
		this.profile = profile;
	}
	
	public ProfileData(FriendlyByteBuffer buffer) {
		super(buffer);
		this.profile = buffer.read(GameProfile.class);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.write(this.profile);
	}
	
	public GameProfile getProfile() {
		return this.profile;
	}
	
}
