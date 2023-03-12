package net.luis.network;

import io.netty.channel.Channel;
import net.luis.network.instance.ClientInstance;
import net.luis.network.packet.Packet;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 *
 * @author Luis-st
 *
 */

public class SecondaryConnection {

	private final ClientInstance instance;
	private Connection connection;
	
	public SecondaryConnection(@NotNull Supplier<Packet> handshake) {
		this.instance = new ClientInstance(this::createConnection, handshake);
	}
	
	private Connection createConnection(Channel channel) {
		this.connection = new Connection(channel);
		return this.connection;
	}
	
	public void open(String host, int port) {
		this.instance.open(host, port);
	}
	
	public void send(@NotNull Packet packet) {
		if (this.connection == null) {
			throw new IllegalStateException("Connection has not been created yet");
		} else {
			this.connection.send(packet);
		}
	}
	
	public void close() {
		this.connection.close();
		this.instance.close();
	}
	
}
