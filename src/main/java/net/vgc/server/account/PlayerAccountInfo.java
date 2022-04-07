package net.vgc.server.account;

import net.vgc.common.InfoResult;

public record PlayerAccountInfo(InfoResult info, PlayerAccount account) {
	
	public boolean isSuccess() {
		return this.info.isSuccess() && account != PlayerAccount.UNKNOWN;
	}
	
}
