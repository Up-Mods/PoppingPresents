package dev.upcraft.poppingpresents.init;

import dev.upcraft.poppingpresents.item.PresentItem;
import dev.upcraft.poppingpresents.platform.IPlatform;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

public class PPCreativeTabs {

    public static final Supplier<CreativeModeTab> PRESENTS = IPlatform.INSTANCE.registerCreativeModeTab("presents", Items.DIAMOND::getDefaultInstance, builder -> builder.displayItems(PresentItem::fillCreativeTab));
}
