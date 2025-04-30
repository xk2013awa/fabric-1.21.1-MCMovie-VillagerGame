package com.villager.model;

import com.villager.entity.ExclamationEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

import static com.villager.Villager.MOD_ID;

public class ExclamationModel extends GeoModel<ExclamationEntity> {
    @Override
    public Identifier getModelResource(ExclamationEntity animatable) {
        return Identifier.of(MOD_ID, "geo/exclamation.geo.json");
    }

    @Override
    public Identifier getTextureResource(ExclamationEntity animatable) {
        return Identifier.of(MOD_ID, animatable.isRed()
                ? "textures/entity/exclamation_red.png"
                : "textures/entity/exclamation.png");
    }

    @Override
    public Identifier getAnimationResource(ExclamationEntity animatable) {
        return Identifier.of(MOD_ID, "animations/exclamation.animation.json");
    }
}
