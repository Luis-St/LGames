package net.luis.client.games.wins4.map;

import com.google.common.collect.Lists;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import net.luis.client.Client;
import net.luis.client.fx.game.IndexToggleButton;
import net.luis.client.fx.game.wrapper.StackPaneWrapper;
import net.luis.client.game.map.AbstractClientGameMap;
import net.luis.client.games.wins4.map.field.Wins4ClientField;
import net.luis.fxutils.FxUtils;
import net.luis.game.Game;
import net.luis.game.GameResult;
import net.luis.game.map.field.GameField;
import net.luis.game.map.field.GameFieldInfo;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.map.field.GameFieldType;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.GamePlayerType;
import net.luis.game.player.figure.GameFigure;
import net.luis.games.wins4.map.field.Wins4FieldPos;
import net.luis.network.packet.client.ClientPacket;
import net.luis.network.packet.client.game.GameResultPacket;
import net.luis.network.packet.client.game.UpdateGameMapPacket;
import net.luis.network.packet.listener.PacketListener;
import net.luis.network.packet.listener.PacketSubscriber;
import net.luis.player.GameProfile;
import net.luis.utils.math.Mth;
import net.luis.utils.util.Utils;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

@PacketSubscriber("#getGame#getMap")
public class Wins4ClientMap extends AbstractClientGameMap implements StackPaneWrapper {
	
	private final ToggleGroup group;
	private final StackPane stackPane;
	private final GridPane fieldPane;
	private final GridPane interactionPane;
	
	public Wins4ClientMap(Client client, Game game) {
		super(client, game);
		this.group = new ToggleGroup();
		this.stackPane = new StackPane();
		this.fieldPane = FxUtils.makeGrid(Pos.CENTER, 0.0, 0.0);
		this.interactionPane = FxUtils.makeGrid(Pos.CENTER, 0.0, 0.0);
		this.init();
		this.addFields();
		this.addInteractions();
		this.mergePanes();
	}
	
	@Override
	public StackPane getStackPane() {
		return this.stackPane;
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
	
	private void addField(Wins4FieldPos fieldPos) {
		Wins4ClientField field = new Wins4ClientField(this.getClient(), this, fieldPos, 120.0);
		this.fieldPane.add(field.getLabel(), fieldPos.getColumn(), fieldPos.getRow());
		this.addField(field);
		this.group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			this.getFields().stream().filter(GameField::isShadowed).forEach(GameField::clearShadow);
		});
	}
	
	public void addInteractions() {
		for (int i = 0; i < 7; i++) {
			this.addInteraction(i, i);
		}
	}
	
	private void mergePanes() {
		this.getChildren().addAll(this.fieldPane, this.interactionPane);
	}
	
	private void addInteraction(int index, int column) {
		IndexToggleButton button = new IndexToggleButton(index);
		button.setToggleGroup(this.group);
		button.setBackground(null);
		button.setPrefSize(120.0, 6120.0);
		button.setOnAction((event) -> {
			if (this.getClient().getPlayer().isCurrent()) {
				List<GameField> fields = Utils.reverseList(this.getFieldsForColumn(index));
				for (GameField field : fields) {
					if (field.isEmpty()) {
						field.setShadowed(true);
						break;
					}
				}
			} else {
				this.getFields().stream().filter(GameField::isShadowed).forEach(GameField::clearShadow);
				this.group.selectToggle(null);
			}
		});
		this.interactionPane.add(button, column, 0);
	}
	
	@Override
	public GameField getField(GameFieldType fieldType, GamePlayerType playerType, GameFieldPos fieldPos) {
		return this.getFields().get(fieldPos.getPosition());
	}
	
	@Override
	public final GameField getNextField(GameFigure figure, int count) {
		GameMap.LOGGER.warn("Fail to get next field for figure {} of player {}, since the 4 wins figures does not have a next field", figure.getCount(), figure.getPlayer().getPlayer().getProfile().getName());
		return null;
	}
	
	public List<GameField> getFieldsForColumn(int column) {
		if (Mth.isInBounds(column, 0, 6)) {
			List<GameField> fields = Lists.newArrayList();
			for (int i = 0; i < 6; i++) {
				fields.add(this.getField(null, null, Wins4FieldPos.of(i, column)));
			}
			return fields;
		}
		return Lists.newArrayList();
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
	public final GameField getSelectedField() {
		GameMap.LOGGER.warn("Fail to get the selected field, since the 4 wins map does not have a selected field");
		return null;
	}
	
	public int getSelectedColumn() {
		Toggle toggle = this.group.getSelectedToggle();
		if (toggle instanceof IndexToggleButton button) {
			return button.getIndex();
		}
		GameMap.LOGGER.info("Fail to get the selected column, since there is no column selected");
		return -1;
	}
	
	@PacketListener
	public void handlePacket(ClientPacket clientPacket) {
		if (clientPacket instanceof UpdateGameMapPacket packet) {
			for (GameFieldInfo fieldInfo : packet.getFieldInfos()) {
				GameProfile profile = fieldInfo.getProfile();
				GameFieldPos fieldPos = fieldInfo.getFieldPos();
				GameField field = this.getField(null, null, fieldPos);
				if (field != null) {
					if (field.isShadowed()) {
						field.setShadowed(false);
					}
					if (field.getResult() != GameResult.NO) {
						field.setResult(GameResult.NO);
					}
					GamePlayer player = this.getGame().getPlayerFor(profile);
					if (player != null) {
						GameFigure figure = player.getFigure(fieldInfo.getFigureCount());
						assert figure != null;
						UUID uuid = figure.getUUID();
						UUID serverUUID = fieldInfo.getFigureUUID();
						if (uuid.equals(serverUUID)) {
							field.setFigure(figure);
						} else {
							GameMap.LOGGER.warn("Fail to place figure {} of player {} at field {}, since the figure uuid {} does not match with the server on {}", figure.getCount(), profile.getName(), fieldPos.getPosition(), uuid, serverUUID);
						}
					} else if (profile.equals(GameProfile.EMPTY)) {
						field.setFigure(null);
					} else {
						GameMap.LOGGER.warn("Fail to place a figure of player {} at field {}, since the player does not exists", profile.getName(), fieldPos.getPosition());
					}
				} else {
					GameMap.LOGGER.warn("Fail to update game field, since there is not field for pos {}", fieldPos.getPosition());
				}
			}
		} else if (clientPacket instanceof GameResultPacket packet) {
			GameResult result = packet.getResult();
			if (result != GameResult.NO) {
				for (GameField field : this.getFields()) {
					field.setResult(result);
				}
			} else {
				GameMap.LOGGER.warn("Fail to handle game result {}", result);
			}
		}
	}
	
	@Override
	public String toString() {
		return "Win4ClientMap";
	}
	
}
