package com.villager.network.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.villager.Villager.MOD_ID;

public record HammerAttackGroundC2SPayload(int entityId) implements CustomPayload {
    public static final Id<HammerAttackGroundC2SPayload> ID = new Id<>(Identifier.of(MOD_ID, "hammer_attack_ground"));

    public static final PacketCodec<RegistryByteBuf, HammerAttackGroundC2SPayload> CODEC = PacketCodec.of(
            (payload, buf) -> buf.writeInt(payload.entityId),
            buf -> new HammerAttackGroundC2SPayload(buf.readInt())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}