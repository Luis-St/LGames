package net.vgc.client.screen.game;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import net.vgc.client.fx.ButtonBox;
import net.vgc.client.fx.FxUtil;
import net.vgc.client.fx.game.DiceButton;
import net.vgc.client.fx.game.PlayerInfoPane;
import net.vgc.client.game.games.ludo.LudoClientGame;
import net.vgc.client.game.games.ludo.map.field.LudoClientField;
import net.vgc.language.TranslationKey;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.network.packet.client.game.dice.RolledDicePacket;
import net.vgc.network.packet.server.game.ExitGameRequestPacket;
import net.vgc.network.packet.server.game.PlayAgainGameRequestPacket;

public class LudoScreen extends GameScreen {
	
	protected final LudoClientGame game;
	protected PlayerInfoPane playerInfo;
	protected DiceButton diceButton;
	protected ButtonBox leaveButton;
	protected ButtonBox playAgainButton;
	protected ButtonBox confirmActionButton;
	
	public LudoScreen(LudoClientGame game) {
		this.game = game;
		this.width = 1550;
		this.height = 1150;
	}
	
	@Override
	public void init() {
		this.playerInfo = new PlayerInfoPane(this.game, 150.0);
		this.diceButton = new DiceButton(this.client, 100.0);
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
		}
	}
	
	protected void handleConfirmAction() {
		LudoClientField field = this.game.getMap().getSelectedField();
		if (field != null) {
//			this.client.getServerHandler().send(null); // TODO: add packet
			this.getPlayer().setCanSelect(false);
		} else {
			LOGGER.info("No field selected");
		}
	}
	
	@Override
	public void tick() {
		
	}
	
	@Override
	public void handlePacket(ClientPacket clientPacket) {
		this.playerInfo.update();
		if (clientPacket instanceof RolledDicePacket packet) {
			this.diceButton.setCount(packet.getCount());
		}
	}

	@Override
	protected Pane createPane() {
		BorderPane pane = new BorderPane();
		pane.setLeft(this.playerInfo);
		pane.setRight(this.createDicePane());
		pane.setCenter(this.game.getMap());
		pane.setBottom(this.createActionPane());
		return pane;
	}
	
	protected Pane createDicePane() {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		pane.addColumn(0, this.diceButton);
		return pane;
	}
	
	protected Pane createActionPane() {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 30.0);
		pane.addRow(0, this.leaveButton, this.playAgainButton, this.confirmActionButton);
		return pane;
	}

}
