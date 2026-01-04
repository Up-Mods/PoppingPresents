package dev.upcraft.poppingpresents.fabric.client;

import dev.upcraft.poppingpresents.client.model.entity.PresentEntityModel;
import dev.upcraft.poppingpresents.client.render.entity.PresentEntityRenderer;
import dev.upcraft.poppingpresents.init.PPEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class PoppingPresentsClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRenderers.register(PPEntities.PRESENT.get(), PresentEntityRenderer::new);
        InvalidateRenderStateCallback.EVENT.register(PresentEntityModel::clearCache);
    }
}
