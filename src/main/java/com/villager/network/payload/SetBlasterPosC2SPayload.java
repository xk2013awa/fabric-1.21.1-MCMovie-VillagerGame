package com.villager.network.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import static com.villager.Villager.MOD_ID;

public record SetBlasterPosC2SPayload(int entityId, double x, double y, double z) implements CustomPayload {
    public static final Id<SetBlasterPosC2SPayload> ID = new Id<>(Identifier.of(MOD_ID, "set_blaster_pos"));

    public static final PacketCodec<RegistryByteBuf, SetBlasterPosC2SPayload> CODEC = PacketCodec.of(
            (payload, buf) -> {
                buf.writeInt(payload.entityId);
                buf.writeDouble(payload.x);
                buf.writeDouble(payload.y);
                buf.writeDouble(payload.z);
            },
            buf -> new SetBlasterPosC2SPayload(buf.readInt(), buf.readDouble(), buf.readDouble(), buf.readDouble())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public Vec3d getPos() {
        return new Vec3d(x, y, z);
    }
}
