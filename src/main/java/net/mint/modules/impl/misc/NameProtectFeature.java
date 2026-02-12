package net.mint.modules.impl.misc;

import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.TextSetting;

@FeatureInfo(name = "NameProtect", category = Category.Misc)
public class NameProtectFeature extends Feature {

    public static NameProtectFeature INSTANCE;

    public final TextSetting replacement = new TextSetting("Replacement", "Text to replace your name with", "REDACTED");

    public NameProtectFeature() {
        INSTANCE = this;
    }
}