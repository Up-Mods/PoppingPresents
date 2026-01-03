package dev.upcraft.poppingpresents.fabric;

import dev.upcraft.poppingpresents.PPCommon;
import net.fabricmc.api.ModInitializer;

public class PoppingPresentsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        PPCommon.init();
    }
}
