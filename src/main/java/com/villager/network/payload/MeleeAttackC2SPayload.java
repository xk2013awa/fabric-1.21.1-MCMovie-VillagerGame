package com.villager.network.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.villager.Villager.MOD_ID;

public record MeleeAttackC2SPayload(int entityId) implements CustomPayload {
    public static final Id<MeleeAttackC2SPayload> ID = new Id<>(Identifier.of(MOD_ID, "melee_attack"));

    public static final PacketCodec<RegistryByteBuf, MeleeAttackC2SPayload> CODEC = PacketCodec.of(
            (payload, buf) -> buf.writeInt(payload.entityId),
            buf -> new MeleeAttackC2SPayload(buf.readInt())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}