package dev.upcraft.poppingpresents.entity;

import dev.upcraft.poppingpresents.init.PPEntities;
import dev.upcraft.poppingpresents.init.PPEntityDataSerializers;
import dev.upcraft.poppingpresents.item.PresentItem;
import dev.upcraft.poppingpresents.platform.IPlatform;
import dev.upcraft.poppingpresents.present.PresentType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class PresentEntity extends Entity implements GeoEntity, OwnableEntity, ContainerEntity, HasCustomInventoryScreen {

    public static final EntityDataAccessor<Boolean> DATA_ID_OPEN = SynchedEntityData.defineId(PresentEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Holder<PresentType>> DATA_ID_PRESENT_TYPE = SynchedEntityData.defineId(PresentEntity.class, PPEntityDataSerializers.PRESENT_TYPE);
    protected static final EntityDataAccessor<Integer> DATA_ID_HURT = SynchedEntityData.defineId(PresentEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> DATA_ID_HURTDIR = SynchedEntityData.defineId(PresentEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Float> DATA_ID_DAMAGE = SynchedEntityData.defineId(PresentEntity.class, EntityDataSerializers.FLOAT);
    public static final String NBT_KEY_PRESENT_TYPE = "PresentType";
    public static final String NBT_KEY_OWNER = "Owner";
    private static final int MAX_SLOTS = 27;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private NonNullList<ItemStack> inventory = NonNullList.withSize(MAX_SLOTS, ItemStack.EMPTY);
    private @Nullable ResourceKey<LootTable> lootTable;
    private long lootTableSeed = 0L;
    private @Nullable EntityReference<LivingEntity> owner;

    // FIXME write custom openers counter that works with entities
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            animateOpen();
            setOpen(true);
            System.out.println("OPENING!");
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            animateClose();
            setOpen(false);
            System.out.println("CLOSING!");
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int count, int openCount) {
            // NO-OP
        }

        @Override
        public boolean isOwnContainer(Player player) {
            // TODO update when making custom GUI class
            return player.containerMenu instanceof ChestMenu chest
                && chest.getContainer() == PresentEntity.this;
        }
    };

    public PresentEntity(EntityType<PresentEntity> entityType, Level level) {
        super(entityType, level);
        this.refreshDimensions();
    }

    public PresentEntity(Level level, Holder<PresentType> type, long lootTableSeed) {
        this(PPEntities.PRESENT.get(), level);
        this.setPresentType(type);
        this.setContainerLootTableSeed(lootTableSeed);
        this.setContainerLootTable(type.value().lootTable(level.registryAccess()));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ID_OPEN, false);
        builder.define(DATA_ID_PRESENT_TYPE, getDefaultPresentType());
        builder.define(DATA_ID_HURT, 0);
        builder.define(DATA_ID_HURTDIR, 1);
        builder.define(DATA_ID_DAMAGE, 0.0F);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if(this.tickCount % 5 == 0) {
            openersCounter.recheckOpeners(level(), blockPosition(), Blocks.BEDROCK.defaultBlockState());
        }
    }

    @Override
    public boolean hurtClient(DamageSource damageSource) {
        return true;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        if (this.isRemoved()) {
            return true;
        } else if (this.isInvulnerableToBase(damageSource)) {
            return false;
        } else {
            this.setHurtDir(-this.getHurtDir());
            this.setHurtTime(10);
            this.markHurt();
            this.setDamage(this.getDamage() + amount * 10.0F);
            this.gameEvent(GameEvent.ENTITY_DAMAGE, damageSource.getEntity());
            boolean flag = damageSource.getEntity() instanceof Player player && player.getAbilities().instabuild;
            if ((flag || !(this.getDamage() > 40.0F)) && !this.shouldSourceDestroy(damageSource)) {
                if (flag) {
                    this.discard();
                }
            } else {
                this.destroy(level, damageSource);
            }

            return true;
        }
    }

    protected void destroy(ServerLevel level, DamageSource damageSource) {
        this.kill(level);
        this.chestVehicleDestroyed(damageSource, level, this);
    }

    protected boolean shouldSourceDestroy(DamageSource damageSource) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        input.read(NBT_KEY_PRESENT_TYPE, PresentType.HOLDER_CODEC).ifPresent(this::setPresentType);
        this.owner = EntityReference.read(input, NBT_KEY_OWNER);
        this.readChestVehicleSaveData(input);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.store(NBT_KEY_PRESENT_TYPE, PresentType.HOLDER_CODEC, this.getPresentType());
        EntityReference.store(this.owner, output, NBT_KEY_OWNER);
        this.addChestVehicleSaveData(output);
    }

    public boolean isOpen() {
        return entityData.get(DATA_ID_OPEN);
    }

    public void setOpen(boolean open) {
        entityData.set(DATA_ID_OPEN, open);
    }

    public Holder<PresentType> getPresentType() {
        return entityData.get(DATA_ID_PRESENT_TYPE);
    }

    public int getHurtTime() {
        return this.entityData.get(DATA_ID_HURT);
    }

    public void setHurtTime(int hurtTime) {
        this.entityData.set(DATA_ID_HURT, hurtTime);
    }

    public int getHurtDir() {
        return this.entityData.get(DATA_ID_HURTDIR);
    }

    public void setHurtDir(int hurtDir) {
        this.entityData.set(DATA_ID_HURTDIR, hurtDir);
    }

    public float getDamage() {
        return this.entityData.get(DATA_ID_DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DATA_ID_DAMAGE, damage);
    }

    public void setPresentType(Holder<PresentType> value) {
        entityData.set(DATA_ID_PRESENT_TYPE, value);
        refreshDimensions();
    }

    public Holder<PresentType> getDefaultPresentType() {
        var registry = PresentType.registry(level());
        return registry.getOrThrow(PresentType.REGISTRY_DEFAULT_KEY);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if(DATA_ID_PRESENT_TYPE.equals(key)) {
            refreshDimensions();
        }
    }

    @Override
    public @Nullable EntityReference<LivingEntity> getOwnerReference() {
        return this.owner;
    }

    public void setOwner(LivingEntity entity) {
        this.owner = EntityReference.of(entity);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        var type = getPresentType().value();
        return EntityDimensions.fixed(type.width(), type.height());
    }

    @Override
    public boolean canBeCollidedWith(@Nullable Entity other) {
        if(!this.isAlive()) {
            return false;
        }

        if(other instanceof LivingEntity livingEntity) {
            if(livingEntity.isShiftKeyDown()) {
                return false;
            }

            if(owner != null) {
                return owner.matches(livingEntity);
            }

            return other instanceof Player player && !IPlatform.INSTANCE.isFakePlayer(player);
        }

        return !this.isOpen();
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public @Nullable ItemStack getPickResult() {
        return PresentItem.forType(this.getPresentType());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(AnimationControllers.MAIN, test -> {
                if(this.isOpen()) {
                    System.out.println("OPEN!!");
                    return test.setAndContinue(Animations.STATE_OPEN);
                }

                return PlayState.STOP;
            })
            .triggerableAnim(AnimationTriggers.TRIGGER_OPEN, Animations.INTERACT_OPEN)
            .triggerableAnim(AnimationTriggers.TRIGGER_CLOSE, Animations.INTERACT_CLOSE)
        );
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if(!this.isAlive()) {
            return InteractionResult.PASS;
        }

        var superResult = super.interact(player, hand);
        if(superResult != InteractionResult.PASS) {
            return superResult;
        }

        var tryOpenResult = this.interactWithContainerVehicle(player);
        if(tryOpenResult.consumesAction()) {
            this.gameEvent(GameEvent.ENTITY_INTERACT, player);
            return tryOpenResult;
        }

        return InteractionResult.PASS;
    }

    public void animateOpen() {
        if(!level().isClientSide()) {
            stopTriggeredAnim(AnimationControllers.MAIN, AnimationTriggers.TRIGGER_CLOSE);
            triggerAnim(AnimationControllers.MAIN, AnimationTriggers.TRIGGER_OPEN);
        }
    }

    public void animateClose() {
        if(!level().isClientSide()) {
            stopTriggeredAnim(AnimationControllers.MAIN, AnimationTriggers.TRIGGER_OPEN);
            triggerAnim(AnimationControllers.MAIN, AnimationTriggers.TRIGGER_CLOSE);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public @Nullable ResourceKey<LootTable> getContainerLootTable() {
        return this.lootTable;
    }

    @Override
    public void setContainerLootTable(@Nullable ResourceKey<LootTable> lootTable) {
        this.lootTable = lootTable;
    }

    @Override
    public long getContainerLootTableSeed() {
        return this.lootTableSeed;
    }

    @Override
    public void setContainerLootTableSeed(long lootTableSeed) {
        this.lootTableSeed = lootTableSeed;
    }

    @Override
    public NonNullList<ItemStack> getItemStacks() {
        return this.inventory;
    }

    @Override
    public void clearItemStacks() {
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    }

    @Override
    public int getContainerSize() {
        return MAX_SLOTS;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.getChestVehicleItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return this.removeChestVehicleItem(slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return this.removeChestVehicleItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.setChestVehicleItem(slot, stack);
    }

    @Override
    public void setChanged() {
        // NO-OP
    }

    @Override
    public boolean stillValid(Player player) {
        return this.isChestVehicleStillValid(player);
    }

    @Override
    public void clearContent() {
        this.clearChestVehicleContent();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        if (this.lootTable != null && player.isSpectator()) {
            return null;
        } else {
            this.unpackChestVehicleLootTable(player);
            return ChestMenu.threeRows(containerId, playerInventory, this);
        }
    }

    @Override
    public void startOpen(ContainerUser user) {
        if(this.isAlive() && !user.getLivingEntity().isSpectator()) {
            openersCounter.incrementOpeners(user.getLivingEntity(), level(), blockPosition(), Blocks.BEDROCK.defaultBlockState(), user.getContainerInteractionRange());
        }
    }

    @Override
    public void stopOpen(ContainerUser user) {
        if(this.isAlive() && !user.getLivingEntity().isSpectator()) {
            openersCounter.incrementOpeners(user.getLivingEntity(), level(), blockPosition(), Blocks.BEDROCK.defaultBlockState(), user.getContainerInteractionRange());
        }
    }

    @Override
    public List<ContainerUser> getEntitiesWithContainerOpen() {
        return openersCounter.getEntitiesWithContainerOpen(level(), blockPosition());
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        // TODO setting this to false messes up the loot table generation :/
        return true;
    }

    @Override
    public InteractionResult interactWithContainerVehicle(Player player) {
        this.openCustomInventoryScreen(player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void openCustomInventoryScreen(Player player) {
        player.openMenu(this);
        if (this.level() instanceof ServerLevel serverLevel) {
            this.gameEvent(GameEvent.CONTAINER_OPEN, player);
            PiglinAi.angerNearbyPiglins(serverLevel, player, true);
        }
    }

    @Override
    protected Component getTypeName() {
        return PresentType.translate(getPresentType());
    }

    public static class AnimationControllers {
        public static final String MAIN = "main";
    }

    public static class AnimationTriggers {
        public static final String TRIGGER_OPEN = "trigger.open";
        public static final String TRIGGER_CLOSE = "trigger.close";
    }

    public static class Animations {
        public static final RawAnimation INTERACT_OPEN = RawAnimation.begin().thenPlay("interact.open");
        public static final RawAnimation INTERACT_CLOSE = RawAnimation.begin().thenPlay("interact.close");

        public static final RawAnimation STATE_OPEN = RawAnimation.begin().thenPlay("state.open");
    }
}
