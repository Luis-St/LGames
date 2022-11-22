package net.vgc.game.action.data.gobal;

import net.vgc.game.action.data.ActionData;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.player.GameProfile;

public class ProfileData extends ActionData {
	
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
