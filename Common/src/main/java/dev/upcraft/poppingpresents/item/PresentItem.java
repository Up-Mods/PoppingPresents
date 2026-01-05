package dev.upcraft.poppingpresents.item;

import dev.upcraft.poppingpresents.entity.PresentEntity;
import dev.upcraft.poppingpresents.init.PPEntities;
import dev.upcraft.poppingpresents.init.PPItems;
import dev.upcraft.poppingpresents.present.PresentType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.packs.VanillaChestLoot;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.Consumer;

public class PresentItem extends SpawnEggItem {

    public PresentItem(Properties properties) {
        super(properties.spawnEgg(PPEntities.PRESENT.get()));
    }

    public static ItemStack forType(Holder<PresentType> type, Consumer<CompoundTag> extraData) {
        var stack = new ItemStack(PPItems.PRESENT.get());
        stack.set(DataComponents.RARITY, type.value().rarity());

        // TODO custom data component type, don't abuse SpawnEgg
        var tag = new CompoundTag();
        extraData.accept(tag);
        tag.putString(PresentEntity.NBT_KEY_PRESENT_TYPE, type.unwrapKey().orElseThrow(() -> new IllegalStateException("Holder not registered!")).identifier().toString());
        stack.set(DataComponents.ENTITY_DATA, TypedEntityData.of(PPEntities.PRESENT.get(), tag));
        return stack;
    }

    public static ItemStack forType(Holder<PresentType> type) {
        return forType(type, tag -> {});
    }

    public static ItemStack withLootTable(Holder<PresentType> type, ResourceKey<LootTable> lootTable) {
        return forType(type, tag -> tag.putString("LootTable", lootTable.identifier().toString()));
    }

    public static ItemStack withLootTableFromType(Holder<PresentType> type) {
        var lootTable = type.value().customLootTableId().orElseGet(() -> PresentType.fromRegistryId(type.unwrapKey().orElseThrow(() -> new IllegalStateException("Tried to get loot table for unregistered present type!"))));
        return withLootTable(type, lootTable);
    }

    public static void fillCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        parameters.holders().lookup(PresentType.REGISTRY_ID).ifPresent(registry ->
            registry.listElements().map(PresentItem::withLootTableFromType).forEach(output::accept));
    }
}
