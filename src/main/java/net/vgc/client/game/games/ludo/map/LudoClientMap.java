package net.vgc.client.game.games.ludo.map;

import java.util.List;

import com.google.common.collect.Lists;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import net.vgc.Constans;
import net.vgc.client.Client;
import net.vgc.client.game.games.ludo.map.field.LudoClientField;
import net.vgc.client.game.games.ludo.player.LudoClientPlayer;
import net.vgc.client.game.games.ludo.player.figure.LudoClientFigure;
import net.vgc.client.game.map.ClientGameMap;
import net.vgc.client.player.LocalPlayer;
import net.vgc.game.games.ludo.map.field.LudoFieldPos;
import net.vgc.game.games.ludo.map.field.LudoFieldType;
import net.vgc.game.games.ludo.player.LudoPlayerType;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.field.GameFigure;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.util.Mth;
import net.vgc.util.Util;

public class LudoClientMap extends GridPane implements ClientGameMap, PacketHandler<ClientPacket> {
	
	protected final Client client;
	protected final List<LudoClientField> fields = Lists.newArrayList();
	protected final List<LudoClientField> homeFields = Lists.newArrayList();
	protected final List<LudoClientField> winFields = Lists.newArrayList();
	protected final ToggleGroup group;
	
	public LudoClientMap(Client client) {
		this.client = client;
		this.group = new ToggleGroup();
		this.init();
		this.addFields();
	}
	
	@Override
	public void init() {
		this.setAlignment(Pos.CENTER);
		this.setHgap(10.0);
		this.setVgap(10.0);
		this.setPadding(new Insets(20.0));
		this.setGridLinesVisible(Constans.DEBUG);
		this.group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			LocalPlayer player = this.client.getPlayer();
			if (player.isCurrent() && player.canSelect() && Mth.isInBounds(player.getCount(), 1, 6)) {
				
			} else {
				this.group.selectToggle(null);
			}
			this.getFields().forEach(LudoClientField::resetShadow);
		});
	}
	
	@Override
	public void addFields() {
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.GREEN, LudoFieldPos.ofGreen(0), 0, 4);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(1), 1, 4);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(2), 2, 4);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(3), 3, 4);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(4), 4, 4);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(5), 4, 3);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(6), 4, 2);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(7), 4, 1);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(8), 4, 0);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(9), 5, 0);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.YELLOW, LudoFieldPos.ofGreen(10), 6, 0);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(11), 6, 1);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(12), 6, 2);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(13), 6, 3); 
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(14), 6, 4);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(15), 7, 4);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(16), 8, 4);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(17), 9, 4);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(18), 10, 4);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(19), 10, 5);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.BLUE, LudoFieldPos.ofGreen(20), 10, 6);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(21), 9, 6);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(22), 8, 6);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(23), 7, 6);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(24), 6, 6);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(25), 6, 7);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(26), 6, 8);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(27), 6, 9);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(28), 6, 10);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(29), 5, 10);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.RED, LudoFieldPos.ofGreen(30), 4, 10);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(31), 4, 9);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(32), 4, 8);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(33), 4, 7);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(34), 4, 6);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(35), 3, 6);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(36), 2, 6);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(37), 1, 6);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(38), 0, 6);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(39), 0, 5);
		this.addField(LudoFieldType.HOME, LudoPlayerType.GREEN, LudoFieldPos.of(0), 0, 0);
		this.addField(LudoFieldType.HOME, LudoPlayerType.GREEN, LudoFieldPos.of(1), 1, 0);
		this.addField(LudoFieldType.HOME, LudoPlayerType.GREEN, LudoFieldPos.of(2), 1, 1);
		this.addField(LudoFieldType.HOME, LudoPlayerType.GREEN, LudoFieldPos.of(3), 0, 1);
		this.addField(LudoFieldType.HOME, LudoPlayerType.YELLOW, LudoFieldPos.of(0), 9, 0);
		this.addField(LudoFieldType.HOME, LudoPlayerType.YELLOW, LudoFieldPos.of(1), 10, 0);
		this.addField(LudoFieldType.HOME, LudoPlayerType.YELLOW, LudoFieldPos.of(2), 10, 1);
		this.addField(LudoFieldType.HOME, LudoPlayerType.YELLOW, LudoFieldPos.of(3), 9, 1);
		this.addField(LudoFieldType.HOME, LudoPlayerType.BLUE, LudoFieldPos.of(0), 9, 9);
		this.addField(LudoFieldType.HOME, LudoPlayerType.BLUE, LudoFieldPos.of(1), 10, 9);
		this.addField(LudoFieldType.HOME, LudoPlayerType.BLUE, LudoFieldPos.of(3), 10, 10);
		this.addField(LudoFieldType.HOME, LudoPlayerType.BLUE, LudoFieldPos.of(4), 9, 10);
		this.addField(LudoFieldType.HOME, LudoPlayerType.RED, LudoFieldPos.of(0), 0, 9);
		this.addField(LudoFieldType.HOME, LudoPlayerType.RED, LudoFieldPos.of(1), 1, 9);
		this.addField(LudoFieldType.HOME, LudoPlayerType.RED, LudoFieldPos.of(3), 1, 10);
		this.addField(LudoFieldType.HOME, LudoPlayerType.RED, LudoFieldPos.of(4), 0, 10);
		this.addField(LudoFieldType.WIN, LudoPlayerType.GREEN, LudoFieldPos.of(0), 1, 5);
		this.addField(LudoFieldType.WIN, LudoPlayerType.GREEN, LudoFieldPos.of(1), 2, 5);
		this.addField(LudoFieldType.WIN, LudoPlayerType.GREEN, LudoFieldPos.of(2), 3, 5);
		this.addField(LudoFieldType.WIN, LudoPlayerType.GREEN, LudoFieldPos.of(3), 4, 5);
		this.addField(LudoFieldType.WIN, LudoPlayerType.YELLOW, LudoFieldPos.of(0), 5, 1);
		this.addField(LudoFieldType.WIN, LudoPlayerType.YELLOW, LudoFieldPos.of(1), 5, 2);
		this.addField(LudoFieldType.WIN, LudoPlayerType.YELLOW, LudoFieldPos.of(2), 5, 3);
		this.addField(LudoFieldType.WIN, LudoPlayerType.YELLOW, LudoFieldPos.of(3), 5, 4);
		this.addField(LudoFieldType.WIN, LudoPlayerType.BLUE, LudoFieldPos.of(3), 6, 5);
		this.addField(LudoFieldType.WIN, LudoPlayerType.BLUE, LudoFieldPos.of(2), 7, 5);
		this.addField(LudoFieldType.WIN, LudoPlayerType.BLUE, LudoFieldPos.of(1), 8, 5);
		this.addField(LudoFieldType.WIN, LudoPlayerType.BLUE, LudoFieldPos.of(0), 9, 5);
		this.addField(LudoFieldType.WIN, LudoPlayerType.RED, LudoFieldPos.of(3), 5, 6);
		this.addField(LudoFieldType.WIN, LudoPlayerType.RED, LudoFieldPos.of(2), 5, 7);
		this.addField(LudoFieldType.WIN, LudoPlayerType.RED, LudoFieldPos.of(1), 5, 8);
		this.addField(LudoFieldType.WIN, LudoPlayerType.RED, LudoFieldPos.of(0), 5, 9);
	}
	
	protected void addField(LudoFieldType fieldType, LudoPlayerType colorType, LudoFieldPos fieldPos, int column, int row) {
		LudoClientField field = new LudoClientField(this.group, fieldType, colorType, fieldPos, 100.0);
		field.setOnAction((event) -> {
			LocalPlayer player = this.client.getPlayer();
			if (player.isCurrent() && player.canSelect() && Mth.isInBounds(player.getCount(), 1, 6)) {
				if (field.canSelectField()) {
					this.getFields().forEach(LudoClientField::resetShadow);
					LudoClientFigure figure = field.getFigure();
					if (figure != null) {
						LudoClientField nextField = this.getNextField(figure, player.getCount());
						if (nextField != null) {
							nextField.setShadowed(true);
						} else {
							LOGGER.warn("Fail to display shadow figure, since there is no next field");
						}
					}
				}
			}
		});
		this.add(field, column, row);
		if (fieldType == LudoFieldType.DEFAULT) {
			this.fields.add(field);
		} else if (fieldType == LudoFieldType.HOME) {
			this.homeFields.add(field);
		} else if (fieldType == LudoFieldType.WIN) {
			this.winFields.add(field);
		} else {
			LOGGER.warn("Fail to add field {} with type {} to ludo client map", fieldPos.getPosition(), fieldType);
		}
	}
	
	@Override
	public void init(List<? extends GamePlayer> players) {
		this.getFields().forEach(LudoClientField::clear);
		for (GamePlayer gamePlayer : players) {
			if (gamePlayer instanceof LudoClientPlayer player) {
				for (LudoClientFigure figure : player.getFigures()) {
					this.getField(LudoFieldType.HOME, player.getPlayerType(), figure.getHomePos()).setFigure(figure);
				}
			} else {
				LOGGER.error("Can not add a game player of type {} to a ludo game", gamePlayer.getClass().getSimpleName());
				throw new RuntimeException("Can not add a game player of type " + gamePlayer.getClass().getSimpleName() + " to a ludo game");
			}
		}
	}

	@Override
	public List<LudoClientField> getFields() {
		return Util.concatLists(this.fields, this.homeFields, this.winFields);
	}

	@Override
	public LudoClientField getField(GameFigure figure) {
		for (LudoClientField field : this.getFields()) {
			if (!field.isEmpty() && field.getFigure().equals(figure)) {
				return field;
			}
		}
		return null;
	}

	@Override
	public LudoClientField getField(GameFieldType fieldType, GamePlayerType playerType, GameFieldPos fieldPos) {
		if (playerType != null) {
			if (fieldType == LudoFieldType.HOME) {
				return this.getHomeFields(playerType).get(fieldPos.getPositionFor(playerType));
			} else if (fieldType == LudoFieldType.WIN) {
				return this.getWinFields(playerType).get(fieldPos.getPositionFor(playerType));
			}
			LOGGER.warn("Fail to get a default field with type {} at position {}", fieldType, fieldPos.getPosition());
		}
		return this.fields.get(fieldPos.getPosition());
	}

	@Override
	public LudoClientField getNextField(GameFigure figure, int count) {
		String playerName = figure.getPlayer().getPlayer().getProfile().getName();
		LudoPlayerType playerType = (LudoPlayerType) figure.getPlayerType();
		LudoClientField currentField = this.getField(figure);
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
							LOGGER.info("The next field for figure {} of player {}, is out of map", figure.getCount(), playerName);
							return null;
						}
						return this.getWinFields(playerType).get(position);
					} else {
						if (position > 43) {
							LOGGER.info("The next field for figure {} of player {}, is out of map", figure.getCount(), playerName);
							return null;
						} else if (position > 39) {
							return this.getWinFields(playerType).get(position - 40);
						} else {
							return this.fields.get(LudoFieldPos.of(playerType, position).getPosition());
						}
					}
				}
			}
			return currentField;
		}
		LOGGER.warn("Fail to get next field for figure {} of player {}, since the current field is null", figure.getCount(), playerName);
		return null;
	}

	@Override
	public List<LudoClientField> getHomeFields(GamePlayerType playerType) {
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
	public List<LudoClientField> getStartFields(GamePlayerType playerType) {
		switch ((LudoPlayerType) playerType) {
			case GREEN: Lists.newArrayList(LudoFieldPos.ofGreen(0));
			case YELLOW: Lists.newArrayList(LudoFieldPos.ofYellow(0));
			case BLUE: Lists.newArrayList(LudoFieldPos.ofBlue(0));
			case RED: Lists.newArrayList(LudoFieldPos.ofRed(0));
			default: break;
		}
		LOGGER.warn("Fail to get start field for type {}", playerType);
		return Lists.newArrayList();
	}

	@Override
	public List<LudoClientField> getWinFields(GamePlayerType playerType) {
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
	public LudoClientField getSelectedField() {
		Toggle toggle = this.group.getSelectedToggle();
		if (toggle instanceof LudoClientField field) {
			if (!field.isEmpty()) {
				return field;
			} else {
				LOGGER.warn("Fail to get the selected field, since the selected field does not have a figure on it");
				return null;
			}
		}
		LOGGER.info("Fail to get the selected field, since there is no field selected");
		return null;
	}
	
	@Override
	public void handlePacket(ClientPacket packet) {
		ClientGameMap.super.handlePacket(packet);
		
	}
	
	@Override
	public String toString() {
		return "LudoClientMap";
	}

}
