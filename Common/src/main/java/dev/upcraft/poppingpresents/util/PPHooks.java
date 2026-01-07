package dev.upcraft.poppingpresents.util;

import com.mojang.logging.LogUtils;
import dev.upcraft.poppingpresents.entity.PresentEntity;
import dev.upcraft.poppingpresents.present.PresentType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.Mth;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.Optional;

public class PPHooks {

    private static final int PRESENT_SPAWN_ATTEMPTS = 16;
    private static final double PRESENT_SPAWN_CHANCE = 0.2D;
    private static final boolean SET_OWNER_ON_SPAWN = true;

    private static final Logger LOGGER = LogUtils.getLogger();

    public static void onFinishedSleeping(ServerLevelAccessor level) {
        var mutable = new BlockPos.MutableBlockPos();
        var weightedList = makeWeightedList(PresentType.registry(level));
        if (weightedList.isEmpty()) {
            LOGGER.error("Unable to spawn present because PresentType registry was empty!");
            return;
        }

        level.players().forEach(player -> {
            var random = player.getRandom();
            for (int i = 0; i < PRESENT_SPAWN_ATTEMPTS; i++) {
                if (random.nextDouble() < PRESENT_SPAWN_CHANCE) {
                    mutable.set(
                        player.getBlockX() + random.nextInt(-16, 17),
                        player.getBlockY() + random.nextInt(-5, 6),
                        player.getBlockZ() + random.nextInt(-16, 17)
                    );

                    var type = weightedList.getRandom(random).orElseThrow();
                    var entity = new PresentEntity(level.getLevel(), type, random.nextLong());
                    var heightToCheck = Math.max(1, Mth.ceil(entity.getBbHeight()));

                    findPos(level, mutable, heightToCheck).ifPresent(pos -> {
                        entity.setPos(pos);
                        if (level.noBlockCollision(entity, entity.getBoundingBox(), true)) {
                            if (SET_OWNER_ON_SPAWN) {
                                entity.setOwner(player);
                            }
                            level.addFreshEntity(entity);
                        }
                    });
                }
            }
        });
    }

    private static Optional<Vec3> findPos(LevelAccessor level, BlockPos.MutableBlockPos mutable, int heightToCheck) {
        if (!isFree(level, mutable)) {
            loop:
            for (int j = 0; j < 5; j++) {
                if (isFree(level, mutable.move(Direction.UP))) {
                    var pos = mutable.getBottomCenter();
                    for (int k = 1; k < heightToCheck; k++) {
                        if (!isFree(level, mutable.move(Direction.UP))) {
                            j += k;
                            continue loop;
                        }
                    }

                    return Optional.of(pos);
                }
            }
        } else {
            loop:
            for (int j = 0; j < 5; j++) {
                if (isFree(level, mutable.move(Direction.DOWN))) {
                    int emptySpace = 0;
                    while (mutable.getY() > level.getMinY() && emptySpace < Math.max(heightToCheck * 2, 5)) {
                        if (isFree(level, mutable.move(Direction.DOWN))) {
                            emptySpace++;
                        } else {
                            if(emptySpace >= heightToCheck) {
                                return Optional.of(mutable.move(Direction.UP).getBottomCenter());
                            }

                            j += emptySpace;
                            continue loop;
                        }
                    }

                    return Optional.empty();
                }
            }
        }

        return Optional.empty();
    }

    public static boolean isFree(LevelAccessor level, BlockPos pos) {
        return level.isStateAtPosition(pos, BlockBehaviour.BlockStateBase::canBeReplaced);
    }

    public static WeightedList<Holder<PresentType>> makeWeightedList(HolderLookup.RegistryLookup<PresentType> registry) {
        return WeightedList.of(registry.listElements()
            .map(holder -> new Weighted<Holder<PresentType>>(holder, holder.value().spawnWeight()))
            .toList()
        );
    }
}
