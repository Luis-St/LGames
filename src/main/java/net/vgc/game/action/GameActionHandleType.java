package net.vgc.game.action;

import org.jetbrains.annotations.Nullable;

import net.vgc.game.action.handler.GameActionHandler;

/**
 *
 * @author Luis-st
 *
 */

public enum GameActionHandleType {
	
	GLOBAL() {
		@Override
		public boolean handle(GameAction<?> action, GameActionHandler specificHandler, GameActionHandler globalHandler) {
			return globalHandler.handle(action);
		}
	},
	SPECIFIC() {
		@Override
		public boolean handle(GameAction<?> action, GameActionHandler specificHandler, GameActionHandler globalHandler) {
			if (specificHandler != null) {
				return specificHandler.handle(action);
			}
			return false;
		}
	},
	BOTH() {
		@Override
		public boolean handle(GameAction<?> action, GameActionHandler specificHandler, GameActionHandler globalHandler) {
			boolean flag = globalHandler.handle(action);
			if (specificHandler != null) {
				flag |= specificHandler.handle(action);
			}
			return flag;
		}
	};
	
	public abstract boolean handle(GameAction<?> action, @Nullable GameActionHandler specificHandler, GameActionHandler globalHandler);
	
}
