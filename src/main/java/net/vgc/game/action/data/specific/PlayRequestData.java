package net.vgc.game.action.data.specific;

import java.util.List;

import net.vgc.game.action.data.ActionData;
import net.vgc.game.type.GameType;
import net.vgc.game.type.GameTypes;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.player.GameProfile;

public class PlayRequestData extends ActionData {
	
	private final GameType<?, ?> gameType;
	private final List<GameProfile> profiles;
	
	public PlayRequestData(GameType<?, ?> gameType, List<GameProfile> profiles) {
		super();
		this.gameType = gameType;
		this.profiles = profiles;
	}
	
	public PlayRequestData(FriendlyByteBuffer buffer) {
		super(buffer);
		this.gameType = GameTypes.fromName(buffer.readString());
		this.profiles = buffer.readList(() -> {
			return buffer.read(GameProfile.class);
		});
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeString(this.gameType.getName());
		buffer.writeList(this.profiles, (profile) -> {
			buffer.write(profile);
		});
	}
	
	public GameType<?, ?> getGameType() {
		return this.gameType;
	}
	
	public List<GameProfile> getProfiles() {
		return this.profiles;
	}
	
}
