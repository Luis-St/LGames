package net.vgc.client.screen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.Main;
import net.vgc.client.Client;
import net.vgc.client.fx.InputHandler;
import net.vgc.client.fx.ScreenScene;
import net.vgc.client.fx.Showable;
import net.vgc.language.TranslationKey;
import net.vgc.util.Tickable;

public abstract class Screen implements Showable, Tickable, InputHandler {
	
	protected static final Logger LOGGER = LogManager.getLogger(Main.class);
	
	protected final Client client;
	public String title = TranslationKey.createAndGet("client.constans.name");
	public int width = 400;
	public int height = 400;
	public boolean shouldCenter = false;
	
	public Screen() {
		this.client = Client.getInstance();
	}
	
	public void init() {
		
	}
	
	@Override
	public void tick() {
		
	}
	
	@Override
	public abstract ScreenScene show();
	
}
