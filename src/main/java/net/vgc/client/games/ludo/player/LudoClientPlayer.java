package net.vgc.client.games.ludo.player;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import net.luis.utils.util.ToString;
import net.vgc.client.game.player.AbstractClientGamePlayer;
import net.vgc.client.games.ludo.player.figure.LudoClientFigure;
import net.vgc.game.Game;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.figure.GameFigure;
import net.vgc.games.ludo.map.field.LudoFieldPos;
import net.vgc.player.Player;

public class LudoClientPlayer extends AbstractClientGamePlayer {
	
	private final List<GameFigure> figures;
	
	public LudoClientPlayer(Game game, Player player, GamePlayerType playerType, List<UUID> uuids) {
		super(game, player, playerType);
		this.figures = createFigures(this, playerType, uuids);
	}
	
	private static List<GameFigure> createFigures(GamePlayer player, GamePlayerType type, List<UUID> uuids) {
		List<GameFigure> figures = Lists.newArrayList();
		for (int i = 0; i < uuids.size(); i++) {
			figures.add(new LudoClientFigure(player, i, uuids.get(i)));
		}
		return figures;
	}
	
	@Override
	public List<GameFigure> getFigures() {
		return this.figures;
	}
	
	@Override
	public List<GameFieldPos> getWinPoses() {
		return Lists.newArrayList(LudoFieldPos.of(0), LudoFieldPos.of(1), LudoFieldPos.of(2), LudoFieldPos.of(3));
	}
	
	@Override
	public boolean equals(Object object) {
		if (!super.equals(object)) {
			return false;
		} else if (object instanceof LudoClientPlayer player) {
			return this.figures.equals(player.figures);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
}
