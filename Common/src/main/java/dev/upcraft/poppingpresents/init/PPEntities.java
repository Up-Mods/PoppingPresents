package dev.upcraft.poppingpresents.init;

import dev.upcraft.poppingpresents.PoppingPresents;
import dev.upcraft.poppingpresents.entity.PresentEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Supplier;

public class PPEntities {

    public static final Supplier<EntityType<PresentEntity>> PRESENT = PoppingPresents.PLATFORM.registerEntity("present", PresentEntity::new, MobCategory.MISC, builder -> builder
        .sized(0.5F, 0.7F)
        .spawnDimensionsScale(1.2F)
        .fireImmune()
        .immuneTo(Blocks.POWDER_SNOW, Blocks.MAGMA_BLOCK, Blocks.WITHER_ROSE, Blocks.SWEET_BERRY_BUSH)
        .canSpawnFarFromPlayer()
        .clientTrackingRange(10)
    );
}
