package net.vgc.client.screen.game;

import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.layout.Pane;
import net.vgc.client.fx.FxUtil;
import net.vgc.language.TranslationKey;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.network.packet.server.ExitGameRequestPacket;

public class TTTScreen extends GameScreen {
	
	public TTTScreen() {
		
	}
	
	@Override
	public void init() {
		
	}
	
	@Override
	public void handlePacket(ClientPacket clientPacket) {
		
	}
	
	@Override
	protected Menu createGameMenu() {
		Menu menu = new Menu(TranslationKey.createAndGet("screen.lobby.game"));
		CustomMenuItem leaveItem = new CustomMenuItem();
		leaveItem.setContent(FxUtil.makeButton(TranslationKey.createAndGet("screen.lobby.leave"), () -> {
			this.client.getServerHandler().send(new ExitGameRequestPacket(this.client.getPlayer().getGameProfile()));
		}));
		menu.getItems().add(leaveItem);
		return menu;
	}

	@Override
	protected Pane createGame() {
		return new Pane();
	}

}
