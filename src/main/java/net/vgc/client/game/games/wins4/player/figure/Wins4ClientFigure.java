package net.vgc.client.game.games.wins4.player.figure;

import java.util.UUID;

import net.vgc.client.game.games.wins4.player.Wins4ClientPlayer;
import net.vgc.client.game.player.figure.ClientGameFigure;
import net.vgc.game.games.wins4.map.field.Wins4FieldPos;
import net.vgc.game.games.wins4.player.Wins4PlayerType;

public class Wins4ClientFigure implements ClientGameFigure {
	
	protected final Wins4ClientPlayer player;
	protected final int count;
	protected final UUID uuid;
	
	public Wins4ClientFigure(Wins4ClientPlayer player, int count, UUID uuid) {
		this.player = player;
		this.count = count;
		this.uuid = uuid;
	}
	
	@Override
	public Wins4ClientPlayer getPlayer() {
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
		if (object instanceof Wins4ClientFigure figure) {
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
		StringBuilder builder = new StringBuilder("Win4ClientFigure{");
		builder.append("count=").append(this.count).append(",");
		builder.append("uuid=").append(this.uuid).append("}");
		return builder.toString();
	}

}
