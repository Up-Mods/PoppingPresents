package dev.upcraft.poppingpresents.present;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.upcraft.poppingpresents.PoppingPresents;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.LevelReader;

import java.util.Optional;

// TODO use spawn weight
public record PresentType(float width, float height, float spawnWeight, Optional<Identifier> customLootTableId) {

    public static final ResourceKey<Registry<PresentType>> REGISTRY_ID = ResourceKey.createRegistryKey(PoppingPresents.id("present_type"));
    public static final Identifier REGISTRY_DEFAULT_KEY = PoppingPresents.id("small_present");

    public static final Codec<PresentType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ExtraCodecs.POSITIVE_FLOAT.fieldOf("width").forGetter(PresentType::width),
        ExtraCodecs.POSITIVE_FLOAT.fieldOf("height").forGetter(PresentType::height),
        ExtraCodecs.POSITIVE_FLOAT.fieldOf("spawn_weight").forGetter(PresentType::spawnWeight),
        Identifier.CODEC.optionalFieldOf("loot_table").forGetter(PresentType::customLootTableId)
    ).apply(instance, PresentType::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, PresentType> STREAM_CODEC = ByteBufCodecs.registry(REGISTRY_ID);

    public static Registry<PresentType> registry(LevelReader level) {
        return level.registryAccess().lookupOrThrow(REGISTRY_ID);
    }

    public static Codec<PresentType> byNameCodec(LevelReader level) {
        return registry(level).byNameCodec();
    }

    // TODO implement loot
    public Identifier lootTable(RegistryAccess registryAccess) {
        // FIXME correct format for loot table IDs
        return customLootTableId().orElseGet(() -> registryAccess.lookupOrThrow(REGISTRY_ID).getKey(this));
    }
}
