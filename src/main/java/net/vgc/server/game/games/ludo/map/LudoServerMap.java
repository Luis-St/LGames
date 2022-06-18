package net.vgc.server.game.games.ludo.map;

import java.util.List;

import com.google.common.collect.Lists;

import net.vgc.game.games.ludo.map.field.LudoFieldPos;
import net.vgc.game.games.ludo.map.field.LudoFieldType;
import net.vgc.game.games.ludo.player.LudoPlayerType;
import net.vgc.game.map.field.GameField;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.field.GameFigure;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.game.games.ludo.LudoServerGame;
import net.vgc.server.game.games.ludo.map.field.LudoServerField;
import net.vgc.server.game.games.ludo.player.LudoServerPlayer;
import net.vgc.server.game.games.ludo.player.figure.LudoServerFigure;
import net.vgc.server.game.map.ServerGameMap;
import net.vgc.util.Mth;
import net.vgc.util.Util;
import net.vgc.util.exception.InvalidValueException;

public class LudoServerMap implements ServerGameMap, PacketHandler<ServerPacket> {
	
	protected final DedicatedServer server;
	protected final LudoServerGame game;
	protected final List<LudoServerField> fields = Util.make(Lists.newArrayList(), (fields) -> {
		for (int i = 0; i < 40; i++) {
			if (i == 0) {
				fields.add(new LudoServerField(LudoFieldType.DEFAULT, LudoPlayerType.GREEN, LudoFieldPos.ofGreen(i)));
			} else if (i == 10) {
				fields.add(new LudoServerField(LudoFieldType.DEFAULT, LudoPlayerType.YELLOW, LudoFieldPos.ofGreen(i)));
			} else if (i == 20) {
				fields.add(new LudoServerField(LudoFieldType.DEFAULT, LudoPlayerType.BLUE, LudoFieldPos.ofGreen(i)));
			} else if (i == 30) {
				fields.add(new LudoServerField(LudoFieldType.DEFAULT, LudoPlayerType.RED, LudoFieldPos.ofGreen(i)));
			} else {
				fields.add(new LudoServerField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(i)));
			}
		}
	});
	protected final List<LudoServerField> homeFields = Util.make(Lists.newArrayList(), (fields) -> {
		for (int i = 0; i < 16; i++) {
			fields.add(new LudoServerField(LudoFieldType.HOME, this.getFieldColor(i), LudoFieldPos.of(i % 4)));
		}
	});
	protected final List<LudoServerField> winFields = Util.make(Lists.newArrayList(), (fields) -> {
		for (int i = 0; i < 16; i++) {
			fields.add(new LudoServerField(LudoFieldType.WIN, this.getFieldColor(i), LudoFieldPos.of(i % 4)));
		}
	});
	
	protected LudoPlayerType getFieldColor(int i) {
		if (Mth.isInBounds(i, 0, 3)) {
			return LudoPlayerType.GREEN;
		} else if (Mth.isInBounds(i, 4, 7)) {
			return LudoPlayerType.YELLOW;
		} else if (Mth.isInBounds(i, 8, 11)) {
			return LudoPlayerType.BLUE;
		} else if (Mth.isInBounds(i, 12, 15)) {
			return LudoPlayerType.RED;
		}
		throw new InvalidValueException("Fail to get field color for index " + i);
	}
	
	public LudoServerMap(DedicatedServer server, LudoServerGame game) {
		this.server = server;
		this.game = game;
	}
	
	@Override
	public void init(List<? extends GamePlayer> players) {
		this.getFields().forEach(LudoServerField::clear);
		for (GamePlayer gamePlayer : players) {
			if (gamePlayer instanceof LudoServerPlayer player) {
				LOGGER.debug("Add figures ({}) of player {}, to their home fields", player.getFigures().size(), player.getPlayer().getProfile().getName());
				for (LudoServerFigure figure : player.getFigures()) {
					this.getField(LudoFieldType.HOME, player.getPlayerType(), figure.getHomePos()).setFigure(figure);
				}
			} else {
				LOGGER.error("Can not add a game player of type {} to a ludo game", gamePlayer.getClass().getSimpleName());
				throw new RuntimeException("Can not add a game player of type " + gamePlayer.getClass().getSimpleName() + " to a ludo game");
			}
		}
	}
	
	@Override
	public LudoServerGame getGame() {
		return this.game;
	}

	@Override
	public List<LudoServerField> getFields() {
		return Util.concatLists(this.fields, this.homeFields, this.winFields);
	}

	@Override
	public LudoServerField getField(GameFigure figure) {
		for (LudoServerField field : this.getFields()) {
			if (!field.isEmpty() && field.getFigure().equals(figure)) {
				return field;
			}
		}
		return null;
	}

	@Override
	public LudoServerField getField(GameFieldType fieldType, GamePlayerType playerType, GameFieldPos fieldPos) {
		playerType = Util.warpNullTo(playerType, LudoPlayerType.NO);
		if (fieldType == LudoFieldType.DEFAULT) {
			if (playerType != LudoPlayerType.NO && fieldPos.getPosition() % 10 == 0) {
				return this.fields.get(fieldPos.getPosition());
			}
			return this.fields.get(fieldPos.getPosition());
		} else if (playerType != LudoPlayerType.NO) {
			if (fieldType == LudoFieldType.HOME) {
				return this.getHomeFields(playerType).get(fieldPos.getPositionFor(playerType));
			} else if (fieldType == LudoFieldType.WIN) {
				return this.getWinFields(playerType).get(fieldPos.getPositionFor(playerType));
			}
		}
		LOGGER.warn("Fail to get a field with type {} and color {} at position {}", fieldType, playerType, fieldPos.getPosition());
		return null;
	}

	@Override
	public LudoServerField getNextField(GameFigure figure, int count) {
		String playerName = figure.getPlayer().getPlayer().getProfile().getName();
		LudoPlayerType playerType = (LudoPlayerType) figure.getPlayerType();
		LudoServerField currentField = this.getField(figure);
		if (currentField != null) {
			if (count > 0) {
				if (currentField.getFieldType() == LudoFieldType.HOME) {
					if (count == 6) {
						return this.getStartFields(playerType).get(0);
					}
					return null;
				} else {
					int position = currentField.getFieldPos().getPositionFor(playerType) + count;
					if (currentField.getFieldType() == LudoFieldType.WIN) {
						if (position > 3) {
							return null;
						}
						return this.getWinFields(playerType).get(position);
					} else {
						if (position > 43) {
							return null;
						} else if (position > 39) {
							return this.getWinFields(playerType).get(position - 40);
						} else {
							return this.fields.get(LudoFieldPos.of(playerType, position).getPosition());
						}
					}
				}
			}
			LOGGER.warn("Fail to get next field for figure {} of player {}, since the count must be larger than 0 but it is {}", figure.getCount(), playerName, count);
			return currentField;
		}
		LOGGER.warn("Fail to get next field for figure {} of player {}, since the current field is null", figure.getCount(), playerName);
		return null;
	}

	@Override
	public List<LudoServerField> getHomeFields(GamePlayerType playerType) {
		switch ((LudoPlayerType) playerType) {
			case GREEN: return this.homeFields.subList(0, 4);
			case YELLOW: return this.homeFields.subList(4, 8);
			case BLUE: return this.homeFields.subList(8, 12);
			case RED: return this.homeFields.subList(12, 16);
			default: break;
		}
		LOGGER.warn("Fail to get home fields for type {}", playerType);
		return Lists.newArrayList();
	}

	@Override
	public List<LudoServerField> getStartFields(GamePlayerType playerType) {
		return switch ((LudoPlayerType) playerType) {
			case GREEN, YELLOW, BLUE, RED -> Lists.newArrayList(this.fields.get(LudoFieldPos.of((LudoPlayerType) playerType, 0).getPosition()));
			default -> {
				LOGGER.warn("Fail to get start field for type {}", playerType);
				yield Lists.newArrayList();
			}
		};
	}

	@Override
	public List<LudoServerField> getWinFields(GamePlayerType playerType) {
		switch ((LudoPlayerType) playerType) {
			case GREEN: return this.winFields.subList(0, 4);
			case YELLOW: return this.winFields.subList(4, 8);
			case BLUE: return this.winFields.subList(8, 12);
			case RED: return this.winFields.subList(12, 16);
			default: break;
		}
		LOGGER.warn("Fail to get win fields for type {}", playerType);
		return Lists.newArrayList();
	}

	@Override
	public boolean moveFigureTo(GameFigure figure, GameField field) {
		String playerName = figure.getPlayer().getPlayer().getProfile().getName();
		if (field != null) {
			if (field.isEmpty()) {
				this.getField(figure).clear();
				field.setFigure(figure);
				return true;
			} else {
				GameFigure opponentFigure = field.getFigure();
				String opponentName = opponentFigure.getPlayer().getPlayer().getProfile().getName();
				if (opponentFigure.isKickable()) {
					if (figure.canKick(opponentFigure)) {
						if (this.moveFigureTo(opponentFigure, this.getField(LudoFieldType.HOME, opponentFigure.getPlayerType(), opponentFigure.getHomePos()))) {
							this.getField(figure).clear();
							field.setFigure(figure);
							return true;
						} else {
							LOGGER.warn("Fail to move figure {} of player {} to it's home field {}", opponentFigure.getCount(), opponentName, opponentFigure.getHomePos().getPosition());
							LOGGER.warn("Can not move figure {} of player {} to field {}, since there was an error while moving the figure on the field home", figure.getCount(), playerName);
							return false;
						}
					} else {
						LOGGER.warn("Can not move figure {} of player {} to field {}, since the figure on the field is not kickable by the figure of the player", figure.getCount(), playerName, field.getFieldPos().getPosition());
						return false;
					}
				} else {
					LOGGER.warn("Can not move figure {} of player {} to field {}, since the figure on the field is not kickable", figure.getCount(), playerName, field.getFieldPos().getPosition());
					return false;
				}
			}
		}
		LOGGER.warn("Can not move figure {} of player {}, since the new field is null", figure.getCount(), playerName);
		return false;
	}
	
	@Override
	public void handlePacket(ServerPacket serverPacket) {
		ServerGameMap.super.handlePacket(serverPacket);
		
	}
	
	@Override
	public String toString() {
		return "LudoServerMap";
	}

}
