package eu.asangarin.arikeys.util;

import lombok.Getter;
import com.mojang.blaze3d.platform.InputConstants;

import java.util.HashSet;
import java.util.Set;

@Getter
public enum ModifierKey {
	NONE(-1, -1),
	LEFT_CTRL(4, InputConstants.KEY_LCONTROL),
	LEFT_SHIFT(0, InputConstants.KEY_LSHIFT),
	LEFT_ALT(2, InputConstants.KEY_LALT),
	RIGHT_CTRL(5, InputConstants.KEY_RCONTROL),
	RIGHT_SHIFT(1, InputConstants.KEY_RSHIFT),
	RIGHT_ALT(3, InputConstants.KEY_RALT);

	public final static ModifierKey[] ALL = new ModifierKey[] {
			LEFT_CTRL, LEFT_SHIFT, LEFT_ALT, RIGHT_CTRL, RIGHT_SHIFT, RIGHT_ALT
	};

	private final int id, code;
	private final InputConstants.Key key;
	private final String translationKey;

	ModifierKey(int id, int code) {
		this.id = id;
		this.code = code;
		this.key = InputConstants.Type.KEYBOARD.getOrCreate(code);
		this.translationKey = "arikeys.modifier." + name().toLowerCase();
	}

	public static Set<ModifierKey> getFromArray(int[] modifiers) {
		Set<ModifierKey> keys = new HashSet<>();
		for (int i : modifiers) {
			ModifierKey key = fromCode(i);
			if (key == ModifierKey.NONE) continue;
			keys.add(key);
		}
		return keys;
	}

	private static ModifierKey fromCode(int i) {
		for (ModifierKey key : ALL)
			if (key.id == i) return key;
		return ModifierKey.NONE;
	}
}
