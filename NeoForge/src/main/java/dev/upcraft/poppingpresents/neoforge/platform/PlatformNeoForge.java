package dev.upcraft.poppingpresents.neoforge.platform;

import com.google.auto.service.AutoService;
import dev.upcraft.poppingpresents.PoppingPresents;
import dev.upcraft.poppingpresents.neoforge.PoppingPresentsNeo;
import dev.upcraft.poppingpresents.platform.IPlatform;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@AutoService(IPlatform.class)
public class PlatformNeoForge implements IPlatform {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(PoppingPresents.MOD_ID);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.isProduction();
    }

    @Override
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String id, Supplier<BlockEntityType<T>> blockEntityType) {
        return PoppingPresentsNeo.BLOCK_ENTITIES.register(id, blockEntityType);
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(String id, Function<BlockBehaviour.Properties, T> factory) {
        return PoppingPresentsNeo.BLOCKS.registerBlock(id, factory);
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(String id, Function<BlockBehaviour.Properties, T> factory, Supplier<BlockBehaviour.Properties> propertiesGetter) {
        return PoppingPresentsNeo.BLOCKS.registerBlock(id, factory, propertiesGetter);
    }

    @Override
    public <T extends Entity> Supplier<EntityType<T>> registerEntity(String id, EntityType.EntityFactory<T> entity, MobCategory mobCategory, UnaryOperator<EntityType.Builder<T>> properties) {
        return PoppingPresentsNeo.ENTITIES.registerEntityType(id, entity, mobCategory, properties);
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(String id, Function<Item.Properties, T> factory) {
        return PoppingPresentsNeo.ITEMS.registerItem(id, factory);
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(String id, Function<Item.Properties, T> factory, Supplier<Item.Properties> propertiesGetter) {
        return PoppingPresentsNeo.ITEMS.registerItem(id, factory, propertiesGetter);
    }

    @Override
    public <T extends SoundEvent> Supplier<T> registerSound(String id, Supplier<T> sound) {
        return PoppingPresentsNeo.SOUND_EVENTS.register(id, sound);
    }

    @Override
    public <T extends CreativeModeTab> Supplier<T> registerCreativeModeTab(String id, Supplier<T> tab) {
        return PoppingPresentsNeo.CREATIVE_TABS.register(id, tab);
    }

    @Override
    public CreativeModeTab.Builder newCreativeTabBuilder() {
        return CreativeModeTab.builder();
    }
}
