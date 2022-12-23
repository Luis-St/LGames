package net.vgc.client.fx.game;

import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import net.luis.utils.util.SimpleEntry;
import net.vgc.Constans;
import net.vgc.game.Game;
import net.vgc.game.player.GamePlayer;
import net.vgc.language.TranslationKey;
import net.vgc.player.Player;

/**
 *
 * @author Luis-st
 *
 */

public class PlayerScorePane extends GridPane {
	
	private final Game game;
	private final PlayerScorePane.Type scoreType;
	private final List<Entry<GamePlayer, Text>> playerScoreInfos;
	
	public PlayerScorePane(Game game, PlayerScorePane.Type scoreType) {
		this.game = game;
		this.scoreType = scoreType;
		this.playerScoreInfos = Lists.newArrayList();
		this.init();
	}
	
	private void init() {
		this.setAlignment(Pos.CENTER);
		this.setVgap(10.0);
		this.setHgap(10.0);
		this.setGridLinesVisible(Constans.DEBUG);
		this.makePlayerScore();
	}
	
	private void makePlayerScore() {
		int i = 0;
		for (GamePlayer gamePlayer : this.game.getPlayers()) {
			Player player = gamePlayer.getPlayer();
			Text text = new Text(TranslationKey.createAndGet("screen.tic_tac_toe.player_score", player.getProfile().getName(), this.scoreType.getValue(player)));
			this.add(text, 0, i++);
			this.playerScoreInfos.add(new SimpleEntry<GamePlayer, Text>(gamePlayer, text));
		}
	}
	
	public PlayerScorePane.Type getScoreType() {
		return this.scoreType;
	}
	
	public void update() {
		for (Entry<GamePlayer, Text> entry : this.playerScoreInfos) {
			Player player = entry.getKey().getPlayer();
			entry.getValue().setText(TranslationKey.createAndGet("screen.tic_tac_toe.player_score", player.getProfile().getName(), this.scoreType.getValue(player)));
		}
	}
	
	public static enum Type {
		
		WIN("win") {
			@Override
			public int getValue(Player player) {
				return player.getScore().getWins();
			}
		},
		LOSE("lose") {
			@Override
			public int getValue(Player player) {
				return player.getScore().getLoses();
			}
		},
		DRAW("draw") {
			@Override
			public int getValue(Player player) {
				return player.getScore().getDraws();
			}
		},
		SCORE("score") {
			@Override
			public int getValue(Player player) {
				return player.getScore().getScore();
			}
		};
		
		private final String name;
		
		private Type(String name) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
		
		public abstract int getValue(Player player);
		
		@Override
		public String toString() {
			return this.name;
		}
		
	}
	
}
