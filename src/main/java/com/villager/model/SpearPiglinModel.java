package com.villager.model;

import com.villager.entity.SpearPiglinEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

import static com.villager.Villager.MOD_ID;


public class SpearPiglinModel extends GeoModel<SpearPiglinEntity> {
    @Override
    public Identifier getModelResource(SpearPiglinEntity animatable) {
        return Identifier.of(MOD_ID, "geo/spear_piglin.geo.json");
    }

    @Override
    public Identifier getTextureResource(SpearPiglinEntity animatable) {
        return Identifier.of(MOD_ID, "textures/entity/spear_piglin.png");
    }

    @Override
    public Identifier getAnimationResource(SpearPiglinEntity animatable) {
        return Identifier.of(MOD_ID, "animations/spear_piglin.animation.json");
    }
}
