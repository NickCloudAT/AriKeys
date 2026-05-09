package eu.asangarin.arikeys;

import eu.asangarin.arikeys.mixin.AKKeyboardFabricMixin;
import eu.asangarin.arikeys.util.AriKeysPayloads;
import eu.asangarin.arikeys.util.network.KeyPressData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AriKeysPlatform {
	public static void sendHandshake() {
		ClientPlayNetworking.send(new AriKeysPayloads.Handshake());
	}

	public static void sendKey(KeyPressData data) {
		ClientPlayNetworking.send(new AriKeysPayloads.Key(data));
	}

    public static Collection<KeyMapping> getKeyBinding(InputConstants.Key code) {
        Object value = AKKeyboardFabricMixin.getKeyBindings().get(code);

        if (value == null) return Collections.emptyList();

        // Single KeyBinding
        if (value instanceof KeyMapping kb) {
            return Collections.singleton(kb);
        }

        // List of KeyBindings
        if (value instanceof Collection<?> list) {
            List<KeyMapping> keys = new ArrayList<>();
            for (Object o : list) {
                if (o instanceof KeyMapping k) keys.add(k);
            }
            return keys;
        }

        return Collections.emptyList();
    }
}
