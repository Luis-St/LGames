package net.vgc.game.action.type;

import java.util.function.Function;

import net.vgc.game.action.GameActionHandleType;
import net.vgc.game.action.SimpleAction;
import net.vgc.game.action.data.GameActionData;
import net.vgc.network.NetworkDirection;
import net.vgc.network.buffer.FriendlyByteBuffer;

/**
 *
 * @author Luis-st
 *
 */

public class SimpleGameActionType<T extends GameActionData> extends GameActionType<SimpleAction<T>, T> {
	
	private SimpleGameActionType(String name, int id, GameActionHandleType handleType, NetworkDirection networkDirection, Function<FriendlyByteBuffer, T> dataFactory) {
		super(name, id, handleType, networkDirection, SimpleAction::new, dataFactory);
	}
	
	public static <T extends GameActionData> SimpleGameActionType<T> toServer(String name, int id, GameActionHandleType handleType, Function<FriendlyByteBuffer, T> dataFactory) {
		return new SimpleGameActionType<>(name, id, handleType, NetworkDirection.CLIENT_TO_SERVER, dataFactory);
	}
	
	public static <T extends GameActionData> SimpleGameActionType<T> toClient(String name, int id, GameActionHandleType handleType, Function<FriendlyByteBuffer, T> dataFactory) {
		return new SimpleGameActionType<>(name, id, handleType, NetworkDirection.SERVER_TO_CLIENT, dataFactory);
	}
	
}
