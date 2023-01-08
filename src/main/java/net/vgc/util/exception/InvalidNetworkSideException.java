package net.vgc.util.exception;

import net.vgc.network.Network;
import net.vgc.network.NetworkSide;

import java.io.Serial;

/**
 *
 * @author Luis-st
 *
 */

public class InvalidNetworkSideException extends RuntimeException {
	
	@Serial
	private static final long serialVersionUID = -6784865518985390672L;
	
	public InvalidNetworkSideException() {
		super();
	}
	
	public InvalidNetworkSideException(NetworkSide networkSide) {
		super("Invalid network side for " + networkSide + " since the current network side is " + Network.INSTANCE.getNetworkSide());
	}
	
	public InvalidNetworkSideException(NetworkSide networkSide, Throwable cause) {
		super("Invalid network side for " + networkSide + " since the current network side is " + Network.INSTANCE.getNetworkSide(), cause);
	}
	
}
