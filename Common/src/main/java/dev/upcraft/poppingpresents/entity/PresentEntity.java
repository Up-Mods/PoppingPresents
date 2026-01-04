package dev.upcraft.poppingpresents.entity;

import dev.upcraft.poppingpresents.init.PPEntityDataSerializers;
import dev.upcraft.poppingpresents.platform.IPlatform;
import dev.upcraft.poppingpresents.present.PresentType;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PresentEntity extends Entity implements GeoEntity, OwnableEntity {

    public static final EntityDataAccessor<Boolean> OPEN = SynchedEntityData.defineId(PresentEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<PresentType> PRESENT_TYPE = SynchedEntityData.defineId(PresentEntity.class, PPEntityDataSerializers.PRESENT_TYPE);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    // TODO open/close
    private int containerListeners;
    private @Nullable EntityReference<LivingEntity> owner;

    public PresentEntity(EntityType<PresentEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OPEN, false);
        builder.define(PRESENT_TYPE, getDefaultPresentType());
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.setOpen(input.getBooleanOr("Open", false));
        this.setPresentType(input.read("PresentType", PresentType.byNameCodec(level())).orElseGet(this::getDefaultPresentType));
        this.owner = EntityReference.read(input, "Owner");
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putBoolean("Open", this.isOpen());
        output.store("PresentType", PresentType.byNameCodec(level()), this.getPresentType());
        EntityReference.store(this.owner, output, "Owner");
    }

    public boolean isOpen() {
        return entityData.get(OPEN);
    }

    public void setOpen(boolean open) {
        entityData.set(OPEN, open);
    }

    public PresentType getPresentType() {
        return entityData.get(PRESENT_TYPE);
    }

    public void setPresentType(PresentType value) {
        entityData.set(PRESENT_TYPE, value);
    }

    public PresentType getDefaultPresentType() {
        var registry = PresentType.registry(level());
        return registry.getValue(PresentType.REGISTRY_DEFAULT_KEY);
    }

    @Override
    public @Nullable EntityReference<LivingEntity> getOwnerReference() {
        return this.owner;
    }

    public void setOwner(LivingEntity entity) {
        this.owner = EntityReference.of(entity);
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(AnimationControllers.MAIN, test -> {
                if(this.isOpen()) {
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

        var tryOpenResult = this.presentInteract(player, hand);
        if(tryOpenResult.consumesAction()) {
            this.gameEvent(GameEvent.ENTITY_INTERACT, player);
            return tryOpenResult;
        }

        return InteractionResult.PASS;
    }

    private InteractionResult presentInteract(Player player, InteractionHand hand) {
        // TODO open screen

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

        public static final RawAnimation STATE_OPEN = RawAnimation.begin().thenPlayAndHold("state.open");
    }
}
