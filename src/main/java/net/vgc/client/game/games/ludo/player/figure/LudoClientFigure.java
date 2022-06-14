package net.vgc.client.game.games.ludo.player.figure;

import java.util.UUID;

import net.vgc.client.game.games.ludo.player.LudoClientPlayer;
import net.vgc.client.game.player.figure.ClientGameFigure;
import net.vgc.game.games.ludo.map.field.LudoFieldPos;
import net.vgc.game.games.ludo.player.LudoPlayerType;
import net.vgc.game.player.field.GameFigure;
import net.vgc.network.packet.client.ClientPacket;

public class LudoClientFigure implements ClientGameFigure {
	
	protected final LudoClientPlayer player;
	protected final int count;
	protected final UUID uuid;
	
	public LudoClientFigure(LudoClientPlayer player, int count, UUID uuid) {
		this.player = player;
		this.count = count;
		this.uuid = uuid;
	}
	
	@Override
	public LudoClientPlayer getPlayer() {
		return this.player;
	}

	@Override
	public LudoPlayerType getPlayerType() {
		return this.player.getPlayerType();
	}

	@Override
	public int getCount() {
		return this.count;
	}

	@Override
	public UUID getUUID() {
		return this.uuid;
	}
	
	@Override
	public LudoFieldPos getHomePos() {
		return LudoFieldPos.of(this.count);
	}

	@Override
	public LudoFieldPos getStartPos() {
		return LudoFieldPos.of(this.getPlayerType(), 0);
	}
	
	@Override
	public boolean isKickable() {
		return true;
	}
	
	@Override
	public boolean canKick(GameFigure figure) {
		return !this.equals(figure) && this.getPlayerType() != figure.getPlayerType();
	}
	
	@Override
	public void handlePacket(ClientPacket clientPacket) {
		
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof LudoClientFigure figure) {
			if (!this.player.equals(figure.player)) {
				return false;
			} else if (this.count != figure.count) {
				return false;
			} else {
				return this.uuid.equals(figure.uuid);
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("LudoClientFigure{");
		builder.append("count=").append(this.count).append(",");
		builder.append("uuid=").append(this.uuid).append("}");
		return builder.toString();
	}

}
