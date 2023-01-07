package net.vgc.client;

import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public record ClientAccount(String name, int id, String mail, UUID uuid, boolean guest) {
	
}
