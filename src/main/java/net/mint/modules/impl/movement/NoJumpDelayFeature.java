package net.mint.modules.impl.movement;

import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.NumberSetting;

@FeatureInfo(name = "NoJumpDelay", category = Category.Movement)
public class NoJumpDelayFeature extends Feature {
    public NumberSetting ticks = new NumberSetting("Ticks", "The amount of ticks it takes to start jumping again.", 1, 0, 20);

    @Override
    public String getInfo() {
        return String.valueOf(ticks.getValue());
    }
}
