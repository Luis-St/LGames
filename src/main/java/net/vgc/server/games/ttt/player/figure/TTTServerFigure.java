package net.vgc.server.games.ttt.player.figure;

import java.util.UUID;

import net.luis.utils.util.ToString;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.figure.AbstractGameFigure;
import net.vgc.games.ttt.map.field.TTTFieldPos;

public class TTTServerFigure extends AbstractGameFigure {
	
	public TTTServerFigure(GamePlayer player, int count, UUID uuid) {
		super(player, count, uuid);
	}
	
	@Override
	public GameFieldPos getHomePos() {
		return TTTFieldPos.NO;
	}
	
	@Override
	public GameFieldPos getStartPos() {
		return TTTFieldPos.NO;
	}
	
	@Override
	public String toString() {
		return ToString.toString(this, "player");
	}
	
}
