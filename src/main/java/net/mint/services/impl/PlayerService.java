package net.mint.services.impl;

import lombok.Getter;
import net.mint.Mint;
import net.mint.events.SubscribeEvent;
import net.mint.events.impl.PacketReceiveEvent;
import net.mint.events.impl.PacketSendEvent;
import net.mint.services.Service;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;

@Getter
public class PlayerService extends Service {
    public int serverSlot;

    public PlayerService() {
        super("Player", "Handles player functions within the client");
        Mint.EVENT_HANDLER.subscribe(this);
    }

    @SubscribeEvent
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacket() instanceof UpdateSelectedSlotC2SPacket packet) {
            int packetSlot = packet.getSelectedSlot();

            if (!PlayerInventory.isValidHotbarIndex(packetSlot) || serverSlot == packetSlot) {
                event.setCancelled(true);
                return;
            }

            serverSlot = packetSlot;
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof UpdateSelectedSlotS2CPacket(int slot))
            serverSlot = slot;
    }
}