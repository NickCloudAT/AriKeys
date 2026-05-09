package eu.asangarin.arikeys.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import eu.asangarin.arikeys.AriKey;
import eu.asangarin.arikeys.AriKeys;
import eu.asangarin.arikeys.AriKeysPlatform;
import eu.asangarin.arikeys.util.network.KeyPressData;
import net.minecraft.IdentifierException;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// TODO(Ravel): can not resolve target class KeyBinding
@Mixin(KeyMapping.class)
public class AKKeyboardMixin {
	@Unique
	private static final List<InputConstants.Key> arikeys$pressedKeys = new ArrayList<>();

	// TODO(Ravel): no target class
    @Inject(method = "set", at = @At("HEAD"))
	private static void input(InputConstants.Key key, boolean pressed, CallbackInfo ci) {
		// Only check for keybinds while outside a GUI
		//if (MinecraftClient.getInstance().currentScreen != null) return;

		Collection<KeyMapping> keyBindings = AriKeysPlatform.getKeyBinding(key);
		for(KeyMapping binding : keyBindings) {
			if (binding != null) {
				String path = arikeys$cleanTranslationKey(binding.saveString());
				try {
					Identifier id = Identifier.fromNamespaceAndPath(Identifier.DEFAULT_NAMESPACE, path);
					if (AriKeys.getVanillaKeys().contains(id))
						arikeys$registerPress(id, key, pressed);
				} catch (IdentifierException id) {
					//noinspection CallToPrintStackTrace
					id.printStackTrace();
				}
			}
		}

		for (AriKey ariKey : AriKeys.getModifierSortedKeybinds())
			if (key.equals(ariKey.getBoundKeyCode()) && ariKey.testModifiers())
				arikeys$registerPress(ariKey.getId(), key, pressed);
	}

	@Unique
	private static void arikeys$registerPress(Identifier id, InputConstants.Key key, boolean pressed) {
		// Check if the button was pressed or released
		if (pressed) {
			boolean held = arikeys$pressedKeys.contains(key);
			// Check if it is already being pressed
			if (!held) {
				// Add it to the list of currently pressed keys
				arikeys$pressedKeys.add(key);
				arikeys$sendPacket(id, false);
			}
		} else {
			// Remove it from the list of currently pressed keys
			arikeys$pressedKeys.remove(key);
			arikeys$sendPacket(id, true);
		}
	}

	@Unique
	private static void arikeys$sendPacket(Identifier id, boolean release) {
		// Call the platform specific packet sending code
		AriKeysPlatform.sendKey(new KeyPressData(id, release));
	}

	@Unique
	private static String arikeys$cleanTranslationKey(String key) {
		return key.replace("key.", "").replace(".", "")
			.replace(" ", "_").toLowerCase();
	}
}
