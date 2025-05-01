package com.villager.mixin;

import com.villager.access.VillagerFriendshipAccess;
import com.villager.entity.ChefPiglinEntity;
import com.villager.entity.DrumPiglinEntity;
import com.villager.entity.ExclamationEntity;
import com.villager.entity.ModEntities;
import com.villager.screen.VillagerScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;

@Mixin(VillagerEntity.class)
public class VillagerMixin implements VillagerFriendshipAccess {
    @Unique
    private final VillagerEntity self = (VillagerEntity) (Object) this;

    @Unique
    private long lastGiftReceivedTime = 0;
    @Unique
    private static final long GIFT_COOLDOWN_TIME = 30000;

    @Unique
    private static final int MAX_DAILY_FRIENDSHIP = 80;

    @Unique
    private long lastDay = 0;

    @Unique
    private int dailyGainedFriendship = 0;

    @Unique
    private UUID exclamationId = null;

    @Unique
    private static final TrackedData<Integer> FRIENDSHIP_LEVEL =
            DataTracker.registerData(VillagerEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Unique
    private static final double EXCLAMATION_OFFSET_Y = 2.5;


    @Unique
    private LivingEntity forcedLookTarget = null;

    @Unique
    private static TrackedData<Boolean> AI_DISABLED = DataTracker.registerData(VillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);;

    @Unique private UUID redExclamationId = null;
    @Unique private static final double RED_EXCLAMATION_OFFSET_Y = 2.8;
    @Unique
    private boolean villagerProtected = false;

    @Override
    public void setAiDisabledByScreen(boolean disabled) {
        self.getDataTracker().set(AI_DISABLED, disabled);
        if (!disabled) {
            this.forcedLookTarget = null;
        }
    }

    @Override
    public void setForcedLookTarget(LivingEntity target) {
        this.forcedLookTarget = target;
    }

    @Override
    public void setFriendshipLevel(int level) {
        self.getDataTracker().set(FRIENDSHIP_LEVEL, Math.min(level, 100000));
        System.out.println(self.getDataTracker().get(FRIENDSHIP_LEVEL));
    }

    @Override
    public void setVillagerProtected(boolean value) {
        this.villagerProtected = value;
    }

    @Override
    public boolean isVillagerProtected() {
        return this.villagerProtected;
    }

    @Override
    public void removeRedExclamation() {
        removeExclamationIfPresent(redExclamationId);
        redExclamationId = null;
    }

    @Unique
    private boolean villagerHasConfessed = false;

    @Override
    public boolean hasConfessed() {
        return villagerHasConfessed;
    }

    @Override
    public void setConfessed(boolean confessed) {
        this.villagerHasConfessed = confessed;
    }

    private static final TrackedData<Boolean> MARRIED =
            DataTracker.registerData(VillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Override
    public boolean isMarried() {
        return self.getDataTracker().get(MARRIED);
    }

    @Override
    public void setMarried(boolean married) {
        self.getDataTracker().set(MARRIED, married);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void injectInitDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(FRIENDSHIP_LEVEL, 0);
        builder.add(AI_DISABLED, false);
        builder.add(MARRIED, false);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void injectWriteNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("FriendshipLevel", self.getDataTracker().get(FRIENDSHIP_LEVEL));
        nbt.putBoolean("AIDisabled", self.getDataTracker().get(AI_DISABLED));
        nbt.putLong("LastGiftReceivedTime", this.lastGiftReceivedTime);
        nbt.putLong("VillagerLastDay", this.lastDay);
        nbt.putInt("VillagerDailyGained", this.dailyGainedFriendship);
        nbt.putBoolean("VillagerMarried", self.getDataTracker().get(MARRIED));
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void injectReadNbt(NbtCompound nbt, CallbackInfo ci) {
        self.getDataTracker().set(FRIENDSHIP_LEVEL, nbt.getInt("FriendshipLevel"));
        self.getDataTracker().set(AI_DISABLED, nbt.getBoolean("AIDisabled"));
        this.lastGiftReceivedTime = nbt.getLong("LastGiftReceivedTime");
        this.lastDay = nbt.getLong("VillagerLastDay");
        this.dailyGainedFriendship = nbt.getInt("VillagerDailyGained");
        self.getDataTracker().set(MARRIED, nbt.getBoolean("VillagerMarried"));
    }

    @Inject(
            method = "interactMob",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!player.getWorld().isClient()) return;

        VillagerFriendshipAccess access = this;
        int level = access.getFriendshipLevel();
        if (level <= -60) {
            cir.setReturnValue(ActionResult.PASS);
            return;
        }

        if (self.getVillagerData().getProfession() == VillagerProfession.NITWIT) {
            MinecraftClient.getInstance().setScreen(new VillagerScreen((VillagerEntity)(Object)this));
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    @Inject(method = "mobTick", at = @At("HEAD"))
    private void onMobTick(CallbackInfo ci) {
        long currentTime = System.currentTimeMillis();
        int level = self.getDataTracker().get(FRIENDSHIP_LEVEL);

        if (self.getDataTracker().get(FRIENDSHIP_LEVEL) >= 100) {
            if (currentTime - lastGiftReceivedTime >= GIFT_COOLDOWN_TIME) {
                World world = self.getWorld();
                BlockPos villagerPos = self.getBlockPos();
                Box searchArea = new Box(villagerPos).expand(3);
                List<PlayerEntity> nearbyPlayers = world.getEntitiesByClass(PlayerEntity.class, searchArea, player -> true);

                if (!nearbyPlayers.isEmpty()) {
                    PlayerEntity player = nearbyPlayers.getFirst();
                    generateRandomGift(self, player);
                    lastGiftReceivedTime = currentTime;
                    player.sendMessage(Text.of("村民送了你一点礼物"), true);
                }
            }
        }

        if (self.getDataTracker().get(AI_DISABLED)) {
            self.setVelocity(Vec3d.ZERO);
            self.getNavigation().stop();
            if (forcedLookTarget != null) {
                double dx = forcedLookTarget.getX() - self.getX();
                double dz = forcedLookTarget.getZ() - self.getZ();
                float yaw = (float) (MathHelper.atan2(dz, dx) * (180.0F / Math.PI)) - 90.0F;
                self.setYaw(yaw);
                self.setHeadYaw(yaw);
                self.setBodyYaw(yaw);
            }
        }

        if (isDailyCapReached()) {
            if (exclamationId == null || !isEntityValid(exclamationId)) {
                spawnExclamation(EXCLAMATION_OFFSET_Y, id -> exclamationId = id, false);
            }
            removeExclamationIfPresent(redExclamationId);
            redExclamationId = null;
        } else if (level >= 80 && !villagerProtected && !isDailyCapReached()) {
            if (redExclamationId == null || !isEntityValid(redExclamationId)) {
                spawnExclamation(RED_EXCLAMATION_OFFSET_Y, id -> redExclamationId = id, true);
            }
            removeExclamationIfPresent(exclamationId);
            exclamationId = null;
        } else {
            removeExclamationIfPresent(exclamationId);
            removeExclamationIfPresent(redExclamationId);
            exclamationId = null;
            redExclamationId = null;
        }
    }

    @Unique
    private void spawnExclamation(double offsetY, java.util.function.Consumer<UUID> setId, boolean isRed) {
        ServerWorld world = (ServerWorld) self.getWorld();
        ExclamationEntity ex = new ExclamationEntity(ModEntities.EXCLAMATION, world);
        ex.setOwner(self);
        ex.setRed(isRed);
        ex.refreshPositionAndAngles(
                self.getX(),
                self.getY() + offsetY,
                self.getZ(),
                0, 0
        );
        world.spawnEntity(ex);
        setId.accept(ex.getUuid());
    }

    @Unique
    private boolean isEntityValid(UUID uuid) {
        Entity e = ((ServerWorld) self.getWorld()).getEntity(uuid);
        return e instanceof ExclamationEntity && e.isAlive();
    }

    @Unique
    private void removeExclamationIfPresent(UUID uuid) {
        if (uuid != null) {
            Entity e = ((ServerWorld) self.getWorld()).getEntity(uuid);
            if (e instanceof ExclamationEntity) {
                e.discard();
            }
        }
    }

    @Override
    public int tryIncreaseFriendship(int amount) {
        refreshDailyIfNeeded();

        int current = self.getDataTracker().get(FRIENDSHIP_LEVEL);

        if (amount < 0) {
            self.getDataTracker().set(FRIENDSHIP_LEVEL, Math.max(current + amount, -100));
            return amount;
        }

        int remaining = MAX_DAILY_FRIENDSHIP - dailyGainedFriendship;
        if (remaining <= 0) return 0;

        int actualAdd = Math.min(amount, remaining);
        self.getDataTracker().set(FRIENDSHIP_LEVEL, Math.min(current + actualAdd, 100));
        dailyGainedFriendship += actualAdd;
        return actualAdd;
    }

    @Override
    public int getFriendshipLevel() {
        return self.getDataTracker().get(FRIENDSHIP_LEVEL);
    }

    @Unique
    private static void generateRandomGift(VillagerEntity villager, PlayerEntity player) {
        World world = player.getWorld();

        Item[] giftItems = {
                Items.DIAMOND,
                Items.GOLDEN_APPLE,
                Items.IRON_INGOT,
                Items.EMERALD,
                Items.GOLD_INGOT,
        };

        Item randomItem = giftItems[player.getRandom().nextInt(giftItems.length)];
        spawnItem(world, villager, randomItem, player);
    }

    @Unique
    private static void spawnItem(World world, VillagerEntity villager, Item item, PlayerEntity player) {
        ItemStack stack = new ItemStack(item);
        stack.setCount(world.getRandom().nextInt(15) + 1);

        double offsetX = player.getX() - villager.getX();
        double offsetY = player.getY() - villager.getY();
        double offsetZ = player.getZ() - villager.getZ();

        ItemEntity itemEntity = new ItemEntity(world, villager.getX(), villager.getY() + 1, villager.getZ(), stack);
        itemEntity.setVelocity(offsetX / 10, offsetY / 10, offsetZ / 10);
        world.spawnEntity(itemEntity);
    }

    @Override
    public boolean isDailyCapReached() {
        refreshDailyIfNeeded();
        return dailyGainedFriendship >= MAX_DAILY_FRIENDSHIP;
    }

    private void refreshDailyIfNeeded() {
        long currentDay = self.getWorld().getTimeOfDay() / 24000L;
        if (currentDay != lastDay) {
            lastDay = currentDay;
            dailyGainedFriendship = 0;
        }
    }

    @Override
    public boolean hasExclamation() {
        if (exclamationId == null) return false;
        Entity entity = ((ServerWorld) self.getWorld()).getEntity(exclamationId);
        return entity instanceof ExclamationEntity && entity.isAlive();
    }

//    @Inject(method = "onDeath", at = @At("HEAD"))
//    private void onVillagerDeath(DamageSource source, CallbackInfo ci) {
//        if (!self.getWorld().isClient()) {
//            removeExclamationIfPresent(exclamationId);
//            removeExclamationIfPresent(redExclamationId);
//            exclamationId = null;
//            redExclamationId = null;
//
//            ServerWorld world = (ServerWorld) self.getWorld();
//            world.getOtherEntities(self, self.getBoundingBox().expand(32.0), entity -> (entity instanceof ChefPiglinEntity || entity instanceof DrumPiglinEntity))
//                    .forEach(Entity::discard);;
//        }
//    }
}
