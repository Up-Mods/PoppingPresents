package dev.upcraft.poppingpresents;

import dev.upcraft.poppingpresents.platform.IPlatform;

import java.util.ServiceLoader;

public class PPCommon {

    public static final String MOD_ID = "popping_presents";
    public static final IPlatform COMMON_PLATFORM = ServiceLoader.load(IPlatform.class).findFirst().orElseThrow();

    public static void init() {
    }
}
