package dev.upcraft.poppingpresents.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(Dist.CLIENT)
public class PoppingPresentsClientNeo {

    @SubscribeEvent
    public static void registerEntityModels(EntityRenderersEvent.RegisterRenderers event) {
    }

    @SubscribeEvent
    public static void registerModelLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
    }
}
