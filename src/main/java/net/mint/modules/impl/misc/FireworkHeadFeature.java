package net.mint.modules.impl.misc;

import net.mint.Managers;
import net.mint.events.SubscribeEvent;
import net.mint.events.impl.TickEvent;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.NumberSetting;
import net.mint.utils.entity.player.InteractionUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

@FeatureInfo(name = "FireworkHead", category = Category.Misc)
public class FireworkHeadFeature extends Feature {

    public NumberSetting range = new NumberSetting("Range", "Maximum distance to target players.", 5.0f, 1.0f, 6.0f);

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;

        float r = range.getValue().floatValue();
        float rangeSq = r * r;

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof PlayerEntity player)) continue;
            if (player == mc.player) continue;
            if (!player.isAlive()) continue;
            if (Managers.FRIEND.isFriend(player.getName().getString())) continue;
            if (mc.player.squaredDistanceTo(player) > rangeSq) continue;

            BlockPos pos = player.getBlockPos().up(2);
            InteractionUtil.place(pos, true);
        }
    }
}