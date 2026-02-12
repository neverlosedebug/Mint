package net.mint.macros;

import net.mint.Managers;
import net.mint.utils.Globals;
import org.lwjgl.glfw.GLFW;

public class MacroHandler implements Globals {

    private static final boolean[] lastKeyState = new boolean[512];

    public static void tick() {
        if (mc.getWindow() == null) return;
        if (mc.currentScreen != null) return;

        long window = mc.getWindow().getHandle();

        Managers.MACRO.macros.forEach(macro -> {
            int key = macro.bind;

            if (key <= 0 || key >= lastKeyState.length) return;

            boolean pressed = GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;

            if (pressed && !lastKeyState[key]) {
                macro.trigger();
            }

            lastKeyState[key] = pressed;
        });
    }
}