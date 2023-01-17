package net.luis.client.fx.game;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import net.luis.Constants;
import net.luis.client.Client;
import net.luis.client.fx.Box;
import net.luis.client.player.LocalPlayer;
import net.luis.game.dice.DiceRenderState;
import net.luis.network.packet.server.game.dice.RollDiceRequestPacket;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class DiceButton extends Button {
	
	private final Client client;
	private final double prefSize;
	private int count = 0;
	
	public DiceButton(Client client, double prefSize) {
		this.client = client;
		this.prefSize = prefSize;
		this.init();
	}
	
	private void init() {
		this.setPrefSize(this.prefSize, this.prefSize);
		this.updateState();
		if (!Constants.DEBUG) {
			this.setBackground(null);
		}
		this.setOnAction((event) -> {
			LocalPlayer player = this.client.getPlayer();
			if (player.isCurrent() && player.canRollDice()) {
				this.client.getServerHandler().send(new RollDiceRequestPacket(this.client.getPlayer().getProfile()));
			}
		});
	}
	
	private void updateState() {
		ImageView image = Objects.requireNonNull(DiceRenderState.fromCount(this.count)).getImage(this.prefSize * 0.9, this.prefSize * 0.9);
		if (image != null) {
			this.setGraphic(new Box<>(image, Pos.CENTER));
		} else {
			this.setGraphic(null);
		}
	}
	
	public int getCount() {
		return this.count;
	}
	
	public void setCount(int count) {
		this.count = count;
		this.updateState();
	}
	
}
