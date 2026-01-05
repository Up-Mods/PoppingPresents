package dev.upcraft.poppingpresents.fabric;

import dev.upcraft.poppingpresents.PoppingPresents;
import dev.upcraft.poppingpresents.present.PresentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

public class PoppingPresentsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        DynamicRegistries.registerSynced(PresentType.REGISTRY_ID, PresentType.CODEC);
        PoppingPresents.init();
    }
}
