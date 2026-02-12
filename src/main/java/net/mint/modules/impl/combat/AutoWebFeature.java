package net.mint.modules.impl.combat;

import net.mint.Managers;
import net.mint.modules.PlaceFeature;
import net.mint.events.SubscribeEvent;
import net.mint.events.impl.TickEvent;
import net.mint.modules.Category;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.BooleanSetting;
import net.mint.settings.types.ModeSetting;
import net.mint.settings.types.NumberSetting;
import net.mint.utils.block.hole.HoleUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@FeatureInfo(name = "AutoWeb", category = Category.Combat)
public class AutoWebFeature extends PlaceFeature {

    private final ModeSetting mode = new ModeSetting("Mode", "Normal = web instantly, Smart = wait for hole exit", "Normal", new String[]{"Normal", "Smart"});
    private final NumberSetting enemyRange = new NumberSetting("EnemyRange", "Range to look for targets", 6.0, 1.0, 12.0);
    private final NumberSetting extrapolation = new NumberSetting("Extrapolation", "Predict target movement", 0, 0, 10);
    private final BooleanSetting holeCheck = new BooleanSetting("HoleCheck", "Only web if target is in hole", false);

    private final Map<UUID, Boolean> inHole = new HashMap<>();

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null || shouldDisable()) return;

        PlayerEntity target = findTarget();
        if (target == null) return;

        boolean inHole = HoleUtils.isPlayerInHole(target);
        boolean prevInHole = this.inHole.getOrDefault(target.getUuid(), false);
        this.inHole.put(target.getUuid(), inHole);

        if (mode.getValue().equalsIgnoreCase("Smart")) {
            if (inHole) {
                if (!isTryingToExitHole(target)) return;
            } else {
                if (prevInHole) {
                    if (!isTryingToExitHole(target)) return;
                }
            }
        }

        Vec3d predictedPos = target.getPos().add(target.getVelocity().multiply(extrapolation.getValue().doubleValue()));
        BlockPos webPos = BlockPos.ofFloored(predictedPos);

        if (mode.getValue().equalsIgnoreCase("Smart") && inHole) {
            webPos = webPos.up();
        }

        if (target.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA) && mc.world.getBlockState(webPos).isOf(Blocks.COBWEB)) {
            webPos = webPos.up();
        }

        if (!mc.world.getBlockState(webPos).isReplaceable() || mc.world.getBlockState(webPos).isOf(Blocks.COBWEB))
            return;
        if (isEntityBlocking(webPos, target)) return;

        placeBlocks(Collections.singletonList(webPos), Items.COBWEB);
    }

    private boolean isTryingToExitHole(PlayerEntity target) {
        Vec3d v = target.getVelocity();
        double h2 = (v.x * v.x) + (v.z * v.z);
        return v.y > 0.05 || h2 > 0.0125;
    }

    private boolean isEntityBlocking(BlockPos pos, PlayerEntity target) {
        return mc.world.getOtherEntities(null, new Box(pos)).stream()
                .anyMatch(e -> e != target && e.isAlive());
    }

    private PlayerEntity findTarget() {
        return mc.world.getPlayers().stream()
                .filter(p -> p != mc.player && p.isAlive())
                .filter(p -> !Managers.FRIEND.isFriend(p.getName().getString()))
                .filter(p -> mc.player.distanceTo(p) <= enemyRange.getValue().floatValue())
                .filter(p -> !holeCheck.getValue() || HoleUtils.isPlayerInHole(p))
                .min(Comparator.comparingDouble(p -> mc.player.squaredDistanceTo(p)))
                .orElse(null);
    }
}
