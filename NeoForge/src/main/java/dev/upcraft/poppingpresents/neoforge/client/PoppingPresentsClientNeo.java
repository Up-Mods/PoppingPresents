package dev.upcraft.poppingpresents.neoforge.client;

import dev.upcraft.poppingpresents.client.model.entity.PresentEntityModel;
import dev.upcraft.poppingpresents.client.render.entity.PresentEntityRenderer;
import dev.upcraft.poppingpresents.init.PPEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientResourceLoadFinishedEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(Dist.CLIENT)
public class PoppingPresentsClientNeo {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(PPEntities.PRESENT.get(), PresentEntityRenderer::new);
    }

    @SubscribeEvent
    public static void onReload(ClientResourceLoadFinishedEvent event) {
        if(!event.isInitial()) {
            PresentEntityModel.clearCache();
        }
    }
}
