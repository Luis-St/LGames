package net.vgc.client.game.games.wins4.player;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import net.vgc.client.game.games.wins4.Wins4ClientGame;
import net.vgc.client.game.games.wins4.player.figure.Wins4ClientFigure;
import net.vgc.client.game.player.ClientGamePlayer;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.game.games.wins4.map.field.Wins4FieldPos;
import net.vgc.game.games.wins4.player.Wins4PlayerType;

public class Wins4ClientPlayer implements ClientGamePlayer {
	
	protected final Wins4ClientGame game;
	protected final AbstractClientPlayer player;
	protected final Wins4PlayerType playerType;
	protected final List<Wins4ClientFigure> figures;
	
	public Wins4ClientPlayer(Wins4ClientGame game, AbstractClientPlayer player, Wins4PlayerType playerType, List<UUID> uuids) {
		this.game = game;
		this.player = player;
		this.playerType = playerType;
		this.figures = createFigures(this, this.playerType, uuids);
	}
	
	protected static List<Wins4ClientFigure> createFigures(Wins4ClientPlayer player, Wins4PlayerType type, List<UUID> uuids) {
		List<Wins4ClientFigure> figures = Lists.newArrayList();
		for (int i = 0; i < uuids.size(); i++) {
			figures.add(new Wins4ClientFigure(player, i, uuids.get(i)));
		}
		return figures;
	}
	
	@Override
	public Wins4ClientGame getGame() {
		return this.game;
	}

	@Override
	public AbstractClientPlayer getPlayer() {
		return this.player;
	}

	@Override
	public Wins4PlayerType getPlayerType() {
		return this.playerType;
	}

	@Override
	public List<Wins4ClientFigure> getFigures() {
		return this.figures;
	}

	@Override
	public Wins4ClientFigure getFigure(int figure) {
		return this.figures.get(figure);
	}

	@Override
	public List<Wins4FieldPos> getWinPoses() {
		return Lists.newArrayList();
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Wins4ClientPlayer player) {
			if (!this.game.equals(player.game)) {
				return false;
			} else if (!this.player.equals(player.player)) {
				return false;
			} else if (!this.playerType.equals(player.playerType)) {
				return false;
			} else {
				return this.figures.equals(player.figures);
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Win4ClientPlayer{");
		builder.append("game=").append(this.game).append(",");
		builder.append("player=").append(this.player).append(",");
		builder.append("playerType=").append(this.playerType).append(",");
		builder.append("figures=").append(this.figures).append("}");
		return builder.toString();
	}

}
