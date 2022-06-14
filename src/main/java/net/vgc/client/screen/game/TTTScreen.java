package net.vgc.client.screen.game;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import net.vgc.client.fx.ButtonBox;
import net.vgc.client.fx.FxUtil;
import net.vgc.client.fx.game.PlayerInfoPane;
import net.vgc.client.game.games.ttt.TTTClientGame;
import net.vgc.client.game.games.ttt.map.TTTClientMap;
import net.vgc.client.game.games.ttt.map.field.TTTClientField;
import net.vgc.game.games.ttt.map.field.TTTFieldType;
import net.vgc.language.TranslationKey;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.network.packet.client.game.TTTGameResultPacket;
import net.vgc.network.packet.server.game.ExitGameRequestPacket;
import net.vgc.network.packet.server.game.PlayAgainGameRequestPacket;
import net.vgc.network.packet.server.game.SelectGameFieldPacket;

public class TTTScreen extends GameScreen {
	
	protected final TTTClientGame game;
	protected PlayerInfoPane playerInfo;
	protected ButtonBox leaveButton;
	protected ButtonBox playAgainButton;
	protected ButtonBox confirmActionButton;
	
	public TTTScreen(TTTClientGame game) {
		this.game = game;
		this.width = 850;
		this.height = 700;
	}
	
	@Override
	public void init() {
		this.playerInfo = new PlayerInfoPane(this.game, 150.0);
		this.leaveButton = new ButtonBox(TranslationKey.createAndGet("screen.lobby.leave"), Pos.CENTER, 20.0, this::handleLeave);
		this.playAgainButton = new ButtonBox(TranslationKey.createAndGet("screen.tic_tac_toe.play_again"), Pos.CENTER, 20.0, this::handlePlayAgain);
		this.playAgainButton.getNode().setDisable(true);
		this.confirmActionButton = new ButtonBox(TranslationKey.createAndGet("screen.tic_tac_toe.confirm_action"), Pos.CENTER, 20.0, this::handleConfirmAction);
	}
	
	protected void handleLeave() {
		this.client.getServerHandler().send(new ExitGameRequestPacket(this.getPlayer().getProfile()));
	}
	
	protected void handlePlayAgain() {
		if (this.client.getPlayer().isAdmin()) {
			this.client.getServerHandler().send(new PlayAgainGameRequestPacket(this.getPlayer().getProfile()));
			this.playAgainButton.getNode().setDisable(true);
		}
	}
	
	protected void handleConfirmAction() {
		TTTClientField field = this.game.getMap().getSelectedField();
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
	
	@Override
	public void handlePacket(ClientPacket clientPacket) {
		this.playerInfo.update();
		if (clientPacket instanceof TTTGameResultPacket packet) {
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
	
	protected PlayerInfoPane createInfoPane() {
		return this.playerInfo;
	}
	
	protected TTTClientMap createGamePane() {
		return this.game.getMap();
	}
	
	protected Pane createActionPane() {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 30.0);
		pane.addRow(0, this.leaveButton, this.playAgainButton, this.confirmActionButton);
		return pane;
	}
	
}
