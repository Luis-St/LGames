package net.vgc.server.game.games.wins4.player.figure;

import java.util.UUID;

import net.vgc.game.games.wins4.map.field.Wins4FieldPos;
import net.vgc.game.games.wins4.player.Wins4PlayerType;
import net.vgc.server.game.games.wins4.player.Wins4ServerPlayer;
import net.vgc.server.game.player.figure.ServerGameFigure;

public class Wins4ServerFigure implements ServerGameFigure {
	
	protected final Wins4ServerPlayer player;
	protected final int count;
	protected final UUID uuid;
	
	public Wins4ServerFigure(Wins4ServerPlayer player, int count) {
		this.player = player;
		this.count = count;
		this.uuid = UUID.randomUUID();
	}
	
	@Override
	public Wins4ServerPlayer getPlayer() {
		return this.player;
	}

	@Override
	public Wins4PlayerType getPlayerType() {
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
	public Wins4FieldPos getHomePos() {
		return Wins4FieldPos.NO;
	}

	@Override
	public Wins4FieldPos getStartPos() {
		return Wins4FieldPos.NO;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Wins4ServerFigure figure) {
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
		StringBuilder builder = new StringBuilder("Win4ServerFigure{");
		builder.append("count=").append(this.count).append(",");
		builder.append("uuid=").append(this.uuid).append("}");
		return builder.toString();
	}
	
}
