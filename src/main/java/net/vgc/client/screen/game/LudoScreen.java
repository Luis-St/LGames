package net.vgc.client.screen.game;

import org.jetbrains.annotations.Nullable;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import net.luis.fxutils.FxUtils;
import net.vgc.client.fx.ButtonBox;
import net.vgc.client.fx.game.DiceButton;
import net.vgc.client.fx.game.PlayerInfoPane;
import net.vgc.client.fx.game.PlayerScorePane;
import net.vgc.client.games.ludo.LudoClientGame;
import net.vgc.client.games.ludo.map.LudoClientMap;
import net.vgc.client.screen.update.ScreenUpdateKeys;
import net.vgc.game.action.data.gobal.ProfileData;
import net.vgc.game.action.data.specific.SelectFieldData;
import net.vgc.game.action.type.GameActionTypes;
import net.vgc.game.map.field.GameField;
import net.vgc.language.TranslationKey;

public class LudoScreen extends GameScreen {
	
	private final LudoClientGame game;
	private PlayerInfoPane playerInfo;
	private DiceButton diceButton;
	private ButtonBox leaveButton;
	private ButtonBox playAgainButton;
	private ButtonBox confirmActionButton;
	
	public LudoScreen(LudoClientGame game) {
		this.game = game;
		this.width = 1375;
		this.height = 1050;
	}
	
	@Override
	public void init() {
		this.playerInfo = new PlayerInfoPane(this.game, 200.0, PlayerScorePane.Type.SCORE);
		this.diceButton = new DiceButton(this.client, 100.0);
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
			if (this.getPlayer().canSelect()) {
				GameActionTypes.SELECT_FIELD.send(this.client.getServerHandler(), new SelectFieldData(this.getPlayer().getProfile(), field.getFieldType(), field.getFieldPos()));
				this.getPlayer().setCanSelect(false);
			} else {
				LOGGER.info("It is not the turn of the local player {}", this.getPlayer().getProfile().getName());
			}
		} else {
			LOGGER.debug("No field selected");
		}
	}
	
	@Override
	public void tick() {
		
	}
	
	@Override
	public void onUpdate(@Nullable String updateKey, @Nullable Object object) {
		this.playerInfo.update();
		if (ScreenUpdateKeys.DICE_UPDATE.equals(updateKey)) {
			this.diceButton.setCount((int) object);
		} else if (ScreenUpdateKeys.GAME_RESULT_UPDATE.equals(updateKey)) {
			this.playAgainButton.getNode().setDisable(!this.getPlayer().isAdmin());
		}
	}
	
	@Override
	protected Pane createPane() {
		BorderPane pane = new BorderPane();
		pane.setLeft(this.playerInfo);
		pane.setRight(this.createDicePane());
		pane.setCenter(this.createGamePane());
		pane.setBottom(this.createActionPane());
		return pane;
	}
	
	private Pane createDicePane() {
		GridPane pane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		pane.addColumn(0, this.diceButton);
		return pane;
	}
	
	private Pane createGamePane() {
		if (this.game.getMap() instanceof LudoClientMap map) {
			return map.getGridPane();
		}
		throw new NullPointerException("The map of game ludo is null");
	}
	
	private Pane createActionPane() {
		GridPane pane = FxUtils.makeGrid(Pos.CENTER, 10.0, 30.0);
		pane.addRow(0, this.leaveButton, this.playAgainButton, this.confirmActionButton);
		return pane;
	}
	
}
