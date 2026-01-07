package dev.upcraft.poppingpresents.fabric.mixin;

import dev.upcraft.poppingpresents.util.PPHooks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.WorldGenLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements WorldGenLevel {

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setDayTime(J)V"))
    private void onSkipTime(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        PPHooks.onFinishedSleeping(this);
    }
}
