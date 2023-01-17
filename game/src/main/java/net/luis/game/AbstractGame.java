package net.luis.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.luis.application.ApplicationType;
import net.luis.game.map.GameMap;
import net.luis.game.map.GameMapFactory;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.GamePlayerFactory;
import net.luis.game.player.GamePlayerInfo;
import net.luis.game.win.WinHandler;
import net.luis.network.packet.client.game.CurrentPlayerUpdatePacket;
import net.luis.player.GameProfile;
import net.luis.utils.util.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractGame implements Game {
	
	private final GameMap map;
	private final List<GamePlayer> players;
	private final WinHandler winHandler;
	private GamePlayer player;
	
	protected AbstractGame(GameMapFactory mapFactory, List<GamePlayerInfo> playerInfos, GamePlayerFactory playerFactory, @Nullable WinHandler winHandler) {
		this.map = mapFactory.create(this);
		this.players = createPlayers(this, playerInfos, playerFactory);
		this.winHandler = ApplicationType.SERVER.isOn() ? Objects.requireNonNull(winHandler) : null;
	}
	
	private static List<GamePlayer> createPlayers(Game game, List<GamePlayerInfo> playerInfos, GamePlayerFactory playerFactory) {
		if (!game.getType().hasEnoughPlayers(playerInfos.size())) {
			Game.LOGGER.error("Fail to create game players list with size {}, since a player list with size in bounds {} was expected", playerInfos.size(), game.getType().getBounds());
			throw new IllegalStateException("Fail to create game players list with size " + playerInfos.size() + ", since a player list with size in bounds " + game.getType().getBounds() + " was expected");
		}
		Game.LOGGER.info("Start game {} with players {}", game.getType().getInfoName(), Utils.mapList(playerInfos, GamePlayerInfo::getProfile, GameProfile::getName));
		List<GamePlayer> gamePlayers = Lists.newArrayList();
		for (GamePlayerInfo playerInfo : playerInfos) {
			gamePlayers.add(playerFactory.create(game, playerInfo.getProfile(), playerInfo.getPlayerType(), playerInfo.getUUIDs()));
		}
		return gamePlayers;
	}
	
	@Override
	public void init() {
		this.map.init(this.players);
	}
	
	@Override
	public GameMap getMap() {
		return this.map;
	}
	
	@Override
	public List<GamePlayer> getPlayers() {
		return ApplicationType.SERVER.isOn() ? this.players : ImmutableList.copyOf(this.players);
	}
	
	@Override
	public GamePlayer getPlayer() {
		return this.player;
	}
	
	@Override
	public void setPlayer(GamePlayer player) {
		LOGGER.info("Update current player from {} to {}", Utils.runIfNotNull(this.getPlayer(), GamePlayer::getName), Utils.runIfNotNull(player, GamePlayer::getName));
		this.player = player;
		if (ApplicationType.SERVER.isOn() && this.getPlayer() != null) {
			this.broadcastPlayers(new CurrentPlayerUpdatePacket(this.getPlayer()));
		}
	}
	
	@Nullable
	@Override
	public WinHandler getWinHandler() {
		return this.winHandler;
	}
}
