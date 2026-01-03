package dev.upcraft.poppingpresents.fabric.platform;

import com.google.auto.service.AutoService;
import com.mojang.logging.LogUtils;
import dev.upcraft.poppingpresents.PPCommon;
import dev.upcraft.poppingpresents.platform.IPlatform;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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
import org.slf4j.Logger;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@AutoService(IPlatform.class)
public class PlatformFabric implements IPlatform {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(PPCommon.MOD_ID);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(
        String id, Supplier<BlockEntityType<T>> blockEntityType) {
        return registerSupplier(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, blockEntityType);
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(String id, Function<BlockBehaviour.Properties, T> factory) {
        return registerBlock(id, factory, BlockBehaviour.Properties::of);
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(String id, Function<BlockBehaviour.Properties, T> factory, Supplier<BlockBehaviour.Properties> propertiesGetter) {
        return registerWithProperties(BuiltInRegistries.BLOCK, id, factory, propertiesGetter, (resourceKey, props) -> props.setId(resourceKey));
    }

    @Override
    public <T extends Entity> Supplier<EntityType<T>> registerEntity(String id, EntityType.EntityFactory<T> entity, MobCategory mobCategory, UnaryOperator<EntityType.Builder<T>> properties) {
        return registerSupplier(BuiltInRegistries.ENTITY_TYPE, id, () -> properties.apply(EntityType.Builder.of(entity, mobCategory)).build(ResourceKey.create(Registries.ENTITY_TYPE, PPCommon.id(id))));
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(String id, Function<Item.Properties, T> factory) {
        return registerItem(id, factory, Item.Properties::new);
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(String id, Function<Item.Properties, T> factory, Supplier<Item.Properties> propertiesGetter) {
        return registerWithProperties(BuiltInRegistries.ITEM, id, factory, propertiesGetter, (resourceKey, props) -> props.setId(resourceKey));
    }

    @Override
    public <T extends SoundEvent> Supplier<T> registerSound(String id, Supplier<T> sound) {
        return registerSupplier(BuiltInRegistries.SOUND_EVENT, id, sound);
    }

    @Override
    public <T extends CreativeModeTab> Supplier<T> registerCreativeModeTab(String id, Supplier<T> tab) {
        return registerSupplier(BuiltInRegistries.CREATIVE_MODE_TAB, id, tab);
    }

    @Override
    public <E extends Mob> Supplier<SpawnEggItem> registerSpawnEgg(String id, Supplier<EntityType<E>> entityType, Supplier<Item.Properties> properties) {
        return registerItem(id, SpawnEggItem::new, () -> properties.get().spawnEgg(entityType.get()));
    }

    @Override
    public CreativeModeTab.Builder newCreativeTabBuilder() {
        return FabricItemGroup.builder();
    }

    private static <T> Supplier<T> registerSupplier(Registry<? super T> registry, String id, Supplier<T> factory) {
        var registeredObject = Registry.register(registry, PPCommon.id(id), factory.get());
        return () -> registeredObject;
    }

    private static <R, T extends R, P> Supplier<T> registerWithProperties(Registry<R> registry, String id, Function<P, T> factory, Supplier<P> propertiesGetter, BiFunction<ResourceKey<R>, P, P> keySetter) {
        var registryId = ResourceKey.create(registry.key(), PPCommon.id(id));
        var registeredObject = Registry.register(registry, registryId, factory.apply(keySetter.apply(registryId, propertiesGetter.get())));
        return () -> registeredObject;
    }

    private static <T, R extends Registry<? super T>> Holder<T> registerHolder(Registry<? super T> registry, String id, Supplier<T> factory) {
        return Registry.registerForHolder(registry, PPCommon.id(id), factory.get());
    }
}
