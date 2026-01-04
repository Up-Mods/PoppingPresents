package dev.upcraft.poppingpresents.platform;

import com.google.common.base.Preconditions;
import dev.upcraft.poppingpresents.PoppingPresents;
import dev.upcraft.poppingpresents.present.PresentType;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public interface IModCustomization {

    IModCustomization INSTANCE = PoppingPresents.loadService(IModCustomization.class);

    default Holder<PresentType> registerPresent(Identifier id, Supplier<PresentType> factory) {
        Preconditions.checkArgument(id.getNamespace().equals(PoppingPresents.MOD_ID), "Namespace must be " + PoppingPresents.MOD_ID);
        return registerPresent(id.getPath(), factory);
    }
    Holder<PresentType> registerPresent(String id, Supplier<PresentType> factory);

    DefaultedRegistry<PresentType> presentRegistry();
}
