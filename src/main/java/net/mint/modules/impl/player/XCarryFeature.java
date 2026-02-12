package net.mint.modules.impl.player;

import net.mint.events.SubscribeEvent;
import net.mint.events.impl.PacketSendEvent;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

@FeatureInfo(name = "XCarry", category = Category.Player)
public class XCarryFeature extends Feature {

    @SubscribeEvent
    public void onPacketSend(PacketSendEvent event) {
        if (getNull()) return;
        if (event.getPacket() instanceof CloseHandledScreenC2SPacket) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onDisable() {
        if (getNull()) return;
        mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(mc.player.playerScreenHandler.syncId));
    }
}