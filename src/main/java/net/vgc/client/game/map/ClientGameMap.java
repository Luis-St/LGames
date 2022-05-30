package net.vgc.client.game.map;

import java.util.List;

import javax.annotation.Nullable;

import net.vgc.client.game.map.field.ClientGameField;
import net.vgc.client.game.player.figure.ClientGameFigure;
import net.vgc.game.map.GameMap;
import net.vgc.game.map.field.GameField;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.field.GameFigure;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.client.ClientPacket;

public interface ClientGameMap extends GameMap, PacketHandler<ClientPacket> {
	
	void init();
	
	void addFields();
	
	@Override
	void init(List<? extends GamePlayer> players);
	
	@Override
	List<? extends ClientGameField> getFields();
	
	@Override
	ClientGameField getField(GameFigure figure);
	
	@Override
	ClientGameField getField(GameFieldType fieldType, @Nullable GamePlayerType playerType, GameFieldPos fieldPos);
	
	@Override
	ClientGameField getNextField(GameFigure figure, int count);
	
	@Override
	List<? extends ClientGameField> getHomeFields(GamePlayerType playerType);
	
	@Override
	List<? extends ClientGameField> getStartFields(GamePlayerType playerType);
	
	@Override
	List<? extends ClientGameField> getWinFields(GamePlayerType playerType);
	
	@Override
	default ClientGameFigure getFigure(GamePlayer player, int figure) {
		return (ClientGameFigure) player.getFigure(figure);
	}
	
	@Override
	default boolean moveFigure(GameFigure figure, int count) {
		LOGGER.warn("Can not move figure {} of player {} on client", figure.getCount(), figure.getPlayer().getPlayer().getProfile().getName());
		return false;
	}
	
	@Override
	default boolean moveFigureTo(GameFigure figure, GameField field) {
		LOGGER.warn("Can not move figure {} of player {} on client", figure.getCount(), figure.getPlayer().getPlayer().getProfile().getName());
		return false;
	}
	
	@Override
	default void reset() {
		LOGGER.warn("Can not reset the game map on client");
	}
	
	@Nullable
	ClientGameField getSelectedField();
	
	@Override
	default void handlePacket(ClientPacket packet) {
		for (ClientGameField field : this.getFields()) {
			field.handlePacket(packet);
		}
	}
	
}
