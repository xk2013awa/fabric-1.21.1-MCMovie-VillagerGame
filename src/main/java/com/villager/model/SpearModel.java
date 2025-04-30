package com.villager.model;

import com.villager.entity.SpearEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

import static com.villager.Villager.MOD_ID;


public class SpearModel extends GeoModel<SpearEntity> {
    @Override
    public Identifier getModelResource(SpearEntity animatable) {
        return Identifier.of(MOD_ID, "geo/spear.geo.json");
    }

    @Override
    public Identifier getTextureResource(SpearEntity animatable) {
        return Identifier.of(MOD_ID, "textures/entity/stone.png");
    }

    @Override
    public Identifier getAnimationResource(SpearEntity animatable) {
        return Identifier.of(MOD_ID, "animations/spear.animation.json");
    }
}
