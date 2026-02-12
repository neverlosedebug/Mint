package net.mint.modules.impl.client;

import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.*;

import java.awt.Color;

@FeatureInfo(name = "Renders", category = Category.Client)
public final class RendersFeature extends Feature {

    public final ColorSetting blockColor = new ColorSetting(
            "Blocks", "Colour of the rendered blocks", new Color(255, 0, 0, 80)
    );

    public final ModeSetting renderMode = new ModeSetting(
            "Mode", "How the block disappears", "Fade",
            new String[]{"Fade", "Shrink"}
    );

    public final NumberSetting renderTime = new NumberSetting(
            "Time", "How long a block stays visible (ms).", 500, 50, 2000
    );

    public final ColorSetting friendColor = new ColorSetting(
            "Friends", "Colour of your client-side allies.", new Color(85, 255, 255)
    );

    public Color getBlockColor()          { return blockColor.getColor(); }
    public String getRenderMode()         { return renderMode.getValue(); }
    public long getRenderTimeMillis()    { return renderTime.getValue().longValue(); }
    public Color getFriendColor()          { return friendColor.getColor(); }

}