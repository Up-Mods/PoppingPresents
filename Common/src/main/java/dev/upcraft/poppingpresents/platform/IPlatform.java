package dev.upcraft.poppingpresents.platform;

import dev.upcraft.poppingpresents.PoppingPresents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface IPlatform {

    IPlatform INSTANCE = PoppingPresents.loadService(IPlatform.class);

    String getPlatformName();
    boolean isModLoaded(String modId);
    boolean isDevelopmentEnvironment();
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String id, Supplier<BlockEntityType<T>> blockEntityType);
    <T extends Block> Supplier<T> registerBlock(String id, Function<BlockBehaviour.Properties, T> factory);
    <T extends Block> Supplier<T> registerBlock(String id, Function<BlockBehaviour.Properties, T> factory, Supplier<BlockBehaviour.Properties> propertiesGetter);
    <T extends Entity> Supplier<EntityType<T>> registerEntity(String id, EntityType.EntityFactory<T> entity, MobCategory mobCategory, UnaryOperator<EntityType.Builder<T>> properties);
    <T extends Item> Supplier<T> registerItem(String id, Function<Item.Properties, T> factory);
    <T extends Item> Supplier<T> registerItem(String id, Function<Item.Properties, T> factory, Supplier<Item.Properties> propertiesGetter);
    <T extends SoundEvent> Supplier<T> registerSound(String id, Supplier<T> sound);
    <T extends CreativeModeTab> Supplier<T> registerCreativeModeTab(String id, Supplier<T> tab);
    default <E extends Mob> Supplier<SpawnEggItem> registerSpawnEgg(String id, Supplier<EntityType<E>> entityType) {
        return registerSpawnEgg(id, entityType, Item.Properties::new);
    }
    default <E extends Mob> Supplier<SpawnEggItem> registerSpawnEgg(String id, Supplier<EntityType<E>> entityType, Supplier<Item.Properties> propertiesGetter) {
        return registerItem(id, SpawnEggItem::new, () -> propertiesGetter.get().spawnEgg(entityType.get()));
    }
    CreativeModeTab.Builder newCreativeTabBuilder();
    <T> EntityDataSerializer<T> registerDataSerializer(String id, EntityDataSerializer<T> serializer);
}
