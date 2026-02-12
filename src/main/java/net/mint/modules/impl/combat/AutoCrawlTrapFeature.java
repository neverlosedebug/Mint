package net.mint.modules.impl.combat;

import net.mint.Managers;
import net.mint.modules.PlaceFeature;
import net.mint.services.Services;
import net.mint.events.SubscribeEvent;
import net.mint.events.impl.TickEvent;
import net.mint.modules.Category;
import net.mint.modules.FeatureInfo;
import net.mint.utils.inventory.switches.SearchLogic;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.EntityPose;

import java.util.Collections;

@FeatureInfo(name = "AutoCrawlTrap", category = Category.Combat)
public class AutoCrawlTrapFeature extends PlaceFeature {

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null || shouldDisable()) return;

        PlayerEntity target = findTarget(6.0);
        if (target == null || target.getPose() != EntityPose.SWIMMING) return;

        BlockPos targetPos = getPlayerPos(target).up();
        double range = placeRange.getValue().doubleValue();

        if (!mc.world.getBlockState(targetPos).isReplaceable() || mc.player.getEyePos().distanceTo(Vec3d.ofCenter(targetPos)) > range)
            return;

        Slot obbySlot = Services.INVENTORY.findSlot(SearchLogic.OnlyHotbar, Items.OBSIDIAN, Items.ENDER_CHEST.canBeNested());
        if (obbySlot == null) return;

        placeBlocks(Collections.singletonList(targetPos));
    }

    private PlayerEntity findTarget(double range) {
        return mc.world.getPlayers().stream()
                .filter(p -> p != mc.player && mc.player.distanceTo(p) <= range && !p.isDead() && !Managers.FRIEND.isFriend(p.getName().getString()))
                .min((a, b) -> Double.compare(mc.player.distanceTo(a), mc.player.distanceTo(b)))
                .orElse(null);
    }

    private BlockPos getPlayerPos(PlayerEntity player) {
        double y = player.getY();
        double dec = y - Math.floor(y);
        double blockY = dec > 0.8 ? Math.floor(y) + 1 : Math.floor(y);
        return new BlockPos((int) Math.floor(player.getX()), (int) blockY, (int) Math.floor(player.getZ()));
    }
}