package net.mint.modules.impl.client;

import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.ColorSetting;
import net.mint.settings.types.ModeSetting;
import net.mint.settings.types.NumberSetting;

import java.awt.*;

@FeatureInfo(name = "Color", category = Category.Client)
public class ColorFeature extends Feature {

    public ModeSetting mode = new ModeSetting("Mode", "Color mode", "Static", new String[]{"Static", "Rainbow", "Gradient"}); // gradient applies only to the hud
    public ColorSetting color = new ColorSetting("Color", "The global color that is used.", new Color(163, 255, 202));
    public NumberSetting rainbowSpeed = new NumberSetting("RainbowSpeed", "Speed of the rainbow", 10, 1, 20);
    public NumberSetting rainbowLength = new NumberSetting("RainbowLength", "Length of the rainbow wave", 8, 1, 20);
    public NumberSetting rainbowSaturation = new NumberSetting("RainbowSaturation", "Saturation of the rainbow", 1.0f, 0.0f, 1.0f);

}