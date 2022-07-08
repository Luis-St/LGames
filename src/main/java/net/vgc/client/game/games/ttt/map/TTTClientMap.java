package net.vgc.client.game.games.ttt.map;

import java.util.List;

import com.google.common.collect.Lists;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import net.vgc.Constans;
import net.vgc.client.Client;
import net.vgc.client.fx.game.wrapper.GridPaneWrapper;
import net.vgc.client.game.games.ttt.map.field.TTTClientField;
import net.vgc.client.game.map.AbstractClientGameMap;
import net.vgc.game.Game;
import net.vgc.game.GameResult;
import net.vgc.game.games.ttt.map.field.TTTFieldPos;
import net.vgc.game.map.field.GameField;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.figure.GameFigure;

public class TTTClientMap extends AbstractClientGameMap implements GridPaneWrapper {
	
	private final ToggleGroup group;
	private final GridPane gridPane;
	
	public TTTClientMap(Client client, Game game) {
		super(client, game);
		this.group = new ToggleGroup();
		this.gridPane = new GridPane();
		this.init();
		this.addFields();
		this.addBorders();
	}
	
	@Override
	public GridPane getGridPane() {
		return this.gridPane;
	}
	
	@Override
	public void init() {
		this.setAlignment(Pos.CENTER);
		this.setHgap(10.0);
		this.setVgap(10.0);
		this.setPadding(new Insets(20.0));
		this.setGridLinesVisible(Constans.DEBUG);
		this.group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue instanceof TTTClientField oldField) {
				if (newValue instanceof TTTClientField newField) {
					if (oldField.isShadowed()) {
						oldField.setShadowed(false);
					}
				} else {
					oldField.setSelected(true);
				}
			}
			this.getFields().stream().filter(GameField::isShadowed).forEach(GameField::clearShadow);
		});
	}

	@Override
	public void addFields() {
		this.addField(TTTFieldPos.of(0), 0, 0);
		this.addField(TTTFieldPos.of(1), 2, 0);
		this.addField(TTTFieldPos.of(2), 4, 0);
		this.addField(TTTFieldPos.of(3), 0, 2);
		this.addField(TTTFieldPos.of(4), 2, 2);
		this.addField(TTTFieldPos.of(5), 4, 2);
		this.addField(TTTFieldPos.of(6), 0, 4);
		this.addField(TTTFieldPos.of(7), 2, 4);
		this.addField(TTTFieldPos.of(8), 4, 4);
	}
	
	protected void addField(TTTFieldPos fieldPos, int column, int row) {
		TTTClientField field = new TTTClientField(this.getClient(), this, this.group, fieldPos, 150.0);
		field.setOnAction((event) -> {
			if (this.getClient().getPlayer().isCurrent()) {
				if (field.isEmpty() && field.getResult() == GameResult.NO) {
					field.setShadowed(true);
				}
			} else {
				this.group.selectToggle(null);
			}
		});
		this.add(field.getToggleButton(), column, row);
		this.addField(field);
	}
	
	public void addBorders() {
		this.addBorder(10, 160, 1, 0);
		this.addBorder(10, 160, 3, 0);
		this.addBorder(160, 10, 0, 1);
		this.addBorder(160, 10, 2, 1);
		this.addBorder(160, 10, 4, 1);
		this.addBorder(10, 160, 1, 2);
		this.addBorder(10, 160, 3, 2);
		this.addBorder(160, 10, 0, 3);
		this.addBorder(160, 10, 2, 3);
		this.addBorder(160, 10, 4, 3);
		this.addBorder(10, 160, 1, 4);
		this.addBorder(10, 160, 3, 4);
	}
	
	protected void addBorder(int width, int height, int column, int row) {
		this.add(new Rectangle(width, height, Color.BLACK), column, row);
	}

	@Override
	public GameField getField(GameFieldType fieldType, GamePlayerType playerType, GameFieldPos fieldPos) {
		return this.getFields().get(fieldPos.getPosition());
	}

	@Override
	public final GameField getNextField(GameFigure figure, int count) {
		LOGGER.warn("Fail to get next field for figure {} of player {}, since tic tac toe figures does not have a next field", figure.getCount(), figure.getPlayer().getPlayer().getProfile().getName());
		return null;
	}

	@Override
	public List<GameField> getHomeFields(GamePlayerType playerType) {
		return Lists.newArrayList();
	}

	@Override
	public List<GameField> getStartFields(GamePlayerType playerType) {
		return Lists.newArrayList();
	}

	@Override
	public List<GameField> getWinFields(GamePlayerType playerType) {
		return Lists.newArrayList();
	}

	@Override
	public GameField getSelectedField() {
		Toggle toggle = this.group.getSelectedToggle();
		if (toggle instanceof TTTClientField field) {
			if (field.isEmpty()) {
				return field;
			} else {
				LOGGER.warn("Fail to get the selected field, since the selected field can not have a figure on it");
				return null;
			}
		}
		LOGGER.info("Fail to get the selected field, since there is no field selected");
		return null;
	}
	
	/*@Override
	public void handlePacket(ClientPacket clientPacket) {
		if (clientPacket instanceof UpdateGameMapPacket packet) {
			for (GameFieldInfo fieldInfo : packet.getFieldInfos()) {
				GameProfile profile = fieldInfo.getProfile();
				if (fieldInfo.getFieldPos() instanceof TTTFieldPos fieldPos) {
					TTTClientField field = this.getField(null, null, fieldPos);
					if (field != null) {
						if (field.isShadowed()) {
							field.setShadowed(false);
						}
						if (field.getResult() != GameResult.NO) {
							field.setResult(GameResult.NO);
						}
						TTTClientPlayer player = (TTTClientPlayer) this.game.getPlayerFor(profile);
						if (player != null) {
							TTTClientFigure figure = player.getFigure(fieldInfo.getFigureCount());
							UUID uuid = figure.getUUID();
							UUID serverUUID = fieldInfo.getFigureUUID();
							if (uuid.equals(serverUUID)) {
								field.setFigure(figure);
							} else {
								LOGGER.warn("Fail to place figure {} of player {} at field {}, since the figure uuid {} does not match with the server on {}", figure.getCount(), profile.getName(), fieldPos.getPosition(), uuid, serverUUID);
							}
						} else if (profile.equals(GameProfile.EMPTY)) {
							field.setFigure(null);
						} else {
							LOGGER.warn("Fail to place a figure of player {} at field {}, since the player does not exsists", profile.getName(), fieldPos.getPosition());
						}
					} else {
						LOGGER.warn("Fail to update game field, since there is not field for pos {}", fieldPos.getPosition());
					}
				} else {
					LOGGER.warn("Fail to update game field, since field pos is a instance of {}", fieldInfo.getFieldPos().getClass().getSimpleName());
				}
			}
		} else if (clientPacket instanceof TTTGameResultPacket packet) {
			GameResult result = packet.getResult();
			if (result != GameResult.NO) {
				TTTResultLine resultLine = packet.getResultLine();
				if (result == GameResult.DRAW) {
					for (TTTClientField field : this.fields) {
						field.setResult(GameResult.DRAW);
					}
				} else {
					if (resultLine != TTTResultLine.EMPTY) {
						for (TTTFieldPos fieldPos : resultLine.getPoses()) {
							this.getField(null, null, fieldPos).setResult(result);
						}
					} else {
						LOGGER.warn("Fail to handle game result {}, since there is no result line", result);
					}
				}
			} else {
				LOGGER.warn("Fail to handle game result {}", result);
			}
		}
	}*/
	
	@Override
	public String toString() {
		return "TTTClientMap";
	}

}
