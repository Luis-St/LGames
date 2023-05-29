package net.luis.server.players;

import com.google.common.collect.Lists;
import net.luis.game.application.FxApplication;
import net.luis.game.player.GameProfile;
import net.luis.game.player.Player;
import net.luis.game.players.AbstractPlayerList;
import net.luis.netcore.packet.Packet;
import net.luis.server.Server;
import net.luis.utility.Util;
import net.luis.utils.util.Utils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class ServerPlayerList extends AbstractPlayerList {
	
	private static final Logger LOGGER = LogManager.getLogger(ServerPlayerList.class);
	
	private final MutableObject<UUID> admin = new MutableObject<>(Utils.EMPTY_UUID);
	
	public ServerPlayerList(FxApplication application) {
		super(application);
	}
	
	public @NotNull UUID getAdminUUID() {
		return this.admin.getValue();
	}
	
	public void setAdminUUID(UUID admin) {
		this.admin.setValue(Objects.requireNonNull(admin));
	}
	
	private void refresh() {
		Server.getInstance().getScreen().refresh();
	}
	
	//region Adding players
	@Override
	public void addPlayer(Player player) {
		super.addPlayer(player);
		this.broadcastAllExclude(new PlayerAddPacket(player.getProfile()), player);
		if (this.getAdminUUID().equals(player.getProfile().getUniqueId())) {
			if (!Utils.isEmpty(this.getAdminUUID())) {
				throw new IllegalStateException("Multiple admins not allowed on the server");
			} else {
				this.setAdminUUID(player.getProfile().getUniqueId());
				Util.runDelayed("PacketSendDelay", 250, () -> {
					this.broadcastAll(new SyncPermissionPacket(player.getProfile()));
				});
				LOGGER.info("Admin {} joined the server", player.getProfile().getName());
			}
		} else if (!Utils.isEmpty(this.getAdminUUID())) {
			Util.runDelayed("DelayedPacketSender", 250, () -> {
				this.broadcast(player, new SyncPermissionPacket(Objects.requireNonNull(this.getPlayer(this.getAdminUUID())).getProfile()));
			});
		}
		this.refresh();
	}
	//endregion
	
	//region Removing players
	@Override
	public void removePlayer(Player player) {
		super.removePlayer(player);
		this.broadcastAll(new PlayerRemovePacket(player.getProfile()));
		if (this.getAdminUUID().equals(player.getProfile().getUniqueId())) {
			this.broadcastAll(new SyncPermissionPacket(GameProfile.EMPTY));
			this.setAdminUUID(Utils.EMPTY_UUID);
			LOGGER.info("Admin {} left the server", player.getProfile().getName());
		}
		this.refresh();
	}
	
	@Override
	public void removeAllPlayers() {
		this.broadcastAll(new ServerClosedPacket());
		this.players.clear();
		this.refresh();
	}
	//endregion
	
	public Player getAdmin() {
		if (Utils.isEmpty(this.getAdminUUID())) {
			return null;
		}
		return this.getPlayer(this.getAdminUUID());
	}
	
	//region Broadcast to players
	public void broadcast(Player player, Packet packet) {
		Connection connection = player.getConnection();
		if (connection.isConnected()) {
			connection.send(packet);
		} else if (this.getPlayer(player.getProfile().getUniqueId()) == null) {
			LOGGER.warn("Cannot send packets of type {} to player {} because the connection is closed", packet.getClass().getSimpleName(), player.getProfile().getName());
		} else {
			LOGGER.debug("The request to send a packet of type {} to player {} was canceled because the player left the server a few seconds ago", packet.getClass().getSimpleName(), player.getProfile().getName());
		}
	}
	
	public void broadcastAll(Packet packet) {
		for (Player player : this.getPlayers()) {
			this.broadcast(player, packet);
		}
	}
	
	public void broadcastAll(List<Player> players, Packet packet) {
		for (Player player : Objects.requireNonNull(players, "Players must not be null")) {
			this.broadcast(player, packet);
		}
	}
	
	public void broadcastAllExclude(Packet packet, Player... players) {
		List<Player> excluded = Lists.newArrayList(players);
		for (Player player : this.getPlayers()) {
			if (!excluded.contains(player)) {
				this.broadcast(player, packet);
			}
		}
	}
	//endregion
}
