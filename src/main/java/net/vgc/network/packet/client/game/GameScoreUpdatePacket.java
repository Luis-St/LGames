package net.vgc.network.packet.client.game;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.oldgame.score.GameScore;

public class GameScoreUpdatePacket implements ClientPacket {
	
	protected final GameScore score;
	
	public GameScoreUpdatePacket(GameScore score) {
		this.score = score;
	}
	
	public GameScoreUpdatePacket(FriendlyByteBuffer buffer) {
		this.score = buffer.read(GameScore.class);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.write(this.score);
	}

	@Override
	public void handle(ClientPacketListener listener) {
		
	}
	
	public GameScore getScore() {
		return this.score;
	}

}
