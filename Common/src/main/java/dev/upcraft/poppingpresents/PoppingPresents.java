package dev.upcraft.poppingpresents;

import com.mojang.logging.LogUtils;
import dev.upcraft.poppingpresents.init.PPEntityDataSerializers;
import dev.upcraft.poppingpresents.init.PPEntities;
import dev.upcraft.poppingpresents.init.PPItems;
import dev.upcraft.poppingpresents.init.PPPresents;
import dev.upcraft.poppingpresents.present.PresentType;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.ServiceLoader;

public class PoppingPresents {

    public static final String MOD_ID = "popping_presents";
    public static final ResourceKey<Registry<PresentType>> PRESENT_TYPES_REGISTRY_ID = ResourceKey.createRegistryKey(id("present_type"));
    public static final Identifier PRESENT_TYPES_REGISTRY_DEFAULT = id("present_type");

    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        loadClass(PPItems.class);
        loadClass(PPEntityDataSerializers.class);
        loadClass(PPEntities.class);
        loadClass(PPPresents.class);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static <T> T loadService(Class<T> serviceClass) {
        return ServiceLoader.load(serviceClass, serviceClass.getClassLoader()).findFirst()
            .orElseThrow(() -> new NoSuchElementException("Unable to find implementation service for " + serviceClass.getName()));
    }

    private static void loadClass(Class<?> clazz) {
        var mask = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;
        var count = Arrays.stream(clazz.getDeclaredFields()).filter(field -> (field.getModifiers() | mask) == mask).map(field -> {
                try {
                    return field.get(null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Access error while registering %s from %s".formatted(field.getName(), clazz.getName()), e);
                }
            })
            .filter(Objects::nonNull)
            .count();
        LOGGER.debug("Loaded {} objects from {}", count, clazz.getName());
    }
}
