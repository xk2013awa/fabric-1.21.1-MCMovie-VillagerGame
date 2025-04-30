package com.villager.model;

import com.villager.entity.ChefPiglinEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

import static com.villager.Villager.MOD_ID;

public class ChefPiglinModel extends GeoModel<ChefPiglinEntity> {
    @Override
    public Identifier getModelResource(ChefPiglinEntity animatable) {
        return Identifier.of(MOD_ID, "geo/chef_piglin.geo.json");
    }

    @Override
    public Identifier getTextureResource(ChefPiglinEntity animatable) {
        return Identifier.of(MOD_ID, "textures/entity/chef_piglin.png");
    }

    @Override
    public Identifier getAnimationResource(ChefPiglinEntity animatable) {
        return Identifier.of(MOD_ID, "animations/chef_piglin.animation.json");
    }
}
