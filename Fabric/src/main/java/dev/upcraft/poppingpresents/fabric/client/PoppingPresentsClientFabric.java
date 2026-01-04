package dev.upcraft.poppingpresents.fabric.client;

import dev.upcraft.poppingpresents.PoppingPresents;
import dev.upcraft.poppingpresents.client.model.entity.PresentEntityModel;
import dev.upcraft.poppingpresents.client.render.entity.PresentEntityRenderer;
import dev.upcraft.poppingpresents.init.PPEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.ResourceReloaderKeys;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class PoppingPresentsClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRenderers.register(PPEntities.PRESENT.get(), PresentEntityRenderer::new);
        var resourceLoader = ResourceLoader.get(PackType.CLIENT_RESOURCES);
        var modelDataId = PoppingPresents.id("model_data");
        resourceLoader.registerReloader(modelDataId, (ResourceManagerReloadListener) manager -> PresentEntityModel.clearCache());
        resourceLoader.addReloaderOrdering(ResourceReloaderKeys.Client.MODELS, modelDataId);
    }
}
