package net.luis.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.luis.game.application.ApplicationType;
import net.luis.game.map.GameMap;
import net.luis.game.map.GameMapFactory;
import net.luis.game.player.Player;
import net.luis.game.player.game.GamePlayer;
import net.luis.game.player.game.GamePlayerFactory;
import net.luis.game.player.game.GamePlayerType;
import net.luis.game.screen.GameScreen;
import net.luis.game.screen.GameScreenFactory;
import net.luis.game.win.WinHandler;
import net.luis.network.packet.client.game.CurrentPlayerUpdatePacket;
import net.luis.utils.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractGame implements Game {
	
	private static final Logger LOGGER = LogManager.getLogger(AbstractGame.class);
	
	private final UUID uuid = UUID.randomUUID();
	private final GameMap map;
	private final List<GamePlayer> players;
	private final GameScreen screen;
	private final WinHandler winHandler;
	private GamePlayer player;
	
	protected AbstractGame(@NotNull GameMapFactory mapFactory, @NotNull GamePlayerFactory playerFactory, @NotNull List<Player> players, @NotNull GamePlayerType[] playerTypes, int figureCount, GameScreenFactory screenFactory,
						   @Nullable WinHandler winHandler) {
		this.map = mapFactory.create(this);
		this.players = createPlayers(this, playerFactory, players, playerTypes, figureCount);
		this.screen = screenFactory.create(this);
		this.winHandler = ApplicationType.SERVER.isOn() ? Objects.requireNonNull(winHandler) : null;
	}
	
	private static List<GamePlayer> createPlayers(@NotNull Game game, @NotNull GamePlayerFactory playerFactory, @NotNull List<Player> players, @NotNull GamePlayerType[] playerTypes, int figureCount) {
		if (!game.getType().hasEnoughPlayers(players.size())) {
			LOGGER.error("Fail to create game players list with size {}, since a player list with size in bounds {} was expected", players.size(), game.getType().getBounds());
			throw new IllegalStateException("Fail to create game players list with size " + players.size() + ", since a player list with size in bounds " + game.getType().getBounds() + " was expected");
		}
		LOGGER.info("Start game {} with players {}", game.getType().getInfoName(), Utils.mapList(players, Player::getName));
		List<GamePlayer> gamePlayers = Lists.newArrayList();
		for (int i = 0; i < players.size(); i++) {
			List<UUID> figureIds = Lists.newArrayList();
			for (int j = 0; j < figureCount; j++) {
				figureIds.add(UUID.randomUUID());
			}
			gamePlayers.add(playerFactory.create(game, players.get(i), playerTypes[i], figureIds));
		}
		return gamePlayers;
	}
	
	@Override
	public void init() {
		this.map.init(this.players);
	}
	
	@Override
	public @NotNull UUID getUniqueId() {
		return this.uuid;
	}
	
	@Override
	public @NotNull GameMap getMap() {
		return this.map;
	}
	
	@Override
	public @NotNull List<GamePlayer> getPlayers() {
		return ApplicationType.SERVER.isOn() ? this.players : ImmutableList.copyOf(this.players);
	}
	
	@Override
	public @NotNull GameScreen getScreen() {
		return this.screen;
	}
	
	@Override
	public GamePlayer getPlayer() {
		return this.player;
	}
	
	@Override
	public void setPlayer(@NotNull GamePlayer player) {
		LOGGER.info("Update current player from {} to {}", Utils.runIfNotNull(this.getPlayer(), GamePlayer::getName), Utils.runIfNotNull(player, GamePlayer::getName));
		this.player = player;
		if (ApplicationType.SERVER.isOn() && this.getPlayer() != null) {
			this.broadcastPlayers(new CurrentPlayerUpdatePacket(this.getPlayer().getPlayer().getProfile()));
		}
	}
	
	@Nullable
	@Override
	public WinHandler getWinHandler() {
		return this.winHandler;
	}
}
