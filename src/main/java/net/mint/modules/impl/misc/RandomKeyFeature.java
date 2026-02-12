package net.mint.modules.impl.misc;

import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.NumberSetting;
import net.mint.utils.miscellaneous.RandomKey;

@FeatureInfo(name = "RandomKey", category = Category.Misc)
public class RandomKeyFeature extends Feature {

    public NumberSetting keyLength = new NumberSetting(
            "Length (bytes)",
            "Key length in bytes (16 = 128-bit, 32 = 256-bit, etc.)",
            32,
            16,
            64
    );

    @Override
    public void onEnable() {
        if (getNull()) {
            setEnabled(false);
            return;
        }

        RandomKey.generateAndSendToChat((int) keyLength.getValue());
        setEnabled(false);
    }
}