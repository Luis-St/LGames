package net.luis.client.screen.game;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import net.luis.client.fx.PlayerInfoPane;
import net.luis.client.fx.PlayerScorePane;
import net.luis.client.games.ttt.TTTClientGame;
import net.luis.client.games.ttt.map.TTTClientMap;
import net.luis.fx.ButtonBox;
import net.luis.fxutils.FxUtils;
import net.luis.game.map.field.GameField;
import net.luis.games.ttt.map.field.TTTFieldType;
import net.luis.language.TranslationKey;
import net.luis.network.packet.client.ClientPacket;
import net.luis.network.packet.client.game.GameResultPacket;
import net.luis.network.packet.listener.PacketListener;
import net.luis.network.packet.listener.PacketSubscriber;
import net.luis.network.packet.server.game.ExitGameRequestPacket;
import net.luis.network.packet.server.game.PlayAgainGameRequestPacket;
import net.luis.network.packet.server.game.SelectGameFieldPacket;

/**
 *
 * @author Luis-st
 *
 */

@PacketSubscriber("#getStage#getScene#getScreen")
public class TTTScreen extends GameScreen {
	
	private final TTTClientGame game;
	private PlayerInfoPane playerInfo;
	private ButtonBox leaveButton;
	private ButtonBox playAgainButton;
	private ButtonBox confirmActionButton;
	
	public TTTScreen(TTTClientGame game) {
		super(TranslationKey.createAndGet("client.constans.name"), 900, 700);
		this.game = game;
	}
	
	@Override
	public void init() {
		this.playerInfo = new PlayerInfoPane(this.game, 200.0, PlayerScorePane.Type.WIN);
		this.leaveButton = new ButtonBox(TranslationKey.createAndGet("screen.lobby.leave"), Pos.CENTER, 20.0, this::handleLeave);
		this.playAgainButton = new ButtonBox(TranslationKey.createAndGet("screen.tic_tac_toe.play_again"), Pos.CENTER, 20.0, this::handlePlayAgain);
		this.playAgainButton.getNode().setDisable(true);
		this.confirmActionButton = new ButtonBox(TranslationKey.createAndGet("screen.tic_tac_toe.confirm_action"), Pos.CENTER, 20.0, this::handleConfirmAction);
	}
	
	private void handleLeave() {
		this.client.getServerHandler().send(new ExitGameRequestPacket(this.getPlayer().getProfile()));
	}
	
	private void handlePlayAgain() {
		if (this.client.getPlayer().isAdmin()) {
			this.client.getServerHandler().send(new PlayAgainGameRequestPacket(this.getPlayer().getProfile()));
			this.playAgainButton.getNode().setDisable(true);
		}
	}
	
	private void handleConfirmAction() {
		GameField field = this.game.getMap().getSelectedField();
		if (field != null) {
			if (this.getPlayer().isCurrent()) {
				this.client.getServerHandler().send(new SelectGameFieldPacket(this.getPlayer().getProfile(), TTTFieldType.DEFAULT, field.getFieldPos()));
				this.getPlayer().setCurrent(false);
			} else {
				LOGGER.info("It is not the turn of the local player {}", this.getPlayer().getProfile().getName());
			}
		} else {
			LOGGER.info("No field selected");
		}
	}
	
	@PacketListener
	public void handlePacket(ClientPacket clientPacket) {
		this.playerInfo.update();
		if (clientPacket instanceof GameResultPacket) {
			this.playAgainButton.getNode().setDisable(!this.getPlayer().isAdmin());
		}
	}
	
	@Override
	protected Pane createPane() {
		BorderPane pane = new BorderPane();
		pane.setLeft(this.createInfoPane());
		pane.setCenter(this.createGamePane());
		pane.setBottom(this.createActionPane());
		return pane;
	}
	
	private PlayerInfoPane createInfoPane() {
		return this.playerInfo;
	}
	
	private Pane createGamePane() {
		if (this.game.getMap() instanceof TTTClientMap map) {
			return map.getGridPane();
		}
		throw new NullPointerException("The map of game tic tac toe is null");
	}
	
	private Pane createActionPane() {
		GridPane pane = FxUtils.makeGrid(Pos.CENTER, 10.0, 30.0);
		pane.addRow(0, this.leaveButton, this.playAgainButton, this.confirmActionButton);
		return pane;
	}
	
}
