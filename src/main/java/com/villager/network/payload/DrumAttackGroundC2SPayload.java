package com.villager.network.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.villager.Villager.MOD_ID;

public record DrumAttackGroundC2SPayload(int entityId) implements CustomPayload {
    public static final Id<DrumAttackGroundC2SPayload> ID = new Id<>(Identifier.of(MOD_ID, "drum_attack_ground"));

    public static final PacketCodec<RegistryByteBuf, DrumAttackGroundC2SPayload> CODEC = PacketCodec.of(
            (payload, buf) -> buf.writeInt(payload.entityId),
            buf -> new DrumAttackGroundC2SPayload(buf.readInt())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}