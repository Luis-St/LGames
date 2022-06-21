package net.vgc.client.game.games.wins4.map;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import net.vgc.client.Client;
import net.vgc.client.fx.FxUtil;
import net.vgc.client.fx.game.IndexToggleButton;
import net.vgc.client.game.games.wins4.Wins4ClientGame;
import net.vgc.client.game.games.wins4.map.field.Wins4ClientField;
import net.vgc.client.game.games.wins4.player.Wins4ClientPlayer;
import net.vgc.client.game.games.wins4.player.figure.Wins4ClientFigure;
import net.vgc.client.game.map.ClientGameMap;
import net.vgc.game.GameResult;
import net.vgc.game.games.wins4.Wins4ResultLine;
import net.vgc.game.games.wins4.map.field.Wins4FieldPos;
import net.vgc.game.map.field.GameFieldInfo;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.field.GameFigure;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.network.packet.client.game.UpdateGameMapPacket;
import net.vgc.network.packet.client.game.Wins4GameResultPacket;
import net.vgc.player.GameProfile;
import net.vgc.util.Mth;
import net.vgc.util.Util;

public class Wins4ClientMap extends StackPane implements ClientGameMap {
	
	protected final Client client;
	protected final ToggleGroup group;
	protected final Wins4ClientGame game;
	protected final GridPane fieldPane;
	protected final GridPane interactionPane;
	protected final List<Wins4ClientField> fields = Lists.newArrayList();
	
	public Wins4ClientMap(Client client, Wins4ClientGame game) {
		this.client = client;
		this.group = new ToggleGroup();
		this.game = game;
		this.fieldPane = FxUtil.makeGrid(Pos.CENTER, 0.0, 0.0);
		this.interactionPane = FxUtil.makeGrid(Pos.CENTER, 0.0, 0.0);
		this.init();
		this.addFields();
		this.addInteractions();
		this.mergePanes();
	}
	
	@Override
	public void init() {
		this.setAlignment(Pos.CENTER);
		this.setPadding(new Insets(20.0));
	}

	@Override
	public void addFields() {
		for (int i = 0; i < 42; i++) {
			this.addField(Wins4FieldPos.of(i));
		}
	}
	
	protected void addField(Wins4FieldPos fieldPos) {
		Wins4ClientField field = new Wins4ClientField(this.client, this.game, fieldPos, 120.0);
		this.fieldPane.add(field, fieldPos.getColumn(), fieldPos.getRow());
		this.fields.add(field);
		this.group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			this.fields.stream().filter(Wins4ClientField::isShadowed).forEach(Wins4ClientField::resetShadow);
		});
	}
	
	public void addInteractions() {
		for (int i = 0; i < 7; i++) {
			this.addInteraction(i, i, 0);
		}
	}
	
	protected void mergePanes() {
		this.getChildren().addAll(this.fieldPane, this.interactionPane);
	}
	
	protected void addInteraction(int index, int column, int row) {
		IndexToggleButton button = new IndexToggleButton(index);
		button.setToggleGroup(this.group);
		button.setBackground(null);
		button.setPrefSize(120.0, 6 * 120.0);
		button.setOnAction((event) -> {
			if (this.client.getPlayer().isCurrent()) {
				List<Wins4ClientField> fields = Util.reverseList(this.getFieldsForColumn(index));
				for (Wins4ClientField field : fields) {
					if (field.isEmpty()) {
						field.setShadowed(true);
						break;
					}
				}
			} else {
				this.fields.stream().filter(Wins4ClientField::isShadowed).forEach(Wins4ClientField::resetShadow);
				this.group.selectToggle(null);
			}
		});
		this.interactionPane.add(button, column, row);
	}

	@Override
	public void init(List<? extends GamePlayer> players) {
		this.getFields().forEach(Wins4ClientField::clear);
	}

	@Override
	public Wins4ClientGame getGame() {
		return this.game;
	}

	@Override
	public List<Wins4ClientField> getFields() {
		return this.fields;
	}

	@Override
	public Wins4ClientField getField(GameFigure figure) {
		for (Wins4ClientField field : this.getFields()) {
			if (!field.isEmpty() && field.getFigure().equals(figure)) {
				return field;
			}
		}
		return null;
	}

	@Override
	public Wins4ClientField getField(GameFieldType fieldType, GamePlayerType playerType, GameFieldPos fieldPos) {
		return this.fields.get(fieldPos.getPosition());
	}

	@Override
	public Wins4ClientField getNextField(GameFigure figure, int count) {
		LOGGER.warn("Fail to get next field for figure {} of player {}, since the 4 wins figures does not have a next field", figure.getCount(), figure.getPlayer().getPlayer().getProfile().getName());
		return null;
	}
	
	public List<Wins4ClientField> getFieldsForColumn(int column) {
		if (Mth.isInBounds(column, 0, 6)) {
			List<Wins4ClientField> fields = Lists.newArrayList();
			for (int i = 0; i < 6; i++) {
				fields.add(this.getField(null, null, Wins4FieldPos.of(i, column)));
			}
			return fields;
		}
		return Lists.newArrayList();
	}

	@Override
	public List<Wins4ClientField> getHomeFields(GamePlayerType playerType) {
		return Lists.newArrayList();
	}

	@Override
	public List<Wins4ClientField> getStartFields(GamePlayerType playerType) {
		return Lists.newArrayList();
	}

	@Override
	public List<Wins4ClientField> getWinFields(GamePlayerType playerType) {
		return Lists.newArrayList();
	}

	@Override
	public Wins4ClientField getSelectedField() {
		LOGGER.warn("Fail to get the selected field, since the 4 wins map does not have a selected field");
		return null;
	}
	
	public int getSelectedColumn() {
		Toggle toggle = this.group.getSelectedToggle();
		if (toggle instanceof IndexToggleButton button) {
			return button.getIndex();
		}
		LOGGER.info("Fail to get the selected column, since there is no column selected");
		return -1;
	}
	
	@Override
	public void handlePacket(ClientPacket clientPacket) {
		if (clientPacket instanceof UpdateGameMapPacket packet) {
			for (GameFieldInfo fieldInfo : packet.getFieldInfos()) {
				GameProfile profile = fieldInfo.getProfile();
				if (fieldInfo.getFieldPos() instanceof Wins4FieldPos fieldPos) {
					Wins4ClientField field = this.getField(null, null, fieldPos);
					if (field != null) {
						if (field.isShadowed()) {
							field.setShadowed(false);
						}
						if (field.getResult() != GameResult.NO) {
							field.setResult(GameResult.NO);
						}
						Wins4ClientPlayer player = (Wins4ClientPlayer) this.game.getPlayerFor(profile);
						if (player != null) {
							Wins4ClientFigure figure = player.getFigure(fieldInfo.getFigureCount());
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
		} else if (clientPacket instanceof Wins4GameResultPacket packet) {
			GameResult result = packet.getResult();
			if (result != GameResult.NO) {
				for (Wins4ClientField field : this.fields) {
					field.setResult(result);
				}
			} else {
				LOGGER.warn("Fail to handle game result {}", result);
			}
		}
	}
	
	@Override
	public String toString() {
		return "Win4ClientMap";
	}

}
