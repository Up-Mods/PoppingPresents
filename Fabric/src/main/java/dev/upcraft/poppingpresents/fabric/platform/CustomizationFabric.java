package dev.upcraft.poppingpresents.fabric.platform;


import com.google.auto.service.AutoService;
import dev.upcraft.poppingpresents.platform.IModCustomization;
import dev.upcraft.poppingpresents.present.PresentType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Holder;

import java.util.function.Supplier;

@AutoService(IModCustomization.class)
public class CustomizationFabric implements IModCustomization {

    private static final DefaultedRegistry<PresentType> PRESENT_REGISTRY = FabricRegistryBuilder.createDefaulted(PresentType.REGISTRY_ID, PresentType.REGISTRY_DEFAULT_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    @Override
    public Holder<PresentType> registerPresent(String id, Supplier<PresentType> factory) {
        return PlatformFabric.registerHolder(PRESENT_REGISTRY, id, factory);
    }

    @Override
    public DefaultedRegistry<PresentType> presentRegistry() {
        return PRESENT_REGISTRY;
    }
}
