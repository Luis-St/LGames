package net.luis.ludo;

import net.luis.game.AbstractGame;
import net.luis.game.GameResult;
import net.luis.game.application.ApplicationType;
import net.luis.game.map.field.GameField;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.map.field.GameFieldType;
import net.luis.game.player.game.GamePlayer;
import net.luis.game.player.game.GamePlayerFactory;
import net.luis.game.player.game.GamePlayerInfo;
import net.luis.game.player.GameProfile;
import net.luis.game.player.game.figure.GameFigure;
import net.luis.game.player.score.PlayerScore;
import net.luis.game.type.GameType;
import net.luis.game.type.GameTypes;
import net.luis.game.win.GameResultLine;
import net.luis.ludo.dice.LudoDiceHandler;
import net.luis.ludo.map.LudoMap;
import net.luis.ludo.win.LudoWinHandler;
import net.luis.network.packet.client.SyncPlayerDataPacket;
import net.luis.network.packet.client.game.CanSelectGameFieldPacket;
import net.luis.network.packet.client.game.GameResultPacket;
import net.luis.network.packet.client.game.UpdateGameMapPacket;
import net.luis.network.packet.client.game.dice.CanRollDiceAgainPacket;
import net.luis.network.listener.PacketListener;
import net.luis.network.packet.server.ServerPacket;
import net.luis.network.packet.server.game.SelectGameFieldPacket;
import net.luis.utils.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class LudoGame extends AbstractGame {
	
	private static final Logger LOGGER = LogManager.getLogger(LudoGame.class);
	
	private final LudoDiceHandler diceHandler = new LudoDiceHandler(this, 1, 6);
	
	public LudoGame(List<GamePlayerInfo> playerInfos, GamePlayerFactory playerFactory) {
		super(LudoMap::new, playerInfos, playerFactory, new LudoWinHandler());
	}
	
	@Override
	public @NotNull GameType<?> getType() {
		return GameTypes.LUDO;
	}
	
	@Override
	public void setPlayer(@NotNull GamePlayer player) {
		if (ApplicationType.SERVER.isOn()) {
			player.setRollCount(player.hasAllFiguresAt(GameField::isHome) ? 3 : 1);
		}
		super.setPlayer(player);
	}
	
	@Override
	public boolean isDiceGame() {
		return true;
	}
	
	@Nullable
	@Override
	public LudoDiceHandler getDiceHandler() {
		return ApplicationType.SERVER.isOn() ? this.diceHandler : null;
	}
	
	@PacketListener(ServerPacket.class)
	public void handlePacket(ServerPacket serverPacket) {
		if (serverPacket instanceof SelectGameFieldPacket packet) {
			GamePlayer player = this.getPlayerFor(packet.getProfile().cast(GameProfile.class));
			assert player != null;
			if (Objects.equals(this.getPlayer(), player)) {
				int count = this.diceHandler.getLastCount(player);
				if (count != -1) {
					GameField currentField = this.getMap().getField((GameFieldType) packet.getFieldType(), player.getPlayerType(), (GameFieldPos) packet.getFieldPos());
					assert currentField != null;
					if (!currentField.isEmpty()) {
						GameFigure figure = currentField.getFigure();
						assert figure != null;
						GameField nextField = this.getMap().getNextField(figure, count);
						if (nextField != null) {
							if (this.getMap().moveFigureTo(figure, nextField)) {
								this.broadcastPlayers(new UpdateGameMapPacket(Utils.mapList(this.getMap().getFields(), GameField::getFieldInfo)));
								assert this.getWinHandler() != null;
								if (this.getWinHandler().hasPlayerFinished(player)) {
									this.getWinHandler().onPlayerFinished(player);
									if (this.getWinHandler().getWinOrder().size() - this.getPlayers().size() > 1) {
										this.nextPlayer(false);
									} else {
										LOGGER.info("Finished game {} with player win order: {}", this.getType().getInfoName(), Utils.mapList(this.getWinHandler().getWinOrder(), GamePlayer::getName));
										for (GamePlayer gamePlayer : this.getPlayers()) {
											PlayerScore score = gamePlayer.getPlayer().getScore();
											score.setScore(this.getWinHandler().getScoreFor(this, gamePlayer));
											this.broadcastPlayers(new SyncPlayerDataPacket(gamePlayer.getPlayer().getProfile(), true, score));
										}
										this.broadcastPlayers(new GameResultPacket(GameResult.NO, GameResultLine.EMPTY));
									}
								} else if (this.diceHandler.canRollAfterMove(player, currentField, nextField, count)) {
									player.setRollCount(1);
									this.broadcastPlayer(new CanRollDiceAgainPacket(), player);
								} else {
									this.nextPlayer(false);
								}
							} else {
								LOGGER.warn("Fail to move figure {} of player {} to field {}", figure.getCount(), player.getName(), nextField.getFieldPos().getPosition());
								this.broadcastPlayer(new CanSelectGameFieldPacket(), player);
							}
						} else {
							LOGGER.warn("Fail to move figure {} of player {}, since there is no next field for the figure", figure.getCount(), player.getName());
							this.broadcastPlayer(new CanSelectGameFieldPacket(), player);
						}
					} else {
						LOGGER.warn("Fail to get a figure of player {} from field {}, since the field is empty", player.getName(), currentField.getFieldPos().getPosition());
						this.broadcastPlayer(new CanSelectGameFieldPacket(), player);
					}
				} else {
					LOGGER.warn("Fail to move figure of player {}, since the player has not rolled the dice yet", player.getName());
					this.stop();
				}
			} else {
				LOGGER.warn("Player {} tries to change the map at pos {} to {}, but it is not his turn", player.getName(), packet.getFieldPos().cast(GameFieldPos.class).getPosition(), player.getPlayerType());
			}
		}
	}
	
	@Override
	public String toString() {
		return "LudoGame";
	}
}
