package net.mint.gui.main.impl;

import lombok.Getter;
import net.mint.Managers;
import net.mint.gui.main.api.Button;
import net.mint.gui.main.api.Window;
import net.mint.modules.Feature;
import net.mint.modules.impl.client.ClickGuiFeature;
import net.mint.settings.Setting;
import net.mint.settings.types.*;
import net.mint.utils.animations.Animation;
import net.mint.utils.animations.Easing;
import net.mint.utils.graphics.impl.Renderer2D;
import net.mint.utils.graphics.impl.font.FontUtils;
import net.mint.utils.miscellaneous.ColorUtils;
import net.mint.utils.sounds.SoundUtils;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.ArrayList;

@Getter
public class FeatureButton extends Button {
    private final Feature feature;
    private final ArrayList<Button> buttons = new ArrayList<>();

    private final Animation animation = new Animation(300, Easing.Method.EASE_OUT_CUBIC);
    private final Animation textAnimation = new Animation(300, Easing.Method.EASE_OUT_CUBIC);
    private final Animation xAnimation = new Animation(150, Easing.Method.EASE_OUT_CUBIC);

    private long openTime = 0L;
    private float currentProgress = 0;

    private boolean open = false;
    private boolean searchMatch = false;

    private Color[] glowCache = null;
    private Color lastGlowColor = null;
    private static final int GLOW_STEPS = 12;

    public FeatureButton(Feature feature, Window window) {
        super(window);
        this.feature = feature;
        animation.get(feature.isEnabled() ? 200 : 0);

        for (Setting uncastedSetting : feature.getSettings()) {
            switch (uncastedSetting) {
                case BooleanSetting setting -> buttons.add(new BooleanButton(setting, window));
                case BindSetting setting -> buttons.add(new BindButton(setting, window));
                case NumberSetting setting -> buttons.add(new NumberButton(setting, window));
                case ModeSetting setting -> buttons.add(new ModeButton(setting, window));
                case ColorSetting setting -> buttons.add(new ColorButton(setting, window));
                case TextSetting setting -> buttons.add(new StringButton(setting, window));
                case WhitelistSetting setting -> buttons.add(new WhitelistButton(setting, window));
                default -> {
                }
            }
        }
    }

    public void updateSearchMatch(String query) {
        if (query == null || query.isEmpty()) {
            searchMatch = false;
            return;
        }
        String name = feature.getName().toLowerCase();
        searchMatch = name.contains(query);
    }

    public boolean isSearchMatch() {
        return searchMatch;
    }

    private void updateGlowCache(Color baseColor) {
        if (glowCache == null || !baseColor.equals(lastGlowColor)) {
            glowCache = new Color[GLOW_STEPS];
            lastGlowColor = baseColor;

            for (int i = 0; i < GLOW_STEPS; i++) {
                float progress = (float) i / GLOW_STEPS;
                float alphaFactor = 1.0f - progress * progress;
                int alpha = (int) (baseColor.getAlpha() * alphaFactor);
                alpha = Math.max(0, Math.min(255, alpha));

                glowCache[i] = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha);
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float x1 = getX() - 2.5f;
        float y1 = getY() + 0.5f;
        float x2 = getX() + getWidth() + 2.5f;
        float y2 = getY() + super.getHeight() - 0.5f;

        Renderer2D.renderQuad(context, x1, y1, x2, y2, ColorUtils.getGlobalColor((int) animation.get(feature.isEnabled() ? 200 : 0)));

        int moduleAlpha = (int) getHoverAnimation().get(isHovering(mouseX, mouseY) ? 75 : 0);
        Renderer2D.renderQuad(context, x1, y1, x2, y2, new Color(0, 0, 0, moduleAlpha));

        if (searchMatch) {
            Renderer2D.renderOutline(context, x1, y1, x2, y2, new Color(100, 180, 255, 200));
        }

        ClickGuiFeature clickGui = Managers.FEATURE.getFeatureFromClass(ClickGuiFeature.class);

        if (clickGui.accentBar.getValue()) {
            Color barColor = clickGui.accentColor.getColor();
            float barX1, barX2;
            if ("Right".equals(clickGui.accentSide.getValue())) {
                barX1 = getX() + getWidth() + 1.5f;
                barX2 = getX() + getWidth() + 2.5f;
            } else {
                barX1 = getX() - 2.5f;
                barX2 = getX() - 1.5f;
            }
            Renderer2D.renderQuad(context, barX1, y1, barX2, y2, barColor);
        }

        if (clickGui.glowEffect.getValue()) {
            Color glowColor = clickGui.glowColor.getColor();
            updateGlowCache(glowColor);

            float startX = x1;
            float endX = getX() + getWidth() / 2.0f;
            float width = endX - startX;

            if (width > 0) {
                float stepWidth = width / GLOW_STEPS;

                for (int i = 0; i < GLOW_STEPS; i++) {
                    float quadX1 = startX + stepWidth * i;
                    float quadX2 = startX + stepWidth * (i + 1);
                    Renderer2D.renderQuad(context, quadX1, y1, quadX2, y2, glowCache[i]);
                }
            }
        }

        if (clickGui.moduleOutline.getValue()) {
            Renderer2D.renderOutline(context, x1, y1, x2, y2, clickGui.outlineColor.getColor());
        }

        if (clickGui.showGear.getValue()) {
            String symbol = open ? clickGui.closeSymbol.getValue() : clickGui.openSymbol.getValue();
            drawTextWithShadow(context, symbol, getX() + getWidth() - 10, getY() + getVerticalPadding(), Color.WHITE);
        }

        float tuffX = xAnimation.get(open ? getX() + getWidth() / 2 - (FontUtils.getWidth(feature.getName()) / 2.0F) : getX());
        drawTextWithShadow(context, feature.getName(), tuffX, getY() + getVerticalPadding(), getTextColor(textAnimation, feature.isEnabled()));

        float currentY = super.getHeight();
        float targetY = 0;

        for (Button button : buttons) {
            if (!button.getSetting().isVisible())
                continue;

            button.setX(getX());
            button.setY(getY() + currentY);

            currentY += button.getHeight();
            targetY += button.getHeight();
        }

        float scale = Easing.toDelta(openTime, 150);

        if (open || scale != 1.0f) {
            float scaledHeight = targetY * (open ? scale : 1.0f - scale);

            if (scale != 1.0f) {
                context.enableScissor(
                        (int) x1,
                        (int) (getY() + super.getHeight() - 0.5f),
                        (int) (getX() + getWidth()),
                        (int) (getY() + super.getHeight() + scaledHeight)
                );
            }

            Renderer2D.renderQuad(context, x1, getY() + super.getHeight() - 0.5f, getX() - 1.5f, getY() + super.getHeight() - 0.5f + scaledHeight, ColorUtils.getGlobalColor(200));
            Renderer2D.renderQuad(context, x1, getY() + super.getHeight() - 0.5f, getX() - 1.5f, getY() + super.getHeight() - 0.5f + scaledHeight, new Color(0, 0, 0, moduleAlpha));

            context.getMatrices().pushMatrix();
            context.getMatrices().translate(0, -targetY + scaledHeight);

            for (Button button : buttons) {
                if (!button.getSetting().isVisible()) continue;

                int alpha = (int) button.getHoverAnimation().get(button.isHovering(mouseX, mouseY) ? 75 : 0);
                Renderer2D.renderQuad(context, button.getX() - 1, button.getY(), button.getX() + button.getWidth() + 2.5f, button.getY() + super.getHeight(), new Color(0, 0, 0, alpha));

                button.render(context, mouseX, mouseY, delta);
            }

            context.getMatrices().popMatrix();

            if (scale != 1.0f)
                context.disableScissor();

            currentProgress = scaledHeight;
        } else {
            currentProgress = 0;
        }
    }

    @Override
    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {

    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovering(mouseX, mouseY)) {
            if (button == 0) {
                if (Managers.FEATURE.getFeatureFromClass(ClickGuiFeature.class).sounds.getValue()) {
                    if (feature.isEnabled()) {
                        SoundUtils.playSound("disabled.wav", 67);
                    } else {
                        SoundUtils.playSound("enabled.wav", 67);
                    }
                }

                feature.setEnabled(!feature.isEnabled());
            } else if (button == 1) {
                open = !open;
                openTime = System.currentTimeMillis();
            }
        }

        if (open)
            buttons.stream().filter(b -> b.getSetting().isVisible()).forEach(b -> b.mouseClicked(mouseX, mouseY, button));
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        buttons.stream().filter(b -> b.getSetting().isVisible()).forEach(b -> b.mouseReleased(mouseX, mouseY, button));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (open) {
            for (Button button : buttons) {
                if (!button.getSetting().isVisible()) continue;
                if (button.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (open)
            buttons.stream().filter(b -> b.getSetting().isVisible()).forEach(b -> b.keyPressed(keyCode, scanCode, modifiers));
    }

    @Override
    public void charTyped(char chr, int modifiers) {
        if (open) buttons.stream().filter(b -> b.getSetting().isVisible()).forEach(b -> b.charTyped(chr, modifiers));
    }

    @Override
    public float getHeight() {
        return super.getHeight() + currentProgress;
    }
}