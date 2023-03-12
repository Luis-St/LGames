package net.luis.game.application;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Luis-st
 *
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface Application {
	
	@NotNull ApplicationType value();
	
}
