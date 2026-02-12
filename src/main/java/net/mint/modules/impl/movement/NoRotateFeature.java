package net.mint.modules.impl.movement;

import net.mint.events.SubscribeEvent;
import net.mint.events.impl.PacketReceiveEvent;
import net.mint.mixins.accessors.PlayerPositionAccessor;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;

@FeatureInfo(name = "NoRotate", category = Category.Movement)
public class NoRotateFeature extends Feature {

    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (event.getPacket() instanceof PlayerPositionLookS2CPacket packet) {
            ((PlayerPositionAccessor) (Object) packet.change()).setYaw(mc.player.getYaw());
            ((PlayerPositionAccessor) (Object) packet.change()).setPitch(mc.player.getPitch());

            packet.relatives().remove(PositionFlag.X_ROT);
            packet.relatives().remove(PositionFlag.Y_ROT);
        }
    }
}