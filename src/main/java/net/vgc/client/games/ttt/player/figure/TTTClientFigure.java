package net.vgc.client.games.ttt.player.figure;

import java.util.UUID;

import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.figure.AbstractGameFigure;
import net.vgc.games.ttt.map.field.TTTFieldPos;
import net.vgc.util.ToString;

public class TTTClientFigure extends AbstractGameFigure {
	
	public TTTClientFigure(GamePlayer player, int count, UUID uuid) {
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
