package net.mint.modules.impl.render;

import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.NumberSetting;
import net.minecraft.client.option.Perspective;

@FeatureInfo(name = "Freelook", category = Category.Render)
public class FreelookFeature extends Feature {
    public NumberSetting speed = new NumberSetting("Speed", "Changes speed for Freelook.", 5f, .1f, 10f);
    public float yaw, pitch;

    @Override
    public void onDisable() {
        if (getNull())
            return;

        mc.options.setPerspective(Perspective.FIRST_PERSON);
    }

    @Override
    public void onEnable() {
        if (getNull())
            return;

        yaw = mc.player.getYaw();
        pitch = mc.player.getPitch();

        mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
    }
}
