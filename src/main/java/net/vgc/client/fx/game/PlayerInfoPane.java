package net.vgc.client.fx.game;

import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import net.vgc.Constans;
import net.vgc.client.fx.FxUtil;
import net.vgc.client.game.ClientGame;
import net.vgc.client.game.player.ClientGamePlayer;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.language.TranslationKey;
import net.vgc.util.SimpleEntry;

public class PlayerInfoPane extends GridPane {
	
	protected final ClientGame game;
	protected final double separatorLength;
	protected final Text currentPlayerInfo;
	protected final List<Entry<ClientGamePlayer, Text>> playerScoreInfos;
	
	public PlayerInfoPane(ClientGame game, double separatorLength) {
		this.game = game;
		this.separatorLength = separatorLength;
		this.currentPlayerInfo = new Text(TranslationKey.createAndGet("screen.tic_tac_toe.no_current_player"));
		this.playerScoreInfos = Lists.newArrayList();
		this.init();
		this.update();
	}
	
	protected void init() {
		this.setAlignment(Pos.CENTER);
		this.setVgap(10.0);
		this.setHgap(10.0);
		this.setGridLinesVisible(Constans.DEBUG);
		this.add(new Text(TranslationKey.createAndGet("screen.tic_tac_toe.player_info")), 0, 0);
		this.add(this.makeSeparator(), 1, 0);
		this.add(this.makeCurrentPlayerPane(), 2, 0);
		this.add(this.makeSeparator(), 3, 0);
		this.add(this.makePlayersPane(), 4, 0);
		this.add(this.makeSeparator(), 5, 0);
		this.add(this.makePlayerScorePane(), 4, 0);
	}
	
	protected Separator makeSeparator() {
		Separator separator = new Separator(Orientation.HORIZONTAL);
		separator.setPrefWidth(this.separatorLength);
		return separator;
	}
	
	protected String getName(ClientGamePlayer player) {
		return player.getPlayer().getProfile().getName();
	}
	
	protected GridPane makeCurrentPlayerPane() {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 10.0);
		pane.add(this.currentPlayerInfo, 0, 0);
		return pane;
	}
	
	protected GridPane makePlayersPane() {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 10.0);
		int i = 0;
		for (ClientGamePlayer player : this.game.getPlayers()) {
			pane.add(new Text(player.getPlayerType().getTranslation().getValue(this.getName(player))), i++, 0);
		}
		return pane;
	}
	
	protected GridPane makePlayerScorePane() {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 10.0);
		int i = 0;
		for (ClientGamePlayer gamePlayer : this.game.getPlayers()) {
			AbstractClientPlayer player = gamePlayer.getPlayer();
			Text text = new Text(TranslationKey.createAndGet("screen.tic_tac_toe.player_score", player.getProfile().getName(), player.getScore().getWins()));
			pane.add(text, i++, 0);
			this.playerScoreInfos.add(new SimpleEntry<ClientGamePlayer, Text>(gamePlayer, text, true));
		}
		return pane;
	}
	
	public void update() {
		this.currentPlayerInfo.setText(TranslationKey.createAndGet("screen.tic_tac_toe.current_player", this.getName(this.game.getCurrentPlayer())));
		for (Entry<ClientGamePlayer, Text> entry : this.playerScoreInfos) {
			AbstractClientPlayer player = entry.getKey().getPlayer();
			entry.getValue().setText(TranslationKey.createAndGet("screen.tic_tac_toe.player_score", player.getProfile().getName(), player.getScore().getWins()));
		}
	}
	
}
