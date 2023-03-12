package net.luis.game.application;

import javafx.application.Platform;
import javafx.stage.Stage;
import net.luis.fx.ScreenScene;
import net.luis.fx.screen.AbstractScreen;
import net.luis.game.resources.ResourceManager;
import net.luis.network.instance.NetworkInstance;
import net.luis.utility.data.DataHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 *
 * @author Luis-st
 *
 */

public abstract class FxApplication implements Runnable, DataHandler {
	
	private static final Logger LOGGER = LogManager.getLogger(FxApplication.class);
	private static FxApplication instance;
	
	public static FxApplication getInstance() {
		return instance;
	}
	
	private final ApplicationType type;
	private final ResourceManager resourceManager;
	private final Stage stage;
	private final AbstractScreen mainScreen;
	private final boolean dynamic;
	
	public FxApplication(ApplicationType type, ResourceManager resourceManager, Stage stage, AbstractScreen mainScreen, boolean dynamic) {
		this.type = type;
		this.resourceManager = resourceManager;
		this.stage = stage;
		this.mainScreen = mainScreen;
		this.dynamic = dynamic;
		instance = this;
	}
	
	@Override
	public abstract void load(@NotNull String[] args) throws Exception;
	
	@Override
	public abstract void run();
	
	public @NotNull ApplicationType getType() {
		return this.type;
	}
	
	public abstract @NotNull NetworkInstance getNetworkInstance();
	
	public @NotNull ResourceManager getResourceManager() {
		return this.resourceManager;
	}
	
	public @NotNull Stage getStage() {
		return this.stage;
	}
	
	public @NotNull AbstractScreen getScreen() {
		if (this.dynamic) {
			if (this.stage.getScene() instanceof ScreenScene scene) {
				return scene.getScreen();
			} else {
				throw new IllegalStateException("The current scene is not a ScreenScene");
			}
		} else {
			return this.mainScreen;
		}
	}
	
	public void setScreen(@NotNull AbstractScreen screen) {
		if (this.dynamic) {
			screen.init();
			Stage stage = this.stage;
			if (!screen.getTitle().isEmpty()) {
				stage.setTitle(screen.getTitle());
			} else {
				stage.setTitle("");
			}
			screen.show(stage);
		} else {
			throw new UnsupportedOperationException("It is not possible to update the screen of a non-dynamic FxManager");
		}
	}
	
	public void exit() {
		LOGGER.info("Exit {}", this.getType().getName());
		Platform.exit();
	}
	
	@Override
	public abstract void save() throws IOException;
	
}
