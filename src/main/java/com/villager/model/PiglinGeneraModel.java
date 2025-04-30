package com.villager.model;

import com.villager.entity.PiglinGeneralEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

import static com.villager.Villager.MOD_ID;


public class PiglinGeneraModel extends GeoModel<PiglinGeneralEntity> {
    @Override
    public Identifier getModelResource(PiglinGeneralEntity animatable) {
        return Identifier.of(MOD_ID, "geo/piglin_general.geo.json");
    }

    @Override
    public Identifier getTextureResource(PiglinGeneralEntity animatable) {
        return Identifier.of(MOD_ID, "textures/entity/piglin_general.png");
    }

    @Override
    public Identifier getAnimationResource(PiglinGeneralEntity animatable) {
        return Identifier.of(MOD_ID, "animations/piglin_general.animation.json");
    }
}
