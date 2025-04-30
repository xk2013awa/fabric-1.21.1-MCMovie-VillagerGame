package com.villager.model;

import com.villager.entity.PickaxePiglinEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

import static com.villager.Villager.MOD_ID;


public class PickaxePiglinModel extends GeoModel<PickaxePiglinEntity> {
    @Override
    public Identifier getModelResource(PickaxePiglinEntity animatable) {
        return Identifier.of(MOD_ID, "geo/pickaxe_piglin.geo.json");
    }

    @Override
    public Identifier getTextureResource(PickaxePiglinEntity animatable) {
        return Identifier.of(MOD_ID, "textures/entity/pickaxe_piglin.png");
    }

    @Override
    public Identifier getAnimationResource(PickaxePiglinEntity animatable) {
        return Identifier.of(MOD_ID, "animations/pickaxe_piglin.animation.json");
    }
}
