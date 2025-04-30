package com.villager.network.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.villager.Villager.MOD_ID;

public record VillagerSetProtectedC2SPayload(int villagerId, boolean protectedStatus) implements CustomPayload {
    public static final Id<VillagerSetProtectedC2SPayload> ID = new Id<>(Identifier.of(MOD_ID, "villager_protected"));

    public static final PacketCodec<RegistryByteBuf, VillagerSetProtectedC2SPayload> CODEC = PacketCodec.of(
            (packet, buf) -> {
                buf.writeInt(packet.villagerId());
                buf.writeBoolean(packet.protectedStatus());
            },
            buf -> new VillagerSetProtectedC2SPayload(buf.readInt(), buf.readBoolean())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
