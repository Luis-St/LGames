package net.luis.utility.settings;


/*@Deprecated
public abstract class AbstractSettings implements Serializable {
	
	protected static final Logger LOGGER = LogManager.getLogger(AbstractSettings.class);
	
	private final List<Setting<?>> settings;
	
	protected AbstractSettings(CompoundTag tag) {
		this(TagUtils.readList(tag.getList("Settings", Tag.COMPOUND_TAG), (settingTag) -> {
			return new Setting<>((CompoundTag) settingTag);
		}));
	}
	
	protected AbstractSettings(List<Setting<?>> settings) {
		this.settings = settings;
		this.init();
	}
	
	protected abstract void init();
	
	protected final void register(Setting<?> setting) {
		if (!this.settings.contains(setting)) {
			this.settings.add(setting);
		}
	}
	
	protected final <T> void addListener(Setting<T> setting, BiConsumer<T, T> listener) {
		if (this.settings.contains(setting)) {
			setting.addListener(listener);
		}
	}
	
	public List<Setting<?>> getSettings() {
		return this.settings;
	}
	
	@Override
	public CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		tag.putList("Settings", TagUtils.writeList(this.settings, Setting::serialize));
		return tag;
	}
	
}*/
