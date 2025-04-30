package com.villager.network.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.villager.Villager.MOD_ID;

public record GeneralSpinAttackC2SPayload(int entityId, String attackType) implements CustomPayload {
    public static final Id<GeneralSpinAttackC2SPayload> ID = new Id<>(Identifier.of(MOD_ID, "general_spin_attack"));

    public static final PacketCodec<RegistryByteBuf, GeneralSpinAttackC2SPayload> CODEC = PacketCodec.of(
            (payload, buf) -> {
                buf.writeInt(payload.entityId);
                buf.writeString(payload.attackType);
            },
            buf -> new GeneralSpinAttackC2SPayload(buf.readInt(), buf.readString())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
