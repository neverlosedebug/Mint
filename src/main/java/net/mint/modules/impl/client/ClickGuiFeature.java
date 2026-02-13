package net.mint.modules.impl.client;

import net.mint.Mint;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.*;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

@FeatureInfo(name = "ClickGui", category = Category.Client, bind = GLFW.GLFW_KEY_R)
public class ClickGuiFeature extends Feature {

    public BooleanSetting sounds = new BooleanSetting("Sounds", "Custom sounds when enabling or disabling modules.", false);
    public BooleanSetting outline = new BooleanSetting("Outline", "Enable window outline.", true);
    public BooleanSetting accentBar = new BooleanSetting("Accent Bar", "Show a colored accent bar next to each module.", false);
    public ColorSetting accentColor = new ColorSetting(
            "Accent Color",
            "Color of the module accent bar.",
            new Color(163, 255, 202),
            () -> accentBar.getValue()
    );
    public ModeSetting accentSide = new ModeSetting(
            "Accent Side",
            "Position of the accent bar.",
            "Left",
            new String[]{"Left", "Right"},
            () -> accentBar.getValue()
    );
    public BooleanSetting moduleOutline = new BooleanSetting("Module Outline", "Show a colored outline around each module.", true);
    public ColorSetting outlineColor = new ColorSetting(
            "Outline Color",
            "Color of the module outline.",
            new Color(0, 0, 0),
            () -> moduleOutline.getValue()
    );
    public ColorSetting enabledTextColor = new ColorSetting("Enabled Text", "Color of enabled module names.", Color.WHITE);
    public ColorSetting disabledTextColor = new ColorSetting("Disabled Text", "Color of disabled module names.", new Color(192, 192, 192));
    public BooleanSetting glowEffect = new BooleanSetting( // todo: optimize this
            "Glow Effect",
            "Enable a subtle fade-in glow from the left edge of each module.",
            false
    );

    public ColorSetting glowColor = new ColorSetting(
            "Glow Color",
            "Color and transparency of the subtle glow effect.",
            new Color(163, 255, 202, 100),
            () -> glowEffect.getValue()
    );
    public BooleanSetting topGlow = new BooleanSetting(
            "Top Glow",
            "Show a smooth dark gradient below the window header.",
            false
    );

    public ColorSetting topGlowColor = new ColorSetting(
            "Top Glow Color",
            "Color of the top glow effect (recommended: black with alpha).",
            new Color(0, 0, 0, 100),
            () -> topGlow.getValue()
    );
    public BooleanSetting showGear = new BooleanSetting("Show Gear", "Show open/close symbols next to module names.", false);
    public TextSetting openSymbol = new TextSetting(
            "OpenGear",
            "Gear shown when module settings are collapsed",
            "=",
            () -> showGear.getValue()
    );

    public TextSetting closeSymbol = new TextSetting(
            "CloseGear",
            "Gear shown when module settings are expanded",
            "≡",
            () -> showGear.getValue()
    );
    public ColorSetting backgroundColor = new ColorSetting("Background", "GUI background color.", new Color(30, 30, 30, 150));
    public NumberSetting animationSpeed = new NumberSetting("Anim Speed", "Window open/close animation speed (ms).", 150, 50, 500);

    public BooleanSetting resetToDefault = new BooleanSetting("Reset to Default", "Reset all ClickGUI settings to their default values.", false) {
        @Override
        public void setValue(boolean value) {
            if (value) {
                sounds.setValue(false);
                outline.setValue(true);
                accentBar.setValue(false);
                accentColor.setValue(new Color(163, 255, 202));
                accentSide.setValue("Left");
                moduleOutline.setValue(false);
                outlineColor.setValue(new Color(0, 0, 0));
                glowEffect.setValue(false);
                glowColor.setValue(new Color(163, 255, 202, 100));
                topGlow.setValue(false);
                topGlowColor.setValue(new Color(0, 0, 0, 100));
                showGear.setValue(false);
                openSymbol.setValue("=");
                closeSymbol.setValue("≡");
                backgroundColor.setValue(new Color(30, 30, 30, 150));
                animationSpeed.setValue(150.0);
                enabledTextColor.setValue(Color.WHITE);
                disabledTextColor.setValue(new Color(192, 192, 192));

                super.setValue(false);
            } else {
                super.setValue(false);
            }
        }
    };

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