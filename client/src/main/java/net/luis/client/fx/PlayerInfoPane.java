package net.luis.client.fx;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import net.luis.Constants;
import net.luis.fxutils.FxUtils;
import net.luis.game.Game;
import net.luis.game.player.GamePlayer;
import net.luis.language.TranslationKey;
import net.luis.utility.Util;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class PlayerInfoPane extends GridPane {
	
	private final Game game;
	private final double separatorLength;
	private final Text currentPlayerInfo;
	private final PlayerScorePane scorePane;
	
	public PlayerInfoPane(Game game, double separatorLength, PlayerScorePane.Type scoreType) {
		this.game = game;
		this.separatorLength = separatorLength;
		this.currentPlayerInfo = new Text(TranslationKey.createAndGet("screen.tic_tac_toe.no_current_player"));
		this.scorePane = new PlayerScorePane(this.game, scoreType);
		this.init();
	}
	
	private void init() {
		this.setAlignment(Pos.CENTER);
		this.setVgap(10.0);
		this.setHgap(10.0);
		this.setGridLinesVisible(Constants.DEBUG);
		this.add(this.makePlayerInfoPane(), 0, 0);
		this.add(this.makeSeparator(), 0, 1);
		this.add(this.makeCurrentPlayerPane(), 0, 2);
		this.add(this.makeSeparator(), 0, 3);
		this.add(this.makePlayersPane(), 0, 4);
		this.add(this.makeSeparator(), 0, 5);
		this.add(this.scorePane, 0, 6);
	}
	
	private Separator makeSeparator() {
		Separator separator = new Separator(Orientation.HORIZONTAL);
		separator.setPrefWidth(this.separatorLength);
		return separator;
	}
	
	private String getName(GamePlayer player) {
		return player.getPlayer().getProfile().getName();
	}
	
	private GridPane makePlayerInfoPane() {
		GridPane pane = FxUtils.makeGrid(Pos.CENTER, 0.0, 5.0);
		pane.add(new Text(TranslationKey.createAndGet("screen.tic_tac_toe.player_info")), 0, 0);
		return pane;
	}
	
	private GridPane makeCurrentPlayerPane() {
		GridPane pane = FxUtils.makeGrid(Pos.CENTER, 5.0, 5.0);
		pane.add(this.currentPlayerInfo, 0, 0);
		return pane;
	}
	
	private GridPane makePlayersPane() {
		GridPane pane = FxUtils.makeGrid(Pos.CENTER, 5.0, 5.0);
		int i = 0;
		for (GamePlayer player : this.game.getPlayers()) {
			pane.add(new Text(player.getPlayerType().getTranslation().getValue(this.getName(player))), 0, i++);
		}
		return pane;
	}
	
	public void update() {
		Util.runDelayed("DelayedPlayerInfoUpdate", 500, () -> {
			this.currentPlayerInfo.setText(TranslationKey.createAndGet("screen.tic_tac_toe.current_player", this.getName(Objects.requireNonNull(this.game.getPlayer()))));
			this.scorePane.update();
		});
	}
	
}
