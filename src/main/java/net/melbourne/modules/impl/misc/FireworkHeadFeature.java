package net.melbourne.modules.impl.misc;

import net.melbourne.Managers;
import net.melbourne.events.SubscribeEvent;
import net.melbourne.events.impl.TickEvent;
import net.melbourne.modules.Category;
import net.melbourne.modules.Feature;
import net.melbourne.modules.FeatureInfo;
import net.melbourne.settings.types.NumberSetting;
import net.melbourne.utils.entity.player.InteractionUtil;
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