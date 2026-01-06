package dev.upcraft.poppingpresents.init;

import dev.upcraft.poppingpresents.entity.PresentEntity;
import dev.upcraft.poppingpresents.platform.IPlatform;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Supplier;

public class PPEntities {

    public static final Supplier<EntityType<PresentEntity>> PRESENT = IPlatform.INSTANCE.registerEntity("present", PresentEntity::new, MobCategory.MISC, builder -> builder
        .sized(0.7F, 0.7F)
        .fireImmune()
        .noSummon()
        .immuneTo(Blocks.POWDER_SNOW, Blocks.MAGMA_BLOCK, Blocks.WITHER_ROSE, Blocks.SWEET_BERRY_BUSH)
        .canSpawnFarFromPlayer()
        .clientTrackingRange(10)
    );
}
