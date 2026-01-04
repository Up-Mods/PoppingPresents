package dev.upcraft.poppingpresents.client.render.entity;

import dev.upcraft.poppingpresents.client.model.entity.PresentEntityModel;
import dev.upcraft.poppingpresents.entity.PresentEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class PresentEntityRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<PresentEntity, R> {

    public PresentEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new PresentEntityModel());
    }
}
