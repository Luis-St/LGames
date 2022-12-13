package net.vgc.network.packet.listener;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.vgc.network.NetworkSide;

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
