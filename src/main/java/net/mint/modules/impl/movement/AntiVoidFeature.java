package net.mint.modules.impl.movement;

import net.mint.events.SubscribeEvent;
import net.mint.events.impl.PlayerUpdateEvent;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.ModeSetting;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@FeatureInfo(name = "AntiVoid", category = Category.Movement)
public class AntiVoidFeature extends Feature {

    public ModeSetting mode = new ModeSetting("Mode", "Anti-void mode.", "Fling", new String[]{"Fling", "Float", "Bounce"});

    @SubscribeEvent
    public void onPlayerUpdate(PlayerUpdateEvent event) {
        if (getNull()) return;

        BlockPos playerPos = mc.player.getBlockPos();

        BlockPos bottomPos = new BlockPos(mc.player.getBlockX(), mc.world.getBottomY(), mc.player.getBlockZ());

        if (mc.world.getBlockState(bottomPos).isOf(Blocks.AIR)) {

            if (playerPos.getY() <= mc.world.getBottomY()) {

                switch (mode.getValue()) {
                    case "Fling":
                        mc.player.setVelocity(mc.player.getVelocity().withAxis(Direction.Axis.Y, 1.0));
                        break;
                    case "Bounce":
                        mc.player.jump();
                        break;
                    case "Float":
                        mc.player.setVelocity(mc.player.getVelocity().withAxis(Direction.Axis.Y, 0.0));
                        break;
                }
            }
        }
    }

    @Override
    public String getInfo() {
        return mode.getValue();
    }
}