package net.vgc.game.action.type;

import net.vgc.game.action.data.gobal.EmptyData;
import net.vgc.game.action.data.gobal.IntegerData;
import net.vgc.game.action.data.gobal.ProfileData;
import net.vgc.game.action.data.specific.FieldInfoData;
import net.vgc.game.action.data.specific.PlayRequestData;
import net.vgc.game.action.data.specific.SelectFieldData;
import net.vgc.game.action.data.specific.StartGameData;
import net.vgc.game.action.data.specific.SyncPlayerData;

public class ActionTypes {
	
	public static final SimpleActionType<ProfileData> DICE_REQUEST = SimpleActionType.toServer("dice_request", 0, ProfileData::new);
	public static final SimpleActionType<ProfileData> EXIT_GAME_REQUEST = SimpleActionType.toServer("exit_game_request", 1, ProfileData::new);
	public static final SimpleActionType<ProfileData> PLAY_AGAIN_REQUEST = SimpleActionType.toServer("play_again_request", 2, ProfileData::new);
	public static final SimpleActionType<SelectFieldData> SELECT_FIELD = SimpleActionType.toServer("select_field", 3, SelectFieldData::new);
	public static final SimpleActionType<PlayRequestData> PLAY_REQUEST = SimpleActionType.toServer("play_request", 4, PlayRequestData::new);
	
	public static final SimpleActionType<FieldInfoData> UPDATE_MAP = SimpleActionType.toClient("update_map", 5, FieldInfoData::new);
	public static final SimpleActionType<ProfileData> UPDATE_CURRENT_PLAYER = SimpleActionType.toClient("update_current_player", 6, ProfileData::new);
	public static final SimpleActionType<EmptyData> EXIT_GAME = SimpleActionType.toClient("exit_game", 7, EmptyData::new);
	public static final SimpleActionType<EmptyData> STOP_GAME = SimpleActionType.toClient("stop_game", 8, EmptyData::new);
	public static final SimpleActionType<EmptyData> CAN_SELECT_FIELD = SimpleActionType.toClient("can_select_field", 9, EmptyData::new);
	public static final SimpleActionType<StartGameData> START_GAME = SimpleActionType.toClient("start_game", 10, StartGameData::new);
	public static final SimpleActionType<EmptyData> CANCEL_PLAY_REQUEST = SimpleActionType.toClient("cancel_play_request", 11, EmptyData::new);
	public static final SimpleActionType<EmptyData> CANCEL_PLAY_AGAIN_REQUEST = SimpleActionType.toClient("cancel_play_again_request", 12, EmptyData::new);
	public static final SimpleActionType<IntegerData> ROLLED_DICE = SimpleActionType.toClient("rolled_dice", 13, IntegerData::new);
	public static final SimpleActionType<EmptyData> CAN_ROLL_DICE_AGAIN = SimpleActionType.toClient("can_roll_dice_again", 14, EmptyData::new);
	public static final SimpleActionType<EmptyData> CANCEL_ROLL_DICE_REQUEST = SimpleActionType.toClient("cancel_roll_dice_request", 15, EmptyData::new);
	public static final SimpleActionType<SyncPlayerData> SYNC_PLAYER_DATA = SimpleActionType.toClient("sync_player_data", 16, SyncPlayerData::new);
	
}
