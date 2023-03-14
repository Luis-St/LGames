package net.luis.wins4.screen;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import net.luis.fxutils.FxUtils;
import net.luis.game.Game;
import net.luis.game.fx.PlayerInfoPane;
import net.luis.game.fx.PlayerScorePane;
import net.luis.game.screen.GameScreen;
import net.luis.language.TranslationKey;
import net.luis.network.listener.PacketListener;
import net.luis.network.packet.client.ClientPacket;
import net.luis.network.packet.client.game.GameResultPacket;
import net.luis.network.packet.server.game.ExitGameRequestPacket;
import net.luis.network.packet.server.game.PlayAgainGameRequestPacket;
import net.luis.network.packet.server.game.SelectGameFieldPacket;
import net.luis.wins4.map.Wins4Map;
import net.luis.wins4.map.field.Wins4FieldPos;
import net.luis.wins4.map.field.Wins4FieldType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public class Wins4Screen extends GameScreen {
	
	private static final Logger LOGGER = LogManager.getLogger(Wins4Screen.class);
	
	private PlayerInfoPane playerInfo;
	private Button leaveButton;
	private Button playAgainButton;
	private Button confirmActionButton;
	
	public Wins4Screen(Game game) {
		super(game, TranslationKey.createAndGet("client.constans.name"), 1150, 860);
	}
	
	@Override
	public void init() {
		this.playerInfo = new PlayerInfoPane(this.getGame(), 200.0, PlayerScorePane.Type.WIN);
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
		int column;
		if (this.getGame().getMap() instanceof Wins4Map map) {
			column = map.getSelectedColumn();
		} else {
			throw new NullPointerException("The map of game 4 wins is null");
		}
		if (column != -1) {
			if (this.getPlayer().isCurrent()) {
				this.broadcastPlayer(new SelectGameFieldPacket(this.getPlayer().getProfile(), Wins4FieldType.DEFAULT, Wins4FieldPos.of(0, column)));
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
			this.playAgainButton.setDisable(!this.getPlayer().isAdmin());
		}
	}
	
	@Override
	protected @NotNull Pane createPane() {
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
		if (this.getGame().getMap() instanceof Wins4Map map) {
			return map.getStackPane();
		}
		throw new NullPointerException("The map of game 4 wins is null");
	}
	
	private Pane createActionPane() {
		GridPane pane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		pane.addRow(0, FxUtils.makeVBox(20.0, this.leaveButton), FxUtils.makeVBox(20.0, this.playAgainButton), FxUtils.makeVBox(20.0, this.confirmActionButton));
		return pane;
	}
	
}
