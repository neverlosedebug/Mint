package net.melbourne.modules.impl.misc;

import net.melbourne.modules.Category;
import net.melbourne.modules.Feature;
import net.melbourne.modules.FeatureInfo;
import net.melbourne.settings.types.NumberSetting;

@FeatureInfo(name = "UnfocusedFPS", category = Category.Misc)
public class UnfocusedFPS extends Feature {

    public NumberSetting limit = new NumberSetting("Limit", "FPS limit when the game window is not focused", 30, 1, 120);

}