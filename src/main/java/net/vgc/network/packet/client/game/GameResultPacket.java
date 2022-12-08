package net.vgc.network.packet.client.game;

import org.jetbrains.annotations.Nullable;

import net.luis.utils.util.ReflectionHelper;
import net.vgc.client.network.ClientPacketHandler;
import net.vgc.game.GameResult;
import net.vgc.game.win.GameResultLine;
import net.vgc.network.buffer.Encodable;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;

/**
 *
 * @author Luis-st
 *
 */

public class GameResultPacket implements ClientPacket {
	
	private final GameResult result;
	private final Encodable object;
	
	public GameResultPacket(GameResult result, Encodable object) {
		this.result = result;
		this.object = object;
	}
	
	@SuppressWarnings("unchecked")
	public GameResultPacket(FriendlyByteBuffer buffer) {
		this.result = buffer.readEnum(GameResult.class);
		boolean nullObject = buffer.readBoolean();
		if (!nullObject) {
			String clazz = buffer.readString();
			this.object = buffer.read((Class<Encodable>) ReflectionHelper.getClassForName(clazz));
		} else {
			this.object = null;
		}
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeEnum(this.result);
		buffer.writeBoolean(this.object == null);
		if (this.object != null) {
			buffer.writeString(this.object.getClass().getName());
			buffer.write(this.object);
		}
	}
	
	@Override
	public void handle(ClientPacketHandler handler) {
		
	}
	
	public GameResult getResult() {
		return this.result;
	}
	
	public Encodable getObject() {
		return this.object;
	}
	
	@Nullable
	public GameResultLine getResultLine() {
		if (this.object instanceof GameResultLine resultLine) {
			return resultLine;
		}
		return null;
	}
	
}
