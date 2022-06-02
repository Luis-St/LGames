package net.vgc.network.buffer;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.Lists;

import net.vgc.game.games.ludo.map.field.LudoFieldPos;
import net.vgc.game.games.ttt.map.field.TTTFieldPos;
import net.vgc.util.Util;

public interface Decodable {
	
	public static final int LUDO_FIELD_POS = 0;
	public static final int TTT_FIELD_POS = 1;
	
	public static final List<Function<FriendlyByteBuffer, Decodable>> DECODER = Util.make(Lists.newArrayList(), (list) -> {
		list.add(LudoFieldPos::new);
		list.add(TTTFieldPos::new);
	});
	
	int getDecoderId();
	
	public static Function<FriendlyByteBuffer, Decodable> getDecoder(int id) {
		return DECODER.get(id);
	}
	
}
