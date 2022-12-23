package net.vgc.server.games.ludo.map;

import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.luis.utils.math.Mth;
import net.vgc.game.Game;
import net.vgc.game.map.field.GameField;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.figure.GameFigure;
import net.vgc.games.ludo.map.field.LudoFieldPos;
import net.vgc.games.ludo.map.field.LudoFieldType;
import net.vgc.games.ludo.player.LudoPlayerType;
import net.vgc.server.Server;
import net.vgc.server.game.map.AbstractServerGameMap;
import net.vgc.server.games.ludo.map.field.LudoServerField;
import net.vgc.server.games.ludo.player.LudoServerPlayer;
import net.vgc.util.Util;
import net.vgc.util.exception.InvalidValueException;

/**
 *
 * @author Luis-st
 *
 */

public class LudoServerMap extends AbstractServerGameMap {
	
	private final List<GameField> homeFields;
	private final List<GameField> winFields;
	
	public LudoServerMap(Server server, Game game) {
		super(server, game);
		this.homeFields = Lists.newArrayList();
		this.winFields = Lists.newArrayList();
		this.addFields();
	}
	
	@Override
	public void init(List<GamePlayer> players) {
		super.init(players);
		for (GamePlayer gamePlayer : players) {
			if (gamePlayer instanceof LudoServerPlayer player) {
				LOGGER.debug("Add figures ({}) of player {}, to their home fields", player.getFigures().size(), player.getPlayer().getProfile().getName());
				for (GameFigure figure : player.getFigures()) {
					this.getField(LudoFieldType.HOME, player.getPlayerType(), figure.getHomePos()).setFigure(figure);
				}
			} else {
				LOGGER.error("Can not add a game player of type {} to a ludo game", gamePlayer.getClass().getSimpleName());
				throw new RuntimeException("Can not add a game player of type " + gamePlayer.getClass().getSimpleName() + " to a ludo game");
			}
		}
	}
	
	@Override
	public void addFields() {
		for (int i = 0; i < 40; i++) {
			if (i == 0) {
				this.addField(new LudoServerField(this, LudoFieldType.DEFAULT, LudoPlayerType.GREEN, LudoFieldPos.ofGreen(i)));
			} else if (i == 10) {
				this.addField(new LudoServerField(this, LudoFieldType.DEFAULT, LudoPlayerType.YELLOW, LudoFieldPos.ofGreen(i)));
			} else if (i == 20) {
				this.addField(new LudoServerField(this, LudoFieldType.DEFAULT, LudoPlayerType.BLUE, LudoFieldPos.ofGreen(i)));
			} else if (i == 30) {
				this.addField(new LudoServerField(this, LudoFieldType.DEFAULT, LudoPlayerType.RED, LudoFieldPos.ofGreen(i)));
			} else {
				this.addField(new LudoServerField(this, LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(i)));
			}
		}
		for (int i = 0; i < 16; i++) {
			this.homeFields.add(new LudoServerField(this, LudoFieldType.HOME, this.getFieldColor(i), LudoFieldPos.of(i % 4)));
		}
		for (int i = 0; i < 16; i++) {
			this.winFields.add(new LudoServerField(this, LudoFieldType.WIN, this.getFieldColor(i), LudoFieldPos.of(i % 4)));
		}
	}
	
	private LudoPlayerType getFieldColor(int i) {
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
	
	@Override
	public List<GameField> getFields() {
		return Stream.of(super.getFields(), this.homeFields, this.winFields).flatMap(List::stream).collect(ImmutableList.toImmutableList());
	}
	
	@Override
	public GameField getField(GameFieldType fieldType, GamePlayerType playerType, GameFieldPos fieldPos) {
		playerType = Util.warpNullTo(playerType, LudoPlayerType.NO);
		if (fieldType == LudoFieldType.DEFAULT) {
			if (playerType != LudoPlayerType.NO && fieldPos.getPosition() % 10 == 0) {
				return this.getFields().get(fieldPos.getPosition());
			}
			return this.getFields().get(fieldPos.getPosition());
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
	public GameField getNextField(GameFigure figure, int count) {
		String playerName = figure.getPlayer().getPlayer().getProfile().getName();
		GamePlayerType playerType = figure.getPlayerType();
		GameField currentField = this.getField(figure);
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
							return this.getFields().get(LudoFieldPos.of(playerType, position).getPosition());
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
	public List<GameField> getHomeFields(GamePlayerType playerType) {
		switch ((LudoPlayerType) playerType) {
			case GREEN:
				return this.homeFields.subList(0, 4);
			case YELLOW:
				return this.homeFields.subList(4, 8);
			case BLUE:
				return this.homeFields.subList(8, 12);
			case RED:
				return this.homeFields.subList(12, 16);
			default:
				break;
		}
		LOGGER.warn("Fail to get home fields for type {}", playerType);
		return Lists.newArrayList();
	}
	
	@Override
	public List<GameField> getStartFields(GamePlayerType playerType) {
		return switch ((LudoPlayerType) playerType) {
			case GREEN, YELLOW, BLUE, RED -> Lists.newArrayList(this.getFields().get(LudoFieldPos.of((LudoPlayerType) playerType, 0).getPosition()));
			default -> {
				LOGGER.warn("Fail to get start field for type {}", playerType);
				yield Lists.newArrayList();
			}
		};
	}
	
	@Override
	public List<GameField> getWinFields(GamePlayerType playerType) {
		switch ((LudoPlayerType) playerType) {
			case GREEN:
				return this.winFields.subList(0, 4);
			case YELLOW:
				return this.winFields.subList(4, 8);
			case BLUE:
				return this.winFields.subList(8, 12);
			case RED:
				return this.winFields.subList(12, 16);
			default:
				break;
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
	public String toString() {
		return "LudoServerMap";
	}
	
}
