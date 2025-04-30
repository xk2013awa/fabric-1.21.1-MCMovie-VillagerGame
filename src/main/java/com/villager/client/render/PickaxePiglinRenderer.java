package com.villager.client.render;

import com.villager.entity.PickaxePiglinEntity;
import com.villager.model.PickaxePiglinModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PickaxePiglinRenderer extends GeoEntityRenderer<PickaxePiglinEntity> {
    public PickaxePiglinRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new PickaxePiglinModel());
    }
}
