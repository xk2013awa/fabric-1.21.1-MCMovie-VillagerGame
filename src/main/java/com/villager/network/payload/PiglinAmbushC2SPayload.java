package com.villager.network.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static com.villager.Villager.MOD_ID;
public record PiglinAmbushC2SPayload(BlockPos center, boolean targetPlayer) implements CustomPayload {
    public static final Id<PiglinAmbushC2SPayload> ID = new Id<>(Identifier.of(MOD_ID, "piglin_ambush"));

    public static final PacketCodec<RegistryByteBuf, PiglinAmbushC2SPayload> CODEC = PacketCodec.of(
            (payload, buf) -> {
                buf.writeBlockPos(payload.center());
                buf.writeBoolean(payload.targetPlayer());
            },
            buf -> new PiglinAmbushC2SPayload(buf.readBlockPos(), buf.readBoolean())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}