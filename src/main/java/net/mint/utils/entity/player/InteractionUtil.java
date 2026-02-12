package net.mint.utils.entity.player;

import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static net.mint.utils.Globals.mc;

public class InteractionUtil {

    public static boolean place(BlockPos pos, boolean airPlace) {
        return place(pos, airPlace, Hand.MAIN_HAND);
    }

    public static boolean place(BlockPos pos, boolean airPlace, Hand hand) {
        if (mc.world == null || mc.player == null || mc.interactionManager == null) {
            return false;
        }

        if (!mc.world.getBlockState(pos).isReplaceable()) {
            return false;
        }

        Direction direction = getPlaceSide(pos);
        if (direction == null) {
            if (!airPlace) {
                return false;
            }
            direction = Direction.UP;
        }

        BlockPos neighbour = airPlace ? pos : pos.offset(direction);
        Vec3d hitVec = airPlace
                ? Vec3d.ofCenter(pos)
                : Vec3d.ofCenter(neighbour).add(
                direction.getOffsetX() * 0.5,
                direction.getOffsetY() * 0.5,
                direction.getOffsetZ() * 0.5
        );

        Direction hitSide = airPlace ? direction.getOpposite() : direction;

        BlockHitResult blockHitResult = new BlockHitResult(hitVec, hitSide, neighbour, false);

        mc.interactionManager.interactBlock(mc.player, hand, blockHitResult);
        mc.player.swingHand(hand);

        return true;
    }

    private static Direction getPlaceSide(BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos offset = pos.offset(dir);
            if (!mc.world.getBlockState(offset).isAir()) {
                return dir;
            }
        }
        return null;
    }
}