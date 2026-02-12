package net.mint.modules.impl.render;

import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.NumberSetting;

@FeatureInfo(name = "Zoom", category = Category.Render)
public class ZoomFeature extends Feature {

    public NumberSetting zoomFov = new NumberSetting(
            "Zoom FOV",
            "Field of view when zoom is active",
            30,
            10,
            120
    );

    private float originalFov = -1.0f;

    @Override
    public void onEnable() {
        if (mc.options == null || mc.player == null) return;

        originalFov = mc.options.getFov().getValue();
        mc.options.getFov().setValue(zoomFov.getValue().intValue());
    }

    @Override
    public void onDisable() {
        if (originalFov != -1.0f && mc.options != null) {
            mc.options.getFov().setValue((int) originalFov);
            originalFov = -1.0f;
        }
    }
}