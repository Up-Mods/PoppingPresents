package dev.upcraft.poppingpresents.init;

import dev.upcraft.poppingpresents.PoppingPresents;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class PPItems {

    public static final Supplier<Item> PRESENT = PoppingPresents.PLATFORM.registerItem("present", Item::new, () -> new Item.Properties().stacksTo(1));
}
