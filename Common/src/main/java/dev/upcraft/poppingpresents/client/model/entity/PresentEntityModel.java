package dev.upcraft.poppingpresents.client.model.entity;

import com.mojang.logging.LogUtils;
import dev.upcraft.poppingpresents.PoppingPresents;
import dev.upcraft.poppingpresents.entity.PresentEntity;
import dev.upcraft.poppingpresents.present.PresentType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import software.bernie.geckolib.cache.model.BakedGeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class PresentEntityModel extends GeoModel<PresentEntity> {

    public static final DataTicket<PresentType> TYPE = DataTicket.create(PoppingPresents.id("type").toDebugFileName(), PresentType.class);
    public static final Identifier MISSING_MODEL = PoppingPresents.id("missingno");
    /**
     * due to this being a weak map, we don't need to care about datapack sync;
     * it will be cleared automatically.
     */
    private static final Map<PresentType, ModelData> MODEL_DATA_CACHE = new WeakHashMap<>();
    private static final Set<Identifier> MISSING_MODELS = new HashSet<>();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void clearCache() {
        LOGGER.debug("Clearing ModelData cache");
        MODEL_DATA_CACHE.clear();
        MISSING_MODELS.clear();
    }

    @Override
    public void addAdditionalStateData(PresentEntity animatable, Object relatedObject, GeoRenderState renderState) {
        renderState.addGeckolibData(PresentEntityModel.TYPE, animatable.getPresentType());
        renderState.addGeckolibData(DataTickets.OPEN, animatable.isOpen());
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        var type = renderState.getGeckolibData(TYPE);
        return getModelData(type).modelPath();
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        var type = renderState.getGeckolibData(TYPE);
        var modelData = getModelData(type);

        if(MISSING_MODELS.contains(modelData.modelPath)) {
            return MissingTextureAtlasSprite.getLocation();
        }

        return modelData.texturePath();
    }

    @Override
    public Identifier getAnimationResource(PresentEntity animatable) {
        return getModelData(animatable.getPresentType()).animationsPath();
    }

    private record ModelData(Identifier modelPath, Identifier animationsPath, Identifier texturePath) {

        private static final ModelData EMPTY = ModelData.fromId(PresentType.REGISTRY_DEFAULT_KEY);

        private static ModelData fromId(Identifier typeId) {
            // assets/<modid>/geckolib/models/entity/popping_presents/present/<path>.geo.json
            // assets/<modid>/geckolib/animations/entity/popping_presents/present/<path>.animation.json
            // assets/<modid>/textures/entity/popping_presents/present/<path>.png
            var commonPrefix = "entity/%s/present/".formatted(PoppingPresents.MOD_ID);
            var modelPath = typeId.withPrefix(commonPrefix);
            var animationsPath = typeId.withPrefix(commonPrefix);
            var texturePath = typeId.withPath(path -> "textures/%s%s.png".formatted(commonPrefix, path));

            return new ModelData(modelPath, animationsPath, texturePath);
        }

        public static ModelData of(@Nullable PresentType type) {
            if(type == null) {
                return EMPTY;
            }

            var level = Minecraft.getInstance().level;
            if(level == null) {
                LOGGER.warn("Tried to look up ModelData outside of level!");
                return EMPTY;
            }

            var registry = PresentType.registry(Minecraft.getInstance().level);
            var typeId = registry.getKey(type);
            if(typeId == null) {
                return EMPTY;
            }

            return fromId(typeId);
        }
    }

    private static ModelData getModelData(@Nullable PresentType type) {
        return MODEL_DATA_CACHE.computeIfAbsent(type, ModelData::of);
    }

    @Override
    public BakedGeoModel getBakedModel(Identifier location) {
        try {
            return super.getBakedModel(location);
        } catch (Exception e) {
            if(MISSING_MODELS.add(location)) {
                LOGGER.error("Unable to find model: {}", location, e);
            }

            return super.getBakedModel(MISSING_MODEL);
        }
    }
}
