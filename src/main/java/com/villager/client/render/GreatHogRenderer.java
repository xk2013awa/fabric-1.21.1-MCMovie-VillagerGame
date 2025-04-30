package com.villager.client.render;

import com.villager.entity.GreatHogEntity;
import com.villager.model.GreatHogModel;
import com.villager.network.payload.SetBlasterPosC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GreatHogRenderer extends GeoEntityRenderer<GreatHogEntity> {
    public GreatHogRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new GreatHogModel());
    }

    @Override
    public void renderRecursively(MatrixStack poseStack, GreatHogEntity animatable, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                                  int packedOverlay, int colour) {

        if (bone.getName().equals("particle_source") && MinecraftClient.getInstance().player != null) {
            Matrix4f matrix = bone.getWorldSpaceMatrix();
            Vec3d blasterPos = new Vec3d(matrix.m30(), matrix.m31(), matrix.m32());

            ClientPlayNetworking.send(new SetBlasterPosC2SPayload(
                    animatable.getId(),
                    blasterPos.x,
                    blasterPos.y,
                    blasterPos.z
            ));
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, colour);
    }
}
