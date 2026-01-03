package dev.upcraft.poppingpresents.client.render.entity;

import dev.upcraft.poppingpresents.entity.PresentEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class PresentEntityRenderer extends EntityRenderer<PresentEntity, PresentEntityRenderer.State> {

    public PresentEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    public static class State extends EntityRenderState {

    }
}
