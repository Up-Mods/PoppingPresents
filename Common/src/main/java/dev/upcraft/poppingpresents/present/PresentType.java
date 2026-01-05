package dev.upcraft.poppingpresents.present;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.upcraft.poppingpresents.PoppingPresents;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Optional;

// TODO use spawn weight
public record PresentType(float width, float height, Rarity rarity, int spawnWeight, Optional<ResourceKey<LootTable>> customLootTableId) {

    public static final ResourceKey<Registry<PresentType>> REGISTRY_ID = ResourceKey.createRegistryKey(PoppingPresents.id("present_type"));
    public static final Identifier REGISTRY_DEFAULT_KEY = PoppingPresents.id("small_present");

    public static final Codec<PresentType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ExtraCodecs.POSITIVE_FLOAT.fieldOf("width").forGetter(PresentType::width),
        ExtraCodecs.POSITIVE_FLOAT.fieldOf("height").forGetter(PresentType::height),
        Rarity.CODEC.optionalFieldOf("rarity", Rarity.COMMON).forGetter(PresentType::rarity),
        ExtraCodecs.POSITIVE_INT.optionalFieldOf("spawn_weight", 10).forGetter(PresentType::spawnWeight),
        ResourceKey.codec(Registries.LOOT_TABLE).lenientOptionalFieldOf("loot_table").forGetter(PresentType::customLootTableId)

    ).apply(instance, PresentType::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, PresentType> STREAM_CODEC = ByteBufCodecs.registry(REGISTRY_ID);

    public static Registry<PresentType> registry(LevelReader level) {
        return level.registryAccess().lookupOrThrow(REGISTRY_ID);
    }

    public static Codec<PresentType> byNameCodec(LevelReader level) {
        return registry(level).byNameCodec();
    }

    public static ResourceKey<LootTable> fromRegistryId(ResourceKey<PresentType> id) {
        // data/<modid>/loot_table/popping_presents/present/<path>
        return ResourceKey.create(Registries.LOOT_TABLE, id.identifier().withPrefix("%s/present/".formatted(PoppingPresents.MOD_ID)));
    }

    public ResourceKey<LootTable> lootTable(RegistryAccess registryAccess) {
        return customLootTableId().orElseGet(() -> fromRegistryId(registryAccess.lookupOrThrow(REGISTRY_ID).getResourceKey(this).orElseThrow(() -> new IllegalStateException("Tried to get loot table for unregistered present type!"))));
    }
}
