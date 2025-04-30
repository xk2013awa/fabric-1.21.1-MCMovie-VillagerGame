package com.villager.network.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.villager.Villager.MOD_ID;

public record ParticlesC2SPayload(int entityId, double x, double y, double z, String effectType) implements CustomPayload {
    public static final Id<ParticlesC2SPayload> ID = new Id<>(Identifier.of(MOD_ID, "particles"));

    public static final PacketCodec<RegistryByteBuf, ParticlesC2SPayload> CODEC = PacketCodec.of(
            (payload, buf) -> {
                buf.writeInt(payload.entityId);
                buf.writeDouble(payload.x);
                buf.writeDouble(payload.y);
                buf.writeDouble(payload.z);
                buf.writeString(payload.effectType);
            },
            buf -> new ParticlesC2SPayload(
                    buf.readInt(),
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readString()
            )
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
