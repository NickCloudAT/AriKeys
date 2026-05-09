package eu.asangarin.arikeys;

import eu.asangarin.arikeys.util.ModifierKey;
import eu.asangarin.arikeys.util.network.KeyAddData;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.Identifier;

import java.util.HashSet;
import java.util.Set;

@Getter
public class AriKey {
	private final Identifier id;
	private final String name, category;
	private final InputConstants.Key keyCode;
	private InputConstants.Key boundKeyCode;
	private final Set<ModifierKey> modifiers;
	@Setter
	private Set<ModifierKey> boundModifiers;

	public AriKey(KeyAddData data) {
		this(data.getId(), data.getName(), data.getCategory(), InputConstants.Type.KEYSYM.getOrCreate(data.getDefKey()), data.getModifiers());
	}

	public AriKey(Identifier id, String name, String category, InputConstants.Key keyCode, int[] modifiers) {
		this.id = id;
		this.name = name;
		this.category = category;
		this.keyCode = keyCode;
		this.boundKeyCode = keyCode;
		this.modifiers = ModifierKey.getFromArray(modifiers);
		this.boundModifiers = new HashSet<>(this.modifiers);
	}

	public void setBoundKey(InputConstants.Key key, boolean handleModifiers) {
		if (handleModifiers) {
			Set<ModifierKey> mods = new HashSet<>();
			for (ModifierKey modifier : ModifierKey.ALL)
				if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), modifier.getCode())) mods.add(modifier);
			setBoundModifiers(mods);
		}
		this.boundKeyCode = key;
	}

	public void resetBoundModifiers() {
		setBoundModifiers(new HashSet<>(this.modifiers));
	}

	public boolean hasChanged() {
		return !keyCode.equals(boundKeyCode) || !testModifiers(this.modifiers);
	}

	public boolean isUnbound() {
		return boundKeyCode.equals(InputConstants.UNKNOWN);
	}

	public boolean testModifiers() {
		for (ModifierKey key : boundModifiers)
			if (!InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), key.getCode())) return false;
		return true;
	}

	public boolean testModifiers(Set<ModifierKey> otherKeys) {
		return boundModifiers.containsAll(otherKeys) && otherKeys.containsAll(boundModifiers);
	}
}
