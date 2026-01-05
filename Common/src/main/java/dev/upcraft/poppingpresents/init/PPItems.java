package dev.upcraft.poppingpresents.init;

import dev.upcraft.poppingpresents.item.PresentItem;
import dev.upcraft.poppingpresents.platform.IPlatform;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.TypedEntityData;

import java.util.function.Supplier;

public class PPItems {

    public static final Supplier<Item> PRESENT = IPlatform.INSTANCE.registerItem("present", PresentItem::new, () -> new Item.Properties().stacksTo(1).component(DataComponents.RARITY, Rarity.COMMON).component(DataComponents.ENTITY_DATA, TypedEntityData.of(PPEntities.PRESENT.get(), new CompoundTag())));
}
