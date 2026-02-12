package net.mint.modules.impl.render;

import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.ColorSetting;
import net.mint.settings.types.NumberSetting;

import java.awt.*;

@FeatureInfo(name = "ExtraTab", category = Category.Render)
public class ExtraTabFeature extends Feature {
    public NumberSetting limit = new NumberSetting("Limit", "The player limit on the tab list.", 1000, 1, 1000);
    public ColorSetting selfColor = new ColorSetting("Self", "The color of your own name.", new Color(255, 255, 255));

    @Override
    public String getInfo() {
        return String.valueOf(limit.getValue().intValue());
    }
}
