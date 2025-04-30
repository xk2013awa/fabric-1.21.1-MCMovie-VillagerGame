package com.villager.client.render;

import com.villager.entity.SpearEntity;
import com.villager.model.SpearModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SpearRenderer extends GeoEntityRenderer<SpearEntity> {
    public SpearRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SpearModel());
    }

    @Override
    protected void applyRotations(SpearEntity entity, MatrixStack matrices, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        float yaw = entity.getYaw();
        float pitch = entity.getPitch();

        matrices.translate(0, -0.2, 0);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch + 40));

        super.applyRotations(entity, matrices, ageInTicks, rotationYaw, partialTick, nativeScale);
    }
}