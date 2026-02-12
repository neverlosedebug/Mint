package net.mint.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.mint.Managers;
import net.mint.Mint;
import net.mint.events.impl.RenderHudEvent;
import net.mint.modules.impl.client.HudFeature;
import net.mint.modules.impl.render.CrosshairFeature;
import net.mint.modules.impl.render.NoRenderFeature;
import net.mint.utils.Globals;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profilers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin implements Globals {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (mc.options.hudHidden)
            return;

        Profilers.get().push(Mint.MOD_ID + "_render2d");

        context.createNewRootLayer();

        RenderHudEvent event = new RenderHudEvent(context);
        Mint.EVENT_HANDLER.post(event);

        if (event.isCancelled())
            ci.cancel();

        context.createNewRootLayer();

        Profilers.get().pop();
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void onRenderCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (Managers.FEATURE.getFeatureFromClass(CrosshairFeature.class).isEnabled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At("HEAD"), cancellable = true)
    private void onRenderScoreboardSidebar(DrawContext context, ScoreboardObjective objective, CallbackInfo callbackInfo) {
        NoRenderFeature noRender = Managers.FEATURE.getFeatureFromClass(NoRenderFeature.class);
        if (noRender.isEnabled() && noRender.scoreboard.getValue()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", at = @At("HEAD"), cancellable = true)
    private void onRenderScoreboardSidebar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo callbackInfo) {
        NoRenderFeature noRender = Managers.FEATURE.getFeatureFromClass(NoRenderFeature.class);
        if (noRender.isEnabled() && noRender.scoreboard.getValue()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    private void renderPortalOverlay(DrawContext context, float nauseaStrength, CallbackInfo info) {
        if (Managers.FEATURE.getFeatureFromClass(NoRenderFeature.class).isEnabled() && Managers.FEATURE.getFeatureFromClass(NoRenderFeature.class).portalOverlay.getValue())
            info.cancel();
    }

    @Inject(method = "renderVignetteOverlay", at = @At("HEAD"), cancellable = true)
    private void renderVignetteOverlay(DrawContext context, Entity entity, CallbackInfo info) {
        if (Managers.FEATURE.getFeatureFromClass(NoRenderFeature.class).isEnabled() && Managers.FEATURE.getFeatureFromClass(NoRenderFeature.class).vignette.getValue())
            info.cancel();
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    private void renderStatusEffectOverlay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo info) {
        if (Managers.FEATURE.getFeatureFromClass(HudFeature.class).isEnabled())
            info.cancel();
    }

    @WrapWithCondition(method = "renderMiscOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;F)V", ordinal = 0))
    private boolean renderPumpkinOverlay(InGameHud instance, DrawContext context, Identifier texture, float opacity) {
        return !(Managers.FEATURE.getFeatureFromClass(NoRenderFeature.class).isEnabled() && Managers.FEATURE.getFeatureFromClass(NoRenderFeature.class).pumpkinOverlay.getValue());
    }

    @WrapWithCondition(method = "renderMiscOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;F)V", ordinal = 1))
    private boolean renderSnowOverlay(InGameHud instance, DrawContext context, Identifier texture, float opacity) {
        return !(Managers.FEATURE.getFeatureFromClass(NoRenderFeature.class).isEnabled() && Managers.FEATURE.getFeatureFromClass(NoRenderFeature.class).snowOverlay.getValue());
    }
}