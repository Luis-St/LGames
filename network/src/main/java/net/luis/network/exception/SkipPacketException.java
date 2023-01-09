package net.luis.network.exception;

import io.netty.handler.codec.EncoderException;

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
	
	public SkipPacketException(String message) {
		super(message);
	}
	
	public SkipPacketException(Throwable cause) {
		super(cause);
	}
	
	public SkipPacketException(String message, Throwable cause) {
		super(message, cause);
	}
	
}