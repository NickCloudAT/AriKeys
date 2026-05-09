package eu.asangarin.arikeys.util.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;

@Getter
@RequiredArgsConstructor
public class KeyAddData {
	private final Identifier id;
	private final String name, category;
	private final int defKey;
	private final int[] modifiers;

	public static KeyAddData fromBuffer(FriendlyByteBuf buf) {
		buf.readByte();
		String path = buf.readUtf();
		String key = buf.readUtf();
		int defKey = buf.readInt();
		String name = buf.readUtf();
		String category = buf.readUtf();
		int[] modifiers = buf.readVarIntArray();

		Identifier id = Identifier.fromNamespaceAndPath(path, key);
		return new KeyAddData(id, name, category, defKey, modifiers);
	}
}