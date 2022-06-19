package net.vgc.client.game.player.figure;

import java.util.UUID;

import net.vgc.client.game.player.ClientGamePlayer;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.field.GameFigure;

public interface ClientGameFigure extends GameFigure {
	
	@Override
	ClientGamePlayer getPlayer();
	
	@Override
	GamePlayerType getPlayerType();
	
	@Override
	int getCount();
	
	@Override
	UUID getUUID();
	
	@Override
	GameFieldPos getHomePos();
	
	@Override
	GameFieldPos getStartPos();
	
}
