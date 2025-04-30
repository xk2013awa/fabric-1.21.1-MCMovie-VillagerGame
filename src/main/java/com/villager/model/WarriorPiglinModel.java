package com.villager.model;

import com.villager.entity.WarriorPiglinEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

import static com.villager.Villager.MOD_ID;


public class WarriorPiglinModel extends GeoModel<WarriorPiglinEntity> {
    @Override
    public Identifier getModelResource(WarriorPiglinEntity animatable) {
        return Identifier.of(MOD_ID, "geo/warrior_piglin.geo.json");
    }

    @Override
    public Identifier getTextureResource(WarriorPiglinEntity animatable) {
        return Identifier.of(MOD_ID, "textures/entity/warrior_piglin.png");
    }

    @Override
    public Identifier getAnimationResource(WarriorPiglinEntity animatable) {
        return Identifier.of(MOD_ID, "animations/warrior_piglin.animation.json");
    }
}
