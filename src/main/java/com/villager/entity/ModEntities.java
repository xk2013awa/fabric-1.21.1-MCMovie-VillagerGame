package com.villager.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.villager.Villager.MOD_ID;

public class ModEntities {
    public static final EntityType<ExclamationEntity> EXCLAMATION = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "exclamation"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ExclamationEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                    .build()
    );

    public static final EntityType<MalgoshaEntity> MALGOSHA = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "malgosha"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, MalgoshaEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                    .build()
    );

    public static final EntityType<FireballSmallEntity> FIREBALL_SMALL = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "fireball_small"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, FireballSmallEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .build()
    );

    public static final EntityType<GreatHogEntity> GREAT_HOG = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "great_hog"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, GreatHogEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                    .build()
    );

    public static final EntityType<SpearPiglinEntity> SPEAR_PIGLIN = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "spear_piglin"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SpearPiglinEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                    .build()
    );

    public static final EntityType<SpearEntity> SPEAR = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "spear"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SpearEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                    .build()
    );

    public static final EntityType<ChefPiglinEntity> CHEF_PIGLIN = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "chef_piglin"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ChefPiglinEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                    .build()
    );

    public static final EntityType<DrumPiglinEntity> DRUM_PIGLIN = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "drum_piglin"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, DrumPiglinEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                    .build()
    );

    public static final EntityType<WarriorPiglinEntity> WARRIOR_PIGLIN = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "warrior_piglin"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, WarriorPiglinEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                    .build()
    );
    public static final EntityType<PickaxePiglinEntity> PICKAXE_PIGLIN = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "pickaxe_piglin"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, PickaxePiglinEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                    .build()
    );

    public static final EntityType<HammerPiglinEntity> HAMMER_PIGLIN = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "hammer_piglin"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, HammerPiglinEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                    .build()
    );

    public static final EntityType<PiglinGeneralEntity> PIGLIN_GENERAL = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "piglin_general"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, PiglinGeneralEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                    .build()
    );
    
    public static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(ModEntities.MALGOSHA, MalgoshaEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.GREAT_HOG, GreatHogEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.SPEAR_PIGLIN, SpearPiglinEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.CHEF_PIGLIN, ChefPiglinEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.DRUM_PIGLIN, DrumPiglinEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.WARRIOR_PIGLIN, WarriorPiglinEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.PICKAXE_PIGLIN, PickaxePiglinEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.HAMMER_PIGLIN, HammerPiglinEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.PIGLIN_GENERAL, PiglinGeneralEntity.createMobAttributes());
    }
    public static void registerEntities() {
    }

}
