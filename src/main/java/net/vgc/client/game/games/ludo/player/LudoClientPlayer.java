package net.vgc.client.game.games.ludo.player;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.vgc.client.game.games.ludo.LudoClientGame;
import net.vgc.client.game.games.ludo.player.figure.LudoClientFigure;
import net.vgc.client.game.player.ClientGamePlayer;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.game.games.ludo.map.field.LudoFieldPos;
import net.vgc.game.games.ludo.player.LudoPlayerType;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.client.ClientPacket;

public class LudoClientPlayer implements ClientGamePlayer, PacketHandler<ClientPacket> {
	
	protected final LudoClientGame game;
	protected final AbstractClientPlayer player;
	protected final LudoPlayerType playerType;
	protected final List<LudoClientFigure> figures;
	
	public LudoClientPlayer(LudoClientGame game, AbstractClientPlayer player, LudoPlayerType playerType, List<UUID> uuids) {
		this.game = game;
		this.player = player;
		this.playerType = playerType;
		this.figures = createFigures(this, this.playerType, uuids);
	}
	
	protected static List<LudoClientFigure> createFigures(LudoClientPlayer player, LudoPlayerType type, List<UUID> uuids) {
		List<LudoClientFigure> figures = Lists.newArrayList();
		for (int i = 0; i < uuids.size(); i++) {
			figures.add(new LudoClientFigure(player, i, uuids.get(i)));
		}
		return figures;
	}
	
	@Override
	public LudoClientGame getGame() {
		return this.game;
	}

	@Override
	public AbstractClientPlayer getPlayer() {
		return this.player;
	}

	@Override
	public LudoPlayerType getPlayerType() {
		return this.playerType;
	}

	@Override
	public List<LudoClientFigure> getFigures() {
		return this.figures;
	}
	
	@Nullable
	@Override
	public LudoClientFigure getFigure(int figure) {
		return this.figures.get(figure);
	}

	@Override
	public List<LudoFieldPos> getWinPoses() {
		return Lists.newArrayList(LudoFieldPos.of(0), LudoFieldPos.of(1), LudoFieldPos.of(2), LudoFieldPos.of(3));
	}
	
	@Override
	public void handlePacket(ClientPacket clientPacket) {
		ClientGamePlayer.super.handlePacket(clientPacket);
		
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof LudoClientPlayer player) {
			if (!this.game.equals(player.game)) {
				return false;
			} else if (!this.player.equals(player.player)) {
				return false;
			} else if (!this.playerType.equals(player.playerType)) {
				return false;
			} else {
				return this.figures.equals(player.figures);
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("LudoClientPlayer{");
		builder.append("game=").append(this.game).append(",");
		builder.append("player=").append(this.player).append(",");
		builder.append("playerType=").append(this.playerType).append(",");
		builder.append("figures=").append(this.figures).append("}");
		return builder.toString();
	}
	
}
