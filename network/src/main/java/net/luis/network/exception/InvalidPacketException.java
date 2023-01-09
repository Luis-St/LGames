package net.luis.network.exception;

import java.io.Serial;

/**
 *
 * @author Luis-st
 *
 */

public class InvalidPacketException extends RuntimeException {
	
	@Serial
	private static final long serialVersionUID = 443649863499410053L;
	
	public InvalidPacketException() {
		super();
	}
	
	public InvalidPacketException(String message) {
		super(message);
	}
	
	public InvalidPacketException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
