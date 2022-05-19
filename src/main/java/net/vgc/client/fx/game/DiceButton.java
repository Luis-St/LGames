package net.vgc.client.fx.game;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import net.vgc.Constans;
import net.vgc.client.Client;
import net.vgc.client.fx.Box;
import net.vgc.game.DiceState;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.network.packet.client.game.CancelRollDiceRequestPacket;
import net.vgc.network.packet.client.game.RolledDicePacket;
import net.vgc.network.packet.server.game.RollDiceRequestPacket;

public class DiceButton extends Button implements PacketHandler<ClientPacket> {
	
	protected final Client client;
	protected final double prefSize;
	protected DiceState state;
	
	public DiceButton(Client client, double prefSize) {
		this.client = client;
		this.prefSize = prefSize;
		this.init();
	}
	
	protected void init() {
		this.setPrefSize(this.prefSize, this.prefSize);
		this.setState(DiceState.ZERO);
		if (!Constans.DEBUG) {
			this.setBackground(null);
		}
		this.setOnAction((event) -> {
			this.client.getServerHandler().send(new RollDiceRequestPacket(this.client.getPlayer().getProfile()));
		});
	}
	
	public void setState(DiceState state) {
		this.state = state;
		ImageView image = this.state.getImage(this.prefSize * 0.9, this.prefSize * 0.9);
		if (image != null) {
			this.setGraphic(new Box<>(image, Pos.CENTER));
		} else {
			this.setGraphic(null);
		}
	}

	@Override
	public void handlePacket(ClientPacket clientPacket) {
		if (clientPacket instanceof CancelRollDiceRequestPacket packet) {
			this.setState(DiceState.ZERO);
		} else if (clientPacket instanceof RolledDicePacket packet) {
			this.setState(DiceState.fromCount(packet.getCount()));
		}
	}
	
}
