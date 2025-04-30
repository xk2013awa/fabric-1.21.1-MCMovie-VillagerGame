package com.villager.client.render;

import com.villager.entity.DrumPiglinEntity;
import com.villager.model.DrumPiglinModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DrumPiglinRenderer extends GeoEntityRenderer<DrumPiglinEntity> {

    public DrumPiglinRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DrumPiglinModel());
    }

    @Override
    public void renderRecursively(MatrixStack poseStack, DrumPiglinEntity animatable, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                                  int packedOverlay, int colour) {

        if (bone.getName().equals("drumstick_right")) {
            Matrix4f matrix = bone.getWorldSpaceMatrix();
            Vec3d rightEffectPos = new Vec3d(matrix.m30(), matrix.m31(), matrix.m32());
            animatable.setRightEffectPosition(rightEffectPos);
        }

        if (bone.getName().equals("drumstick_left")) {
            Matrix4f matrix = bone.getWorldSpaceMatrix();
            Vec3d leftEffectPos = new Vec3d(matrix.m30(), matrix.m31(), matrix.m32());
            animatable.setLeftEffectPosition(leftEffectPos);
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}