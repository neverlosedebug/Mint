package net.melbourne.utils.graphics.impl.elements;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.awt.Color;

public class GradientTextUtil {

    public static void drawAnimatedGradientText(
            DrawContext context,
            TextRenderer textRenderer,
            String text,
            float x,
            float y,
            double timeOffset,
            Color startColor,
            Color endColor,
            boolean shadow
    ) {
        if (text == null || text.isEmpty()) return;

        int len = text.length();
        float currentX = x;

        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            String s = String.valueOf(c);
            int charWidth = textRenderer.getWidth(s);

            double t = (len == 1) ? 0.0 : (double) i / (len - 1);

            double animatedT = (t + timeOffset) % 2.0;
            if (animatedT > 1.0) animatedT = 2.0 - animatedT;

            Color interpolated = lerpColor(startColor, endColor, animatedT);
            int finalColor = interpolated.getRGB();

            if (shadow) {
                context.drawTextWithShadow(textRenderer, s, (int) currentX, (int) y, finalColor);
            } else {
                context.drawText(textRenderer, s, (int) currentX, (int) y, finalColor, false);
            }

            currentX += charWidth;
        }
    }

    private static Color lerpColor(Color a, Color b, double t) {
        t = Math.max(0.0, Math.min(1.0, t));
        int r = (int) (a.getRed() + t * (b.getRed() - a.getRed()));
        int g = (int) (a.getGreen() + t * (b.getGreen() - a.getGreen()));
        int blue = (int) (a.getBlue() + t * (b.getBlue() - a.getBlue()));
        int alpha = (int) (a.getAlpha() + t * (b.getAlpha() - a.getAlpha()));
        return new Color(r, g, blue, alpha);
    }

    public static Color darken(Color color, float factor) {
        return new Color(
                Math.max(0, (int) (color.getRed() * (1 - factor))),
                Math.max(0, (int) (color.getGreen() * (1 - factor))),
                Math.max(0, (int) (color.getBlue() * (1 - factor))),
                color.getAlpha()
        );
    }
}