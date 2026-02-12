package net.mint.modules.impl.movement;

import net.mint.events.SubscribeEvent;
import net.mint.events.impl.TickEvent;
import net.mint.mixins.accessors.AbstractBlockAccessor;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.NumberSetting;
import net.mint.utils.miscellaneous.math.MathUtils;
import net.minecraft.block.Blocks;

@FeatureInfo(name = "IceSpeed", category = Category.Movement)
public class IceSpeedFeature extends Feature {
    public NumberSetting slipperiness = new NumberSetting("Slipperiness", "How much the ice will affect your movement.", 0.4, 0.2, 1.5);

    @Override
    public void onDisable() {
        if (getNull())
            return;

        ((AbstractBlockAccessor) Blocks.ICE).setSlipperiness(0.98f);
        ((AbstractBlockAccessor) Blocks.PACKED_ICE).setSlipperiness(0.98f);
        ((AbstractBlockAccessor) Blocks.FROSTED_ICE).setSlipperiness(0.98f);
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (getNull())
            return;

        ((AbstractBlockAccessor) Blocks.ICE).setSlipperiness(slipperiness.getValue().floatValue());
        ((AbstractBlockAccessor) Blocks.PACKED_ICE).setSlipperiness(slipperiness.getValue().floatValue());
        ((AbstractBlockAccessor) Blocks.FROSTED_ICE).setSlipperiness(slipperiness.getValue().floatValue());
    }

    @Override
    public String getInfo() {
        return MathUtils.round(slipperiness.getValue().doubleValue(), 1) + "";
    }
}
