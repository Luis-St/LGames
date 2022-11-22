package net.vgc.account;

import java.util.UUID;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import net.luis.fxutils.FxUtils;
import net.luis.utils.data.tag.TagUtil;
import net.luis.utils.data.tag.tags.CompoundTag;
import net.vgc.Constans;
import net.vgc.data.serialization.Serializable;
import net.vgc.language.TranslationKey;
import net.vgc.network.NetworkSide;
import net.vgc.network.buffer.Encodable;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.util.Util;
import net.vgc.util.annotation.DecodingConstructor;

public final class PlayerAccount implements Encodable, Serializable {
	
	public static final PlayerAccount UNKNOWN = new PlayerAccount("Unknown", "unknown", Util.EMPTY_UUID, false);
	
	private final String name;
	private final String password;
	private final UUID uuid;
	private final boolean guest;
	private boolean taken;
	
	public PlayerAccount(String name, String password, UUID uuid, boolean guest) {
		this.name = name;
		this.password = password;
		this.uuid = uuid;
		this.guest = guest;
	}
	
	@DecodingConstructor
	private PlayerAccount(FriendlyByteBuffer buffer) {
		this.name = buffer.readString();
		this.password = buffer.readString();
		this.uuid = buffer.readUUID();
		this.guest = buffer.readBoolean();
	}
	
	public PlayerAccount(CompoundTag tag) {
		this.name = tag.getString("name");
		this.password = tag.getString("password");
		this.uuid = TagUtil.readUUID(tag.getCompound("uuid"));
		this.guest = tag.getBoolean("guest");
	}
	
	public TreeItem<String> display() {
		TreeItem<String> treeItem = new TreeItem<>(TranslationKey.createAndGet("account.window.account", this.name));
		treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("account.window.account_name", this.name)));
		treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("account.window.account_password", this.password)));
		treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("account.window.account_uuid", this.uuid)));
		String trueTranslation = TranslationKey.createAndGet("window.create_account.true");
		String falseTranslation = TranslationKey.createAndGet("window.create_account.false");
		treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("account.window.account_guest", this.guest ? trueTranslation : falseTranslation)));
		treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("account.window.account_taken", this.taken ? trueTranslation : falseTranslation)));
		return treeItem;
	}
	
	private String obfuscated() {
		String obfuscated = "";
		for (int i = 0; i < this.password.length(); i++) {
			obfuscated += "?";
		}
		return obfuscated;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getPassword() {
		if (NetworkSide.ACCOUNT.isOn() || Constans.IDE || this.equals(UNKNOWN)) {
			return this.password;
		}
		return this.obfuscated();
	}
	
	public boolean isObfuscated() {
		return !(NetworkSide.ACCOUNT.isOn() || Constans.IDE || this.equals(UNKNOWN));
	}
	
	public void displayPassword(GridPane pane) {
		if (NetworkSide.CLIENT.isOn() || Constans.IDE || this.equals(UNKNOWN)) {
			Text text = new Text(this.obfuscated());
			ToggleButton button = new ToggleButton();
			button.setToggleGroup(new ToggleGroup());
			button.setGraphic(FxUtils.makeImageView("textures/password_invisible.png", 20.0, 20.0));
			button.setOnAction((event) -> {
				if (button.isSelected()) {
					text.setText(this.password);
					button.setGraphic(FxUtils.makeImageView("textures/password_visible.png", 20.0, 20.0));
				} else {
					text.setText(this.obfuscated());
					button.setGraphic(FxUtils.makeImageView("textures/password_invisible.png", 20.0, 20.0));
				}
			});
			pane.add(text, 1, 1);
			pane.add(button, 2, 1);
		}
	}
	
	public UUID getUUID() {
		return this.uuid;
	}
	
	public boolean isGuest() {
		return this.guest;
	}
	
	public boolean isTaken() {
		return this.taken;
	}
	
	public void setTaken(boolean taken) {
		this.taken = taken;
	}
	
	public boolean match(String name, String password) {
		if (this.guest) {
			return this.name.equals(name);
		}
		return this.name.equals(name) && this.password.equals(password);
	}
	
	public boolean match(String name, String password, UUID uuid, boolean guest) {
		if (this.guest && guest) {
			return this.name.equals(name) && this.uuid.equals(uuid);
		}
		return this.name.equals(name) && this.password.equals(password) && this.uuid.equals(uuid) && this.guest == guest;
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeString(this.name);
		buffer.writeString(this.password);
		buffer.writeUUID(this.uuid);
		buffer.writeBoolean(this.guest);
	}
	
	@Override
	public CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		tag.putString("name", this.name);
		tag.putString("password", this.password);
		tag.put("uuid", TagUtil.writeUUID(this.uuid));
		tag.putBoolean("guest", this.guest);
		return tag;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof PlayerAccount account) {
			return this.match(account.name, account.password, account.uuid, account.guest);
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("PlayerAccount{");
		builder.append("name=").append(this.name).append(",");
		if (NetworkSide.ACCOUNT.isOn() || Constans.IDE || this.equals(UNKNOWN)) {
			builder.append("password=").append(this.password).append(",");
		} else {
			builder.append("password=").append(this.obfuscated()).append(",");
		}
		builder.append("uuid=").append(this.uuid).append(",");
		builder.append("guest=").append(this.guest).append("}");
		return builder.toString();
	}
	
}
