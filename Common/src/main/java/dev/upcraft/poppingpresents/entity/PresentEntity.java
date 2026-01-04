package dev.upcraft.poppingpresents.entity;

import dev.upcraft.poppingpresents.init.PPEntityDataSerializers;
import dev.upcraft.poppingpresents.present.PresentType;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PresentEntity extends Entity implements GeoEntity {

    public static final EntityDataAccessor<Boolean> OPEN = SynchedEntityData.defineId(PresentEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<PresentType> PRESENT_TYPE = SynchedEntityData.defineId(PresentEntity.class, PPEntityDataSerializers.PRESENT_TYPE);

    protected static final RawAnimation ANIM_OPEN = RawAnimation.begin().thenPlay("interact.open");
    protected static final RawAnimation ANIM_CLOSE = RawAnimation.begin().thenPlay("interact.close");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    // TODO open/close
    private int containerListeners;

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
        this.setOpen(input.getBooleanOr("open", false));
        this.setPresentType(input.read("present_type", PresentType.byNameCodec(level())).orElseGet(this::getDefaultPresentType));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putBoolean("open", this.isOpen());
        output.store("present_type", PresentType.byNameCodec(level()), this.getPresentType());
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("open_close", test -> {
                // TODO "state_open" animation
                return PlayState.STOP;
            })
            .triggerableAnim("open", ANIM_OPEN)
            .triggerableAnim("close", ANIM_CLOSE)
        );
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        return super.interact(player, hand);
    }

    public void animateOpen() {
        if(!level().isClientSide()) {
            stopTriggeredAnim("open_close", "close");
            triggerAnim("open_close", "open");
        }
    }

    public void animateClose() {
        if(!level().isClientSide()) {
            stopTriggeredAnim("open_close", "open");
            triggerAnim("open_close", "close");
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
