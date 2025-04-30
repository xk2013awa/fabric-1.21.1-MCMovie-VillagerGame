package com.villager.network.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.villager.Villager.MOD_ID;

public record StopMoveC2SPayload(int entityId) implements CustomPayload {
    public static final Id<StopMoveC2SPayload> ID = new Id<>(Identifier.of(MOD_ID, "stop_move"));

    public static final PacketCodec<RegistryByteBuf, StopMoveC2SPayload> CODEC = PacketCodec.of(
            (payload, buf) -> buf.writeInt(payload.entityId),
            buf -> new StopMoveC2SPayload(buf.readInt())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
