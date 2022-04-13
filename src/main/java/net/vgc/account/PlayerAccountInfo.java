package net.vgc.account;

import net.vgc.common.InfoResult;

public record PlayerAccountInfo(InfoResult infoResult, PlayerAccount account) {
	
	public boolean isSuccess() {
		return this.infoResult.isSuccess() && this.account != PlayerAccount.UNKNOWN;
	}
	
}
