package dev.upcraft.poppingpresents.neoforge.platform;

import com.google.auto.service.AutoService;
import dev.upcraft.poppingpresents.PoppingPresents;
import dev.upcraft.poppingpresents.platform.IModCustomization;
import dev.upcraft.poppingpresents.present.PresentType;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@AutoService(IModCustomization.class)
public class CustomizationNeo implements IModCustomization {

    public static final DeferredRegister<PresentType> PRESENT_TYPES = DeferredRegister.create(PresentType.REGISTRY_ID, PoppingPresents.MOD_ID);

    static {
        PRESENT_TYPES.makeRegistry(builder -> builder.defaultKey(PresentType.REGISTRY_DEFAULT_KEY).sync(true));
    }

    @Override
    public Holder<PresentType> registerPresent(String id, Supplier<PresentType> factory) {
        return PRESENT_TYPES.register(id, factory);
    }

    @Override
    public DefaultedRegistry<PresentType> presentRegistry() {
        return (DefaultedRegistry<PresentType>) PRESENT_TYPES.getRegistry().get();
    }
}
