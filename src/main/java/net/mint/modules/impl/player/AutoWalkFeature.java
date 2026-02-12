package net.mint.modules.impl.player;

import net.mint.events.SubscribeEvent;
import net.mint.events.impl.PlayerUpdateEvent;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.minecraft.client.option.KeyBinding;

@FeatureInfo(name = "AutoWalk", category = Category.Player)
public class AutoWalkFeature extends Feature {

    @Override
    public void onDisable() {
        if (getNull())
            return;

        KeyBinding.setKeyPressed(mc.options.forwardKey.getDefaultKey(), false);
    }

    @SubscribeEvent
    public void onPlayerUpdate(PlayerUpdateEvent event) {
        if (getNull())
            return;

        KeyBinding.setKeyPressed(mc.options.forwardKey.getDefaultKey(), true);
    }
}
