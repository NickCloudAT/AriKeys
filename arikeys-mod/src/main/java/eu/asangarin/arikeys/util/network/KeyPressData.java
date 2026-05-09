package eu.asangarin.arikeys.util.network;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;

@RequiredArgsConstructor
public class KeyPressData {
	private final Identifier id;
	private final boolean release;

	public void write(FriendlyByteBuf buf) {
		buf.writeUtf(id.getNamespace());
		buf.writeUtf(id.getPath());
		buf.writeBoolean(release);
	}
}