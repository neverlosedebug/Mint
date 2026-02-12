package net.mint.modules.impl.movement;


import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.NumberSetting;

@FeatureInfo(name = "KeepSprint", category = Category.Movement)
public class KeepSprintFeature extends Feature {

    public NumberSetting motion = new NumberSetting("Motion", "The velocity that will be applied to your movement when attacking.", 100.0f, 0.0f, 100.0f);

    @Override
    public String getInfo() {
        return String.format("%.1f", motion.getValue().doubleValue()) + "%";
    }
}
