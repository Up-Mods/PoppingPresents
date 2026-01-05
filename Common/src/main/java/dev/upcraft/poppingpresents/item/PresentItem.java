package dev.upcraft.poppingpresents.item;

import dev.upcraft.poppingpresents.entity.PresentEntity;
import dev.upcraft.poppingpresents.init.PPEntities;
import dev.upcraft.poppingpresents.init.PPItems;
import dev.upcraft.poppingpresents.present.PresentType;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.TypedEntityData;

public class PresentItem extends SpawnEggItem {

    public PresentItem(Properties properties) {
        super(properties.spawnEgg(PPEntities.PRESENT.get()));
    }

    public static ItemStack forType(Holder<PresentType> type) {
        var stack = new ItemStack(PPItems.PRESENT.get());
        stack.set(DataComponents.RARITY, type.value().rarity());

        // TODO custom data component type, don't abuse SpawnEgg
        var tag = new CompoundTag();
        tag.putString(PresentEntity.NBT_KEY_PRESENT_TYPE, type.unwrapKey().orElseThrow(() -> new IllegalStateException("Holder not registered!")).identifier().toString());
        stack.set(DataComponents.ENTITY_DATA, TypedEntityData.of(PPEntities.PRESENT.get(), tag));
        return stack;
    }

    public static void fillCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        parameters.holders().lookup(PresentType.REGISTRY_ID).ifPresent(registry ->
            registry.listElements().map(PresentItem::forType).forEach(output::accept));
    }
}
