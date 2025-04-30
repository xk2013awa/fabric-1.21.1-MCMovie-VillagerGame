package com.villager.model;

import com.villager.entity.GreatHogEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

import static com.villager.Villager.MOD_ID;


public class GreatHogModel extends GeoModel<GreatHogEntity> {
    @Override
    public Identifier getModelResource(GreatHogEntity animatable) {
        return Identifier.of(MOD_ID, "geo/great_hog.geo.json");
    }

    @Override
    public Identifier getTextureResource(GreatHogEntity animatable) {
        return Identifier.of(MOD_ID, "textures/entity/great_hog.png");
    }

    @Override
    public Identifier getAnimationResource(GreatHogEntity animatable) {
        return Identifier.of(MOD_ID, "animations/great_hog.animation.json");
    }
}
