package dev.upcraft.poppingpresents;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(PPCommon.MOD_ID)
public class PoppingPresentsNeo {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(
        Registries.BLOCK_ENTITY_TYPE, PPCommon.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(
        Registries.BLOCK, PPCommon.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(
        Registries.ENTITY_TYPE, PPCommon.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(
        Registries.ITEM, PPCommon.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(
        Registries.SOUND_EVENT, PPCommon.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(
        Registries.CREATIVE_MODE_TAB, PPCommon.MOD_ID);

    public PoppingPresentsNeo(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
        BLOCKS.register(bus);
        ENTITIES.register(bus);
        ITEMS.register(bus);
        SOUND_EVENTS.register(bus);
        CREATIVE_TABS.register(bus);
        PPCommon.init();
    }
}
