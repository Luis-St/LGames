package net.luis.network.packet.listener;

import net.luis.network.NetworkSide;

import java.lang.annotation.*;

/**
 *
 * @author Luis-st
 *
 */

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PacketSubscriber {
	
	NetworkSide[] value() default {
			NetworkSide.CLIENT, NetworkSide.SERVER, NetworkSide.ACCOUNT
	};
	
	String getter() default "";
	
}
