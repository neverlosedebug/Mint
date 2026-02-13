package net.mint.modules.impl.render;

import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.NumberSetting;

@FeatureInfo(name = "Aspect", category = Category.Render)
public class AspectFeature extends Feature {

    public NumberSetting ratio = new NumberSetting("Value", "Adjusts the game's aspect ratio to stretch or compress the screen.", 1.0f, 0.0f, 5.0f);
}