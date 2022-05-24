package net.vgc.game.ludo.player;

import java.util.UUID;

import net.vgc.game.ludo.LudoType;
import net.vgc.network.buffer.Encodable;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.player.GameProfile;
import net.vgc.util.annotation.DecodingConstructor;

public class LudoFigure implements Encodable {
	
	protected final GameProfile profile;
	protected final int count;
	protected final LudoType type;
	protected final UUID uuid;
	
	public LudoFigure(GameProfile profile, int count, LudoType type) {
		this.profile = profile;
		this.count = count;
		this.type = type;
		this.uuid = UUID.randomUUID();
	}
	
	@DecodingConstructor
	private LudoFigure(FriendlyByteBuffer buffer) {
		this.profile = buffer.read(GameProfile.class);
		this.count = buffer.readInt();
		this.type = buffer.readEnum(LudoType.class);
		this.uuid = buffer.readUUID();
	}
	
	public GameProfile getProfile() {
		return this.profile;
	}
	
	public int getCount() {
		return this.count;
	}
	
	public LudoType getType() {
		return this.type;
	}
	
	public UUID getUUID() {
		return this.uuid;
	}
	
	public boolean canKick(LudoFigure figure) {
		if (this.equals(figure)) {
			return false;
		} else if (this.type == figure.type) {
			return false;
		}
		return !this.uuid.equals(figure.uuid);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.write(this.profile);
		buffer.writeInt(this.count);
		buffer.writeEnum(this.type);
		buffer.writeUUID(this.uuid);
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof LudoFigure figure) {
			if (this.type != figure.type) {
				return false;
			} else {
				return this.uuid.equals(figure.uuid);
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("LudoFigure{");
		builder.append("profile=").append(this.profile).append(",");
		builder.append("type=").append(this.type).append(",");
		builder.append("uuid=").append(this.uuid).append("}");
		return builder.toString();
	}
	
}
