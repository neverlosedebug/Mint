package net.mint.modules.impl.player;

import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.NumberSetting;


@FeatureInfo(name = "Reach", category = Category.Player)
public class ReachFeature extends Feature {
    public NumberSetting amount = new NumberSetting("Amount", "The maximum distance at which you will be able to interact with blocks.", 6.0, 0.0, 8.0);

    @Override
    public String getInfo() {
        return String.valueOf(amount.getValue().doubleValue());
    }
}
