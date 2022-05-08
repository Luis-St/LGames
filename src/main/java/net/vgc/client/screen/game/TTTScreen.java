package net.vgc.client.screen.game;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import net.vgc.client.fx.FxUtil;
import net.vgc.client.fx.game.TTTButton;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.client.player.RemotePlayer;
import net.vgc.game.ttt.TTTState;
import net.vgc.game.ttt.TTTType;
import net.vgc.game.ttt.map.TTTMap;
import net.vgc.game.ttt.map.TTTResultLine;
import net.vgc.language.TranslationKey;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.network.packet.client.game.StartTTTGamePacket;
import net.vgc.network.packet.client.game.TTTGameResultPacket;
import net.vgc.network.packet.client.game.UpdateTTTGamePacket;
import net.vgc.network.packet.server.ExitGameRequestPacket;
import net.vgc.network.packet.server.game.PressTTTFieldPacket;
import net.vgc.player.GameProfile;
import net.vgc.util.Util;

public class TTTScreen extends GameScreen {
	
	protected final List<TTTButton> buttons = Lists.newArrayList();
	
	protected TTTType playerType;
	protected TTTType opponentPlayerType;
	protected RemotePlayer opponentPlayer;
	
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
	protected Button leaveButton;
	protected Button confirmActionButton;
	
	public TTTScreen() {
		
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
						oldButton.setType(TTTType.NO, TTTState.DEFAULT);
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
		double bigBorderSize = 155.0;
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
		this.leaveButton = FxUtil.makeButton(TranslationKey.createAndGet("screen.lobby.leave"), this::handleLeave);
		this.confirmActionButton = FxUtil.makeButton(TranslationKey.createAndGet("screen.tic_tac_toe.confirm_action"), this::handleConfirmAction);
	}
	
	protected TTTButton makeButton(int vMap, int hMap) {
		TTTButton button = new TTTButton(this.group, 150.0, vMap, hMap);
		button.setOnAction((event) -> {
			if (this.currentPlayer) {
				if (button.getType() == TTTType.NO) {
					button.setType(this.playerType, TTTState.SHADOW);
					LOGGER.info("Update state of field with map pos {}:{} to {}", vMap, hMap, TTTState.SHADOW);
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
		this.client.getServerHandler().send(new ExitGameRequestPacket(this.getPlayer().getGameProfile()));
	}
	
	protected void handleConfirmAction() {
		Toggle toggle = this.group.getSelectedToggle();
		if (toggle instanceof TTTButton button) {
			int vMap = button.getVMap();
			int hMap = button.getHMap();
			if (this.currentPlayer) {
				this.client.getServerHandler().send(new PressTTTFieldPacket(this.getPlayer().getGameProfile(), vMap, hMap));
				this.group.selectToggle(null);
			} else {
				LOGGER.info("It is not the turn of the local player {}", this.getPlayer().getGameProfile().getName());
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
			GameProfile localGameProfile = this.client.getPlayer().getGameProfile();
			this.currentPlayer = localGameProfile.equals(packet.getStartPlayerGameProfile());
			this.playerType = packet.getPlayerType();
			int localIndex = packet.getGameProfiles().indexOf(localGameProfile);
			if (localIndex == 0) {
				this.setOpponent(packet.getGameProfiles().get(1));
			} else if (localIndex == 1) {
				this.setOpponent(packet.getGameProfiles().get(0));
			} else {
				LOGGER.warn("Fail to set the opponent player");
			}
		} else if (clientPacket instanceof UpdateTTTGamePacket packet) {
			this.currentPlayer = this.client.getPlayer().getGameProfile().equals(packet.getGameProfile());
			this.applyMap(packet.getMap(), TTTState.DEFAULT);
			this.group.selectToggle(null);
		} else if (clientPacket instanceof TTTGameResultPacket packet) {
			GameProfile winner = packet.getWinnerProfile();
			GameProfile loser = packet.getLoserProfile();
			TTTResultLine resultLine = packet.getResultLine();
			if (winner == GameProfile.EMPTY && loser == GameProfile.EMPTY && packet.getResultLine() == TTTResultLine.EMPTY) {
				for (int v = 0; v < 3; v++) {
					for (int h = 0; h < 3; h++) {
						TTTButton button = this.getButton(v, h);
						button.setType(button.getType(), TTTState.DRAW);
					}
				}
				LOGGER.info("Update field map to state {}", TTTState.DRAW);
				// TODO: handle draw
			} else if (winner != GameProfile.EMPTY && loser != GameProfile.EMPTY) {
				if (resultLine != TTTResultLine.EMPTY) {
					if (winner.equals(this.getPlayer().getGameProfile())) {
						this.applyResultLine(packet.getWinnerType(), resultLine, TTTState.WIN);
						// TODO: handle win
					} else if (loser.equals(this.getPlayer().getGameProfile())) {
						this.applyResultLine(packet.getLoserType(), resultLine, TTTState.LOSE);
						// TODO: handle lose
					} else {
						LOGGER.warn("Fail to handle result, since the winner {} and the loser {} does not match with the local player {}", winner.getName(), loser.getName(), this.getPlayer().getGameProfile().getName());
					}
				} else {
					LOGGER.warn("Fail to handle game result, since the result line is empty but there is a winner {} and a loser", winner.getName(), loser.getName());
				}
			} else {
				LOGGER.warn("Fail to handle game result, since the winner {} and the loser {} must not be empty", winner.getName(), loser.getName());
			}
		}
	}
	
	protected void setOpponent(GameProfile gameProfile) {
		AbstractClientPlayer player = this.client.getPlayer(gameProfile);
		if (player instanceof RemotePlayer opponentPlayer) {
			this.opponentPlayer = opponentPlayer;
			LOGGER.info("Set opponent player of {} to {}", this.getPlayer().getGameProfile().getName(), this.opponentPlayer.getGameProfile().getName());
			if (this.playerType != null) {
				this.opponentPlayerType = this.playerType.getOpponent();
			} else {
				LOGGER.warn("Fail to set the type for the opponent player {}, since the type for the local player {} is not set", this.opponentPlayer.getGameProfile().getName(), this.getPlayer().getGameProfile().getName());
			}
		} else {
			LOGGER.warn("Fail to set the opponent player to a player of type {}", (Object) Util.runIfNotNull(player, (clientPlayer) -> {
				return clientPlayer.getClass().getSimpleName();
			}));
		}
	}
	
	protected void applyMap(TTTMap map, TTTState state) {
		for (int v = 0; v < 3; v++) {
			for (int h = 0; h < 3; h++) {
				this.getButton(v, h).setType(map.getType(v, h), state);
			}
		}
		LOGGER.info("Update field map to state {}", state);
	}
	
	protected void applyResultLine(TTTType type, TTTResultLine resultLine, TTTState state) {
		if (resultLine.getType() == type) {
			this.applyResult(type, resultLine.getVMap0(), resultLine.getHMap0(), state);
			this.applyResult(type, resultLine.getVMap1(), resultLine.getHMap1(), state);
			this.applyResult(type, resultLine.getVMap2(), resultLine.getHMap2(), state);
			LOGGER.info("Apply line from map pos {}:{} to {}:{} with type {} to result {}", resultLine.getVMap0(), resultLine.getHMap0(), resultLine.getVMap2(), resultLine.getHMap2(), resultLine.getType(), state);
		} else {
			LOGGER.warn("Fail to apply line, since the line type {} does not match with the player type {}", resultLine.getType(), type);
		}
	}
	
	protected void applyResult(TTTType type, int vMap, int hMap, TTTState state) {
		TTTButton button = this.getButton(vMap, hMap);
		if (button != null) {
			TTTType buttonType = button.getType();
			if (buttonType == type) {
				button.setType(buttonType, state);
			} else {
				LOGGER.warn("Fail to apply result to field at map pos {}:{}, since button type {} does not match with result type {}", vMap, hMap, buttonType, type);
			}
		} else {
			LOGGER.warn("Fail to apply result to field at map pos {}:{}, since \"button\" is null", vMap, hMap);
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
	
	protected Pane createInfoPane() { // TODO
		return new Pane();
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
		BorderPane pane = new BorderPane();
		pane.setLeft(this.leaveButton);
		pane.setRight(this.confirmActionButton);
		return pane;
	}
	
}
