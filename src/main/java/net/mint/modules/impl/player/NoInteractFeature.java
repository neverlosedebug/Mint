package net.mint.modules.impl.player;

import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.BooleanSetting;
import net.mint.settings.types.ModeSetting;
import net.minecraft.item.Items;

@FeatureInfo(name = "NoInteract", category = Category.Player)
public class NoInteractFeature extends Feature {

    public ModeSetting mode = new ModeSetting("Mode", "The way that right-clickable blocks will be ignored.", "Sneak", new String[]{"Sneak", "Disable"});
    public BooleanSetting gapple = new BooleanSetting("Gapple", "Only disables interactions when holding a golden apple in your main hand.", false);

    @Override
    public String getInfo() {
        return mode.getValue();
    }

    public boolean shouldNoInteract() {
        return !gapple.getValue() || mc.player.getMainHandStack().getItem().equals(Items.ENCHANTED_GOLDEN_APPLE);
    }
}
