package net.vgc.client;

import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public record ClientAccount(String name, int id, String mail, UUID uuid) {
	
	public boolean isGuest() {
		return this.mail.equals("guest@vgc.net");
	}
	
}
