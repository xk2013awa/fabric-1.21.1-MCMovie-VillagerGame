package com.villager.model;

import com.villager.entity.HammerPiglinEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

import static com.villager.Villager.MOD_ID;


public class HammerPiglinModel extends GeoModel<HammerPiglinEntity> {
    @Override
    public Identifier getModelResource(HammerPiglinEntity animatable) {
        return Identifier.of(MOD_ID, "geo/hammer_piglin.geo.json");
    }

    @Override
    public Identifier getTextureResource(HammerPiglinEntity animatable) {
        return Identifier.of(MOD_ID, "textures/entity/hammer_piglin.png");
    }

    @Override
    public Identifier getAnimationResource(HammerPiglinEntity animatable) {
        return Identifier.of(MOD_ID, "animations/hammer_piglin.animation.json");
    }
}
