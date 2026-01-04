package dev.upcraft.poppingpresents.init;

import dev.upcraft.poppingpresents.PoppingPresents;
import dev.upcraft.poppingpresents.platform.IModCustomization;
import dev.upcraft.poppingpresents.present.PresentType;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Holder;

// TODO might be feasible to make this a datapack registry, will have to see once the renderers etc are done
public class PPPresents {

    public static DefaultedRegistry<PresentType> registry() {
        return IModCustomization.INSTANCE.presentRegistry();
    }

    public static final Holder<PresentType> DEFAULT = IModCustomization.INSTANCE.registerPresent(PoppingPresents.PRESENT_TYPES_REGISTRY_DEFAULT, () -> new PresentType(0.5F, 0.7F));

}
