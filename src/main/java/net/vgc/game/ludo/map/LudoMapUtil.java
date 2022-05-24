package net.vgc.game.ludo.map;

import java.util.List;

import net.vgc.game.ludo.map.field.LudoField;
import net.vgc.game.ludo.map.field.LudoFieldPos;
import net.vgc.game.ludo.map.field.LudoFieldType;

public class LudoMapUtil {
	
	public static void createDefaultFields(List<LudoField> fields) {
		for (int i = 0; i < 40; i++) {
			fields.add(new LudoField(LudoFieldType.DEFAULT, LudoFieldPos.ofGreen(i)));
		}
	}
	
	public static void createHomeFields(List<LudoField> fields) {
		for (int i = 0; i < 16; i++) {
			fields.add(new LudoField(LudoFieldType.HOME, i % 4, i % 4, i % 4, i % 4));
		}
	}
	
	public static void createWinFields(List<LudoField> fields) {
		for (int i = 0; i < 16; i++) {
			fields.add(new LudoField(LudoFieldType.WIN, i % 4, i % 4, i % 4, i % 4));
		}
	}
	
}
