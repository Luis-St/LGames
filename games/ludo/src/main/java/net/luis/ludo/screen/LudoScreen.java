package net.luis.ludo.screen;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import net.luis.fxutils.FxUtils;
import net.luis.game.Game;
import net.luis.game.fx.DiceButton;
import net.luis.game.fx.PlayerInfoPane;
import net.luis.game.fx.PlayerScorePane;
import net.luis.game.map.field.GameField;
import net.luis.game.player.Player;
import net.luis.game.screen.GameScreen;
import net.luis.language.TranslationKey;
import net.luis.ludo.map.LudoMap;
import net.luis.network.listener.PacketListener;
import net.luis.network.packet.client.ClientPacket;
import net.luis.network.packet.client.game.GameResultPacket;
import net.luis.network.packet.client.game.dice.RolledDicePacket;
import net.luis.network.packet.server.game.ExitGameRequestPacket;
import net.luis.network.packet.server.game.PlayAgainGameRequestPacket;
import net.luis.network.packet.server.game.SelectGameFieldPacket;
import net.luis.network.packet.server.game.dice.RollDiceRequestPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public class LudoScreen extends GameScreen {
	
	private static final Logger LOGGER = LogManager.getLogger(LudoScreen.class);
	
	private PlayerInfoPane playerInfo;
	private DiceButton diceButton;
	private Button leaveButton;
	private Button playAgainButton;
	private Button confirmActionButton;
	
	public LudoScreen(Game game) {
		super(game, TranslationKey.createAndGet("client.constans.name"), 1375, 1050);
	}
	
	@Override
	public void init() {
		this.playerInfo = new PlayerInfoPane(this.getGame(), 200.0, PlayerScorePane.Type.SCORE);
		this.diceButton = new DiceButton(100.0, () -> {
			Player player = this.getPlayer();
			if (player.isCurrent() && player.canRollDice()) {
				this.broadcastPlayer(new RollDiceRequestPacket(player.getProfile()));
			}
		});
		this.leaveButton = FxUtils.makeButton(TranslationKey.createAndGet("screen.lobby.leave"), this::handleLeave);
		this.playAgainButton = FxUtils.makeButton(TranslationKey.createAndGet("screen.tic_tac_toe.play_again"), this::handlePlayAgain);
		this.playAgainButton.setDisable(true);
		this.confirmActionButton = FxUtils.makeButton(TranslationKey.createAndGet("screen.tic_tac_toe.confirm_action"), this::handleConfirmAction);
	}
	
	private void handleLeave() {
		this.broadcastPlayer(new ExitGameRequestPacket(this.getPlayer().getProfile()));
	}
	
	private void handlePlayAgain() {
		if (this.getPlayer().isAdmin()) {
			this.broadcastPlayer(new PlayAgainGameRequestPacket(this.getPlayer().getProfile()));
			this.playAgainButton.setDisable(true);
		}
	}
	
	private void handleConfirmAction() {
		GameField field = this.getGame().getMap().getSelectedField();
		if (field != null) {
			if (this.getPlayer().canSelect()) {
				this.broadcastPlayer(new SelectGameFieldPacket(this.getPlayer().getProfile(), (Enum<?>) field.getFieldType(), field.getFieldPos()));
				this.getPlayer().setCanSelect(false);
			} else {
				LOGGER.info("It is not the turn of the local player {}", this.getPlayer().getProfile().getName());
			}
		} else {
			LOGGER.debug("No field selected");
		}
	}
	
	@PacketListener
	public void handlePacket(ClientPacket clientPacket) {
		this.playerInfo.update();
		if (clientPacket instanceof RolledDicePacket packet) {
			this.diceButton.setCount(packet.getCount());
		} else if (clientPacket instanceof GameResultPacket) {
			this.playAgainButton.setDisable(!this.getPlayer().isAdmin());
		}
	}
	
	@Override
	protected @NotNull Pane createPane() {
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
		if (this.getGame().getMap() instanceof LudoMap map) {
			return map.getGridPane();
		}
		throw new NullPointerException("The map of game ludo is null");
	}
	
	private Pane createActionPane() {
		GridPane pane = FxUtils.makeGrid(Pos.CENTER, 10.0, 30.0);
		pane.addRow(0, FxUtils.makeVBox(20.0, this.leaveButton), FxUtils.makeVBox(20.0, this.playAgainButton), FxUtils.makeVBox(20.0, this.confirmActionButton));
		return pane;
	}
	
}
