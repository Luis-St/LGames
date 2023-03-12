package net.luis.network.exception;

import io.netty.handler.codec.EncoderException;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;

/**
 *
 * @author Luis-st
 *
 */

public class SkipPacketException extends EncoderException {
	
	@Serial
	private static final long serialVersionUID = 182028572541416074L;
	
	public SkipPacketException() {
		
	}
	
	public SkipPacketException(@NotNull String message) {
		super(message);
	}
	
	public SkipPacketException(@NotNull Throwable cause) {
		super(cause);
	}
	
	public SkipPacketException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause);
	}
	
}