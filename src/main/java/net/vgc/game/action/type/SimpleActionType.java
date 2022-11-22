package net.vgc.game.action.type;

import java.util.function.Function;

import net.vgc.game.action.SimpleAction;
import net.vgc.game.action.data.ActionData;
import net.vgc.network.NetworkDirection;
import net.vgc.network.buffer.FriendlyByteBuffer;

public class SimpleActionType<T extends ActionData> extends ActionType<SimpleAction<T>, T> {
	
	private SimpleActionType(String name, int id, NetworkDirection networkDirection, Function<FriendlyByteBuffer, T> dataFactory) {
		super(name, id, networkDirection, SimpleAction::new, dataFactory);
	}
	
	public static <T extends ActionData> SimpleActionType<T> toServer(String name, int id, Function<FriendlyByteBuffer, T> dataFactory) {
		return new SimpleActionType<>(name, id, NetworkDirection.CLIENT_TO_SERVER, dataFactory);
	}
	
	public static <T extends ActionData> SimpleActionType<T> toClient(String name, int id, Function<FriendlyByteBuffer, T> dataFactory) {
		return new SimpleActionType<>(name, id, NetworkDirection.SERVER_TO_CLIENT, dataFactory);
	}
	
}
