package dev.upcraft.poppingpresents.entity;

import dev.upcraft.poppingpresents.init.PPEntityDataSerializers;
import dev.upcraft.poppingpresents.init.PPPresents;
import dev.upcraft.poppingpresents.present.PresentType;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class PresentEntity extends Entity {

    public static final EntityDataAccessor<Boolean> OPEN = SynchedEntityData.defineId(PresentEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<PresentType> PRESENT_TYPE = SynchedEntityData.defineId(PresentEntity.class, PPEntityDataSerializers.PRESENT_TYPE);

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
        this.setPresentType(input.read("present_type", PPPresents.registry().byNameCodec()).orElseGet(this::getDefaultPresentType));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putBoolean("open", this.isOpen());
        output.store("present_type", PPPresents.registry().byNameCodec(), this.getPresentType());
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
        var registry = PPPresents.registry();
        return registry.getValue(registry.getDefaultKey());
    }
}
