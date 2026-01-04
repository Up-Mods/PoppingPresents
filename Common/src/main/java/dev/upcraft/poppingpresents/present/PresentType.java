package dev.upcraft.poppingpresents.present;

import dev.upcraft.poppingpresents.PoppingPresents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PresentType(float width, float height) {

    public static final StreamCodec<RegistryFriendlyByteBuf, PresentType> STREAM_CODEC = ByteBufCodecs.registry(PoppingPresents.PRESENT_TYPES_REGISTRY_ID);
}
