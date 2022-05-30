package net.vgc.server.game.games.ttt.player.figure;

import java.util.UUID;

import net.vgc.game.games.ttt.map.field.TTTFieldPos;
import net.vgc.game.games.ttt.player.TTTPlayerType;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.server.game.games.ttt.player.TTTServerPlayer;
import net.vgc.server.game.player.figure.ServerGameFigure;

public class TTTServerFigure implements ServerGameFigure {
	
	protected final TTTServerPlayer player;
	protected final int count;
	protected final UUID uuid;
	
	public TTTServerFigure(TTTServerPlayer player, int count) {
		this.player = player;
		this.count = count;
		this.uuid = UUID.randomUUID();
	}
	
	@Override
	public TTTServerPlayer getPlayer() {
		return this.player;
	}
	
	@Override
	public TTTPlayerType getPlayerType() {
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
	public TTTFieldPos getHomePos() {
		return TTTFieldPos.NO;
	}

	@Override
	public TTTFieldPos getStartPos() {
		return TTTFieldPos.NO;
	}

	@Override
	public void handlePacket(ServerPacket packet) {
		
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof TTTServerFigure figure) {
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
		StringBuilder builder = new StringBuilder("TTTServerFigure{");
		builder.append("player=").append(this.player).append(",");
		builder.append("count=").append(this.count).append(",");
		builder.append("uuid=").append(this.uuid).append("}");
		return builder.toString();
	}

}
