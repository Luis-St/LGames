package net.vgc.network.packet;

public class InvalidPacketException extends RuntimeException {

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
