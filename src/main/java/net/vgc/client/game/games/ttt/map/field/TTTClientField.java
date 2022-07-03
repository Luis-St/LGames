package net.vgc.client.game.games.ttt.map.field;

import java.util.Objects;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import net.vgc.Constans;
import net.vgc.client.Client;
import net.vgc.client.fx.FxUtil;
import net.vgc.client.game.games.ttt.TTTClientGame;
import net.vgc.client.game.games.ttt.player.figure.TTTClientFigure;
import net.vgc.client.game.map.field.ClientGameField;
import net.vgc.game.GameResult;
import net.vgc.game.games.ttt.map.field.TTTFieldPos;
import net.vgc.game.games.ttt.map.field.TTTFieldType;
import net.vgc.game.games.ttt.player.TTTPlayerType;
import net.vgc.game.map.field.GameFieldInfo;
import net.vgc.game.player.field.GameFigure;
import net.vgc.player.GameProfile;
import net.vgc.util.Util;

public class TTTClientField extends ToggleButton implements ClientGameField {
	
	protected final Client client;
	protected final TTTClientGame game;
	protected final ToggleGroup group;
	protected final TTTFieldPos fieldPos;
	protected final double imageSize;
	protected TTTClientFigure figure;
	protected boolean shadowed = false;
	protected GameResult result = GameResult.NO;
	
	public TTTClientField(Client client, TTTClientGame game, ToggleGroup group, TTTFieldPos fieldPos, double imageSize) {
		this.client = client;
		this.game = game;
		this.group = group;
		this.fieldPos = fieldPos;
		this.imageSize = imageSize;
		this.init();
	}
	
	@Override
	public void init() {
		this.setToggleGroup(this.group);
		this.setPrefSize(this.imageSize, this.imageSize);
		this.setFocusTraversable(false);
		if (!Constans.DEBUG) {
			this.setBackground(null);
		}
		this.updateFieldGraphic();
	}

	@Override
	public TTTFieldType getFieldType() {
		LOGGER.warn("Fail to get field type of field {}, since tic tac toe fields does not have a field type", this.getFieldPos().getPosition());
		return TTTFieldType.DEFAULT;
	}

	@Override
	public TTTFieldPos getFieldPos() {
		return this.fieldPos;
	}

	@Override
	public boolean isHome() {
		return false;
	}

	@Override
	public boolean isStart() {
		return false;
	}

	@Override
	public boolean isStartFor(GameFigure figure) {
		return false;
	}

	@Override
	public boolean isWin() {
		return false;
	}

	@Override
	public TTTClientFigure getFigure() {
		return this.figure;
	}
	
	@Override
	public void setFigure(GameFigure figure) {
		this.figure = (TTTClientFigure) figure;
		this.updateFieldGraphic();
	}

	@Override
	public ImageView getFieldBackground() {
		return null;
	}

	@Override
	public boolean canSelectField() {
		return this.figure == null;
	}

	@Override
	public boolean isShadowed() {
		return this.shadowed;
	}

	@Override
	public void setShadowed(boolean shadowed) {
		this.shadowed = shadowed;
		this.updateFieldGraphic();
	}
	
	public GameResult getResult() {
		return this.result;
	}
	
	public void setResult(GameResult result) {
		this.result = result;
		this.updateFieldGraphic();
	}

	@Override
	public void updateFieldGraphic() {
		TTTPlayerType playerType = (TTTPlayerType) this.game.getPlayerFor(this.client.getPlayer()).getPlayerType();
		if (this.isEmpty()) {
			if (this.isShadowed()) {
				if (this.getResult() == GameResult.NO) {
					this.setGraphic(FxUtil.makeImageView(playerType.getPath() + "_shadow.png", this.imageSize * 0.95, this.imageSize * 0.95));
				} else {
					this.setShadowed(false);
					this.updateFieldGraphic();
				}
			} else {
				this.setGraphic(null);
			}
		} else {
			if (this.isShadowed()) {
				this.setShadowed(false);
				LOGGER.warn("Can not display a shadow figure on a non empty field");
				this.updateFieldGraphic();
			} else {
				this.setGraphic(this.getFigureTexture());
			}
		}
	}
	
	protected ImageView getFigureTexture() {
		TTTPlayerType playerType = this.figure.getPlayerType();
		return switch (this.result) {
			case WIN -> FxUtil.makeImageView(playerType.getPath() + "_win.png", this.imageSize * 0.95, this.imageSize * 0.95);
			case LOSE -> FxUtil.makeImageView(playerType.getPath() + "_lose.png", this.imageSize * 0.95, this.imageSize * 0.95);
			case DRAW -> FxUtil.makeImageView(playerType.getPath() + "_draw.png", this.imageSize * 0.95, this.imageSize * 0.95);
			default -> FxUtil.makeImageView(playerType.getPath() + ".png", this.imageSize * 0.95, this.imageSize * 0.95);
		};
	}
	
	@Override
	public GameFieldInfo getFieldInfo() {
		if (this.isEmpty()) {
			return new GameFieldInfo(this.getFieldType(), TTTPlayerType.NO, this.fieldPos, GameProfile.EMPTY, -1, Util.EMPTY_UUID);
		}
		TTTClientFigure figure = this.getFigure();
		return new GameFieldInfo(this.getFieldType(), TTTPlayerType.NO, this.fieldPos, figure.getPlayer().getPlayer().getProfile(), figure.getCount(), figure.getUUID());
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof TTTClientField field) {
			if (!this.fieldPos.equals(field.fieldPos)) {
				return false;
			} else {
				return Objects.equals(this.figure, field.figure);
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("TTTClientField{");
		builder.append("fieldPos=").append(this.fieldPos).append(",");
		builder.append("figure=").append(this.figure == null ? "null" : this.figure).append("}");
		return builder.toString();
	}

}
