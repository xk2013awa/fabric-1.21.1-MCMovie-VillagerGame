package com.villager.network.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.villager.Villager.MOD_ID;

public record FriendshipC2SPayload(int villagerID, int amount) implements CustomPayload {
    public static final Id<FriendshipC2SPayload> ID = new Id<>(Identifier.of(MOD_ID, "increase_friendship"));

    public static final PacketCodec<RegistryByteBuf, FriendshipC2SPayload> CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeInt(value.villagerID);
                buf.writeInt(value.amount);
            },
            buf -> new FriendshipC2SPayload(buf.readInt(), buf.readInt())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
