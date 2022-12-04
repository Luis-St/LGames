package net.vgc.game.action.type;

import static net.vgc.game.action.GameActionHandleType.BOTH;
import static net.vgc.game.action.GameActionHandleType.GLOBAL;
import static net.vgc.game.action.GameActionHandleType.SPECIFIC;

import net.vgc.game.action.data.gobal.EmptyData;
import net.vgc.game.action.data.gobal.IntegerData;
import net.vgc.game.action.data.gobal.ProfileData;
import net.vgc.game.action.data.specific.FieldInfoData;
import net.vgc.game.action.data.specific.GameResultData;
import net.vgc.game.action.data.specific.SelectFieldData;
import net.vgc.game.action.data.specific.StartGameData;
import net.vgc.game.action.data.specific.SyncPlayerData;

public class GameActionTypes {
	
	public static final SimpleGameActionType<ProfileData> DICE_REQUEST = SimpleGameActionType.toServer("dice_request", 0, GLOBAL, ProfileData::new);
	public static final SimpleGameActionType<ProfileData> EXIT_GAME_REQUEST = SimpleGameActionType.toServer("exit_game_request", 1, GLOBAL, ProfileData::new);
	public static final SimpleGameActionType<ProfileData> PLAY_AGAIN_REQUEST = SimpleGameActionType.toServer("play_again_request", 2, GLOBAL, ProfileData::new);
	public static final SimpleGameActionType<SelectFieldData> SELECT_FIELD = SimpleGameActionType.toServer("select_field", 3, SPECIFIC, SelectFieldData::new);
	
	public static final SimpleGameActionType<FieldInfoData> UPDATE_MAP = SimpleGameActionType.toClient("update_map", 4, SPECIFIC, FieldInfoData::new);
	public static final SimpleGameActionType<ProfileData> UPDATE_CURRENT_PLAYER = SimpleGameActionType.toClient("update_current_player", 5, GLOBAL, ProfileData::new);
	public static final SimpleGameActionType<EmptyData> EXIT_GAME = SimpleGameActionType.toClient("exit_game", 6, GLOBAL, EmptyData::new);
	public static final SimpleGameActionType<EmptyData> STOP_GAME = SimpleGameActionType.toClient("stop_game", 7, GLOBAL, EmptyData::new);
	public static final SimpleGameActionType<EmptyData> ACTION_FAILED = SimpleGameActionType.toClient("action_failed", 7, GLOBAL, EmptyData::new);
	public static final SimpleGameActionType<EmptyData> CAN_SELECT_FIELD = SimpleGameActionType.toClient("can_select_field", 8, GLOBAL, EmptyData::new);
	public static final SimpleGameActionType<StartGameData> START_GAME = SimpleGameActionType.toClient("start_game", 9, GLOBAL, StartGameData::new);
	public static final SimpleGameActionType<EmptyData> CANCEL_PLAY_REQUEST = SimpleGameActionType.toClient("cancel_play_request", 10, GLOBAL, EmptyData::new);
	public static final SimpleGameActionType<EmptyData> CANCEL_PLAY_AGAIN_REQUEST = SimpleGameActionType.toClient("cancel_play_again_request", 11, GLOBAL, EmptyData::new);
	public static final SimpleGameActionType<IntegerData> ROLLED_DICE = SimpleGameActionType.toClient("rolled_dice", 12, GLOBAL, IntegerData::new);
	public static final SimpleGameActionType<EmptyData> CAN_ROLL_DICE_AGAIN = SimpleGameActionType.toClient("can_roll_dice_again", 13, GLOBAL, EmptyData::new);
	public static final SimpleGameActionType<EmptyData> CANCEL_ROLL_DICE_REQUEST = SimpleGameActionType.toClient("cancel_roll_dice_request", 14, GLOBAL, EmptyData::new);
	public static final SimpleGameActionType<SyncPlayerData> SYNC_PLAYER = SimpleGameActionType.toClient("sync_player", 15, GLOBAL, SyncPlayerData::new);
	public static final SimpleGameActionType<GameResultData> GAME_RESULT = SimpleGameActionType.toClient("game_result", 16, BOTH, GameResultData::new);
	
}
