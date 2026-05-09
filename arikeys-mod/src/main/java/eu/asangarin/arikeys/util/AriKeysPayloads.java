package eu.asangarin.arikeys.util;

import eu.asangarin.arikeys.util.network.KeyAddData;
import eu.asangarin.arikeys.util.network.KeyPressData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class AriKeysPayloads {
	public record Handshake() implements CustomPacketPayload {
		public static final Type<Handshake> ID = new Type<>(AriKeysChannels.HANDSHAKE_CHANNEL);
		public static final StreamCodec<FriendlyByteBuf, Handshake> CODEC = StreamCodec.of(Handshake::write, Handshake::read);

		private static void write(FriendlyByteBuf buf, Handshake handshake) {}

		private static Handshake read(FriendlyByteBuf buf) {
			readFully(buf);
			return new Handshake();
		}

		@Override
		public Type<Handshake> type() {
			return ID;
		}
	}

	public record AddKey(KeyAddData data) implements CustomPacketPayload {
		public static final Type<AddKey> ID = new Type<>(AriKeysChannels.ADD_KEY_CHANNEL);
		public static final StreamCodec<FriendlyByteBuf, AddKey> CODEC = StreamCodec.of(AddKey::write, AddKey::read);

		private static void write(FriendlyByteBuf buf, AddKey addKey) {

		}

		private static AddKey read(FriendlyByteBuf buf) {
			KeyAddData keyData = KeyAddData.fromBuffer(buf);
			readFully(buf);
			return new AddKey(keyData);
		}

		@Override
		public Type<AddKey> type() {
			return ID;
		}
	}

	public record Load() implements CustomPacketPayload {
		public static final Type<Load> ID = new Type<>(AriKeysChannels.LOAD_CHANNEL);
		public static final StreamCodec<FriendlyByteBuf, Load> CODEC = StreamCodec.of(Load::write, Load::read);

		private static void write(FriendlyByteBuf buf, Load load) {}

		private static Load read(FriendlyByteBuf buf) {
			readFully(buf);
			return new Load();
		}

		@Override
		public Type<Load> type() {
			return ID;
		}
	}

	public record Key(KeyPressData data) implements CustomPacketPayload {
		public static final Type<Key> ID = new Type<>(AriKeysChannels.KEY_CHANNEL);
		public static final StreamCodec<FriendlyByteBuf, Key> CODEC = StreamCodec.of(Key::write, Key::read);

		private static void write(FriendlyByteBuf buf, Key key) {
			buf.writeByte(0);
			key.data.write(buf);
		}

		private static Key read(FriendlyByteBuf buf) {
			readFully(buf);
			return new Key(null);
		}

		@Override
		public Type<Key> type() {
			return ID;
		}
	}

	protected static void readFully(FriendlyByteBuf buf) {
		while (buf.readableBytes() != 0)
			buf.readByte();
	}
}