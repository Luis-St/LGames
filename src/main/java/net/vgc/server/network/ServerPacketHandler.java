package net.vgc.server.network;

import java.util.Objects;
import java.util.UUID;

import net.vgc.game.Game;
import net.vgc.game.action.Action;
import net.vgc.network.NetworkSide;
import net.vgc.network.packet.AbstractPacketHandler;
import net.vgc.network.packet.client.ClientJoinedPacket;
import net.vgc.player.GameProfile;
import net.vgc.server.dedicated.DedicatedServer;

public class ServerPacketHandler extends AbstractPacketHandler {
	
	private final DedicatedServer server;
	
	public ServerPacketHandler(DedicatedServer server, NetworkSide networkSide) {
		super(networkSide);
		this.server = server;
	}
	
	public void handleClientJoin(String name, UUID uuid) {
		this.server.enterPlayer(this.connection, new GameProfile(name, uuid));
		this.connection.send(new ClientJoinedPacket(this.server.getPlayerList().getPlayers()));
	}
	
	public void handleClientLeave(UUID uuid) {
		this.server.leavePlayer(this.connection, this.server.getPlayerList().getPlayer(uuid));
	}
	
	public void handleAction(Action<?> action) {
		Game game = this.server.getGame();
		if (game != null) {
			Objects.requireNonNull(game.getActionHandler(), "The action handler of a game must not be null").handle(action);
		} else {
			// TODO: add all other handlers
		}
	}
	
	/*public <S extends Game, C extends Game> void handlePlayGameRequest(GameType<S, C> gameType, List<GameProfile> profiles) {
		MutableBoolean mutable = new MutableBoolean(false);
		List<ServerPlayer> players = this.server.getPlayerList().getPlayers(profiles).stream().filter((player) -> {
			if (player.isPlaying()) {
				mutable.setTrue();
				return false;
			}
			return true;
		}).collect(Collectors.toList());
		if (players.size() == profiles.size()) {
			if (mutable.isFalse()) {
				if (this.server.getGame() == null) {
					S game = gameType.createServerGame(this.server, players);
					if (game != null) {
						this.server.setGame(game);
						game.startGame();
						for (ServerPlayer player : players) {
							player.setPlaying(true);
							ActionTypes.START_GAME.send(player.connection, new StartGameData(gameType, this.createPlayerInfos(game.getPlayers())));
						}
						Util.runDelayed("DelayedSetStartPlayer", 250, () -> {
							game.getStartPlayer();
						});
					}
				} else {
					LOGGER.warn("Fail to start game {}, since there is already a running game {}", gameType.getInfoName(), this.server.getGame().getType().getInfoName());
					ActionTypes.CANCEL_PLAY_REQUEST.send(this.connection, new EmptyData());
				}
			} else {
				LOGGER.warn("Fail to start game {}, since on player is already playing a game (this should normally not happen)", gameType.getName());
				ActionTypes.CANCEL_PLAY_REQUEST.send(this.connection, new EmptyData());
			}
		} else {
			if (mutable.isTrue()) {
				LOGGER.warn("Fail to start game {}, since at least one selected player is already playing a game", gameType.getInfoName());
			} else {
				LOGGER.warn("Fail to start game {}, since there was an error in a player profile", gameType.getInfoName());
			}
			ActionTypes.CANCEL_PLAY_REQUEST.send(this.connection, new EmptyData());
		}
	}*/
	
	/*protected List<GamePlayerInfo> createPlayerInfos(List<GamePlayer> players) {
		List<GamePlayerInfo> playerInfos = Lists.newArrayList();
		for (GamePlayer player : players) {
			playerInfos.add(new GamePlayerInfo(player.getPlayer().getProfile(), player.getPlayerType(), Util.mapList(player.getFigures(), GameFigure::getUUID)));
		}
		return playerInfos;
	}*/
	
	/*public void handlePlayAgainGameRequest(GameProfile profile) {
		ServerPlayer player = this.server.getPlayerList().getPlayer(profile);
		if (player != null) {
			if (this.server.isAdmin(player)) {
				Game game = this.server.getGame();
				if (game != null) {
					if (!game.nextMatch()) {
						LOGGER.warn("Fail to start new match of game {}", game.getType().getInfoName());
						ActionTypes.CANCEL_PLAY_AGAIN_REQUEST.send(this.connection, new EmptyData());
						game.stopGame();
					}
				} else {
					LOGGER.warn("Fail to start new match, since there is no game running");
					ActionTypes.CANCEL_PLAY_AGAIN_REQUEST.send(this.connection, new EmptyData());
				}
			} else {
				LOGGER.warn("Cancel request to start a new match, since the player {} has not the required permission");
				ActionTypes.CANCEL_PLAY_AGAIN_REQUEST.send(this.connection, new EmptyData());
			}
		} else {
			LOGGER.warn("Fail to start a new match, since there was an error in a player profile {}", profile.getName());
			ActionTypes.CANCEL_PLAY_AGAIN_REQUEST.send(this.connection, new EmptyData());
		}
	}*/
	
	/*public void handleRollDiceRequest(GameProfile profile) {
		Game game = this.server.getGame();
		if (game != null) {
			GamePlayer player = game.getPlayerFor(profile);
			if (player != null) {
				if (game.isDiceGame()) {
					DiceHandler diceHandler = game.getDiceHandler();
					if (diceHandler.canRoll(player)) {
						int count;
						if (diceHandler.hasPlayerRolledDice(player)) {
							count = diceHandler.rollExclude(player, diceHandler.getLastCount(player));
						} else {
							count = diceHandler.roll(player);
						}
						LOGGER.info("Player {} rolled a {}", profile.getName(), count);
						ActionTypes.ROLLED_DICE.send(this.connection, new IntegerData(count));
						if (diceHandler.canRollAgain(player, count)) {
							ActionTypes.CAN_ROLL_DICE_AGAIN.send(this.connection, new EmptyData());
						} else if (diceHandler.canPerformGameAction(player, count)) {
							player.setRollCount(0);
							diceHandler.performGameAction(player, count);
						} else {
							player.setRollCount(0);
							game.nextPlayer(false);
						}
					} else {
						LOGGER.warn("Player {} tries to roll the dice, but he is not be able to roll it", profile.getName());
						ActionTypes.CANCEL_ROLL_DICE_REQUEST.send(this.connection, new EmptyData());
						game.nextPlayer(false);
					}
				} else {
					LOGGER.warn("Fail to roll dice, since game {} is not a dice game", game.getType().getInfoName());
					ActionTypes.CANCEL_ROLL_DICE_REQUEST.send(this.connection, new EmptyData());
					game.stopGame();
				}
			} else {
				LOGGER.warn("Fail to roll dice, since the player {} does not play game {}", profile.getName(), game.getType().getInfoName());
				ActionTypes.CANCEL_ROLL_DICE_REQUEST.send(this.connection, new EmptyData());
				game.stopGame();
			}
		} else {
			LOGGER.warn("Fail to roll dice, since there is no running game");
			ActionTypes.CANCEL_ROLL_DICE_REQUEST.send(this.connection, new EmptyData());
		}
	}*/
	
	/*public void handleExitGameRequest(GameProfile profile) {
		Game game = this.server.getGame();
		if (game != null) {
			if (!game.removePlayer(game.getPlayerFor(profile), true)) {
				LOGGER.warn("Fail to remove player {} from game {}, since the player is no playing the game", profile.getName(), game.getType().getInfoName());
			}
		} else {
			for (ServerPlayer player : this.server.getPlayerList().getPlayers()) {
				if (player.isPlaying()) {
					player.setPlaying(false);
					LOGGER.info("Correcting the playing value of player {} to false, since it was not correctly reset", player.getProfile().getName());
				}
			}
			ServerPlayer player = this.server.getPlayerList().getPlayer(profile);
			if (player != null) {
				ActionTypes.EXIT_GAME.send(this.connection, new EmptyData());
			} else {
				LOGGER.warn("Fail to remove player {} from game, since there is no running game", profile.getName());
			}
		}
	}*/
	
}
