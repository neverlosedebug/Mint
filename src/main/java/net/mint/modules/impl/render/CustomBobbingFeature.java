package net.mint.modules.impl.render;

import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.NumberSetting;

@FeatureInfo(name = "CustomBobbing", category = Category.Render)
public class CustomBobbingFeature extends Feature {

    public NumberSetting intensity = new NumberSetting(
            "Intensity",
            "Adjust view bobbing intensity",
            1.0,
            0.0,
            10.0
    );
}