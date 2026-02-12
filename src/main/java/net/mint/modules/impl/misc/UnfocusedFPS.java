package net.mint.modules.impl.misc;

import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.NumberSetting;

@FeatureInfo(name = "UnfocusedFPS", category = Category.Misc)
public class UnfocusedFPS extends Feature {

    public NumberSetting limit = new NumberSetting("Limit", "FPS limit when the game window is not focused", 30, 1, 120);

}