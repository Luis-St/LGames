package net.vgc.client.screen.game;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import net.luis.fxutils.FxUtils;
import net.vgc.client.fx.ButtonBox;
import net.vgc.client.fx.game.PlayerInfoPane;
import net.vgc.client.fx.game.PlayerScorePane;
import net.vgc.client.games.ttt.TTTClientGame;
import net.vgc.client.games.ttt.map.TTTClientMap;
import net.vgc.game.action.data.gobal.ProfileData;
import net.vgc.game.action.data.specific.SelectFieldData;
import net.vgc.game.action.type.GameActionTypes;
import net.vgc.game.map.field.GameField;
import net.vgc.games.ttt.map.field.TTTFieldType;
import net.vgc.language.TranslationKey;

public class TTTScreen extends GameScreen {
	
	private final TTTClientGame game;
	private PlayerInfoPane playerInfo;
	private ButtonBox leaveButton;
	private ButtonBox playAgainButton;
	private ButtonBox confirmActionButton;
	
	public TTTScreen(TTTClientGame game) {
		this.game = game;
		this.width = 900;
		this.height = 700;
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
		GameActionTypes.EXIT_GAME_REQUEST.send(this.client.getServerHandler(), new ProfileData(this.getPlayer().getProfile()));
	}
	
	private void handlePlayAgain() {
		if (this.client.getPlayer().isAdmin()) {
			GameActionTypes.PLAY_AGAIN_REQUEST.send(this.client.getServerHandler(), new ProfileData(this.getPlayer().getProfile()));
			this.playAgainButton.getNode().setDisable(true);
		}
	}
	
	private void handleConfirmAction() {
		GameField field = this.game.getMap().getSelectedField();
		if (field != null) {
			if (this.getPlayer().isCurrent()) {
				GameActionTypes.SELECT_FIELD.send(this.client.getServerHandler(), new SelectFieldData(this.getPlayer().getProfile(), TTTFieldType.DEFAULT, field.getFieldPos()));
				this.getPlayer().setCurrent(false);
			} else {
				LOGGER.info("It is not the turn of the local player {}", this.getPlayer().getProfile().getName());
			}
		} else {
			LOGGER.info("No field selected");
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
