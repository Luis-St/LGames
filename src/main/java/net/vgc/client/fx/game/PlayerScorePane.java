package net.vgc.client.fx.game;

import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import net.vgc.Constans;
import net.vgc.client.game.ClientGame;
import net.vgc.client.game.player.ClientGamePlayer;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.language.TranslationKey;
import net.vgc.util.SimpleEntry;

public class PlayerScorePane extends GridPane {
	
	protected final ClientGame game;
	protected final PlayerScorePane.Type scoreType;
	protected final List<Entry<ClientGamePlayer, Text>> playerScoreInfos;
	
	public PlayerScorePane(ClientGame game, PlayerScorePane.Type scoreType) {
		this.game = game;
		this.scoreType = scoreType;
		this.playerScoreInfos = Lists.newArrayList();
		this.init();
	}
	
	protected void init() {
		this.setAlignment(Pos.CENTER);
		this.setVgap(10.0);
		this.setHgap(10.0);
		this.setGridLinesVisible(Constans.DEBUG);
		this.makePlayerScore();
	}
	
	protected void makePlayerScore() {
		int i = 0;
		for (ClientGamePlayer gamePlayer : this.game.getPlayers()) {
			AbstractClientPlayer player = gamePlayer.getPlayer();
			Text text = new Text(TranslationKey.createAndGet("screen.tic_tac_toe.player_score", player.getProfile().getName(), this.scoreType.getValue(player)));
			this.add(text, 0, i++);
			this.playerScoreInfos.add(new SimpleEntry<ClientGamePlayer, Text>(gamePlayer, text, true));
		}
	}
	
	public PlayerScorePane.Type getScoreType() {
		return this.scoreType;
	}
	
	public void update() {
		for (Entry<ClientGamePlayer, Text> entry : this.playerScoreInfos) {
			AbstractClientPlayer player = entry.getKey().getPlayer();
			entry.getValue().setText(TranslationKey.createAndGet("screen.tic_tac_toe.player_score", player.getProfile().getName(), this.scoreType.getValue(player)));
		}
	}
	
	public static enum Type {
		
		WIN("win") {
			@Override
			public int getValue(AbstractClientPlayer player) {
				return player.getScore().getWins();
			}
		},
		LOSE("lose") {
			@Override
			public int getValue(AbstractClientPlayer player) {
				return player.getScore().getLoses();
			}
		},
		DRAW("draw") {
			@Override
			public int getValue(AbstractClientPlayer player) {
				return player.getScore().getDraws();
			}
		},
		SCORE("score") {
			@Override
			public int getValue(AbstractClientPlayer player) {
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
		
		public abstract int getValue(AbstractClientPlayer player);
		
		@Override
		public String toString() {
			return this.name;
		}
		
	}
	
}
