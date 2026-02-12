package net.mint.modules.impl.client;

import net.mint.Mint;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.BooleanSetting;
import net.mint.settings.types.ColorSetting;
import net.mint.settings.types.NumberSetting;
import net.mint.settings.types.TextSetting;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

@FeatureInfo(name = "ClickGui", category = Category.Client, bind = GLFW.GLFW_KEY_R)
public class ClickGuiFeature extends Feature {

    public BooleanSetting sounds = new BooleanSetting("Sounds", "Custom sounds when enabling or disabling modules.", false);
    public BooleanSetting outline = new BooleanSetting("Outline", "Enable window outline.", true);
    public ColorSetting backgroundColor = new ColorSetting("Background", "GUI background color.", new Color(30, 30, 30, 150));
    public NumberSetting animationSpeed = new NumberSetting("Anim Speed", "Window open/close animation speed (ms).", 150, 50, 500);
    public BooleanSetting showGear = new BooleanSetting("Show Gear", "Show open/close symbols next to module names.", false);
    public TextSetting openSymbol = new TextSetting("OpenGear", "Gear shown when module settings are collapsed", "=");
    public TextSetting closeSymbol = new TextSetting("CloseGear", "Gear shown when module settings are expanded", "≡");

    @Override
    public void onEnable() {
        if (mc.player == null) {
            setEnabled(false);
            return;
        }
        Mint.CLICK_GUI.setClose(false);
        mc.setScreen(Mint.CLICK_GUI);
    }

    @Override
    public void onDisable() {
        if (getNull())
            return;
        mc.setScreen(null);
    }
}