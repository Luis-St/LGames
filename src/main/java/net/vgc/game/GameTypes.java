package net.vgc.game;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.vgc.client.screen.game.LudoScreen;
import net.vgc.client.screen.game.TTTScreen;
import net.vgc.game.ludo.LudoGame;
import net.vgc.game.ttt.TTTGame;
import net.vgc.network.packet.client.game.StartLudoGamePacket;
import net.vgc.network.packet.client.game.StartTTTGamePacket;

public class GameTypes {
	
	public static final List<GameType<?>> GAME_TYPES = Lists.newArrayList();
	
	public static final GameType<TTTGame> TIC_TAC_TOE = register(new GameType<>("Tic Tac Toe", 2, 2, TTTGame::new, (game, player) -> {
		return new StartTTTGamePacket(game.getPlayerType(player), game.getPlayers());
	}, TTTScreen::new));
	public static final GameType<LudoGame> LUDO = register(new GameType<>("Ludo", 2, 4, LudoGame::new, (game, player) -> {
		return new StartLudoGamePacket(game.getPlayerType(player), game.getPlayerTypes());
	}, LudoScreen::new));
	
	protected static <T extends Game> GameType<T> register(GameType<T> gameType) {
		GAME_TYPES.add(gameType);
		return gameType;
	}
	
	@Nullable
	public static GameType<?> fromName(String name) {
		for (GameType<?> gameType : GAME_TYPES) {
			if (gameType.getName().equals(name)) {
				return gameType;
			}
		}
		return null;
	}
	
}
