package com.villager.client.render;

import com.villager.entity.WarriorPiglinEntity;
import com.villager.model.WarriorPiglinModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WarriorPiglinRenderer extends GeoEntityRenderer<WarriorPiglinEntity> {
    public WarriorPiglinRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new WarriorPiglinModel());
    }
}
