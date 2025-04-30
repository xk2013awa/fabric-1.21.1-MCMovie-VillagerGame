package com.villager.model;

import com.villager.entity.DrumPiglinEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

import static com.villager.Villager.MOD_ID;

public class DrumPiglinModel extends GeoModel<DrumPiglinEntity> {
    @Override
    public Identifier getModelResource(DrumPiglinEntity animatable) {
        return Identifier.of(MOD_ID, "geo/drum_piglin.geo.json");
    }

    @Override
    public Identifier getTextureResource(DrumPiglinEntity animatable) {
        return Identifier.of(MOD_ID, "textures/entity/drum_piglin.png");
    }

    @Override
    public Identifier getAnimationResource(DrumPiglinEntity animatable) {
        return Identifier.of(MOD_ID, "animations/drum_piglin.animation.json");
    }
}
