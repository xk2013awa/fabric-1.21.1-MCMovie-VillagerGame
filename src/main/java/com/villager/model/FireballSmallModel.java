package com.villager.model;

import com.villager.entity.FireballSmallEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

import static com.villager.Villager.MOD_ID;

public class FireballSmallModel extends GeoModel<FireballSmallEntity> {
    @Override
    public Identifier getModelResource(FireballSmallEntity animatable) {
        return Identifier.of(MOD_ID, "geo/fireball_small.geo.json");
    }

    @Override
    public Identifier getTextureResource(FireballSmallEntity animatable) {
        return Identifier.of(MOD_ID, "textures/entity/fireball_small.png");
    }

    @Override
    public Identifier getAnimationResource(FireballSmallEntity animatable) {
        return Identifier.of(MOD_ID, "animations/fireball_small.animation.json");
    }
}
