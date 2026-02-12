package net.mint.modules.impl.combat;

import net.mint.events.SubscribeEvent;
import net.mint.events.impl.TickEvent;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

@FeatureInfo(name = "Mainhand", category = Category.Combat)
public class MainhandFeature extends Feature {

    private boolean holding = true;

    @Override
    public void onEnable() {
        holding = mc.player != null && mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING);
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.interactionManager == null) return;
        boolean holdingNow = mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING);
        if (holding && !holdingNow) {
            int slot = findHotbarTotem();
            if (slot != -1) {
                mc.player.getInventory().setSelectedSlot(slot);
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
            }
        }
        holding = holdingNow;
    }

    private int findHotbarTotem() {
        for (int i = 0; i < 9; i++) if (mc.player.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) return i;
        return -1;
    }
}