package net.mint.modules.impl.combat;

import net.mint.modules.PlaceFeature;
import net.mint.events.SubscribeEvent;
import net.mint.events.impl.TickEvent;
import net.mint.modules.Category;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.BooleanSetting;
import net.mint.settings.types.ModeSetting;
import net.mint.utils.block.hole.HoleUtils;
import net.minecraft.util.math.BlockPos;

import java.util.List;

@FeatureInfo(name = "SelfTrap", category = Category.Combat)
public class SelfTrapFeature extends PlaceFeature {

    public final ModeSetting trapMode = new ModeSetting("Mode", "Placement layout", "Full", new String[]{"Partial", "Full"});
    public final BooleanSetting head = new BooleanSetting("Head", "Cover head", true, () -> trapMode.getValue().equalsIgnoreCase("Full"));
    public final BooleanSetting antiStep = new BooleanSetting("AntiStep", "Prevent step out", false);
    public final BooleanSetting antiBomb = new BooleanSetting("AntiBomb", "Extra top block", false);
    public final BooleanSetting holeCheck = new BooleanSetting("HoleCheck", "Only if in hole", false);

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (shouldDisable()) return;
        if (holeCheck.getValue() && !HoleUtils.isPlayerInHole(mc.player)) return;

        List<BlockPos> trapPositions = HoleUtils.getTrapPositions(
                mc.player,
                trapMode.getValue().equalsIgnoreCase("Partial"),
                head.getValue(),
                antiStep.getValue(),
                antiBomb.getValue(),
                strictDirection.getValue()
        );

        if (trapPositions.isEmpty()) return;

        placeBlocks(getPath(trapPositions));
    }
}