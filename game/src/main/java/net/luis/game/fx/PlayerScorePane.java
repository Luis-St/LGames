package net.luis.game.fx;

import com.google.common.collect.Lists;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import net.luis.Constants;
import net.luis.game.Game;
import net.luis.game.player.Player;
import net.luis.game.player.game.GamePlayer;
import net.luis.language.TranslationKey;
import net.luis.utils.util.SimpleEntry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author Luis-st
 *
 */

public class PlayerScorePane extends GridPane {
	
	private final Game game;
	private final PlayerScorePane.Type scoreType;
	private final List<Entry<GamePlayer, Text>> playerScoreInfos;
	
	public PlayerScorePane(@NotNull Game game, @NotNull PlayerScorePane.Type scoreType) {
		this.game = game;
		this.scoreType = scoreType;
		this.playerScoreInfos = Lists.newArrayList();
		this.init();
	}
	
	private void init() {
		this.setAlignment(Pos.CENTER);
		this.setVgap(10.0);
		this.setHgap(10.0);
		this.setGridLinesVisible(Constants.DEBUG_MODE);
		this.makePlayerScore();
	}
	
	private void makePlayerScore() {
		int i = 0;
		for (GamePlayer gamePlayer : this.game.getPlayers()) {
			Player player = gamePlayer.getPlayer();
			Text text = new Text(TranslationKey.createAndGet("screen.tic_tac_toe.player_score", player.getProfile().getName(), this.scoreType.getValue(player)));
			this.add(text, 0, i++);
			this.playerScoreInfos.add(new SimpleEntry<>(gamePlayer, text));
		}
	}
	
	public @NotNull PlayerScorePane.Type getScoreType() {
		return this.scoreType;
	}
	
	public void update() {
		for (Entry<GamePlayer, Text> entry : this.playerScoreInfos) {
			Player player = entry.getKey().getPlayer();
			entry.getValue().setText(TranslationKey.createAndGet("screen.tic_tac_toe.player_score", player.getProfile().getName(), this.scoreType.getValue(player)));
		}
	}
	
	public enum Type {
		
		WIN("win") {
			@Override
			public int getValue(@NotNull Player player) {
				return player.getScore().getWins();
			}
		}, LOSE("lose") {
			@Override
			public int getValue(@NotNull Player player) {
				return player.getScore().getLoses();
			}
		}, DRAW("draw") {
			@Override
			public int getValue(@NotNull Player player) {
				return player.getScore().getDraws();
			}
		}, SCORE("score") {
			@Override
			public int getValue(@NotNull Player player) {
				return player.getScore().getScore();
			}
		};
		
		private final String name;
		
		Type(@NotNull String name) {
			this.name = name;
		}
		
		public @NotNull String getName() {
			return this.name;
		}
		
		public abstract int getValue(@NotNull Player player);
		
		@Override
		public @NotNull String toString() {
			return this.name;
		}
	}
}
