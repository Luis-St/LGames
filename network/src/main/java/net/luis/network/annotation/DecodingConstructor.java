package net.luis.network.annotation;

import java.lang.annotation.*;

/**
 *
 * @author Luis-st
 *
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface DecodingConstructor {
	
}