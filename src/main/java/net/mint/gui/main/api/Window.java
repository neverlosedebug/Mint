package net.mint.gui.main.api;

import lombok.Getter;
import lombok.Setter;
import net.mint.Managers;
import net.mint.gui.main.impl.FeatureButton;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.impl.client.ClickGuiFeature;
import net.mint.utils.animations.Animation;
import net.mint.utils.animations.Easing;
import net.mint.utils.graphics.impl.Renderer2D;
import net.mint.utils.graphics.impl.font.FontUtils;
import net.mint.utils.miscellaneous.ColorUtils;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.ArrayList;

@Getter @Setter
public class Window {
    private final Category category;
    private final ArrayList<FeatureButton> buttons = new ArrayList<>();
    private final Animation animation = new Animation(300, Easing.Method.EASE_OUT_CUBIC);
    private long openTime = 0L;
    private float x;
    private float y;
    private final float width = 100;
    private final float height = 14;
    private int dragX = 0;
    private int dragY = 0;
    private boolean open = true;
    private boolean dragging = false;
    private String searchQuery = null;

    public Window(Category category, int x, int y) {
        this.category = category;
        for (Feature feature : Managers.FEATURE.getFeaturesInCategory(category)) {
            this.buttons.add(new FeatureButton(feature, this));
        }
        this.x = x;
        this.y = y;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (dragging) {
            setX(mouseX - dragX);
            setY(mouseY - dragY);
        }

        boolean hasMatch = hasMatch();
        Color headerColor = hasMatch ? new Color(60, 100, 200, 200) : ColorUtils.getGlobalColor(200);
        Renderer2D.renderQuad(context, x, y, x + width, y + height, headerColor);

        int alpha = (int) animation.get(dragging ? 75 : 0);
        Renderer2D.renderQuad(context, x, y, x + width, y + height, new Color(0, 0, 0, alpha));

        FontUtils.drawCenteredTextWithShadow(context, category.getName(), (int) (x + width / 2), (int) (y + height / 2), Color.WHITE);

        float currentY = height + 1;
        float targetY = 2;
        for (FeatureButton button : buttons) {
            button.setX(x + 4);
            button.setY(y + currentY);
            currentY += button.getHeight();
            targetY += button.getHeight();
        }

        ClickGuiFeature clickGui = Managers.FEATURE.getFeatureFromClass(ClickGuiFeature.class);
        float animDuration = clickGui.animationSpeed.getValue().floatValue();
        float scale = Easing.ease(Easing.toDelta(openTime, (int) animDuration), Easing.Method.EASE_OUT_CUBIC);

        if (open || scale != 1.0f) {
            if (scale != 1.0f)
                context.enableScissor((int) x, (int) (y + height), (int) (x + width), (int) (y + height + (targetY * (open ? scale : 1.0f - scale)) + 2));

            context.getMatrices().pushMatrix();
            context.getMatrices().translate(0, -targetY + (targetY * (open ? scale : 1.0f - scale)));

            Color bg = clickGui.backgroundColor.getColor();
            Renderer2D.renderQuad(context, x, y + height, x + width, y + currentY + 1, bg);

            if (clickGui.topGlow.getValue() && !buttons.isEmpty()) {
                Color base = clickGui.topGlowColor.getColor();
                float startY = y + height;
                float endY = y + height + 12;

                int steps = 16;
                for (int i = 0; i < steps; i++) {
                    float t1 = (float) i / steps;
                    float t2 = (float) (i + 1) / steps;

                    float y1 = startY + (endY - startY) * t1;
                    float y2 = startY + (endY - startY) * t2;

                    float alphaFactor = 1.0f - t1;
                    int glowAlpha = (int) (base.getAlpha() * alphaFactor);
                    glowAlpha = Math.max(0, Math.min(255, glowAlpha));

                    Color fadeColor = new Color(base.getRed(), base.getGreen(), base.getBlue(), glowAlpha);
                    Renderer2D.renderQuad(context, x, y1, x + width, y2, fadeColor);
                }
            }

            if (clickGui.outline.getValue()) {
                Renderer2D.renderOutline(context, x, y + height, x + width, y + currentY + 1, ColorUtils.getGlobalColor(200));
                Renderer2D.renderOutline(context, x, y + height, x + width, y + currentY + 1, new Color(0, 0, 0, alpha));
            }

            buttons.forEach(b -> b.render(context, mouseX, mouseY, delta));
            context.getMatrices().popMatrix();

            if (scale != 1.0f)
                context.disableScissor();
        }
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query;
        for (FeatureButton button : buttons) {
            button.updateSearchMatch(query);
        }
    }

    public boolean hasMatch() {
        if (searchQuery == null || searchQuery.isEmpty()) return false;
        for (FeatureButton button : buttons) {
            if (button.isSearchMatch()) return true;
        }
        return false;
    }

    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {}

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovering(mouseX, mouseY)) {
            if (button == 0) {
                dragging = true;
                dragX = (int) (mouseX - x);
                dragY = (int) (mouseY - y);
                return true;
            } else if (button == 1) {
                open = !open;
                openTime = System.currentTimeMillis();
                return true;
            }
        }
        if (open) buttons.forEach(b -> b.mouseClicked(mouseX, mouseY, button));
        return false;
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) dragging = false;
        buttons.forEach(b -> b.mouseReleased(mouseX, mouseY, button));
    }

    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (x <= mouseX && x + width > mouseX) {
            if (verticalAmount < 0) {
                setY(getY() - 15);
            } else if (verticalAmount > 0) {
                setY(getY() + 15);
            }
        }
        if (open) buttons.forEach(b -> b.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount));
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (open) buttons.forEach(b -> b.keyPressed(keyCode, scanCode, modifiers));
    }

    public void charTyped(char chr, int modifiers) {
        if (open) buttons.forEach(b -> b.charTyped(chr, modifiers));
    }

    public boolean isHovering(double mouseX, double mouseY) {
        return x <= mouseX && y <= mouseY && x + width > mouseX && y + height > mouseY;
    }
}