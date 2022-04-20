package net.vgc.server.dedicated;

import java.nio.file.Path;

import net.vgc.server.players.PlayerList;

public class DedicatedPlayerList extends PlayerList {

	public DedicatedPlayerList(DedicatedServer server, Path playerDirectory) {
		super(server, playerDirectory);
	}
	
}
