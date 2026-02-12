package net.mint.modules.impl.render;

import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.NumberSetting;

@FeatureInfo(name = "ViewClip", category = Category.Render)
public class ViewClipFeature extends Feature {
    public NumberSetting distance = new NumberSetting("Distance", "Changes the reach you have with viewclip", 4.0f, -50.0f, 50.0f);
}
