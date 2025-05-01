package com.villager.network.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.villager.Villager.MOD_ID;

public record VillagerMarryC2SPayload(int villagerID, boolean married) implements CustomPayload {
    public static final Id<VillagerMarryC2SPayload> ID = new Id<>(Identifier.of(MOD_ID, "villager_marry"));

    public static final PacketCodec<RegistryByteBuf, VillagerMarryC2SPayload> CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeInt(value.villagerID);
                buf.writeBoolean(value.married);
            },
            buf -> new VillagerMarryC2SPayload(buf.readInt(), buf.readBoolean())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
