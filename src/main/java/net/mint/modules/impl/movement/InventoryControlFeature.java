package net.mint.modules.impl.movement;

import net.mint.events.SubscribeEvent;
import net.mint.events.impl.TickEvent;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.AbstractCommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

@FeatureInfo(name = "InventoryControl", category = Category.Movement)
public class InventoryControlFeature extends Feature {

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (getNull())
            return;

        KeyBinding[] keys = {
            mc.options.forwardKey,
            mc.options.rightKey,
            mc.options.leftKey,
            mc.options.backKey,
            mc.options.jumpKey,
            mc.options.sprintKey
        };

        if (!(mc.currentScreen instanceof AbstractCommandBlockScreen) &&
            !(mc.currentScreen instanceof AbstractSignEditScreen) &&
            !(mc.currentScreen instanceof AnvilScreen) &&
            !(mc.currentScreen instanceof BookEditScreen) &&
            !(mc.currentScreen instanceof ChatScreen) &&
            (mc.currentScreen != null)) {
            
            for (KeyBinding bind : keys) {
                int keyCode = bind.getDefaultKey().getCode();
                KeyBinding.setKeyPressed(bind.getDefaultKey(), GLFW.glfwGetKey(mc.getWindow().getHandle(), keyCode) == GLFW.GLFW_PRESS);
            }
        }
    }
}