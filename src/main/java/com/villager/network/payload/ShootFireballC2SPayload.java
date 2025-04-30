package com.villager.network.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.villager.Villager.MOD_ID;

public record ShootFireballC2SPayload(int entityId, float pullProgress) implements CustomPayload {
    public static final Id<ShootFireballC2SPayload> ID = new Id<>(Identifier.of(MOD_ID, "shoot_fireball"));

    public static final PacketCodec<RegistryByteBuf, ShootFireballC2SPayload> CODEC = PacketCodec.of(
            (payload, buf) -> {
                buf.writeInt(payload.entityId);
                buf.writeFloat(payload.pullProgress);
            },
            buf -> new ShootFireballC2SPayload(buf.readInt(), buf.readFloat())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
