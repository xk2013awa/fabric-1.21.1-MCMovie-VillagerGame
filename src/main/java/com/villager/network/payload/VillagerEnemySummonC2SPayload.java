package com.villager.network.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.villager.Villager.MOD_ID;

public record VillagerEnemySummonC2SPayload(int villagerId) implements CustomPayload {
    public static final Id<VillagerEnemySummonC2SPayload> ID = new Id<>(Identifier.of(MOD_ID, "enemy_summon"));

    public static final PacketCodec<RegistryByteBuf, VillagerEnemySummonC2SPayload> CODEC = PacketCodec.of(
            (packet, buf) -> buf.writeInt(packet.villagerId),
            buf -> new VillagerEnemySummonC2SPayload(buf.readInt())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}