package net.mint.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.mint.events.Event;
import net.minecraft.network.packet.Packet;

@Getter
@AllArgsConstructor
public class PacketReceiveEvent extends Event {
    private final Packet<?> packet;
}