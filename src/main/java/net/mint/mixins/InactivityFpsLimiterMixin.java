package net.mint.mixins;

import net.mint.Managers;
import net.mint.modules.impl.misc.UnfocusedFPS;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.InactivityFpsLimiter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InactivityFpsLimiter.class)
public abstract class InactivityFpsLimiterMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void onUpdate(CallbackInfoReturnable<Integer> cir) {
        UnfocusedFPS module = Managers.FEATURE.getFeatureFromClass(UnfocusedFPS.class);
        if (module.isEnabled() && !client.isWindowFocused()) {
            cir.setReturnValue(module.limit.getValue().intValue());
        }
    }
}