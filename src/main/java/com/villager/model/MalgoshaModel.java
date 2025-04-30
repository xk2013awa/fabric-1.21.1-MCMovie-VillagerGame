package com.villager.model;

import com.villager.entity.MalgoshaEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

import static com.villager.Villager.MOD_ID;


public class MalgoshaModel extends GeoModel<MalgoshaEntity> {
    @Override
    public Identifier getModelResource(MalgoshaEntity animatable) {
        return Identifier.of(MOD_ID, "geo/malgosha.geo.json");
    }

    @Override
    public Identifier getTextureResource(MalgoshaEntity animatable) {
        return Identifier.of(MOD_ID, "textures/entity/malgosha.png");
    }

    @Override
    public Identifier getAnimationResource(MalgoshaEntity animatable) {
        return Identifier.of(MOD_ID, "animations/malgosha.animation.json");
    }
}
