package net.vgc.util.exception;

public class InvalidValueException extends RuntimeException {
	
	private static final long serialVersionUID = -214146787383637863L;
	
	public InvalidValueException(String message) {
		super(message);
	}
	
	public InvalidValueException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
