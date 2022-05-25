package net.vgc.client.screen.game;

import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import net.vgc.client.fx.ButtonBox;
import net.vgc.client.fx.FxUtil;
import net.vgc.client.fx.game.TTTButton;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.client.player.RemotePlayer;
import net.vgc.client.screen.LobbyScreen;
import net.vgc.game.GameTypes;
import net.vgc.game.score.GameScore;
import net.vgc.game.score.PlayerScore;
import net.vgc.game.ttt.TTTState;
import net.vgc.game.ttt.TTTType;
import net.vgc.game.ttt.map.TTTMap;
import net.vgc.game.ttt.map.TTTResultLine;
import net.vgc.language.TranslationKey;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.network.packet.client.game.CancelPlayAgainGameRequestPacket;
import net.vgc.network.packet.client.game.CurrentPlayerUpdatePacket;
import net.vgc.network.packet.client.game.GameScoreUpdatePacket;
import net.vgc.network.packet.client.game.ttt.StartTTTGamePacket;
import net.vgc.network.packet.client.game.ttt.TTTGameResultPacket;
import net.vgc.network.packet.client.game.ttt.UpdateTTTGamePacket;
import net.vgc.network.packet.server.game.ExitGameRequestPacket;
import net.vgc.network.packet.server.game.PlayAgainGameRequestPacket;
import net.vgc.network.packet.server.game.ttt.PressTTTFieldPacket;
import net.vgc.player.GameProfile;
import net.vgc.util.Util;

public class TTTScreen extends GameScreen {
	
	protected final List<TTTButton> buttons = Lists.newArrayList();
	
	protected AbstractClientPlayer player;
	protected AbstractClientPlayer opponent;
	
	protected ToggleGroup group;
	protected TTTButton topLeftButton;
	protected TTTButton topCenterButton;
	protected TTTButton topRightButton;
	protected TTTButton midLeftButton;
	protected TTTButton midCenterButton;
	protected TTTButton midRightButton;
	protected TTTButton bottomLeftButton;
	protected TTTButton bottomCenterButton;
	protected TTTButton bottomRightButton;
	protected Rectangle topLeftVBorder;
	protected Rectangle topRightVBorder;
	protected Rectangle topLeftHBorder;
	protected Rectangle topCenterHBorder;
	protected Rectangle topRightHBorder;
	protected Rectangle centerLeftVBorder;
	protected Rectangle centerRightVBorder;
	protected Rectangle bottomLeftHBorder;
	protected Rectangle bottomCenterHBorder;
	protected Rectangle bottomRightHBorder;
	protected Rectangle bottomLeftVBorder;
	protected Rectangle bottomRightVBorder;
	protected ButtonBox leaveButton;
	protected ButtonBox playAgainButton;
	protected ButtonBox confirmActionButton;
	protected Text playerInfo;
	protected Text opponentInfo;
	protected Text currentPlayerInfo;
	
	public TTTScreen() {
		this.width = 750;
		this.height = 650;
	}
	
	@Override
	public void init() {
		this.group = new ToggleGroup();
		this.group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue instanceof TTTButton oldButton) {
				if (newValue instanceof TTTButton newButton) {
					TTTType oldType = oldButton.getType();
					TTTState oldState = oldButton.getState();
					if (oldType != TTTType.NO && oldState == TTTState.SHADOW) {
						oldButton.setTypeAndState(TTTType.NO, TTTState.DEFAULT);
					}
				} else {
					oldButton.setSelected(true);
				}
			}
		});
		this.topLeftButton = this.makeButton(0, 0);
		this.topCenterButton = this.makeButton(1, 0);
		this.topRightButton = this.makeButton(2, 0);
		this.midLeftButton = this.makeButton(0, 1);
		this.midCenterButton = this.makeButton(1, 1);
		this.midRightButton = this.makeButton(2, 1);
		this.bottomLeftButton = this.makeButton(0, 2);
		this.bottomCenterButton = this.makeButton(1, 2);
		this.bottomRightButton = this.makeButton(2, 2);
		double bigBorderSize = 160.0;
		double smallBorderSize = 10.0;
		this.topLeftVBorder = this.makeBorder(smallBorderSize, bigBorderSize);
		this.topRightVBorder = this.makeBorder(smallBorderSize, bigBorderSize);
		this.topLeftHBorder = this.makeBorder(bigBorderSize, smallBorderSize);
		this.topCenterHBorder = this.makeBorder(bigBorderSize, smallBorderSize);
		this.topRightHBorder = this.makeBorder(bigBorderSize, smallBorderSize);
		this.centerLeftVBorder = this.makeBorder(smallBorderSize, bigBorderSize);
		this.centerRightVBorder = this.makeBorder(smallBorderSize, bigBorderSize);
		this.bottomLeftHBorder = this.makeBorder(bigBorderSize, smallBorderSize);
		this.bottomCenterHBorder = this.makeBorder(bigBorderSize, smallBorderSize);
		this.bottomRightHBorder = this.makeBorder(bigBorderSize, smallBorderSize);
		this.bottomLeftVBorder = this.makeBorder(smallBorderSize, bigBorderSize);
		this.bottomRightVBorder = this.makeBorder(smallBorderSize, bigBorderSize);
		this.leaveButton = new ButtonBox(TranslationKey.createAndGet("screen.lobby.leave"), Pos.CENTER, 20.0, this::handleLeave);
		this.playAgainButton = new ButtonBox(TranslationKey.createAndGet("screen.tic_tac_toe.play_again"), Pos.CENTER, 20.0, this::handlePlayAgain);
		this.playAgainButton.getNode().setDisable(true);
		this.confirmActionButton = new ButtonBox(TranslationKey.createAndGet("screen.tic_tac_toe.confirm_action"), Pos.CENTER, 20.0, this::handleConfirmAction);
		this.playerInfo = new Text(TranslationKey.createAndGet("screen.tic_tac_toe.no_data"));
		this.opponentInfo = new Text(TranslationKey.createAndGet("screen.tic_tac_toe.no_data"));
		this.currentPlayerInfo = new Text(TranslationKey.createAndGet("screen.tic_tac_toe.no_current_player"));
	}
	
	protected TTTButton makeButton(int vMap, int hMap) {
		TTTButton button = new TTTButton(this.group, 150.0, vMap, hMap);
		button.setOnAction((event) -> {
			if (this.player.isCurrent()) {
				if (button.getType() == TTTType.NO) {
					button.setTypeAndState((TTTType) this.player.getType(), TTTState.SHADOW);
					LOGGER.debug("Update state of field with map pos {}:{} to {}", vMap, hMap, TTTState.SHADOW);
				}
			} else {
				this.group.selectToggle(null);
			}
		});
		return button;
	}
	
	protected Rectangle makeBorder(double width, double height) {
		return new Rectangle(width, height, Color.BLACK);
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
		Toggle toggle = this.group.getSelectedToggle();
		if (toggle instanceof TTTButton button) {
			int vMap = button.getVMap();
			int hMap = button.getHMap();
			if (this.player.isCurrent()) {
				this.client.getServerHandler().send(new PressTTTFieldPacket(this.getPlayer().getProfile(), vMap, hMap));
				this.group.selectToggle(null);
			} else {
				LOGGER.info("It is not the turn of the local player {}", this.getPlayer().getProfile().getName());
				this.group.selectToggle(null);
			}
		} else {
			LOGGER.info("No field selected");
		}
	}
	
	@Nullable
	protected TTTButton getButton(int vMap, int hMap) {
		if (vMap == 0) {
			if (hMap == 0) {
				return this.topLeftButton;
			} else if (hMap == 1) {
				return this.midLeftButton;
			} else if (hMap == 2) {
				return this.bottomLeftButton;
			}
		} else if (vMap == 1) {
			if (hMap == 0) {
				return this.topCenterButton;
			} else if (hMap == 1) {
				return this.midCenterButton;
			} else if (hMap == 2) {
				return this.bottomCenterButton;
			}
		} else if (vMap == 2) {
			if (hMap == 0) {
				return this.topRightButton;
			} else if (hMap == 1) {
				return this.midRightButton;
			} else if (hMap == 2) {
				return this.bottomRightButton;
			}
		}
		LOGGER.warn("Fail to get button for field with map pos {}:{}", vMap, hMap);
		return null;
	}
	
	@Override
	public void handlePacket(ClientPacket clientPacket) {
		if (clientPacket instanceof StartTTTGamePacket packet) {
			GameProfile localProfile = this.client.getPlayer().getProfile();
			this.player = this.client.getPlayer();
			this.player.setType(packet.getPlayerType());
			int localIndex = packet.getProfiles().indexOf(localProfile);
			if (localIndex == 0) {
				this.setOpponent(packet.getProfiles().get(1));
			} else if (localIndex == 1) {
				this.setOpponent(packet.getProfiles().get(0));
			} else {
				LOGGER.warn("Fail to set the opponent player");
			}
		} else if (clientPacket instanceof UpdateTTTGamePacket packet) {
			this.applyMap(packet.getMap(), TTTState.DEFAULT);
			this.group.selectToggle(null);
			if (!this.playAgainButton.getNode().isDisabled()) {
				this.playAgainButton.getNode().setDisable(true);
			}
		} else if (clientPacket instanceof CurrentPlayerUpdatePacket packet) {
			this.setCurrentPlayer(packet.getProfile());
		} else if (clientPacket instanceof TTTGameResultPacket packet) {
			GameProfile winnerProfile = packet.getWinnerProfile();
			GameProfile loserProfile = packet.getLoserProfile();
			TTTResultLine resultLine = packet.getResultLine();
			if (winnerProfile.equals(GameProfile.EMPTY) && loserProfile.equals(GameProfile.EMPTY) && packet.getResultLine().equals(TTTResultLine.EMPTY)) {
				for (int v = 0; v < 3; v++) {
					for (int h = 0; h < 3; h++) {
						TTTButton button = this.getButton(v, h);
						button.setTypeAndState(button.getType(), TTTState.DRAW);
					}
				}
				LOGGER.info("Update field map to state {}", TTTState.DRAW);
			} else if (!winnerProfile.equals(GameProfile.EMPTY) && !loserProfile.equals(GameProfile.EMPTY)) {
				if (!resultLine.equals(TTTResultLine.EMPTY)) {
					if (winnerProfile.equals(this.getPlayer().getProfile())) {
						this.applyResultLine(resultLine, TTTState.WIN);
					} else if (loserProfile.equals(this.getPlayer().getProfile())) {
						this.applyResultLine(resultLine, TTTState.LOSE);
					} else {
						LOGGER.warn("Fail to handle result, since the winner {} and the loser {} does not match with the local player {}", winnerProfile.getName(), loserProfile.getName(), this.getPlayer().getProfile().getName());
					}
				} else {
					LOGGER.warn("Fail to handle game result, since the result line is empty but there is a winner {} and a loser", winnerProfile.getName(), loserProfile.getName());
				}
			} else {
				LOGGER.warn("Fail to handle game result, since the winner {} and the loser {} must not be empty", winnerProfile.getName(), loserProfile.getName());
			}
			this.playAgainButton.getNode().setDisable(!this.client.getPlayer().isAdmin());
		} else if (clientPacket instanceof GameScoreUpdatePacket packet) {
			GameScore score = packet.getScore();
			for (Entry<GameProfile, PlayerScore> entry : score.getScores().entrySet()) {
				if (this.player.getProfile().equals(entry.getKey())) {
					this.player.syncScore(entry.getValue());
				} else if (this.opponent.getProfile().equals(entry.getKey())) {
					this.opponent.syncScore(entry.getValue());
				} else {
					LOGGER.warn("Fail to display score of player {}, since the player is not playing the game {}", entry.getKey().getName(), GameTypes.TIC_TAC_TOE.getName().toLowerCase());
				}
				this.updatePlayerInfo();
			}
			LOGGER.info("Update game score");
		} else if (clientPacket instanceof CancelPlayAgainGameRequestPacket packet) {
			this.showScreen(new LobbyScreen());
			LOGGER.warn("The request to play again was canceled by the server");
		}
	}
	
	protected void setOpponent(GameProfile profile) {
		AbstractClientPlayer player = this.client.getPlayer(profile);
		if (player instanceof RemotePlayer opponentPlayer) {
			this.opponent = opponentPlayer;
			LOGGER.info("Set opponent player of {} to {}", this.getPlayer().getProfile().getName(), opponentPlayer.getProfile().getName());
			if (this.player != null && this.player.getType() != null) {
				this.opponent.setType(this.player.getType().getOpponents().get(0));
			} else {
				LOGGER.warn("Fail to set the type for the opponent player {}, since the type for the local player {} is not set", opponentPlayer.getProfile().getName(), this.getPlayer().getProfile().getName());
			}
			this.updatePlayerInfo();
		} else {
			LOGGER.warn("Fail to set the opponent player to a player of type {}", (Object) Util.runIfNotNull(player, (clientPlayer) -> {
				return clientPlayer.getClass().getSimpleName();
			}));
		}
	}
	
	protected void setCurrentPlayer(GameProfile profile) {
		if (this.player != null) {
			if (this.player.getProfile().equals(profile)) {
				this.player.setCurrent(true);
				this.opponent.setCurrent(false);
				LOGGER.debug("Update current player to {}", this.player.getProfile().getName());
			} else if (this.opponent.getProfile().equals(profile)) {
				this.player.setCurrent(false);
				this.opponent.setCurrent(true);
				LOGGER.debug("Update current player to {}", this.opponent.getProfile().getName());
			} else {
				LOGGER.warn("Fail to set current player to {}", profile.getName());
			}
		} else {
			LOGGER.info("Fail to set current player to {}, since the client players are not load yet", profile.getName());
		}
		this.updatePlayerInfo();
	}
	
	protected void applyMap(TTTMap map, TTTState state) {
		for (int v = 0; v < 3; v++) {
			for (int h = 0; h < 3; h++) {
				this.getButton(v, h).setTypeAndState(map.getType(v, h), state);
			}
		}
		LOGGER.info("Update field map to state {}", state);
	}
	
	protected void applyResultLine(TTTResultLine resultLine, TTTState state) {
		this.applyResult(resultLine.getVMap0(), resultLine.getHMap0(), state);
		this.applyResult(resultLine.getVMap1(), resultLine.getHMap1(), state);
		this.applyResult(resultLine.getVMap2(), resultLine.getHMap2(), state);
		LOGGER.info("Apply line from map pos {}:{} to {}:{} with type {} to result {}", resultLine.getVMap0(), resultLine.getHMap0(), resultLine.getVMap2(), resultLine.getHMap2(), resultLine.getType(), state);
	}
	
	protected void applyResult(int vMap, int hMap, TTTState state) {
		TTTButton button = this.getButton(vMap, hMap);
		if (button != null) {
			button.setTypeAndState(button.getType(), state);
		} else {
			LOGGER.warn("Fail to apply result to field at map pos {}:{}, since \"button\" is null", vMap, hMap);
		}
	}
	
	protected void updatePlayerInfo() {
		if (this.player.getType() != null) {
			this.playerInfo.setText(this.player.getProfile().getName() + " (" + ((TTTType) this.player.getType()).getCharacter() + "): " + this.player.getScore().getWins());
		} else {
			this.playerInfo.setText(TranslationKey.createAndGet("screen.tic_tac_toe.fail_data"));
			LOGGER.warn("Fail to load score of player {}", this.player.getProfile().getName());
		}
		if (this.opponent.getType() != null) {
			this.opponentInfo.setText(this.opponent.getProfile().getName() + " (" + ((TTTType) this.opponent.getType()).getCharacter() + "): " + this.opponent.getScore().getWins());
		} else {
			this.opponentInfo.setText(TranslationKey.createAndGet("screen.tic_tac_toe.fail_data"));
			LOGGER.warn("Fail to load score of player {}", this.opponent.getProfile().getName());
		}
		if (this.player.isCurrent()) {
			this.currentPlayerInfo.setText(TranslationKey.createAndGet("screen.tic_tac_toe.current_player", this.player.getProfile().getName()));
		} else if (this.opponent.isCurrent()) {
			this.currentPlayerInfo.setText(TranslationKey.createAndGet("screen.tic_tac_toe.current_player", this.opponent.getProfile().getName()));
		} else {
			this.currentPlayerInfo.setText(TranslationKey.createAndGet("screen.tic_tac_toe.no_current_player"));
		}
		LOGGER.info("Update player info");
	}
	
	@Override
	protected Pane createPane() {
		BorderPane pane = new BorderPane();
		pane.setLeft(this.createInfoPane());
		pane.setCenter(this.createGamePane());
		pane.setBottom(this.createActionPane());
		return pane;
	}
	
	protected Pane createInfoPane() {
		GridPane playerPane = FxUtil.makeGrid(Pos.CENTER, 10.0, 10.0);
		playerPane.addColumn(0, this.currentPlayerInfo);
		GridPane scorePane = FxUtil.makeGrid(Pos.CENTER, 10.0, 10.0);
		scorePane.addColumn(0, this.playerInfo, this.opponentInfo);
		return FxUtil.makeVerticalBox(Pos.CENTER, 10.0, new Text(TranslationKey.createAndGet("screen.tic_tac_toe.score")), new Separator(Orientation.HORIZONTAL), playerPane, new Separator(Orientation.HORIZONTAL), scorePane);
	}
	
	protected Pane createGamePane() {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 0.0, 20.0);
		pane.addRow(0, this.createButton(0, 0), this.topLeftVBorder, this.createButton(1, 0), this.topRightVBorder, this.createButton(2, 0));
		pane.addRow(2, this.createButton(0, 1), this.centerLeftVBorder, this.createButton(1, 1), this.centerRightVBorder, this.createButton(2, 1));
		pane.addRow(4, this.createButton(0, 2), this.bottomLeftVBorder, this.createButton(1, 2), this.bottomRightVBorder, this.createButton(2, 2));
		pane.add(this.topLeftHBorder, 0, 1);
		pane.add(this.topCenterHBorder, 2, 1);
		pane.add(this.topRightHBorder, 4, 1);
		pane.add(this.bottomLeftHBorder, 0, 3);
		pane.add(this.bottomCenterHBorder, 2, 3);
		pane.add(this.bottomRightHBorder, 4, 3);
		return pane;
	}
	
	protected GridPane createButton(int hMap, int vMap) {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 0.0, 0.0);
		pane.add(Objects.requireNonNull(this.getButton(hMap, vMap)), 0, 0);
		return pane;
	}
	
	protected Pane createActionPane() {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 30.0);
		pane.addRow(0, this.leaveButton, this.playAgainButton, this.confirmActionButton);
		return pane;
	}
	
}
