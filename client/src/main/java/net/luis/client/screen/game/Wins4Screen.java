package net.luis.client.screen.game;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import net.luis.client.fx.PlayerInfoPane;
import net.luis.client.fx.PlayerScorePane;
import net.luis.fx.ButtonBox;
import net.luis.fxutils.FxUtils;
import net.luis.language.TranslationKey;
import net.luis.network.packet.client.ClientPacket;
import net.luis.network.packet.client.game.GameResultPacket;
import net.luis.network.packet.listener.PacketListener;
import net.luis.network.packet.listener.PacketSubscriber;
import net.luis.network.packet.server.game.ExitGameRequestPacket;
import net.luis.network.packet.server.game.PlayAgainGameRequestPacket;
import net.luis.network.packet.server.game.SelectGameFieldPacket;
import net.luis.wins4.Wins4Game;
import net.luis.wins4.map.Wins4Map;
import net.luis.wins4.map.field.Wins4FieldPos;
import net.luis.wins4.map.field.Wins4FieldType;

/**
 *
 * @author Luis-st
 *
 */

@PacketSubscriber("#getStage#getScene#getScreen")
public class Wins4Screen extends GameScreen {
	
	private final Wins4Game game;
	private PlayerInfoPane playerInfo;
	private ButtonBox leaveButton;
	private ButtonBox playAgainButton;
	private ButtonBox confirmActionButton;
	
	public Wins4Screen(Wins4Game game) {
		super(TranslationKey.createAndGet("client.constans.name"), 1150, 860);
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
		int column;
		if (this.game.getMap() instanceof Wins4Map map) {
			column = map.getSelectedColumn();
		} else {
			throw new NullPointerException("The map of game 4 wins is null");
		}
		if (column != -1) {
			if (this.getPlayer().isCurrent()) {
				this.client.getServerHandler().send(new SelectGameFieldPacket(this.getPlayer().getProfile(), Wins4FieldType.DEFAULT, Wins4FieldPos.of(0, column)));
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
		if (clientPacket instanceof GameResultPacket packet) {
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
		if (this.game.getMap() instanceof Wins4Map map) {
			return map.getStackPane();
		}
		throw new NullPointerException("The map of game 4 wins is null");
	}
	
	private Pane createActionPane() {
		GridPane pane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		pane.addRow(0, this.leaveButton, this.playAgainButton, this.confirmActionButton);
		return pane;
	}
	
}
