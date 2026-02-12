package net.mint.gui.main;

import lombok.Getter;
import lombok.Setter;
import net.mint.Managers;
import net.mint.Mint;
import net.mint.modules.Category;
import net.mint.modules.impl.client.ClickGuiFeature;
import net.mint.utils.graphics.impl.Renderer2D;
import net.mint.utils.graphics.impl.font.FontUtils;
import net.mint.gui.main.api.Window;
import net.mint.utils.animations.Animation;
import net.mint.utils.animations.Easing;
import net.mint.utils.miscellaneous.Timer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;

@Getter @Setter
public class ClickGuiScreen extends Screen {
    private final ArrayList<Window> windows = new ArrayList<>();
    private final Animation animation = new Animation(300, Easing.Method.EASE_OUT_CUBIC);
    private boolean close = false;
    private final Timer timer = new Timer();
    private boolean line = false;
    private Color colorClipboard = null;
    public static final VertexConsumerProvider.Immediate VERTEX_CONSUMERS = VertexConsumerProvider.immediate(new BufferAllocator(786432));

    private boolean searchMode = false;
    private StringBuilder searchText = new StringBuilder();

    public ClickGuiScreen() {
        super(Text.literal(Mint.MOD_ID + "-click-gui"));
        int x = 3;
        for (Category category : Category.values()) {
            Window window = new Window(category, x, 3);
            windows.add(window);
            x += (int) (window.getWidth() + 4);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float progress = animation.get(close ? 0.0f : 1.0f);
        if (close && progress == 0.0f) {
            this.close();
            return;
        }

        if (timer.hasTimeElapsed(400)) {
            line = !line;
            timer.reset();
        }

        context.getMatrices().pushMatrix();
        context.getMatrices().translate(0.0f, -height * (1.0f - progress));

        for (Window window : windows) {
            window.render(context, mouseX, mouseY, delta);
        }

        context.getMatrices().popMatrix();

        // render search bar
        if (searchMode) {
            int barY = 5;
            Renderer2D.renderQuad(context, 0, barY, width, barY + 16, new Color(30, 30, 30, 200));
            Renderer2D.renderOutline(context, 0, barY, width, barY + 16, new Color(70, 70, 70, 255));

            String displayText = searchText.toString();
            if (displayText.isEmpty()) {
                FontUtils.drawTextWithShadow(context, "Search modules...", 6, barY + 10, new Color(150, 150, 150, 255));
            } else {
                FontUtils.drawTextWithShadow(context, displayText, 6, barY + 10, Color.WHITE);
            }
        }

        VERTEX_CONSUMERS.draw();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!searchMode) {
            windows.forEach(w -> w.mouseDragged(mouseX, mouseY, button, deltaX, deltaY));
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (searchMode) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                int barY = 5;
                if (mouseY >= barY && mouseY <= barY + 16) {
                    return true;
                } else {
                    searchMode = false;
                    searchText.setLength(0);
                    resetAllSearch();
                    return true;
                }
            }
        }

        if (!searchMode) {
            windows.forEach(w -> w.mouseClicked(mouseX, mouseY, button));
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!searchMode) {
            windows.forEach(w -> w.mouseReleased(mouseX, mouseY, button));
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!searchMode) {
            windows.forEach(w -> w.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount));
        }
        return this.hoveredElement(mouseX, mouseY).filter(element -> element.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)).isPresent();
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.applyBlur(context);
        float progress = animation.get(close ? 0.0f : 1.0f);
        float p = MathHelper.clamp(progress, 0.0f, 1.0f);
        Renderer2D.renderQuad(context, 0, 0, width, height, new Color(0, 0, 0, (int) (100 * p)));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && !close) {
            if (searchMode) {
                searchMode = false;
                searchText.setLength(0);
                resetAllSearch();
                return true;
            } else {
                close = true;
            }
        }

        if (keyCode == GLFW.GLFW_KEY_F && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            searchMode = true;
            searchText.setLength(0);
            resetAllSearch();
            return true;
        }

        if (searchMode) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                performSearch();
                return true;
            }

            if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                if (!searchText.isEmpty()) {
                    searchText.deleteCharAt(searchText.length() - 1);
                }
                performSearch();
                return true;
            }

            return true;
        }

        windows.forEach(w -> w.keyPressed(keyCode, scanCode, modifiers));
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (searchMode) {
            if (chr >= 32 && chr < 127) {
                searchText.append(chr);
                performSearch();
            }
            return true;
        }

        windows.forEach(w -> w.charTyped(chr, modifiers));
        return super.charTyped(chr, modifiers);
    }

    private void performSearch() {
        String query = searchText.toString().trim().toLowerCase();
        for (Window window : windows) {
            window.setSearchQuery(query);
        }
    }

    private void resetAllSearch() {
        for (Window window : windows) {
            window.setSearchQuery(null);
        }
    }

    @Override
    public void close() {
        super.close();
        Managers.FEATURE.getFeatureFromClass(ClickGuiFeature.class).setEnabled(false);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}