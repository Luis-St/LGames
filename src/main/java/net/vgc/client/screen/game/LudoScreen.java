package net.vgc.client.screen.game;

import java.util.List;
import java.util.Map.Entry;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import net.vgc.client.fx.ButtonBox;
import net.vgc.client.fx.FxUtil;
import net.vgc.client.fx.game.DiceButton;
import net.vgc.client.fx.game.map.ClientLudoMap;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.game.ludo.LudoType;
import net.vgc.game.ludo.map.field.LudoFieldPos;
import net.vgc.language.TranslationKey;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.network.packet.client.game.CurrentPlayerUpdatePacket;
import net.vgc.network.packet.client.game.dice.CanRollDiceAgainPacket;
import net.vgc.network.packet.client.game.dice.CancelRollDiceRequestPacket;
import net.vgc.network.packet.client.game.dice.RolledDicePacket;
import net.vgc.network.packet.client.game.ludo.CanSelectLudoFigurePacket;
import net.vgc.network.packet.client.game.ludo.StartLudoGamePacket;
import net.vgc.network.packet.client.game.ludo.UpdateLudoGamePacket;
import net.vgc.network.packet.server.game.ExitGameRequestPacket;
import net.vgc.network.packet.server.game.PlayAgainGameRequestPacket;
import net.vgc.network.packet.server.game.SelectLudoFigurePacket;
import net.vgc.player.GameProfile;
import net.vgc.util.Mth;

public class LudoScreen extends GameScreen {
	
	protected AbstractClientPlayer greenPlayer;
	protected AbstractClientPlayer yellowPlayer;
	protected AbstractClientPlayer bluePlayer;
	protected AbstractClientPlayer redPlayer;
	protected List<AbstractClientPlayer> players;
	protected Text currentPlayerInfo;
	protected Text greenPlayerInfo;
	protected Text yellowPlayerInfo;
	protected Text bluePlayerInfo;
	protected Text redPlayerInfo;
	protected ButtonBox leaveButton;
	protected ButtonBox playAgainButton;
	protected ButtonBox confirmActionButton;
	protected DiceButton diceButton;
	protected boolean canRollDice = false;
	protected ClientLudoMap map;
	
	public LudoScreen() {
		this.width = 1550;
		this.height = 1150;
	}
	
	@Override
	public void init() {
		this.currentPlayerInfo = new Text(TranslationKey.createAndGet("screen.tic_tac_toe.no_current_player"));
		this.greenPlayerInfo = new Text(TranslationKey.createAndGet("screen.ludo.no_green_player"));
		this.yellowPlayerInfo = new Text(TranslationKey.createAndGet("screen.ludo.no_yellow_player"));
		this.bluePlayerInfo = new Text(TranslationKey.createAndGet("screen.ludo.no_blue_player"));
		this.redPlayerInfo = new Text(TranslationKey.createAndGet("screen.ludo.no_red_player"));
		this.diceButton = new DiceButton(this.client, 100.0, () -> {
			return this.getPlayer().isCurrent() && this.canRollDice;
		});
		this.leaveButton = new ButtonBox(TranslationKey.createAndGet("screen.lobby.leave"), Pos.CENTER, 20.0, this::handleLeave);
		this.playAgainButton = new ButtonBox(TranslationKey.createAndGet("screen.tic_tac_toe.play_again"), Pos.CENTER, 20.0, this::handlePlayAgain);
		this.playAgainButton.getNode().setDisable(true);
		this.confirmActionButton = new ButtonBox(TranslationKey.createAndGet("screen.tic_tac_toe.confirm_action"), Pos.CENTER, 20.0, this::handleConfirmAction);
		this.map = new ClientLudoMap(this.client, () -> {
			return this.getPlayer().isCurrent() && this.getPlayer().canSelect() && Mth.isInBounds(this.diceButton.getCount(), 1, 6);
		}, this.diceButton::getCount);
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
		LudoFieldPos pos = this.map.getSelectedFigure();
		if (pos != null) {
			this.client.getServerHandler().send(new SelectLudoFigurePacket(this.getPlayer().getProfile(), pos));
			this.getPlayer().setCanSelect(false);
		} else {
			LOGGER.info("No field selected");
		}
	}
	
	@Override
	public void handlePacket(ClientPacket clientPacket) {
		this.map.handlePacket(clientPacket);
		if (clientPacket instanceof CancelRollDiceRequestPacket packet) {
			LOGGER.info("Roll dice request was canceled from the server");
		} else if (clientPacket instanceof RolledDicePacket packet) {
			this.canRollDice = false;
			this.diceButton.update(packet.getCount());
		} else if (clientPacket instanceof CanRollDiceAgainPacket) {
			this.canRollDice = true;
		} else if (clientPacket instanceof CanSelectLudoFigurePacket) {
			this.getPlayer().setCanSelect(true);
		} else if (clientPacket instanceof StartLudoGamePacket packet) {
			for (Entry<GameProfile, LudoType> entry : packet.getPlayerTypes().entrySet()) {
				AbstractClientPlayer player = this.client.getPlayer(entry.getKey());
				LudoType type = entry.getValue();
				if (player != null) {
					if (type == LudoType.GREEN) {
						this.greenPlayer = player;
						this.greenPlayer.setType(type);
					} else if (type == LudoType.YELLOW) {
						this.yellowPlayer = player;
						this.yellowPlayer.setType(type);
					} else if (type == LudoType.BLUE) {
						this.bluePlayer = player;
						this.bluePlayer.setType(type);
					} else if (type == LudoType.RED) {
						this.redPlayer = player;
						this.redPlayer.setType(type);
					} else {
						LOGGER.warn("Fail to set ludo type of player {}, since ludo type is {}", player.getProfile().getName(), type);
					}
				} else {
					LOGGER.warn("Fail to set player {} to a player of the current game, since the player does not exist on the client", entry.getKey().getName());
				}
			}
		} else if (clientPacket instanceof UpdateLudoGamePacket packet) {
			if (!this.playAgainButton.getNode().isDisabled()) {
				this.playAgainButton.getNode().setDisable(true);
			}
		} else if (clientPacket instanceof CurrentPlayerUpdatePacket packet) {
			this.setCurrentPlayer(packet.getProfile());
			this.updateInfo();
			if (this.getPlayer().getProfile().equals(packet.getProfile())) {
				this.canRollDice = true;
			}
		}
	}
	
	protected void setCurrentPlayer(GameProfile profile) {
		if (this.greenPlayer != null) {
			if (this.greenPlayer.getProfile().equals(profile)) {
				this.greenPlayer.setCurrent(true);
			} else {
				this.greenPlayer.setCurrent(false);
			}
		}
		if (this.yellowPlayer != null) {
			if (this.yellowPlayer.getProfile().equals(profile)) {
				this.yellowPlayer.setCurrent(true);
			} else {
				this.yellowPlayer.setCurrent(false);
			}
		}
		if (this.bluePlayer != null) {
			if (this.bluePlayer.getProfile().equals(profile)) {
				this.bluePlayer.setCurrent(true);
			} else {
				this.bluePlayer.setCurrent(false);
			}
		}
		if (this.redPlayer != null) {
			if (this.redPlayer.getProfile().equals(profile)) {
				this.redPlayer.setCurrent(true);
			} else {
				this.redPlayer.setCurrent(false);
			}
		}
	}

	@Override
	protected Pane createPane() {
		BorderPane pane = new BorderPane();
		pane.setLeft(this.createInfoPane());
		pane.setRight(this.createDicePane());
		pane.setCenter(this.map);
		pane.setBottom(this.createActionPane());
		return pane;
	}
	
	protected Pane createInfoPane() {
		GridPane currentPlayerPane = FxUtil.makeGrid(Pos.CENTER, 10.0, 10.0);
		currentPlayerPane.addColumn(0, this.currentPlayerInfo);
		GridPane playerPane = FxUtil.makeGrid(Pos.CENTER, 10.0, 10.0);
		playerPane.addColumn(0, this.greenPlayerInfo, this.yellowPlayerInfo, this.bluePlayerInfo, this.redPlayerInfo);
		return FxUtil.makeVerticalBox(Pos.CENTER, 10.0, new Text(TranslationKey.createAndGet("screen.ludo.players")), new Separator(Orientation.HORIZONTAL), currentPlayerPane, new Separator(Orientation.HORIZONTAL), playerPane);
	}
	
	protected void updateInfo() {
		if (this.greenPlayer != null) {
			this.greenPlayerInfo.setText(TranslationKey.createAndGet("screen.ludo.green_player", this.greenPlayer.getProfile().getName()));
		} else {
			this.greenPlayerInfo.setText(TranslationKey.createAndGet("screen.ludo.no_green_player"));
		}
		if (this.yellowPlayer != null) {
			this.yellowPlayerInfo.setText(TranslationKey.createAndGet("screen.ludo.yellow_player", this.yellowPlayer.getProfile().getName()));
		} else {
			this.yellowPlayerInfo.setText(TranslationKey.createAndGet("screen.ludo.no_yellow_player"));
		}
		if (this.bluePlayer != null) {
			this.bluePlayerInfo.setText(TranslationKey.createAndGet("screen.ludo.blue_player", this.bluePlayer.getProfile().getName()));
		} else {
			this.bluePlayerInfo.setText(TranslationKey.createAndGet("screen.ludo.no_blue_player"));
		}
		if (this.redPlayer != null) {
			this.redPlayerInfo.setText(TranslationKey.createAndGet("screen.ludo.red_player", this.redPlayer.getProfile().getName()));
		} else {
			this.redPlayerInfo.setText(TranslationKey.createAndGet("screen.ludo.no_red_player"));
		}
		if (this.greenPlayer != null && this.greenPlayer.isCurrent()) {
			this.currentPlayerInfo.setText(TranslationKey.createAndGet("screen.tic_tac_toe.current_player", this.greenPlayer.getProfile().getName()));
		} else if (this.yellowPlayer != null && this.yellowPlayer.isCurrent()) {
			this.currentPlayerInfo.setText(TranslationKey.createAndGet("screen.tic_tac_toe.current_player", this.yellowPlayer.getProfile().getName()));
		} else if (this.bluePlayer != null && this.bluePlayer.isCurrent()) {
			this.currentPlayerInfo.setText(TranslationKey.createAndGet("screen.tic_tac_toe.current_player", this.bluePlayer.getProfile().getName()));
		} else if (this.redPlayer != null && this.redPlayer.isCurrent()) {
			this.currentPlayerInfo.setText(TranslationKey.createAndGet("screen.tic_tac_toe.current_player", this.redPlayer.getProfile().getName()));
		} else {
			LOGGER.warn("Fail to display the current player, since there is no current player");
			this.currentPlayerInfo.setText(TranslationKey.createAndGet("screen.tic_tac_toe.no_current_player"));
		}
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
