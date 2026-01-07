package dev.upcraft.poppingpresents.neoforge;

import dev.upcraft.poppingpresents.PoppingPresents;
import dev.upcraft.poppingpresents.present.PresentType;
import dev.upcraft.poppingpresents.util.PPHooks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@EventBusSubscriber
@Mod(PoppingPresents.MOD_ID)
public class PoppingPresentsNeo {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, PoppingPresents.MOD_ID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(PoppingPresents.MOD_ID);
    public static final DeferredRegister.Entities ENTITIES = DeferredRegister.createEntities(PoppingPresents.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PoppingPresents.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, PoppingPresents.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PoppingPresents.MOD_ID);
    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, PoppingPresents.MOD_ID);

    public PoppingPresentsNeo(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
        BLOCKS.register(bus);
        CREATIVE_TABS.register(bus);
        ENTITIES.register(bus);
        ENTITY_DATA_SERIALIZERS.register(bus);
        ITEMS.register(bus);
        SOUND_EVENTS.register(bus);
        PoppingPresents.init();
    }

    @SubscribeEvent
    public static void registerDynamicRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(PresentType.REGISTRY_ID, PresentType.CODEC, PresentType.CODEC, builder -> builder.sync(true));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onFinishSleeping(SleepFinishedTimeEvent event) {
        if(event.getLevel() instanceof ServerLevelAccessor serverLevelAccessor) {
            PPHooks.onFinishedSleeping(serverLevelAccessor);
        }
    }
}
