package net.vgc.util.exception;

import io.netty.handler.codec.EncoderException;

/**
 *
 * @author Luis-st
 *
 */

public class SkipPacketException extends EncoderException {
	
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