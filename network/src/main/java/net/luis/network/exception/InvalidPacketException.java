package net.luis.network.exception;

import org.jetbrains.annotations.NotNull;

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
	
	public InvalidPacketException(@NotNull String message) {
		super(message);
	}
	
	public InvalidPacketException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause);
	}
	
}
