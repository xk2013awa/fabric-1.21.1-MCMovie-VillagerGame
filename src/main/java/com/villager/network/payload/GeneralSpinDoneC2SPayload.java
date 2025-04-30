package com.villager.network.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.villager.Villager.MOD_ID;

public record GeneralSpinDoneC2SPayload(int entityId) implements CustomPayload {
    public static final Id<GeneralSpinDoneC2SPayload> ID = new Id<>(Identifier.of(MOD_ID, "general_spin_done"));

    public static final PacketCodec<RegistryByteBuf, GeneralSpinDoneC2SPayload> CODEC = PacketCodec.of(
            (payload, buf) -> buf.writeInt(payload.entityId),
            buf -> new GeneralSpinDoneC2SPayload(buf.readInt())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}