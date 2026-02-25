package net.mint.mixins;

import net.mint.Managers;
import net.mint.Mint;
import net.mint.modules.impl.client.ColorFeature;
import net.mint.utils.Globals;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(TitleScreen.class)
public class TitleScreenMixin implements Globals {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;renderPanoramaBackground(Lnet/minecraft/client/gui/DrawContext;F)V", shift = At.Shift.AFTER))
    private void renderTitleText(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (mc.textRenderer == null) return;

        ColorFeature colorFeature = (ColorFeature) Managers.FEATURE.getFeatureByName("Color");
        Color mintColor;

        if (colorFeature != null && colorFeature.isEnabled()) {
            mintColor = (Color) colorFeature.color.getValue();
        } else {
            mintColor = new Color(163, 255, 202);
        }

        Color grayColor = new Color(150, 150, 150);
        Color whiteColor = new Color(255, 255, 255);

        String name = Mint.NAME;
        String version = Mint.MOD_VERSION;
        String hash = Mint.GIT_HASH;

        int x = 2;
        int y = 2;
        int xOffset = 0;

        context.drawText(mc.textRenderer, name, x + xOffset, y, mintColor.getRGB(), true);
        xOffset += mc.textRenderer.getWidth(name);

        String sep = " - ";
        context.drawText(mc.textRenderer, sep, x + xOffset, y, grayColor.getRGB(), true);
        xOffset += mc.textRenderer.getWidth(sep);

        context.drawText(mc.textRenderer, version, x + xOffset, y, whiteColor.getRGB(), true);
        xOffset += mc.textRenderer.getWidth(version);

        String hashPart = "+" + hash;
        context.drawText(mc.textRenderer, hashPart, x + xOffset, y, whiteColor.getRGB(), true);
    }
}