package eu.asangarin.arikeys.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

// TODO(Ravel): can not resolve target class KeyBinding
@Mixin(KeyMapping.class)
public interface AKKeyboardFabricMixin {
	// TODO(Ravel): Could not determine a single target
    @Accessor("MAP")
	static Map<InputConstants.Key, KeyMapping> getKeyBindings() {
		throw new AssertionError();
	}
}
