package net.vgc.server.game.games.ttt.map;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.vgc.game.games.ttt.map.field.TTTFieldPos;
import net.vgc.game.map.field.GameField;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.field.GameFigure;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.game.games.ttt.TTTServerGame;
import net.vgc.server.game.games.ttt.map.field.TTTServerField;
import net.vgc.server.game.map.ServerGameMap;
import net.vgc.util.Util;

public class TTTServerMap implements ServerGameMap {
	
	protected final DedicatedServer server;
	protected final TTTServerGame game;
	protected final List<TTTServerField> fields = Util.make(Lists.newArrayList(), (list) -> {
		for (int i = 0; i < 9; i++) {
			list.add(new TTTServerField(TTTFieldPos.of(i)));
		}
	});
	
	public TTTServerMap(DedicatedServer server, TTTServerGame game) {
		this.server = server;
		this.game = game;
	}
	
	@Override
	public void init(List<? extends GamePlayer> players) {
		this.getFields().forEach(TTTServerField::clear);
	}
	
	@Override
	public TTTServerGame getGame() {
		return this.game;
	}
	
	@Override
	public List<TTTServerField> getFields() {
		return this.fields;
	}
	
	@Nullable
	@Override
	public TTTServerField getField(GameFigure figure) {
		for (TTTServerField field : this.getFields()) {
			if (!field.isEmpty() && field.getFigure().equals(figure)) {
				return field;
			}
		}
		return null;
	}
	
	@Override
	public TTTServerField getField(GameFieldType fieldType, GamePlayerType playerType, GameFieldPos fieldPos) {
		return this.fields.get(fieldPos.getPosition());
	}
	
	@Nullable
	@Override
	public TTTServerField getNextField(GameFigure figure, int count) {
		LOGGER.warn("Fail to get next field for figure {} of player {}, since tic tac toe figures does not have a next field", figure.getCount(), figure.getPlayer().getPlayer().getProfile().getName());
		return null;
	}
	
	@Override
	public List<TTTServerField> getHomeFields(GamePlayerType playerType) {
		return Lists.newArrayList();
	}

	@Override
	public List<TTTServerField> getStartFields(GamePlayerType playerType) {
		return Lists.newArrayList();
	}

	@Override
	public List<TTTServerField> getWinFields(GamePlayerType playerType) {
		return Lists.newArrayList();
	}

	@Override
	public boolean moveFigureTo(GameFigure figure, GameField field) {
		LOGGER.warn("Fail to move figure {} of player {}, since tic tac toe figures are not moveable", figure.getCount(), figure.getPlayer().getPlayer().getProfile().getName());
		return false;
	}

	@Override
	public void handlePacket(ServerPacket packet) {
		ServerGameMap.super.handlePacket(packet);
		
	}
	
	@Override
	public String toString() {
		return "TTTServerMap";
	}
	
}
