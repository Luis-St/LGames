package net.vgc.network;

public class InvalidNetworkSideException extends RuntimeException {
	
	private static final long serialVersionUID = -6784865518985390672L;
	
	public InvalidNetworkSideException() {
		super();
	}
    
	public InvalidNetworkSideException(NetworkSide networkSide) {
		super("Invalid NetworkSide for " + networkSide + " since the current NetworkSide is " + Network.INSTANCE.getNetworkSide());
	}

	public InvalidNetworkSideException(NetworkSide networkSide, Throwable cause) {
		super("Invalid NetworkSide for " + networkSide + " since the current NetworkSide is " + Network.INSTANCE.getNetworkSide(), cause);
	}

}
