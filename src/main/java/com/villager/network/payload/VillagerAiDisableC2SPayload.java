package com.villager.network.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.villager.Villager.MOD_ID;

public record VillagerAiDisableC2SPayload(int villagerId, boolean disabled) implements CustomPayload {
    public static final Id<VillagerAiDisableC2SPayload> ID = new Id<>(Identifier.of(MOD_ID, "ai_toggle"));

    public static final PacketCodec<RegistryByteBuf, VillagerAiDisableC2SPayload> CODEC = PacketCodec.of(
            (packet, buf) -> {
                buf.writeInt(packet.villagerId());
                buf.writeBoolean(packet.disabled());
            },
            buf -> new VillagerAiDisableC2SPayload(buf.readInt(), buf.readBoolean())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}