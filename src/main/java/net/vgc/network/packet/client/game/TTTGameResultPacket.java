package net.vgc.network.packet.client.game;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.game.GameResult;
import net.vgc.games.ttt.TTTResultLine;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;

public class TTTGameResultPacket implements ClientPacket {
	
	private final GameResult result;
	private final TTTResultLine resultLine;
	
	public TTTGameResultPacket(GameResult result, TTTResultLine resultLine) {
		this.result = result;
		this.resultLine = resultLine;
	}
	
	public TTTGameResultPacket(FriendlyByteBuffer buffer) {
		this.result = buffer.readEnum(GameResult.class);
		TTTResultLine resultLine = buffer.read(TTTResultLine.class);
		this.resultLine = resultLine.equals(TTTResultLine.EMPTY) ? TTTResultLine.EMPTY : resultLine;
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeEnum(this.result);
		buffer.write(this.resultLine);
	}

	@Override
	public void handle(ClientPacketListener listener) {
		
	}
	
	public GameResult getResult() {
		return this.result;
	}
	
	public TTTResultLine getResultLine() {
		return this.resultLine;
	}

}
