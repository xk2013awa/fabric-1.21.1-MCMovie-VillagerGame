package com.villager.client.render;

import com.villager.entity.FireballSmallEntity;
import com.villager.model.FireballSmallModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FireballSmallRenderer extends GeoEntityRenderer<FireballSmallEntity> {

    public FireballSmallRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new FireballSmallModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    protected void applyRotations(FireballSmallEntity entity, MatrixStack matrices, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {

        Vec3d cameraPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
        Vec3d entityPos = entity.getPos();

        Vec3d lookVec = cameraPos.subtract(entityPos).normalize();

        Vec3d baseNormal = new Vec3d(0, 0, -1);

        Vec3d axis = baseNormal.crossProduct(lookVec);
        double axisLenSq = axis.lengthSquared();
        if (axisLenSq > 1.0E-8) {
            axis = axis.normalize();
            double dot = baseNormal.dotProduct(lookVec);
            dot = Math.min(1.0, Math.max(-1.0, dot));
            double angle = Math.acos(dot);

            matrices.multiply(RotationAxis.of(axis.toVector3f()).rotation((float) angle));
        }
    }
}
