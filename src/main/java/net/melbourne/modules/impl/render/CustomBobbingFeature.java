package net.melbourne.modules.impl.render;

import net.melbourne.modules.Category;
import net.melbourne.modules.Feature;
import net.melbourne.modules.FeatureInfo;
import net.melbourne.settings.types.NumberSetting;

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